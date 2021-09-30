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
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFIGenBatch;

import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Set;

@SupportedAnnotationTypes({
        "com.alibaba.fastffi.FFIGen",
        "com.alibaba.fastffi.FFIGenBatch"
})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class AnnotationProcessor extends javax.annotation.processing.AbstractProcessor {

    TypeDefRegistry registry = new TypeDefRegistry(this);

    /**
     * Other annotation processor may need to process FFI* annotations.
     * Here we must return false to avoid claiming the annotation has been handled.
     * @param set
     * @param roundEnvironment
     * @return
     */
    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        if (roundEnvironment.processingOver()) {
            registry.lastRound(processingEnv);
            return false;
        }
        if (set.isEmpty()) {
            return false;
        }
        for (TypeElement e : set) {
            boolean isFFIGen = isFFIGen(e);
            boolean isFFIGenBatch = isFFIGenBatch(e);
            for (Element ee : roundEnvironment.getElementsAnnotatedWith(e)) {
                if (ee instanceof TypeElement) {
                    try {
                        if (isFFIGen) processType((TypeElement) ee, true);
                        if (isFFIGenBatch) processTypeBatch((TypeElement) ee, true);
                    } catch (IllegalStateException | IllegalArgumentException exc) {
                        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                                "" + exc.getClass().getName() + ": " + exc.getMessage());
                    }
                } else {
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING,"An unsupported element " + ee + " has been annotated with " + e);
                }
            }
        }

        try {
            registry.generate();
        } catch (FFICodeGenerationException exc) {
            String message = exc.getMessage();
            if (exc.typeDef != null) {
                message = message + "\nType: " + exc.typeDef.getTypeElementName();
                if (exc.executableElement != null) {
                    if (exc.executableType != null) {
                        message = message + "\nExecutable: " + AnnotationProcessorUtils.format(exc.executableElement, exc.executableType);
                    } else {
                        message = message + "\nExecutable: " + AnnotationProcessorUtils.format(exc.executableElement);
                    }
                }
            }
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, message);
        } catch (Throwable exc) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                    "" + exc.getClass().getName() + ": " + exc.getMessage());
        }

        return false;
    }

    void processTypeBatch(TypeElement typeElement, boolean gen) {
        FFIGenBatch genBatch = typeElement.getAnnotation(FFIGenBatch.class);
        if (genBatch == null) {
            throw new IllegalStateException("Oops: must have FFIGenBatch");
        }
        registry.processTypeBatch(processingEnv, genBatch, gen);
    }

    boolean isFFIGen(TypeElement typeElement) {
        return typeElement.getQualifiedName().toString().equals(FFIGen.class.getName());
    }

    boolean isFFIGenBatch(TypeElement typeElement) {
        return typeElement.getQualifiedName().toString().equals(FFIGenBatch.class.getName());
    }

    void processType(TypeElement typeElement, boolean gen) {
        processType(typeElement, typeElement.getAnnotation(FFIGen.class), gen);
    }

    void processType(TypeElement typeElement, FFIGen ffiGen, boolean gen) {
        registry.processType(processingEnv, typeElement, ffiGen, gen);
    }

    void processType(TypeElement typeElement, FFIGen ffiGen, CXXTemplate template, boolean gen) {
        registry.processType(processingEnv, typeElement, ffiGen, template, gen);
    }

    TypeEnv getTypeEnv() {
        if (processingEnv == null) {
            throw new IllegalStateException("Cannot getTypeEnv since we have no processingEnv now.");
        }
        return new TypeEnv(registry, processingEnv);
    }
}
