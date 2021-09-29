/*
 * Copyright 1999-2021 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.fastffi.annotation;

import com.alibaba.fastffi.CXXTemplate;
import com.alibaba.fastffi.FFIFunGen;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFIGenBatch;
import com.alibaba.fastffi.FFILibrary;
import com.alibaba.fastffi.FFITypeAlias;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.function.Function;

import static com.alibaba.fastffi.annotation.AnnotationProcessorUtils.getTypeElement;
import static com.alibaba.fastffi.annotation.AnnotationProcessorUtils.toHeaderGuard;

public class TypeDefRegistry {

    static class Key {
        String cxx;
        String java;

        public Key(String cxx, String java) {
            this.cxx = cxx;
            this.java = java;
        }

        public String toString() {
            return "<" + cxx + "," + java + ">";
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Key key = (Key) o;
            return Objects.equals(cxx, key.cxx) && Objects.equals(java, key.java);
        }

        @Override
        public int hashCode() {
            return Objects.hash(cxx, java);
        }
    }

    private AnnotationProcessor processor;
    /**
     * TODO: currently we require that the mapping of cxx full name to type def must be one-one and onto
     * In the future, we should use cxx full name and base type (i.e., theTypeMirror) as a key to build the mapping.
     */
    private Map<Key, TypeDef> FFIPointerDefMap = new HashMap<>();
    private Map<Key, TypeDef> FFILibraryDefMap = new HashMap<>();
    private Map<Key, TypeDefGenerator> FFIMirrorGeneratorMap = new HashMap<>();
    private Map<String, List<TypeDefGenerator>> FFIMirrorMapGroupByLibraryName = new HashMap<>();
    /**
     * A template is simply a map between the Java interface and the type def.
     */
    private Map<String, TypeDef> FFITemplateDefMap = new HashMap<>();

    // Must be one-one mapping for all FFIPointer and FFILibrary
    private Map<String, TypeDef> javaNameToTypeDef = new HashMap<>();

    private List<TypeDefGenerator> ffiTemplateList = new ArrayList<>();
    private List<TypeDefGenerator> ffiPointerOrFFILibraryList = new ArrayList<>();

    public TypeDefRegistry(AnnotationProcessor processor) {
        this.processor = processor;
    }

    public TypeDef getTypeDefByForeignName(String foreignType, DeclaredType typeMirror, TypeElement typeElement, boolean genIfMissing) {
        Key key = toKey(foreignType, typeElement);
        TypeDef typeDef = FFIPointerDefMap.get(key);
        if (typeDef == null) {
            FFIGen ffiGen = typeElement.getAnnotation(FFIGen.class);
            if (ffiGen != null) {
                if (!genIfMissing) {
                    processor.processType(typeElement, false);
                } else {
                    if (typeElement.getTypeParameters().isEmpty()) {
                        processor.processType(typeElement, ffiGen, null, true);
                    } else {
                        // Now we need to create a CXXTemplate from typeMirror
                        CXXTemplate template = getCXXTemplateForBuiltinType(typeMirror);
                        processor.processType(typeElement, ffiGen, template,true);
                    }
                }
            }
            typeDef = FFIPointerDefMap.get(key);
        }
        return typeDef;
    }

    private CXXTemplate getCXXTemplateForBuiltinType(DeclaredType typeMirror) {
        TypeEnv typeEnv = processor.getTypeEnv();
        return typeEnv.getCXXTemplateForBuiltinType(typeMirror);
    }

    public boolean registerFFITemplateTypeDef(TypeDef def, TypeDefGenerator generator, boolean gen) {
        if (registerFFITemplateTypeDef(def)) {
            if (gen) addGenerator(generator);
            return true;
        }
        return false;
    }

    public TypeDef getFFITemplateTypeDef(String typeName) {
        return FFITemplateDefMap.get(typeName);
    }


    private boolean registerFFITemplateTypeDef(TypeDef def) {
        String key = def.getDeclaredTypeElementName();
        TypeDef check = this.FFITemplateDefMap.get(key);
        if (check == null) {
            this.FFITemplateDefMap.put(key, def);
            return true;
        }
        if (!isSameFFIGen(check.getFFIGen(), def.getFFIGen())) {
            throw new IllegalStateException("A TypeDef has accepted two generation requests: " + check + " vs. " + def);
        }
        return false;
    }

    private boolean isSameFFIGen(FFIGen gen1, FFIGen gen2) {
        if (!gen1.type().equals(gen2.type())) {
            return false;
        }
        if (!gen1.library().equals(gen2.library())) {
            return false;
        }
        if (!isSameCXXTemplates(gen1.templates(), gen2.templates())) {
            return false;
        }
        return isSameFFIFunGens(gen1.functionTemplates(), gen2.functionTemplates());
    }

    private boolean isSameFFIFunGens(FFIFunGen[] templates1, FFIFunGen[] templates2) {
        if (templates1.length != templates2.length) {
            return false;
        }
        for (int i = 0; i < templates1.length; i++) {
            if (!isSameFFIFunGen(templates1[i], templates2[i])) {
                return false;
            }
        }
        return true;
    }

    private boolean isSameFFIFunGen(FFIFunGen gen1, FFIFunGen gen2) {
        if (!gen1.name().equals(gen2.name())) {
            return false;
        }
        if (!gen1.returnType().equals(gen2.returnType())) {
            return false;
        }
        if (!Arrays.equals(gen1.parameterTypes(), gen2.parameterTypes())) {
            return false;
        }
        return isSameCXXTemplates(gen1.templates(), gen2.templates());
    }

    private boolean isSameCXXTemplates(CXXTemplate[] templates1, CXXTemplate[] templates2) {
        if (templates1.length != templates2.length) {
            return false;
        }
        for (int i = 0; i < templates1.length; i++) {
            if (!isSameCXXTemplate(templates1[i], templates2[i])) {
                return false;
            }
        }
        return true;
    }

    private boolean isSameCXXTemplate(CXXTemplate template1, CXXTemplate template2) {
        if (!template1.cxxFull().equals(template2.cxxFull())) {
            return false;
        }
        if (!Arrays.equals(template1.java(), template2.java())) {
            return false;
        }
        return Arrays.equals(template1.cxx(), template2.cxx());
    }

    public void registerFFIPointerTypeDef(TypeDef def, TypeDefGenerator generator, boolean gen) {
        if (registerFFIPointerTypeDef(def)) {
            if (gen) addGenerator(generator);
        }
    }


    public String getFFIMirrorHeaderName(TypeDef typeDef) {
        TypeDefGenerator defGenerator = FFIMirrorGeneratorMap.get(toKey(typeDef));
        if (defGenerator == null) {
            throw new IllegalStateException("Cannot find FFIMirror generator for " + typeDef);
        }
        return defGenerator.getGeneratedHXXFileName();
    }

    private boolean registerFFIPointerTypeDef(TypeDef def) {
        Key key = toKey(def);
        boolean added = this.FFIPointerDefMap.containsKey(key);
        if (added) return false;
        this.FFIPointerDefMap.put(key, def);
        this.javaNameToTypeDef.put(def.getGeneratedJavaClassName(), def);
        return true;
    }

    static Key toKey(String foreignType, TypeElement typeElement) {
        return new Key(foreignType, typeElement.getQualifiedName().toString());
    }

    static Key toKey(TypeDef typeDef) {
        return new Key(typeDef.getTypeRegistryId(), typeDef.getDeclaredTypeElementName());
    }

    public void addGenerator(TypeDefGenerator generator) {
        if (generator.isGenFFILibrary() || generator.isGenFFIPointer()) {
            ffiPointerOrFFILibraryList.add(generator);
            if (generator.getTypeDef().isFFIMirror()) {
                registerFFIMirror(generator);
            }
            return;
        }
        if (generator.isGenFFITemplate()) {
            ffiTemplateList.add(generator);
            return;
        }
        throw new IllegalStateException("Do not know how to generate " + generator.getTypeDef());
    }

    private void registerFFIMirror(TypeDefGenerator generator) {
        if (FFIMirrorGeneratorMap.put(toKey(generator.getTypeDef()), generator) != null) {
            return;
        }
        {
            String libraryName = generator.getLibraryName();
            List<TypeDefGenerator> libraryDefGenerator =
                FFIMirrorMapGroupByLibraryName.computeIfAbsent(libraryName, k -> new ArrayList<>());
            libraryDefGenerator.add(generator);
        }
    }

    public String getFFIMirrorLibraryHeaderName(TypeDefGenerator generator) {
        return getFFIMirrorLibraryHeaderName(generator.getLibraryName());
    }

    public String getFFIMirrorLibraryHeaderName(String libraryName) {
        if (libraryName == null || libraryName.isEmpty()) {
           return "fastffi.h";
        }
        return libraryName + "-ffi.h";
    }

    private boolean registerFFILibraryTypeDef(TypeDef def) {
        Key key = toKey(def);
        boolean added = FFILibraryDefMap.containsKey(key);
        if (added) return false;
        this.FFILibraryDefMap.put(key, def);
        this.javaNameToTypeDef.put(def.getGeneratedJavaClassName(), def);
        return true;
    }

    public void registerFFILibraryTypeDef(TypeDef def, TypeDefGenerator generator, boolean gen) {
        if (registerFFILibraryTypeDef(def)) {
            if (gen) addGenerator(generator);
        }
    }

    static void writeProperties(Properties properties, FileObject fileObject) throws IOException {
        try (Writer writer = fileObject.openWriter()) {
            properties.store(writer, "Generated by fastFFI");
            writer.flush();
        }
    }

    public void generate() {
        // First expand all templates
        if (generateFFITemplate()) {
            return;
        }
        generateFFIPointerFFILibrary();
    }

    public boolean generateFFITemplate() {
        return processWorklist(ffiTemplateList, generator -> {
            if (generator.isCreated()) {
                generator.generate();
                return true;
            }
            return false;
        });
    }

    boolean processWorklist(List<TypeDefGenerator> worklist, Function<TypeDefGenerator, Boolean> function) {
        if (worklist.isEmpty()) {
            return false;
        }
        int begin = 0; int end = worklist.size();
        boolean hasGenerated = false;
        while (true) {
            // now we can accept new template during generate FFITemplate
            for (int i = begin; i < end; i++) {
                TypeDefGenerator generator = worklist.get(i);
                hasGenerated |= function.apply(generator);
            }
            if (end == worklist.size()) {
                break;
            }
            begin = end;
            end = worklist.size();
        }
        return hasGenerated;
    }

    public void generateFFIPointerFFILibrary() {
        processWorklist(ffiPointerOrFFILibraryList, generator -> {
            if (generator.isCreated()) {
                generator.generate();
                return true;
            }
            return false;
        });
    }

    void lastRound(ProcessingEnvironment processingEnv) {
        // after all have done.
        writeProperties(processingEnv);
    }

    void writeProperties(ProcessingEnvironment processingEnv) {
        if (!FFIPointerDefMap.isEmpty()) {
            Properties properties = new Properties();
            FFIPointerDefMap.forEach( (key, value) -> properties.put(value.getGeneratedJavaClassName(), key.cxx));

            try {
                writeProperties(properties, processingEnv.getFiler().createResource(StandardLocation.CLASS_OUTPUT, "", "ffi.properties"));
            } catch (IOException exc) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                        "" + exc.getClass().getName() + ": " + exc.getMessage());
            }
        }
        if (!FFILibraryDefMap.isEmpty()) {
            Properties properties = new Properties();
            FFILibraryDefMap.forEach( (key, value) -> properties.put(value.getGeneratedJavaClassName(), key.cxx));

            try {
                writeProperties(properties, processingEnv.getFiler().createResource(StandardLocation.CLASS_OUTPUT, "", "ffilibrary.properties"));
            } catch (IOException exc) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                        "" + exc.getClass().getName() + ": " + exc.getMessage());
            }
        }
        writeFFIMirrorHead(processingEnv);
    }

    private void writeFFIMirrorHead(ProcessingEnvironment processingEnv) {
        FFIMirrorMapGroupByLibraryName.forEach((k, v) -> {
            StringBuilder hxx = new StringBuilder();
            String header = getFFIMirrorLibraryHeaderName(k);
            String guard = toHeaderGuard(header);
            hxx.append("#ifndef ").append(guard).append("\n");
            hxx.append("#define ").append(guard).append("\n");
            v.forEach(gen -> {
                gen.generateFFIMirrorHeader();
                hxx.append("#include \"").append(gen.getGeneratedHXXFileName()).append("\"\n");
            });
            hxx.append("#endif // ").append(guard).append("\n");

            try {
                FileObject fileObject = processingEnv.getFiler().createResource(StandardLocation.SOURCE_OUTPUT, "",
                        header);
                try (Writer writer = fileObject.openWriter()) {
                    writer.write(hxx.toString());
                    writer.flush();
                }
            }catch (IOException e) {
                throw new IllegalStateException("Cannot write " + header + " due to " + e.getMessage(), e);
            }
        });
    }

    void processType(ProcessingEnvironment processingEnv, TypeElement typeElement, CXXTemplate template, boolean gen) {
        processType(processingEnv, typeElement, typeElement.getAnnotation(FFIGen.class), template, gen);
    }

    void processType(ProcessingEnvironment processingEnv, TypeElement typeElement, FFIGen ffiGen, boolean gen) {
        // No support for non-interface classes
        if (typeElement.getKind() != ElementKind.INTERFACE) {
            throw new IllegalArgumentException("TODO: We only support generate code for interfaces");
        }
        FFITypeAlias typeAlias = typeElement.getAnnotation(FFITypeAlias.class);
        FFILibrary ffiLibrary = typeElement.getAnnotation(FFILibrary.class);
        if (typeAlias == null) {
            if (ffiLibrary == null) {
                throw new IllegalArgumentException("TODO: We need a FFITypeAlias annotation to mark foreign type name for " + typeElement);
            }
        } else {
            if (ffiLibrary != null) {
                throw new IllegalArgumentException("TODO: We cannot have a FFITypeAlias annotation for FFILibrary " + typeElement);
            }
        }

        if (!TypeDef.isSafeJavaName(typeElement.getQualifiedName().toString())) {
            throw new IllegalArgumentException("TODO: The name of " + typeElement + " contains reserved segments, e.g., _cxx_");
        }

        if (!typeElement.getTypeParameters().isEmpty()) {
            CXXTemplate[] templates = typeElement.getAnnotationsByType(CXXTemplate.class);
            for (CXXTemplate template : ffiGen.templates()) {
                processType(processingEnv, typeElement, ffiGen, template, gen);
            }
            if (templates == null) {
                return;
            }
            for (CXXTemplate template : templates) {
                processType(processingEnv, typeElement, ffiGen, template, gen);
            }

        } else {
            processType(processingEnv, typeElement, ffiGen, null, gen);
        }
    }

    void processTypeBatch(ProcessingEnvironment processingEnv, FFIGenBatch genBatch, boolean doGen) {
        if (genBatch == null) {
            throw new IllegalStateException("Oops: must have FFIGenBatch");
        }
        for (FFIGen gen : genBatch.value()) {
            TypeElement theTypeElement = getTypeElement(processingEnv, gen.type());
            CXXTemplate[] templates = gen.templates();
            if (theTypeElement.getTypeParameters().isEmpty()) {
                if (templates.length > 0) {
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "Redundant templates for " + theTypeElement
                            + " in " + gen);
                }
                processType(processingEnv, theTypeElement, gen, doGen);
            } else {
                if (templates.length == 0) {
                    throw new IllegalStateException("A generic type requires CXXTemplate: " + gen);
                }
                for (CXXTemplate template : templates) {
                    processType(processingEnv, theTypeElement, gen, template, doGen);
                }
            }
        }
    }


    void processType(ProcessingEnvironment processingEnv, TypeElement typeElement,
                                 FFIGen ffiGen, CXXTemplate template, boolean gen) {
        TypeDef def = new TypeDef(typeElement, ffiGen, template);
        TypeDefGenerator generator = new TypeDefGenerator(this, processingEnv, typeElement, def, template);
        if (generator.isGenFFITemplate()) {
            registerFFITemplateTypeDef(def, generator, gen);
        } else if (generator.isGenFFIPointer()) {
            registerFFIPointerTypeDef(def, generator, gen);
        } else if (generator.isGenFFILibrary()) {
            registerFFILibraryTypeDef(def, generator, gen);
        } else {
            throw new IllegalArgumentException("Unknown generation type for " + typeElement);
        }
    }
}
