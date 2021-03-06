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
package com.alibaba.fastffi.tool;

import com.alibaba.fastffi.CXXEnum;
import com.alibaba.fastffi.CXXEnumMap;
import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.CXXOperator;
import com.alibaba.fastffi.CXXPointer;
import com.alibaba.fastffi.CXXReference;
import com.alibaba.fastffi.CXXSuperTemplate;
import com.alibaba.fastffi.CXXTemplate;
import com.alibaba.fastffi.CXXValue;
import com.alibaba.fastffi.CXXValueScope;
import com.alibaba.fastffi.FFIConst;
import com.alibaba.fastffi.FFIExpr;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFIGenBatch;
import com.alibaba.fastffi.FFIGetter;
import com.alibaba.fastffi.FFINameAlias;
import com.alibaba.fastffi.FFIPointer;
import com.alibaba.fastffi.FFISetter;
import com.alibaba.fastffi.FFITypeAlias;
import com.alibaba.fastffi.FFITypeFactory;
import com.alibaba.fastffi.FFITypeRefiner;
import com.alibaba.fastffi.clang.ASTContext;
import com.alibaba.fastffi.clang.ASTUnit;
import com.alibaba.fastffi.clang.BuiltinType;
import com.alibaba.fastffi.clang.CXXConstructorDecl;
import com.alibaba.fastffi.clang.CXXDestructorDecl;
import com.alibaba.fastffi.clang.CXXMethodDecl;
import com.alibaba.fastffi.clang.CXXRecordDecl;
import com.alibaba.fastffi.clang.ClassTemplateDecl;
import com.alibaba.fastffi.clang.ClassTemplateSpecializationDecl;
import com.alibaba.fastffi.clang.Decl;
import com.alibaba.fastffi.clang.DeclContext;
import com.alibaba.fastffi.clang.DeclTypeRefiner;
import com.alibaba.fastffi.clang.DecltypeType;
import com.alibaba.fastffi.clang.DependentNameType;
import com.alibaba.fastffi.clang.DirectoryLookup;
import com.alibaba.fastffi.clang.ElaboratedType;
import com.alibaba.fastffi.clang.EnumConstantDecl;
import com.alibaba.fastffi.clang.EnumDecl;
import com.alibaba.fastffi.clang.EnumType;
import com.alibaba.fastffi.clang.FieldDecl;
import com.alibaba.fastffi.clang.FunctionDecl;
import com.alibaba.fastffi.clang.HeaderSearch;
import com.alibaba.fastffi.clang.IdentifierInfo;
import com.alibaba.fastffi.clang.InjectedClassNameType;
import com.alibaba.fastffi.clang.LValueReferenceType;
import com.alibaba.fastffi.clang.NamedDecl;
import com.alibaba.fastffi.clang.NamespaceDecl;
import com.alibaba.fastffi.clang.NestedNameSpecifier;
import com.alibaba.fastffi.clang.NonTypeTemplateParmDecl;
import com.alibaba.fastffi.clang.OverloadedOperatorKind;
import com.alibaba.fastffi.clang.ParmVarDecl;
import com.alibaba.fastffi.clang.PointerType;
import com.alibaba.fastffi.clang.QualType;
import com.alibaba.fastffi.clang.RValueReferenceType;
import com.alibaba.fastffi.clang.RecordDecl;
import com.alibaba.fastffi.clang.RecordType;
import com.alibaba.fastffi.clang.RefQualifierKind;
import com.alibaba.fastffi.clang.ReferenceType;
import com.alibaba.fastffi.clang.Sema;
import com.alibaba.fastffi.clang.SourceLocation;
import com.alibaba.fastffi.clang.SourceManager;
import com.alibaba.fastffi.clang.SubstTemplateTypeParmType;
import com.alibaba.fastffi.clang.TagDecl;
import com.alibaba.fastffi.clang.TemplateArgument;
import com.alibaba.fastffi.clang.TemplateArgumentList;
import com.alibaba.fastffi.clang.TemplateDecl;
import com.alibaba.fastffi.clang.TemplateName;
import com.alibaba.fastffi.clang.TemplateParameterList;
import com.alibaba.fastffi.clang.TemplateSpecializationKind;
import com.alibaba.fastffi.clang.TemplateSpecializationType;
import com.alibaba.fastffi.clang.TemplateTemplateParmDecl;
import com.alibaba.fastffi.clang.TemplateTypeParmDecl;
import com.alibaba.fastffi.clang.TemplateTypeParmType;
import com.alibaba.fastffi.clang.TranslationUnitDecl;
import com.alibaba.fastffi.clang.Type;
import com.alibaba.fastffi.clang.TypeAliasDecl;
import com.alibaba.fastffi.clang.TypeAliasTemplateDecl;
import com.alibaba.fastffi.clang.TypeClass;
import com.alibaba.fastffi.clang.TypedefDecl;
import com.alibaba.fastffi.clang.TypedefNameDecl;
import com.alibaba.fastffi.clang.TypedefType;
import com.alibaba.fastffi.clang.tooling.ClangTool;
import com.alibaba.fastffi.clang.tooling.CommonOptionsParser;
import com.alibaba.fastffi.llvm.StringRef;
import com.alibaba.fastffi.llvm.cl.OptionCategory;
import com.alibaba.fastffi.stdcxx.StdString;
import com.alibaba.fastffi.stdcxx.StdVector;
import com.alibaba.fastffi.stdcxx.StdVectorLite;
import com.alibaba.fastffi.stdcxx.UniquePtr;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;

import javax.lang.model.element.Modifier;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FFIBindingGenerator {

    static boolean debug = true;
    static boolean enumAsInteger = false;

    static class HeaderDirectoryEntry {
        final String directory;
        final boolean isSystemDir;

        public HeaderDirectoryEntry(String directory, boolean isSystemDir) {
            this.directory = directory;
            this.isSystemDir = isSystemDir;
        }

        public String getIncludePath(String file) {
            if (file.isEmpty()) {
                throw new IllegalStateException("Should not be empty");
            }
            return file.substring(directory.length() + File.separator.length());
        }
    }

    static class IncludeEntry {
        final String include;
        final boolean isSystemDir;

        public IncludeEntry(String include, boolean isSystemDir) {
            this.include = include;
            this.isSystemDir = isSystemDir;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            IncludeEntry that = (IncludeEntry) o;
            return isSystemDir == that.isSystemDir && Objects.equals(include, that.include);
        }

        @Override
        public int hashCode() {
            return Objects.hash(include, isSystemDir);
        }
    }

    static class HeaderManager {
        List<HeaderDirectoryEntry> searchingDirectories;

        public HeaderManager(List<HeaderDirectoryEntry> searchingDirectories) {
            this.searchingDirectories = searchingDirectories;
        }

        public HeaderDirectoryEntry getIncludeDirectory(String path) {
            for (HeaderDirectoryEntry entry : searchingDirectories) {
                if (path.startsWith(entry.directory)) {
                    return entry;
                }
            }
            return null;
        }
    }

    static class COptions implements AutoCloseable {
        StdVector<Long> options;
        List<StdString> stdStrings;
        public COptions(String[] args) {
            stdStrings = Arrays.asList(args).stream().map(s -> StdString.create(s)).collect(Collectors.toList());
            StdVector.Factory<Long> factory = FFITypeFactory.getFactory(StdVector.class, "std::vector<int64_t>");
            options = factory.create();
            stdStrings.add(0, StdString.create(FFIBindingGenerator.class.getSimpleName()));
            stdStrings.forEach(s -> {
                options.push_back(s.c_str());
            });
        }

        int getArgc() {
            return Math.toIntExact(options.size());
        }

        long getArgv() {
            return options.data();
        }

        @Override
        public void close() {
            options.delete();
            for (StdString stdString : stdStrings) {
                stdString.delete();
            }
            stdStrings.clear();
        }
    }

    static class MemoryHelper implements AutoCloseable {

        List<CXXPointer> objects = new ArrayList<>();

        public void add(CXXPointer obj) {
            objects.add(obj);
        }

        @Override
        public void close() {
            ListIterator<CXXPointer> li = objects.listIterator(objects.size());
            while (li.hasPrevious()) {
                li.previous().delete();
            }
        }
    }

    static String nextOperand(String[] input, int index, String option) {
        if (index < input.length) {
            return input[index];
        }
        throw new IndexOutOfBoundsException("Cannot get operand for option " + option);
    }

    static Path outputDirectory;
    static String rootPackage;
    static String ffiLibraryName;

    static List<Path> inputFiles;
    static List<Pattern> methodExcludes;
    static List<Pattern> classExcludes;

    // forward declaration is common in C++
    static Map<String, List<String>> fwdHeaders = new HashMap<>();

    static String fileComment = "Code generated by alibaba/fastFFI. DO NOT EDIT.\n";

    static String helpString = String.join("\n", new String[]{
            "FFIBindingGenerator:",
            "",
            "   Automatically Generating Efficient Java Bindings from C++ Headers.",
            "",
            "   --help                     Print the help message",
            "",
            "   --debug                    Enable debugging logs",
            "   --log-level                Set the log level, can be verbose/debug/info/warn/error",
            "",
            "   --emum-as-integer          Generate enums as integral values",
            "",
            "   --search-directory         (Repeatable) Search directory to find header dependencies.",
            "   --search-pattern           (Repeatable) Regex pattern for search directory to find header dependencies.",
            "",
            "   --exclude-file             A file that lists the excluded interfaces during code generation",
            "   --exclude                  (Repeatable) Regex pattern for excluded interfaces",
            "",
            "   --forward-headers-file     A file that lists the extra headers to include to handle forward declarations in C++",
            "   --forward-header           (Repeatable) Extra headers to include to handle forward declarations in C++",
            "",
            "   --output-directory         Output directory for generate Java bindings",
            "   --root-package             Package name for generated Java bindings, e.g., com.alibaba.fastffi",
            "   --ffilibrary-name          The FFI library name of generated JNI bindings",
            "",
            "   --comment-file             A file for comments that need to be inserted at the begining of generate sources",
            "",
    });

    static String[] processCommandLines(String[] input) {
        int i = 0;
        List<String> searchDirectory = new ArrayList<>();
        String searchPattern = ".*[.]h(pp)?";
        List<String> additional = new ArrayList<>();
        while (i < input.length) {
            String cur = input[i++];
            switch (cur) {
                case "--help": {
                    System.err.println(helpString);
                    System.exit(-1);
                }
                case "--":
                    for (int j = i; j < input.length; j++) {
                        Collections.addAll(additional, input[j].split(","));
                    }
                    i = input.length;  // move forward the cursor
                    break;
                case "--debug": {
                    debug = true;
                    continue;
                }
                case "--log-level": {
                    String logLevel = nextOperand(input, i++, cur);
                    Logger.setLevel(logLevel);
                    continue;
                }
                case "--enum-as-integer": {
                    enumAsInteger = true;
                    continue;
                }
                case "--search-directory": {
                    String directory = nextOperand(input, i++, cur);
                    searchDirectory.add(directory);
                    additional.add("--extra-arg-before=-I" + directory);
                    continue;
                }
                case "--search-pattern": {
                    searchPattern = nextOperand(input, i++, cur);
                    continue;
                }
                case "--excludes-file": {
                    Path excludesFile = Paths.get(nextOperand(input, i++, cur));
                    addExcludes(excludesFile);
                    continue;
                }
                case "--exclude": {
                    addExclude(nextOperand(input, i++, cur));
                    continue;
                }
                case "--forward-headers-file": {
                    Path forwardHeadersFile = Paths.get(nextOperand(input, i++, cur));
                    addForwardHeaders(forwardHeadersFile);
                    continue;
                }
                case "--forward-header": {
                    addForwardHeader(nextOperand(input, i++, cur));
                    continue;
                }
                case "--comment-file": {
                    Path commentFile = Paths.get(nextOperand(input, i++, cur));
                    loadCommentFile(commentFile);
                    continue;
                }
                case "--output-directory": {
                    outputDirectory = Paths.get(nextOperand(input, i++, cur));
                    continue;
                }
                case "--root-package": {
                    rootPackage = nextOperand(input, i++, cur);
                    continue;
                }
                case "--ffilibrary-name": {
                    ffiLibraryName = nextOperand(input, i++, cur);
                    continue;
                }
            }
        }
        if (ffiLibraryName == null || ffiLibraryName.isEmpty()) {
            throw new IllegalArgumentException("The argument '--ffilibrary-name' is not specified");
        }
        inputFiles = collectInputFiles(searchDirectory, searchPattern);
        if (outputDirectory == null) {
            outputDirectory = Paths.get("output");
        }
        inputFiles.forEach(p -> {
            additional.add(p.toAbsolutePath().toString());
        });
        return additional.toArray(new String[0]);
    }

    private static List<Path> collectInputFiles(List<String> searchDirectory, String searchPattern) {
        Pattern pattern = Pattern.compile(searchPattern);
        List<Path> results = new ArrayList<>();
        searchDirectory.forEach(s -> {
            Path path = Paths.get(s);
            if (!Files.isDirectory(path)) {
                throw new IllegalArgumentException("Path " + path + " is not a directory");
            }

            try {
                Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                        if (pattern.matcher(file.getFileName().toString()).matches()) {
                            results.add(file);
                        }
                        return FileVisitResult.CONTINUE;
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return results;
    }

    static void addExcludes(Path excludesFile) {
        try (BufferedReader reader = Files.newBufferedReader(excludesFile)) {
            String line;
            while ((line = reader.readLine()) != null && !line.isEmpty()) {
                addExclude(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException("Cannot read excludes file: " + excludesFile);
        }
    }

    static void addExclude(String exclude) {
        if (exclude.isEmpty() || exclude.charAt(0) == '#') {
            return;
        }
        if (exclude.contains("::")) {
            addMethodExclude(exclude);
        } else {
            addClassExclude(exclude);
        }
        if (rootPackage != null && !rootPackage.isEmpty()) {
            String key = rootPackage + "." + exclude;
            if (key.contains("::")) {
                addMethodExclude(key);
            } else {
                addClassExclude(key);
            }
        }
    }

    static void addMethodExclude(String exclude) {
        if (methodExcludes == null) {
            methodExcludes = new ArrayList<>();
        }
        methodExcludes.add(Pattern.compile(exclude));
    }

    static void addClassExclude(String exclude) {
        if (classExcludes == null) {
            classExcludes = new ArrayList<>();
        }
        classExcludes.add(Pattern.compile(exclude));
    }

    static boolean exclude(String className, String methodName) {
        return exclude(methodExcludes, className + "::" + methodName);
    }

    static boolean exclude(String className) {
        return exclude(classExcludes, className);
    }

    static boolean exclude(List<Pattern> excludes, String test) {
        if (excludes == null) {
            return false;
        }
        for (Pattern p : excludes) {
            if (p.matcher(test).matches()) {
                return true;
            }
        }
        return false;
    }

    static void addForwardHeaders(Path fwdHeadersFile) {
        try (BufferedReader reader = Files.newBufferedReader(fwdHeadersFile)) {
            String line;
            while ((line = reader.readLine()) != null && !line.isEmpty()) {
                addForwardHeader(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException("Cannot read excludes file: " + fwdHeadersFile);
        }
    }

    static void addForwardHeader(String line) {
        if (line.isEmpty() || line.charAt(0) == '#') {
            return;
        }
        String[] args = line.split("=");
        if (args.length != 2) {
            throw new IllegalArgumentException("Expects a <class>=<header> pair for forward headers");
        }
        List<String> headers = Arrays.asList(args[1].split(","));
        if (fwdHeaders.containsKey(args[0])) {
            fwdHeaders.get(args[0]).addAll(headers);
        } else {
            fwdHeaders.put(args[0], headers);
        }
        if (rootPackage != null && !rootPackage.isEmpty()) {
            String key = rootPackage + "." + args[0];
            if (fwdHeaders.containsKey(key)) {
                fwdHeaders.get(key).addAll(headers);
            } else {
                fwdHeaders.put(key, headers);
            }
        }
    }

    static void loadCommentFile(Path commentFile) {
        try (BufferedReader reader = Files.newBufferedReader(commentFile)) {
            fileComment = reader.lines().collect(Collectors.joining(System.lineSeparator()));;
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException("Cannot read comment file: " + commentFile);
        }
    }

    public FFIBindingGenerator() {
        Logger.initialize(Logger.Level.INFO);

        this.classBuilderMap = new HashMap<>();
    }

    /**
     * Use -Dkey=value to pass Java options
     * Use application parameters
     * @param args
     */
    public void startWithArgs(String []args) throws IOException {
        try (COptions options = new COptions(processCommandLines(args));
                MemoryHelper memoryHelper = new MemoryHelper();
                CXXValueScope scope = new CXXValueScope()) {
            StdString categoryName = StdString.create("FFIBindingGenerator");
            StdString categoryDesc = StdString.create("A tool that generates bindings for fastFFI");
            memoryHelper.add(categoryName);
            memoryHelper.add(categoryDesc);
            StringRef.Factory stringRefFactory = FFITypeFactory.getFactory(StringRef.class);
            StringRef categoryNameRef = stringRefFactory.create(categoryName);
            StringRef categoryDescRef = stringRefFactory.create(categoryDesc);
            memoryHelper.add(categoryNameRef);
            memoryHelper.add(categoryDescRef);

            OptionCategory.Factory optionCategoryFactory = FFITypeFactory.getFactory(OptionCategory.class);
            OptionCategory optionCategory = optionCategoryFactory.create(categoryNameRef, categoryDescRef);
            memoryHelper.add(optionCategory);

            CommonOptionsParser.Factory optionParserFactory = FFITypeFactory.getFactory(CommonOptionsParser.class);
            CommonOptionsParser optionsParser = optionParserFactory.create(options.getArgc(), options.getArgv(), optionCategory);
            memoryHelper.add(optionsParser);

            ClangTool clangTool = optionsParser.createClangTool();
            memoryHelper.add(clangTool);

            StdVectorLite.Factory<UniquePtr<ASTUnit>> vectorFactory = FFITypeFactory.getFactory(StdVectorLite.class, "std::vector<std::unique_ptr<clang::ASTUnit>>");
            this.astUnits = vectorFactory.create();

            clangTool.buildASTs(this.astUnits);
            memoryHelper.add(this.astUnits);
            this.generate();
        }
    }

    public static void main(String[] args) throws IOException {
        FFIBindingGenerator generator = new FFIBindingGenerator();
        generator.startWithArgs(args);
    }

    StdVectorLite<UniquePtr<ASTUnit>> astUnits;
    Sema currentSema = null;
    Map<String, TypeGen> classBuilderMap;

    Map<FFIType, FFIType> ffiTypeDictionary = new HashMap<>();

    TemplateFactory templateFactory = new TemplateFactory();

    HeaderManager headerManager = null;

    static void dump(StdVectorLite<UniquePtr<ASTUnit>> results) {
        System.out.println("#ASTUnit: " + results.size());
        for (UniquePtr<ASTUnit> p : results) {
            ASTUnit ast = p.get();
            if (ast.getDiagnostics().hasUncompilableErrorOccurred()) {
                System.out.println("#ASTUnit: " + ast.getOriginalSourceFileName().toJavaString() + " has uncompilable errors.");
                return;
            }
            TranslationUnitDecl translationUnitDecl = ast.getASTContext().getTranslationUnitDecl();
            dump("|-", translationUnitDecl);
        }
    }

    /**
     * We need to generate a proper binding for pointee
     * @param ffiType
     * @return
     */
    boolean processPointerOrReference(FFIType ffiType) {
        FFIType pointeeType = typeToFFIType(ffiType.getPointeeType());
        if (pointeeType.isReference() || pointeeType.isVoid()) {
            throw new IllegalStateException("Oops, pointer to references or void is illegal.");
        }
        if (pointeeType.isPointer() || pointeeType.isPrimitive()) {
            return generatePointerOfPrimitiveOrPointer(ffiType, pointeeType);
        } else {
            if (!pointeeType.isValue()) {
                throw new IllegalStateException(
                        String.format("Sanity check failed: pointee is not a value type: %s vs. %s", ffiType, pointeeType));
            }
            if (!ffiType.javaType.equals(pointeeType.javaType)) {
                throw new IllegalStateException(
                        String.format("Sanity check failed: types not match: %s vs. %s", ffiType, pointeeType));
            }
            return generatePointerOfValue(ffiType, pointeeType);
        }
    }

    private boolean generatePointerOfValue(FFIType ffiType, FFIType pointeeType) {
        if (!ffiType.javaType.equals(pointeeType.javaType)) {
            throw new IllegalStateException("Oops");
        }
        return generateValue(pointeeType);
    }

    /**
     * get the decl for the given type; used to locate header
     * @param type
     * @return
     */
    static Decl getTypeDecl(Type type) {
        TypeClass typeClass = type.getTypeClass();
        switch (typeClass) {
            case Builtin:
                return null;
            case Enum: {
                return DeclTypeRefiner.refine(((EnumType) type).getDecl());
            }
            case Record: {
                return DeclTypeRefiner.refine(((RecordType) type).getDecl());
            }
            case TemplateSpecialization:
                return TemplateSpecializationType.dyn_cast(type).getTemplateName().getAsTemplateDecl();
            case Pointer:
                return getTypeDecl(PointerType.dyn_cast(type).getPointeeType().getTypePtr());
            case LValueReference:
                return getTypeDecl(LValueReferenceType.dyn_cast(type).getPointeeType().getTypePtr());
            case RValueReference:
                return getTypeDecl(RValueReferenceType.dyn_cast(type).getPointeeType().getTypePtr());
            case Typedef:
                return TypedefType.dyn_cast(type).getDecl();
            case Elaborated:
                return getTypeDecl(ElaboratedType.dyn_cast(type).desugar().getTypePtr());
            default:
                return null;
        }
    }

    /**
     * return true if we can create a Java binding for the given CXX type
     * @param type
     * @return
     */
    static boolean isJavaDeclarable(Type type) {
        TypeClass typeClass = type.getTypeClass();
        switch (typeClass) {
            case Builtin:
                return getJavaTypeName(BuiltinType.dyn_cast(type)) != null;
            case Enum: {
                EnumType enumType = EnumType.dyn_cast(type);
                return enumType.getDecl().getIdentifier() != null;
            }
            case Record: {
                RecordType recordType = RecordType.dyn_cast(type);
                return recordType.getDecl().getIdentifier() != null;
            }
            case TemplateSpecialization: {
                TemplateSpecializationType templateSpecializationType = TemplateSpecializationType.dyn_cast(type);
                int size = templateSpecializationType.getNumArgs();
                for (int i = 0; i < size; i++) {
                    TemplateArgument arg = templateSpecializationType.getArg(i);
                    if (arg.getKind() != TemplateArgument.ArgKind.Type) {
                        // TODO:
                        return false;
                    }
                    Type argType = arg.getAsType().getTypePtr();
                    if (!isJavaDeclarable(argType)) {
                        return false;
                    }
                }
                return true;
            }
            case Pointer:
                return isJavaDeclarable(PointerType.dyn_cast(type).getPointeeType().getTypePtr());
            case LValueReference:
                return isJavaDeclarable(LValueReferenceType.dyn_cast(type).getPointeeType().getTypePtr());
            case RValueReference:
                return isJavaDeclarable(RValueReferenceType.dyn_cast(type).getPointeeType().getTypePtr());
            case Typedef:
                return isJavaDeclarable(TypedefType.dyn_cast(type).getDecl().getUnderlyingType().getTypePtr());
            case Elaborated:
                return isJavaDeclarable(ElaboratedType.dyn_cast(type).desugar().getTypePtr());
            case DependentName:
            case SubstTemplateTypeParm:
            case TemplateTypeParm:
                return true;
        }
        return false;
    }

    private TypeGen getTypeGen(FFIType ffiType) {
        TypeName typeName = ffiType.javaType;
        if (typeName instanceof ClassName) {
            return getTypeGen((ClassName) typeName);
        }
        if (typeName instanceof ParameterizedTypeName) {
            return getTypeGen(((ParameterizedTypeName) typeName).rawType);
        }
        return null;
    }

    private boolean generateValue(FFIType ffiType) {
        TypeClass typeClass = ffiType.cxxType.getTypeClass();
        switch (typeClass) {
            case Enum: {
                EnumType enumType = EnumType.dyn_cast(ffiType.cxxType);
                EnumDecl enumDecl = enumType.getDecl();
                TypeGen typeGen = getOrCreateTypeGen((ClassName) ffiType.javaType, enumDecl);
                if (!typeGen.isNone()) {
                    return false;
                }
                generate(enumDecl);
                return !typeGen.isNone();
            }
            case Record: {
                ClassName javaName = (ClassName) ffiType.javaType;
                // tricky part: cxxName used in FFITypeAlias is the pointee type.
                String cxxName = getCXXName(ffiType.cxxQualType);
                TypeGen typeGen = getOrCreateTypeGenForFFIPointer(javaName, cxxName, ffiType);
                if (!typeGen.isNone()) {
                    return false;
                }
                RecordType recordType = RecordType.dyn_cast(ffiType.cxxType);
                Decl recordDecl = DeclTypeRefiner.refine(recordType.getDecl());
                generate(recordDecl);
                return !typeGen.isNone();
            }
            case Typedef: {
                return generateTypedef(ffiType);
            }
            case Elaborated: {
                ElaboratedType elaboratedType = ElaboratedType.dyn_cast(ffiType.cxxType);
                QualType desugaredType = elaboratedType.desugar();
                Logger.warn("the ElaboratedType '%s' should occurs there, desugared as: %s, %s",
                        elaboratedType.getAsString(), desugaredType.getAsString(), desugaredType.getTypePtr().getTypeClass());
                return process(typeToFFIType(elaboratedType.desugar()));
            }
            case TemplateSpecialization: {
                TemplateSpecializationType templateSpecializationType = TemplateSpecializationType.dyn_cast(ffiType.cxxType);
                TemplateDecl templateDecl = templateSpecializationType.getTemplateName().getAsTemplateDecl();
                // add an instantiation for the template.
                checkTemplateInstantiation(ffiType);
                return generate(DeclTypeRefiner.refine(templateDecl));
            }
            case InjectedClassName: {
                // no need to generate for injected class name (as the pointee type of a pointer type), as it has already been generated.
                return false;
            }
            default:
                throw unsupportedAST("Unsupported pointee type: " + ffiType);
        }
    }

    private AnnotationSpec ffiExpr(String expr) {
        return AnnotationSpec.builder(FFIExpr.class).addMember("value", "$S", expr).build();
    }

    private boolean generateTypedef(TypedefNameDecl typedefNameDecl, FFIType type) {
        ClassName aliasJavaName = getJavaClassName(typedefNameDecl);
        String aliasCXXName = getCXXName(typedefNameDecl);
        TypeGen typeGen = getOrCreateTypeGenForFFIPointer(aliasJavaName, aliasCXXName, type);
        if (!typeGen.isNone()) {
            return false;
        }

        QualType underlyingQualType = typedefNameDecl.getUnderlyingType();
        List<TypeVariableName> typeVariableNames = getJavaTypeVariablesInContext(typedefNameDecl.getDeclContext());
        getJavaTypeVariablesOnDecl(typedefNameDecl, typeVariableNames);

        try {
            // If the underlying type is not supported, we just skip generating the `get()` method.
            FFIType underlyingFFIType = typeToFFIType(underlyingQualType);
            if (underlyingFFIType.isVoid()) {
                throw unsupportedAST("TODO: alias of void is not supported.");
            }

            // part 1: genenrate getter/setter
            {
                MethodSpec.Builder getter = MethodSpec.methodBuilder("get")
                        .returns(underlyingFFIType.javaType)
                        .addModifiers(Modifier.ABSTRACT, Modifier.PUBLIC);
                MethodSpec.Builder setter = MethodSpec.methodBuilder("set")
                        .returns(TypeName.VOID)
                        .addParameter(underlyingFFIType.javaType, "__value")
                        .addModifiers(Modifier.ABSTRACT, Modifier.PUBLIC);
                if (underlyingFFIType.isPointer() || underlyingFFIType.isReference()) {
                    // cast the dereference of `this' to the aliased type pointer
                    getter.addAnnotation(ffiExpr("(*{0})"));
                    setter.addAnnotation(ffiExpr("*{0} = (" + aliasCXXName + "){1}"));
                } else if (underlyingFFIType.isPrimitive() || underlyingFFIType.isEnum()) {
                    // return the primitive dereference value
                    getter.addAnnotation(ffiExpr("(*{0})"));
                    setter.addAnnotation(ffiExpr("*{0} = (" + aliasCXXName + "){1}"));
                } else {
                    if (underlyingFFIType.javaType.isPrimitive()) {
                        throw new IllegalStateException("Must be a FFIPointer");
                    }
                    // cast `this' to the aliased type pointer
                    getter.addAnnotation(ffiExpr("{0}"));

                    // non- primitive types: `set` is not supported.
                    setter = null;
                }
                typeGen.builder.addMethod(getter.build());
                typeGen.addMethod("get");
                if (setter != null) {
                    typeGen.builder.addMethod(setter.build());
                    typeGen.addMethod("set");
                }
            }

            // part 2: generate creating from alias pointers
            if (!(underlyingFFIType.isPointer() || underlyingFFIType.isReference())) {
                TypeName returnType = aliasJavaName;
                if (!typeVariableNames.isEmpty()) {
                    returnType = ParameterizedTypeName.get(aliasJavaName, typeVariableNames.toArray(new TypeName[0]));
                }
                if (underlyingFFIType.isPrimitive()) {
                    // generate a factory interface
                    TypeSpec.Builder factoryClassBuilder = typeGen.getFactoryBuilder();
                    factoryClassBuilder.addMethod(MethodSpec.methodBuilder("create")
                            .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                            .returns(returnType)
                            .build());
                    factoryClassBuilder.addMethod(MethodSpec.methodBuilder("create")
                            .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                            .returns(returnType)
                            .addParameter(ParameterSpec.builder(underlyingFFIType.javaType, "__value").build())
                            .build());
                } else {
                    // TODO: generate a cast method from alias type, for verbosity and convenience
                }
            }

            // part 3: instantiate the template typedef.
            {
                Type underlyingType = underlyingQualType.getTypePtr();
                CXXRecordDecl cxxRecordDecl = underlyingType.getAsCXXRecordDecl();
                if (cxxRecordDecl != null && !cxxRecordDecl.isNull()) {
                    tryInstantiateTemplateSpecialization(cxxRecordDecl);
                    // build ctors
                    generateCtorMethods(cxxRecordDecl, typeGen, typeVariableNames);
                    // build fields
                    generateFields(cxxRecordDecl, typeGen);
                    // build methods
                    List<CXXMethodDecl> staticMethods = new ArrayList<>();
                    generateMethods(cxxRecordDecl, typeGen, staticMethods);
                    // build static methods
                    generateStaticMethods(cxxRecordDecl, typeGen, staticMethods, typeVariableNames);
                }
            }
        } catch (UnsupportedASTException e) {
            // no `get()` is ok for interfaces
        }

        // the typedef type is a CXXPointer
        typeGen.builder.addSuperinterface(CXXPointer.class);

        // associate type variables
        associateTypeParameters(typeGen, typeVariableNames);

        // mark succ
        addCXXHeader(typeGen);
        typeGen.succ();
        return true;
    }

    private boolean generateTypedef(TypedefNameDecl typedefNameDecl) {
        return generateTypedef(typedefNameDecl, typedefToFFIType(typedefNameDecl));
    }

    private boolean generateTypedef(FFIType type) {
        TypedefNameDecl typedefNameDecl = TypedefType.dyn_cast(type.cxxType).getDecl();
        return generateTypedef(typedefNameDecl, type);
    }

    private ClassName getOrCreatePointerOfPrimitive(FFIType ffiType) {
        return getOrCreatePointerOfPrimitive(getCXXName(ffiType), ffiType.javaType, ffiType);
    }

    private ClassName getOrCreatePointerOfPointer(FFIType ffiType) throws IOException {
        return getOrCreatePointerOfPointer(getCXXName(ffiType), ffiType.javaType, ffiType);
    }

    /**
     * Given the pointee type information
     * @param cxxName
     * @param javaType
     * @param ffiType
     * @return
     * @throws IOException
     */
    private ClassName getOrCreatePointerOfPointer(String cxxName, TypeName javaType, FFIType ffiType) throws IOException {
        ClassName pointerName = (ClassName) getPointerOfPointer(javaType);
        TypeGen typeGen = getTypeGen(pointerName);
        if (typeGen != null) {
            return pointerName;
        }

        typeGen = generatePointerOfPrimitiveOrPointer(cxxName, javaType, pointerName, ffiType);
        addTypeGen(pointerName, typeGen);
        return pointerName;
    }

    private TypeGen generatePointerOfPrimitiveOrPointer(String cxxName, TypeName javaType, ClassName pointerName, FFIType ffiType) {
        TypeSpec.Builder classBuilder = TypeSpec.interfaceBuilder(pointerName).addModifiers(Modifier.PUBLIC, Modifier.STATIC);
        classBuilder.addAnnotation(FFIGen.class);
        classBuilder.addAnnotation(getFFITypeAlias(cxxName));
        TypeGen typeGen = new TypeGen(pointerName, classBuilder, getTypeDecl(ffiType.cxxType), fileComment);
        generatePointerOfPrimitiveOrPointer(cxxName, javaType, pointerName, typeGen);
        return typeGen;
    }

    private TypeGen generatePointerOfPrimitiveOrPointer(String cxxName, TypeName javaType, ClassName pointerName) {
        TypeSpec.Builder classBuilder = TypeSpec.interfaceBuilder(pointerName).addModifiers(Modifier.PUBLIC, Modifier.STATIC);
        classBuilder.addAnnotation(FFIGen.class);
        classBuilder.addAnnotation(getFFITypeAlias(cxxName));
        TypeGen typeGen = new TypeGen(pointerName, classBuilder, null, fileComment);
        generatePointerOfPrimitiveOrPointer(cxxName, javaType, pointerName, typeGen);
        return typeGen;
    }

    private ClassName getOrCreatePointerOfPrimitive(String cxxName, TypeName javaType, FFIType ffiType) {
        ClassName pointerName = getPointerOfPrimitive(cxxName, javaType);
        TypeGen typeGen = getTypeGen(pointerName);
        if (typeGen != null) {
            return pointerName;
        }
        typeGen = generatePointerOfPrimitiveOrPointer(cxxName, javaType, pointerName, ffiType);
        addTypeGen(pointerName, typeGen);
        return pointerName;
    }

    private ClassName getOrCreatePointerOfPrimitive(String cxxName, TypeName javaType)  {
        ClassName pointerName = getPointerOfPrimitive(cxxName, javaType);
        TypeGen typeGen = getTypeGen(pointerName);
        if (typeGen != null) {
            return pointerName;
        }
        typeGen = generatePointerOfPrimitiveOrPointer(cxxName, javaType, pointerName);
        addTypeGen(pointerName, typeGen);
        return pointerName;
    }

    private boolean generatePointerOfPrimitiveOrPointer(FFIType ffiType, FFIType pointeeFFIType) {
        ClassName javaName = (ClassName) ffiType.javaType;
        // tricky part: cxxName used in FFITypeAlias is the pointee type.
        String cxxName = getCXXName(pointeeFFIType);
        TypeGen typeGen = getOrCreateTypeGenForFFIPointer(javaName, cxxName, pointeeFFIType);
        if (typeGen.isFail()) {
            throw new IllegalStateException("Oops: we can always generate pointer of primitives");
        }
        if (typeGen.isSucc()) {
            return false;
        }
        generatePointerOfPrimitiveOrPointer(cxxName, pointeeFFIType.javaType, ffiType.javaType, typeGen);
        return true;
    }

    void generatePointerOfPrimitiveOrPointer(String pointeeCXXName, TypeName pointeeJavaName,
                                             TypeName pointerJavaName, TypeGen typeGen) {
        TypeSpec.Builder classBuilder = typeGen.builder;
        classBuilder.addSuperinterface(CXXPointer.class);

        // generate getter and setter
        classBuilder.addMethod(MethodSpec.methodBuilder("get")
                .returns(pointeeJavaName).addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .addAnnotation(AnnotationSpec.builder(FFIExpr.class)
                        .addMember("value", "$S", "*{0}").build())
                .build());
        classBuilder.addMethod(MethodSpec.methodBuilder("set")
                .returns(TypeName.VOID).addParameter(pointeeJavaName, "value")
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .addAnnotation(AnnotationSpec.builder(FFIExpr.class)
                        .addMember("value", "$S", "*{0} = (" + pointeeCXXName + "){1}").build())
                .build());

        // factory
        TypeSpec.Builder factoryBuilder = typeGen.getFactoryBuilder();
        factoryBuilder.addMethod(MethodSpec.methodBuilder("create")
                .returns(pointerJavaName).addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT).build());

        classBuilder.addAnnotation(AnnotationSpec.builder(CXXHead.class)
                .addMember("system", "$S", "cstdint")
                .build());

        addCXXHeader(typeGen);
        typeGen.succ();
    }

    /**
     * Caller should set the correct cxxName
     * @param className
     * @param cxxName
     * @param ffiType
     * @return
     */
    TypeGen getOrCreateTypeGenForFFIPointer(ClassName className, String cxxName, FFIType ffiType) {
        TypeGen typeGen = getTypeGen(className);
        if (typeGen == null) {
            TypeSpec.Builder builder = TypeSpec.interfaceBuilder(className).addModifiers(Modifier.PUBLIC, Modifier.STATIC);
            {
                builder.addAnnotation(getFFITypeAlias(cxxName));
                builder.addAnnotation(FFIGen.class);
            }
            typeGen = new TypeGen(className, builder, getTypeDecl(ffiType.cxxType), fileComment);
            addTypeGen(className, typeGen);
        }
        return typeGen;
    }

    boolean process(FFIType ffiType) {
        Logger.debug("Processing %s", ffiType);
        try {
            if (ffiType.isPointer() || ffiType.isReference()) {
                return processPointerOrReference(ffiType);
            }
            if (ffiType.isValue()) {
                checkTemplateInstantiation(ffiType);
                return generateValue(ffiType);
            }
            return false;
        } catch (UnsupportedASTException e) {
            return false;
        }
    }

    private void checkTemplateInstantiation(FFIType ffiType) {
        if (!ffiType.isParameterizedType()) {
            return;
        }
        if (ffiType.cxxType.getTypeClass().equals(TypeClass.TemplateSpecialization)) {
            TemplateSpecializationType templateSpecializationType =
                    TemplateSpecializationType.dyn_cast(ffiType.cxxType);
            try {
                forEachTemplateArgument(templateSpecializationType, argType -> {
                    // do sanity check first
                    getCXXName(argType);
                    FFIType argFFIType = typeToFFIType(argType);
                    if (argFFIType.hasTypeVariable()) {
                        // no template variable in template is allowed
                        throw unsupportedAST("TODO: template variable in instantiated templates are not supported");
                    }
                });
                Logger.debug("Add template: %s", ffiType.javaType);
                ParameterizedTypeName parameterizedTypeName = (ParameterizedTypeName) ffiType.javaType;
                templateFactory.add(parameterizedTypeName.rawType, ffiType);
            } catch (ExcludedException e) {
            } catch (UnsupportedASTException e) {
                Logger.error("Failed to check the template instantiation: %s, %s", ffiType, e.getMessage());
                if (Logger.verbose()) {
                    e.printStackTrace();
                }
            }
        } // else may be sugared type
    }

    void iterate() throws IOException {
        // iterate worklist until fixed point
        while (true) {
            boolean changed = false;
            List<FFIType> worklist = new ArrayList<>(ffiTypeDictionary.values());
            for (FFIType ffiType : worklist) {
                if (ffiType.isVoid() || ffiType.isPrimitive() || ffiType.isTemplateVariableDependent()) {
                    continue;
                }
                changed |= process(ffiType);
            }
            if (!changed) {
                break;
            }
        }
        // should not register new FFIType in the loop
        for (FFIType ffiType : ffiTypeDictionary.values()) {
            if (ffiType.isPrimitive() || ffiType.isEnum()) {
                if (ffiType.requirePointer()) {
                    getOrCreatePointerOfPrimitive(ffiType);
                }
            } else if (ffiType.isPointer() || ffiType.isReference()) {
                if (ffiType.requirePointer()) {
                    getOrCreatePointerOfPointer(ffiType);
                }
            } else {
                if (ffiType.requirePointer()) {
                    throw new IllegalStateException("Unknown pointer type: " + ffiType);
                }
            }
        }
    }

    void generate() throws IOException {
        for (UniquePtr<ASTUnit> p : astUnits) {
            generate(p.get());
        }
        iterate();
        complete();
    }

    private void complete() throws IOException {
        Set<TypeGen> added = new HashSet<>();
        List<TypeGen> sorted = new ArrayList<>();
        for (TypeGen typeGen : this.classBuilderMap.values()) {
            checkDeclContext(typeGen);
        }
        for (TypeGen typeGen : this.classBuilderMap.values()) {
            if (typeGen.getEnclosingTypeGen() == null) {
                visit(typeGen, sorted, added);
            }
        }

        BuiltinElaborator elaborator = new BuiltinElaborator(rootPackage);
        TreeSet<String> packages = new TreeSet<>();

        ListIterator<TypeGen> iterator = sorted.listIterator(sorted.size());
        while (iterator.hasPrevious()) {
            TypeGen prev = iterator.previous();
            if (prev.isSucc()) {
                elaborator.elaborate(prev);
                prev.build(outputDirectory);
                packages.add(prev.className.packageName());
            }
        }
        {
            generatePackageInfos(packages);
        }
        {
            instantiateTemplates();
        }
    }

    private void withSema(Sema sema) {
        this.currentSema = sema;
    }

    private Sema getCurrentSema() {
        return this.currentSema;
    }

    private List<AnnotationSpec> collectCXXHead(Type type) {
        Set<IncludeEntry> includeEntries = new HashSet<>();
        collectInclude(type, includeEntries);
        if (includeEntries.isEmpty()) {
            return Collections.emptyList();
        }
        return includeEntries.stream().map(e -> AnnotationSpec.builder(CXXHead.class)
                .addMember(e.isSystemDir ? "system" : "value", "$S", e.include)
                .build()).collect(Collectors.toList());
    }

    private void collectForwardHeader(TypeName typeName, List<AnnotationSpec> includes) {
        if (typeName instanceof ClassName) {
            ClassName className = (ClassName) typeName;
            String key = className.canonicalName();
            List<String> headers = fwdHeaders.get(key);
            if (headers != null) {
                headers.stream().forEach(header -> {
                    AnnotationSpec.Builder builder = AnnotationSpec.builder(CXXHead.class);
                    if (header.charAt(0) == '<' && header.charAt(header.length()-1) == '>') {
                        builder.addMember("system", "$S", header.substring(1, header.length() - 1));
                    } else if (header.charAt(0) == '"' && header.charAt(header.length()-1) == '"') {
                        builder.addMember("value", "$S", header.substring(1, header.length() - 1));
                    } else {
                        builder.addMember("value", "$S", header);
                    }
                    includes.add(builder.build());
                });
            }
        } else if (typeName instanceof ParameterizedTypeName) {
            ParameterizedTypeName parameterizedTypeName = (ParameterizedTypeName) typeName;
            collectForwardHeader(parameterizedTypeName.rawType, includes);
            parameterizedTypeName.typeArguments.forEach(argument -> collectForwardHeader(argument, includes));
        }
    }

    private List<AnnotationSpec> collectForwardHeader(TypeName typeName) {
        List<AnnotationSpec> includes = new ArrayList<>();
        collectForwardHeader(typeName, includes);
        return includes;
    }

    private void collectInclude(Type type, Set<IncludeEntry> includes) {
        TypeClass typeClass = type.getTypeClass();
        switch (typeClass) {
            case TemplateSpecialization: {
                TemplateSpecializationType templateSpecializationType = TemplateSpecializationType.dyn_cast(type);
                IncludeEntry entry = getIncludeEntry(type);
                if (entry != null) {
                    // base type
                    includes.add(entry);
                }
                forEachTemplateArgument(templateSpecializationType, argType -> {
                    collectInclude(argType.getTypePtr(), includes);
                });
                break;
            }
            case Elaborated: {
                collectInclude(ElaboratedType.dyn_cast(type).desugar().getTypePtr(), includes);
                break;
            }
            case Pointer: {
                collectInclude(PointerType.dyn_cast(type).getPointeeType().getTypePtr(), includes);
                break;
            }
            case LValueReference: {
                collectInclude(LValueReferenceType.dyn_cast(type).getPointeeType().getTypePtr(), includes);
                break;
            }
            case RValueReference: {
                collectInclude(RValueReferenceType.dyn_cast(type).getPointeeType().getTypePtr(), includes);
                break;
            }
            case Typedef:
            case Enum:
            case Record: {
                IncludeEntry entry = getIncludeEntry(type);
                if (entry != null) {
                    includes.add(entry);
                }
                break;
            }
            case Builtin:
            case SubstTemplateTypeParm: {
                break;
            }
            default:
                Logger.warn("WARNING: ignore type %s", typeClass);
                break;
        }
    }

    void forEachTemplateArgument(TemplateArgumentList templateArgumentList, TemplateDecl templateDecl, Consumer<QualType> consumer) {
        int size = templateArgumentList.size();
        TemplateParameterList templateParameterList = templateDecl.getTemplateParameters();

        // verify template type parameters
        List<NamedDecl> parameters = collectTypeParameters(templateParameterList);
        if (size > parameters.size()) {
            throw unsupportedAST(
                    "TODO: more arguments than supported parameters, expected " + parameters.size() + ", got " + size);
        }

        for (int i = 0; i < size; i++) {
            TemplateArgument arg = templateArgumentList.get(i);
            if (arg.getKind() != TemplateArgument.ArgKind.Type) {
                throw unsupportedAST("TODO: unsupported template argument: " + arg.getKind());
            }
            consumer.accept(arg.getAsType());
        }
    }

    void forEachTemplateArgument(TemplateSpecializationType templateSpecializationType, Consumer<QualType> consumer) {
        int size = templateSpecializationType.getNumArgs();
        TemplateParameterList templateParameterList = templateSpecializationType.getTemplateName().getAsTemplateDecl().getTemplateParameters();

        // verify template type parameters
        List<NamedDecl> parameters = collectTypeParameters(templateParameterList);
        if (size > parameters.size()) {
            throw unsupportedAST("TODO: more arguments than supported parameters, expected " +
                    parameters.size() + ", got " + size + ", " + templateSpecializationType.getAsString());
        }

        for (int i = 0; i < size; i++) {
            TemplateArgument arg = templateSpecializationType.getArg(i);
            if (arg.getKind() != TemplateArgument.ArgKind.Type) {
                throw unsupportedAST("TODO: unsupported template argument: " + arg.getKind() + " in " + templateSpecializationType.getAsString());
            }
            consumer.accept(arg.getAsType());
        }
    }

    private String searchAvailableTemplateModule(String templatePackageName) throws IOException {
        String outputDir = outputDirectory.toString();
        int index = 0;
        String templatePackagePath = Paths.get(outputDir, templatePackageName.split("\\.")).toString();
        while (true) {
            Path path = Paths.get(templatePackagePath, "T" + index + ".java");
            if (!path.toFile().exists()) {
                return "T" + index;
            }
            index += 1;
        }
    }

    private void instantiateTemplates() throws IOException {
        if (templateFactory.isEmpty()) {
            return;
        }
        String templatePackageName = prependRootPackage("fastffi.templates");
        ClassName interfaceClassName = ClassName.get(templatePackageName, searchAvailableTemplateModule(templatePackageName));
        TypeSpec.Builder builder = TypeSpec.interfaceBuilder(interfaceClassName);
        AnnotationSpec.Builder ffiBatchGen = AnnotationSpec.builder(FFIGenBatch.class);
        for (Map.Entry<ClassName, Set<FFIType>> entry : templateFactory.getTemplates().entrySet()) {
            ClassName base = entry.getKey();
            TypeGen typeGen = getTypeGen(base);
            if (typeGen == null || !typeGen.isSucc()) {
                continue;
            }
            Set<FFIType> ffiTypes = entry.getValue();
            AnnotationSpec.Builder ffiGen = AnnotationSpec.builder(FFIGen.class);
            ffiGen.addMember("type", "$S", base.canonicalName());
            for (FFIType ffiType : ffiTypes) {
                AnnotationSpec.Builder cxxTemplate = AnnotationSpec.builder(CXXTemplate.class);
                TemplateSpecializationType templateSpecializationType = TemplateSpecializationType.dyn_cast(ffiType.cxxType);
                forEachTemplateArgument(templateSpecializationType, argType -> {
                    FFIType argFFIType = typeToFFIType(argType); // add type to TypeGen
                    String cxxName = getCXXName(argFFIType);
                    TypeName javaName = argFFIType.javaType;
                    if (javaName.isPrimitive()) {
                        javaName = getPointerOfPrimitive(cxxName, javaName);
                    } else if (argFFIType.isPointer() || argFFIType.isReference()) {
                        javaName = getPointerOfPointer(javaName);
                    }
                    cxxTemplate.addMember("cxx", "$S", cxxName);
                    cxxTemplate.addMember("java", "$S", javaName);
                    collectCXXHead(argType.getTypePtr()).forEach(c -> {
                        cxxTemplate.addMember("include", "$L", c);
                    });
                    collectForwardHeader(javaName).forEach(c -> {
                        cxxTemplate.addMember("include", "$L", c);
                    });
                });
                ffiGen.addMember("templates", "$L", cxxTemplate.build());
            }
            ffiBatchGen.addMember("value", "$L", ffiGen.build());
        }
        builder.addAnnotation(ffiBatchGen.build());

        JavaFile javaFile = JavaFile.builder(interfaceClassName.packageName(), builder.build()).build();
        javaFile.writeTo(outputDirectory);
    }

    private void visit(TypeGen typeGen, List<TypeGen> sorted, Set<TypeGen> added) {
        if (typeGen.isFail()) {
            return;
        }
        if (added.contains(typeGen)) {
            throw new IllegalStateException("Visited twice!!!");
        }
        sorted.add(typeGen);
        added.add(typeGen);
        for (TypeGen child : typeGen.getEnclosedTypeGenList()) {
            visit(child, sorted, added);
        }
    }

    private void generatePackageInfos(TreeSet<String> packages) throws IOException {
        if (rootPackage != null && !rootPackage.isEmpty()) {
            // only generate for the root package
            packages.clear();
            packages.add(rootPackage);
        }
        // generate for all base package as the root package may be null (top-level).
        String previousPackage = null;
        for (String currentPackage : packages) {
            if (previousPackage == null || !currentPackage.startsWith(previousPackage)) {
                Path path = Paths.get(outputDirectory.toString(),
                        Stream.concat(
                                Arrays.stream(currentPackage.split("\\.")),
                                Stream.of("package-info.java")).toArray(String[]::new));
                try (BufferedWriter writer = Files.newBufferedWriter(path)) {
                    writer.write("// " + fileComment + "\n" +
                            "@FFIApplication(jniLibrary = \"" + ffiLibraryName + "\")\n" +
                            "package " + currentPackage + ";\n" +
                            "\n" +
                            "import com.alibaba.fastffi.FFIApplication;");
                }
                catch (IOException e) {
                    throw new IOException(String.format("Failed to generate package-info.java for '%s': %s", currentPackage, e.getMessage()));
                }
                // move the the next
                previousPackage = currentPackage;
            }
        }
    }

    void dumpSearchDirectory(ASTUnit astUnit) {
        HeaderSearch headerSearch = astUnit.getPreprocessor().getHeaderSearchInfo();
        HeaderSearch.search_dir_iterator begin = headerSearch.search_dir_begin();
        HeaderSearch.search_dir_iterator end = headerSearch.search_dir_end();
        int i = 0;
        for (; begin.notEquals(end); begin.next()) {
            DirectoryLookup directoryLookup = begin.get();
            System.out.format("%03d %s%s%s%s\n", i++, directoryLookup.getName(),
                    directoryLookup.isFramework() ? " Framework" : "",
                    directoryLookup.isNormalDir() ? " NormalDir" : "",
                    directoryLookup.isSystemHeaderDirectory() ? " SystemHeaderDirectory " : "");
        }
    }

    void initHeaderManager(ASTUnit astUnit) {
        if (headerManager != null) {
            return;
        }
        HeaderSearch headerSearch = astUnit.getPreprocessor().getHeaderSearchInfo();
        HeaderSearch.search_dir_iterator begin = headerSearch.search_dir_begin();
        HeaderSearch.search_dir_iterator end = headerSearch.search_dir_end();
        int i = 0;
        List<HeaderDirectoryEntry> entries = new ArrayList<>(headerSearch.search_dir_size());
        for (; begin.notEquals(end); begin.next()) {
            DirectoryLookup directoryLookup = begin.get();
            System.out.format("%03d %s%s%s%s\n", i++, directoryLookup.getName(),
                    directoryLookup.isFramework() ? " Framework" : "",
                    directoryLookup.isNormalDir() ? " NormalDir" : "",
                    directoryLookup.isSystemHeaderDirectory() ? " SystemHeaderDirectory " : "");
            entries.add(new HeaderDirectoryEntry(directoryLookup.getName().toJavaString(), directoryLookup.isSystemHeaderDirectory()));
        }
        headerManager = new HeaderManager(entries);
    }

    void generate(ASTUnit astUnit) {
        if (astUnit.getDiagnostics().hasUncompilableErrorOccurred()) {
            dumpSearchDirectory(astUnit);
            System.out.println("#ASTUnit: " + astUnit.getOriginalSourceFileName().toJavaString() + " has uncompilable errors.");
            return;
        }

        initHeaderManager(astUnit);
        ASTContext astContext = astUnit.getASTContext();

        // try our best the leverage the correct `Sema` in when process declarations and types,
        // as the `Sema` is associated with ASTUnit only.
        this.withSema(astUnit.getSema());
        generate(astContext.getTranslationUnitDecl());
        this.withSema(null);
    }

    void generate(TranslationUnitDecl translationUnitDecl) {
        DeclContext.decl_iterator begin = translationUnitDecl.decls_begin();
        DeclContext.decl_iterator end = translationUnitDecl.decls_end();
        for (; begin.notEquals(end); begin.next()) {
            try {
                Decl decl = begin.get();
                if (decl.isExplicitlyDeclaredInMainFile()) {
                    generate(decl);
                }
            } catch (ExcludedException e) {
            } catch (UnsupportedASTException e) {
                Logger.error("Failed to generate for declaration in translation unit, %s", e.getMessage());
                if (Logger.verbose()) {
                    e.printStackTrace();
                }
            }
        }
    }

    boolean generate(Decl decl) {
        if (decl == null) {
            throw new NullPointerException();
        }
        Decl.Kind kind = decl.getKind();
        TypeGen typeGen = getOrCreateTypeGenIfNecessary(decl);
        boolean changed = false;
        if (typeGen != null) {
            if (!typeGen.isNone()) {
                return false;
            }
            changed = true;
        }
        try {
            switch (kind) {
                case Enum:
                    generateEnum((EnumDecl) decl);
                    break;
                case Record:
                    generateRecord((RecordDecl) decl);
                    break;
                case CXXRecord:
                    generateCXXRecord((CXXRecordDecl) decl);
                    break;
                case ClassTemplate:
                    generateClassTemplate((ClassTemplateDecl) decl);
                    break;
                case ClassTemplateSpecialization:
                    generateClassTemplateSpecialization((ClassTemplateSpecializationDecl) decl);
                    break;
                case TypeAlias:
                    generateTypedef((TypeAliasDecl) decl);
                    break;
                case TypeAliasTemplate:
                    generateTypedef(((TypeAliasTemplateDecl) decl).getTemplatedDecl());
                    break;
                case Typedef:
                    generateTypedef((TypedefDecl) decl);
                default:
                    break;
            }
            if (decl instanceof DeclContext) {
                DeclContext declContext = (DeclContext) decl;
                if (declContext.decls_empty()) {
                    // use the primary context
                    declContext = declContext.getPrimaryContext();
                }
                DeclContext.decl_iterator begin = declContext.decls_begin();
                DeclContext.decl_iterator end = declContext.decls_end();
                for (; begin.notEquals(end); begin.next()) {
                    // mark parent changed of any enclosing element is changed
                    // TODO: begin.get() may be null.
                    Decl child = begin.get();
                    if (child == null || child.isImplicit()) {
                        continue;
                    }
                    changed |= generate(child);
                }
            }
        } catch (ExcludedException e) {
        } catch (UnsupportedASTException e) {
            if (Logger.verbose()) {
                e.printStackTrace();
            }
            if (typeGen != null) {
                typeGen.fail(e.toString());
            }
        }
        return changed;
    }

    /**
     * Probe TypeGen
     * @param decl
     * @return
     */
    private TypeGen getOrCreateTypeGenIfNecessary(Decl decl) {
        Decl.Kind kind = decl.getKind();
        switch (kind) {
            case Enum:
            case Record:
            case CXXRecord:
                TagDecl tagDecl = (TagDecl) decl;
                if (!tagDecl.isThisDeclarationADefinition()) {
                    tagDecl = (TagDecl) DeclTypeRefiner.refine(tagDecl.getDefinition());
                }
                if (tagDecl == null) {
                    Logger.warn("no definition for declaration %s", ((TagDecl) decl).getNameAsString());
                    return null; // e.g., forward declaration
                }
                if (tagDecl.getIdentifier() == null) {
                    return null;
                }
                return getOrCreateTypeGen(tagDecl);
            case ClassTemplate:
                return getOrCreateTypeGenIfNecessary(((ClassTemplateDecl) decl).getTemplatedDecl());
            case TypeAlias:
                return getOrCreateTypeGen((TypeAliasDecl) decl);
            case TypeAliasTemplate:
                return getOrCreateTypeGenIfNecessary(((TypeAliasTemplateDecl) decl).getTemplatedDecl());
            default:
                return null;
        }
    }

    private void generateEnum(EnumDecl decl) {
        TypeName int_pointer_t = null;
        if (!enumAsInteger) {
            // generate a int_pointer_t type for the underlying representation of enums.
            int_pointer_t = getOrCreatePointerOfPrimitive("int", TypeName.INT);
        }

        decl = decl.getDefinition();
        if (!isSupportedTagDecl(decl)) {
            throw unsupportedAST("Unsupported Enum: " + decl);
        }
        TypeGen typeGen = getOrCreateTypeGen(decl);
        ClassName className = typeGen.className;
        String cxxName = getCXXName(decl);
        TypeSpec.Builder classBuilder = typeGen.builder;
        TypeSpec.Builder libraryClassBuilder = typeGen.getLibraryBuilder(cxxName);
        {
            EnumDecl.enumerator_iterator begin = decl.enumerator_begin();
            EnumDecl.enumerator_iterator end = decl.enumerator_end();
            for (; begin.notEquals(end); begin.next()) {
                EnumConstantDecl enumConstantDecl = begin.get();
                String name = enumConstantDecl.getIdentifier().getName().toJavaString();
                classBuilder.addEnumConstant(name, TypeSpec.anonymousClassBuilder("$L", "Library.INSTANCE." + name + "()").build());
                MethodSpec.Builder getter = MethodSpec.methodBuilder(name).addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT);
                getter.addAnnotation(FFIGetter.class);
                getter.returns(TypeName.INT);
                libraryClassBuilder.addMethod(getter.build());
            }
        }
        {
            classBuilder.addSuperinterface(CXXEnum.class);
            classBuilder.addAnnotation(AnnotationSpec.builder(FFITypeRefiner.class)
                    .addMember("value", "$S", className.toString() + ".get").build());

            classBuilder.addMethod(MethodSpec.constructorBuilder()
                    .addStatement("$$value = value").addParameter(TypeName.INT, "value")
                    .build());
            if (!enumAsInteger) {
                classBuilder.addMethod(MethodSpec.constructorBuilder()
                        .addStatement("$$value = value.get()").addParameter(int_pointer_t, "value")
                        .build());
            }

            classBuilder.addMethod(MethodSpec.methodBuilder("get")
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .addParameter(TypeName.INT, "value")
                    .returns(className)
                    .addStatement("return $$map.get(value)").build());
            if (!enumAsInteger) {
                classBuilder.addMethod(MethodSpec.methodBuilder("get")
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .addParameter(int_pointer_t, "value")
                        .returns(className)
                        .addStatement("return $$map.get(value.get())").build());
            }

            classBuilder.addField(TypeName.INT, "$value");
            classBuilder.addMethod(MethodSpec.methodBuilder("getValue")
                    .addModifiers(Modifier.PUBLIC).returns(TypeName.INT).addStatement("return $$value")
                    .build());

            ClassName CXXEnumMapName = ClassName.get(CXXEnumMap.class);
            TypeName mapName = ParameterizedTypeName.get(CXXEnumMapName, className);
            classBuilder.addField(FieldSpec.builder(mapName, "$map", Modifier.STATIC, Modifier.FINAL, Modifier.PRIVATE)
                    .initializer("new $T<>(values())", CXXEnumMap.class).build());
        }
        addCXXHeader(typeGen);
        typeGen.succ();
    }

    private void addCXXHeader(TypeGen typeGen) {
        AnnotationSpec annotationSpec = createCXXHeader(typeGen.decl);
        if (annotationSpec != null) {
            typeGen.addHeader(annotationSpec);
        }
        AnnotationSpec annotationSpecFwd = createForwardHeaders(typeGen.className);
        if (annotationSpecFwd != null) {
            typeGen.addHeader(annotationSpecFwd);
        }
    }

    private void addCXXHeaderOf(TypeGen typeGen, Decl decl) {
        AnnotationSpec annotationSpec = createCXXHeader(decl);
        if (annotationSpec != null) {
            typeGen.addHeader(annotationSpec);
        }
    }

    private void addCXXHeaderOf(TypeGen typeGen, ClassName className) {
        AnnotationSpec annotationSpecFwd = createForwardHeaders(className);
        if (annotationSpecFwd != null) {
            typeGen.addHeader(annotationSpecFwd);
        }
    }

    private AnnotationSpec createCXXHeader(Type type) {
        Decl decl = getTypeDecl(type);
        return createCXXHeader(decl);
    }

    private IncludeEntry getIncludeEntry(Type type) {
        Decl decl = getTypeDecl(type);
        return getIncludeEntry(decl);
    }

    private IncludeEntry getIncludeEntry(Decl decl) {
        if (decl == null) {
            return null;
        }
        SourceManager sourceManager = decl.getASTContext().getSourceManager();
        SourceLocation sourceLocation = sourceManager.getSpellingLoc(decl.getLocation());
        String fileName = sourceManager.getFilename(sourceLocation).toJavaString();
        if (!fileName.isEmpty()) {
            HeaderDirectoryEntry entry = headerManager.getIncludeDirectory(fileName);
            if (entry != null) {
                return new IncludeEntry(entry.getIncludePath(fileName), entry.isSystemDir);
            }
        }
        return null;
    }

    private AnnotationSpec createCXXHeader(Decl decl) {
        if (decl == null) {
            return null;
        }
        IncludeEntry entry = getIncludeEntry(decl);
        if (entry == null) {
            return null;
        }
        String key = entry.isSystemDir ? "system" : "value";
        return AnnotationSpec.builder(CXXHead.class)
                .addMember(key, "$S", entry.include)
                .build();
    }

    private AnnotationSpec createForwardHeaders(ClassName className) {
        String key = className.canonicalName();
        List<String> headers = fwdHeaders.get(key);
        if (headers == null) {
            return null;
        }
        AnnotationSpec.Builder builder = AnnotationSpec.builder(CXXHead.class);
        for (String header: headers) {
            if (header.charAt(0) == '<' && header.charAt(header.length()-1) == '>') {
                builder.addMember("system", "$S", header.substring(1, header.length() - 1));
            } else if (header.charAt(0) == '"' && header.charAt(header.length()-1) == '"') {
                builder.addMember("value", "$S", header.substring(1, header.length() - 1));
            } else {
                builder.addMember("value", "$S", header);
            }
        }
        return builder.build();
    }

    private void checkDeclContext(TypeGen typeGen) {
        ClassName javaName = typeGen.className;
        checkDeclContext(typeGen, javaName);
    }

    private void checkDeclContext(TypeGen typeGen, ClassName javaName) {
        ClassName enclosingName = javaName.enclosingClassName();
        if (enclosingName != null && typeGen.getEnclosingTypeGen() == null) {
            TypeGen parentTypeGen = getTypeGen(enclosingName);
            if (parentTypeGen != null) {
                parentTypeGen.addEnclosedTypeGen(typeGen);
            }
        }
    }

    UnsupportedASTException unsupportedAST(String message) {
        return new UnsupportedASTException(message);
    }

    ExcludedException excludedException(String message) {
        Logger.debug("[EXCLUDE]: %s", message);
        return new ExcludedException(message);
    }

    void buildReturnAndParamTypes(MethodSpec.Builder methodBuilder, TypeName returnType, CXXConstructorDecl methodDecl) {
        methodBuilder.returns(returnType);
        buildParamTypes(methodBuilder, methodDecl);
    }

    /**
     * return null is the given type is not an aliased primitive
     * @param ffiType
     * @return
     */
    TypeName isAliasedPrimitive(FFIType ffiType) {
        return isAliasedPrimitive(ffiType.cxxType);
    }

    TypeName isAliasedPrimitive(Type type) {
        TypeClass typeClass = type.getTypeClass();
        switch (typeClass) {
            case Typedef: {
                TypedefType typedefType = TypedefType.dyn_cast(type);
                Type canonicalType = typedefType.getCanonicalTypeInternal().getTypePtr();
                if (canonicalType.getTypeClass().equals(TypeClass.Builtin)) {
                    return getJavaTypeName(BuiltinType.dyn_cast(canonicalType));
                }
            }
        }

        return null;
    }

    TypeName buildReturnAndParamTypes(MethodSpec.Builder methodBuilder, CXXMethodDecl methodDecl) {
        FFIType ffiType = typeToFFIType(methodDecl.getReturnType());
        TypeName returnType = ffiType.javaType;
        if (ffiType.isEnum()) {
            methodBuilder.addAnnotation(CXXValue.class);
        } else if (ffiType.isReference()) {
            methodBuilder.addAnnotation(CXXReference.class);
        } else if (ffiType.isValue()) {
            TypeName aliasedPrimitive = isAliasedPrimitive(ffiType);
            if (aliasedPrimitive != null) {
                returnType = aliasedPrimitive;
            } else {
                methodBuilder.addAnnotation(CXXValue.class);
            }
        } else {
        }
        if (ffiType.needFFITypeAlias()) {
            methodBuilder.addAnnotation(getFFITypeAlias(ffiType));
        }

        methodBuilder.returns(returnType);
        buildParamTypes(methodBuilder, methodDecl);
        return returnType;
    }

    AnnotationSpec getFFINameAlias(String value) {
        return AnnotationSpec.builder(FFINameAlias.class)
                .addMember("value", "$S", value).build();
    }

    String getFFITypeAliasName(FFIType ffiType) {
        String cxxTypeName;
        if (ffiType.isValue()) {
            cxxTypeName = getCXXName(ffiType, true);
        } else if (ffiType.isPointer() || ffiType.isReference()) {
            cxxTypeName = getCXXName(ffiType.getPointeeType(), true);
        } else if (ffiType.isPrimitive()) {
            cxxTypeName = getCXXName(ffiType, true);
        } else {
            throw new IllegalStateException("Oops");
        }
        return cxxTypeName;
    }

    AnnotationSpec getFFITypeAlias(FFIType ffiType) {
        return getFFITypeAlias(getFFITypeAliasName(ffiType));
    }

    AnnotationSpec getFFITypeAlias(String cxxName) {
        return AnnotationSpec.builder(FFITypeAlias.class)
                .addMember("value", "$S", cxxName).build();
    }

    TypeName typeToTypeName(QualType type) {
        return typeToTypeName(type.getTypePtr());
    }

    TypeName typeToTypeName(Type type) {
        TypeClass typeClass = type.getTypeClass();
        switch (typeClass) {
            case Builtin: {
                return getJavaTypeForBuiltinType((BuiltinType) type);
            }
            case Enum: {
                return getJavaTypeForEnumType((EnumType) type);
            }
            case Record: {
                return getJavaTypeForRecordType((RecordType) type);
            }
            case Pointer: {
                return getJavaTypeForPointerType((PointerType) type);
            }
            case TemplateTypeParm: {
                return getJavaTypeForTemplateTypeParamType((TemplateTypeParmType) type);
            }
            case TemplateSpecialization: {
                return getJavaTypeForTemplateSpecializationType((TemplateSpecializationType) type);
            }
            case LValueReference:{
                return getJavaTypeForReferenceType((ReferenceType) type);
            }
            case RValueReference: {
                throw unsupportedAST("TODO: rvalue reference is not supported now");
            }
            case Typedef: {
                return getJavaTypeForTypedefType((TypedefType) type);
            }
            case DependentName: {
                return getJavaTypeForDependentNameType((DependentNameType) type);
            }
            case SubstTemplateTypeParm: {
                return getJavaTypeForSubstTemplateTypeParmType((SubstTemplateTypeParmType) type);
            }
            case Elaborated: {
                return getJavaTypeForElaboratedType((ElaboratedType) type);
            }
            case InjectedClassName: {
                return getJavaTypeForInjectedClassNameType((InjectedClassNameType) type);
            }
            default: {
                throw unsupportedAST("Unsupported type: " + typeClass + ", " + type.getAsString());
            }
        }
    }

    private TypeName replacePackage(TypeName typeName, String packageName) {
        if (typeName instanceof ClassName) {
            ClassName className = (ClassName) typeName;
            if (className.simpleNames().isEmpty()) {
                return getClassName(packageName, className.simpleName());
            } else {
                return getClassName(packageName, className.simpleName(), className.simpleNames().toArray(new String[0]));
            }
        }
        if (typeName instanceof ParameterizedTypeName) {
            ParameterizedTypeName parameterizedTypeName = (ParameterizedTypeName) typeName;
            ClassName base = (ClassName) replacePackage(parameterizedTypeName.rawType, packageName);
            return ParameterizedTypeName.get(base, parameterizedTypeName.typeArguments.toArray(new TypeName[0]));
        }
        if (typeName instanceof ArrayTypeName) {
            throw new IllegalStateException("TODO: Array type name is not supported.");
        }
        return typeName;
    }

    private ClassName getPointerOfPrimitive(String cxxName, TypeName javaName) {
        if (!javaName.isPrimitive()) {
            throw new IllegalStateException("Must be a primitive type, got " + javaName);
        }
        if (cxxName.endsWith("_t")) {
            cxxName = cxxName.substring(0, cxxName.length() - 2);
        }
        return getClassName(prependRootPackage("std"), 'C' + capitalizeCXXName(cxxName));
    }

    private TypeName getPointerOfPrimitive(FFIType ffiType) {
        if (!ffiType.javaType.isPrimitive()) {
            throw new IllegalStateException("Sanity check: the FFI type is not a primitive: " + ffiType);
        }
        return getPointerOfPrimitive(getCXXName(ffiType), ffiType.javaType);
    }

    private TypeName getPointerOfPointer(TypeName pointeeTypeName) {
        if (!(pointeeTypeName instanceof ClassName)) {
            throw unsupportedAST("Unsupported type: " + pointeeTypeName);
        }
        ClassName className = (ClassName) pointeeTypeName;
        String simpleName = className.simpleName();
        ClassName enclosingClassName = className.enclosingClassName();
        if (enclosingClassName == null) {
            String packageName = className.packageName();
            return getClassName(packageName, simpleName + "P");
        } else {
            return enclosingClassName.nestedClass(simpleName + "P");
        }
    }

    private TypeName getJavaTypeForElaboratedType(ElaboratedType type) {
        return typeToTypeName(type.desugar().getTypePtr());
    }

    private TypeName getJavaTypeForDependentNameType(DependentNameType type) {
        NestedNameSpecifier specifier = type.getQualifier();
        throw unsupportedAST("TODO: Unsupported dependent name type: " + type.getAsString() + ": " + specifier.getKind());
    }

    private TypeName getJavaTypeForSubstTemplateTypeParmType(SubstTemplateTypeParmType type) {
        return typeToTypeName(type.getReplacementType());
    }

    private TypeName getJavaTypeForInjectedClassNameType(InjectedClassNameType type) {
        CXXRecordDecl decl = type.getDecl();
        verifyVisibility(decl);

        List<TypeVariableName> typeVariableNameList = getJavaTypeVariablesInContext(decl.getDeclContext());
        getJavaTypeVariablesOnDecl(decl, typeVariableNameList);

        ClassName className = getJavaClassName(decl);
        TypeName typeName = className;
        if (!typeVariableNameList.isEmpty()) {
            typeName = ParameterizedTypeName.get(className, typeVariableNameList.toArray(new TypeName[0]));
        }
        return typeName;
    }

    /**
     * <ul>
     *     <li>If the typedef is defined from a primitve, then use the primitive type name</li>
     *     <li>otherwise, get the name from the TypedefNameDecl.
     *     In this case, we may create redundant Java bindings for aliased types,
     *     where each type alias has a Java binding. Java does not support type alias
     *     so we use inheritance to support type alias.</li>
     * </ul>
     * @param typedefNameDecl
     * @return
     */
    private TypeName getJavaTypeForTypedefType(TypedefNameDecl typedefNameDecl) {
        verifyVisibility(typedefNameDecl);

        QualType underlyingQualType = typedefNameDecl.getUnderlyingType();
        Type underlyingType = underlyingQualType.getTypePtr();
        FFIType underlyingFFIType;
        try {
            underlyingFFIType = typeToFFIType(underlyingQualType);
        } catch (UnsupportedASTException e) {
            Logger.debug("the unerlying type '%s (%s)', is not supported: %s",
                    underlyingQualType.getAsString(), underlyingType.getTypeClass(), e.getMessage());
            // If the underlying type is not supported, e.g., dependent name type,
            // we generate a place holder for the typedef alias
            underlyingFFIType = new FFIType(underlyingQualType, getJavaClassName(typedefNameDecl));
        }
        if (isJavaDeclarable(underlyingType)) {
            List<TypeVariableName> typeVariableNameList = getJavaTypeVariablesInContext(typedefNameDecl.getDeclContext());
            getJavaTypeVariablesOnDecl(typedefNameDecl, typeVariableNameList);

            ClassName className = getJavaClassName(typedefNameDecl);
            TypeName typeName = className;
            if (!typeVariableNameList.isEmpty()) {
                typeName = ParameterizedTypeName.get(className, typeVariableNameList.toArray(new TypeName[0]));
            }
            return typeName;
        }
        // other cases: only value, pointer and reference of type parameter is allowed.
        if (underlyingFFIType.isTemplateVariableDependent()) {
            return underlyingFFIType.javaType;
        }
        String declName = typedefNameDecl.getDeclName().getAsString().toJavaString();
        throw unsupportedAST("Unsupported TypedefType: " + declName + ", underlying type is: " + underlyingQualType.getAsString());
    }

    private TypeName getJavaTypeForTypedefType(TypedefType typedefType) {
        TypedefNameDecl typedefNameDecl = typedefType.getDecl();
        List<TypeVariableName> typeVariableNameList = getJavaTypeVariablesInContext(typedefNameDecl.getDeclContext());
        // FIXME: assume typedef itself doesn't contain type parameters
        // getJavaTypeVariablesOnDecl(typedefNameDecl, typeVariableNameList);

        ClassName className = getJavaClassName(typedefType.getDecl());
        if (!typeVariableNameList.isEmpty()) {
            return ParameterizedTypeName.get(className, typeVariableNameList.toArray(new TypeName[0]));
        } else {
            return className;
        }
    }

    /**
     * TODO: We currently does not support pointer of pointer
     * unless the pointer type is redefined as a new type via typedef or using.
     * @param pointeeQualType
     * @return
     */
    private TypeName getJavaTypeForPointeeType(QualType pointeeQualType) {
        FFIType pointeeFFIType = typeToFFIType(pointeeQualType);
        if (pointeeFFIType.isVoid()) {
            return TypeName.LONG;
        }
        if (pointeeFFIType.isReference()) {
            throw new IllegalStateException("Sanity check");
        }
        if (pointeeFFIType.isPointer()) {
            if (pointeeFFIType.isTemplateVariableDependent()) {
                throw unsupportedAST("TODO: no pointer to pointer of template variable is supported");
            }
            // here is a little bit tricky;
            // we need to create a pointer to the pointer
            return getPointerOfPointer(pointeeFFIType.javaType);
        }
        if (pointeeFFIType.isPrimitive()) {
            return getPointerOfPrimitive(pointeeFFIType);
        }
        if (pointeeFFIType.isTemplateVariableDependent()) {
            return pointeeFFIType.javaType;
        }
        return pointeeFFIType.javaType;
    }

    private TypeName getJavaTypeForReferenceType(ReferenceType type) {
        return getJavaTypeForPointeeType(type.getPointeeType());
    }

    private TypeName getJavaTypeForTemplateSpecializationType(ClassTemplateSpecializationDecl templateSpecializationDecl) {
        ClassTemplateDecl classTemplateDecl = templateSpecializationDecl.getSpecializedTemplate();
        verifyVisibility(classTemplateDecl);

        ClassName baseClassName = getJavaClassName(classTemplateDecl.getTemplatedDecl());
        TemplateArgumentList templateArgumentList = templateSpecializationDecl.getTemplateArgs();

        int numArgs = templateArgumentList.size();
        if (numArgs == 0) {
            return baseClassName;
        }
        List<TypeName> argTypeNames = new ArrayList<>();

        forEachTemplateArgument(templateArgumentList, classTemplateDecl, argType -> {
            FFIType argFFIType = typeToFFIType(argType);
            TypeName argTypeName = argFFIType.javaType;
            if (argTypeName.isPrimitive()) {
                argFFIType.setRequirePointer();
                argTypeName = getPointerOfPrimitive(getCXXName(argFFIType.cxxQualType), argFFIType.javaType);
            } else if (argFFIType.isPointer() || argFFIType.isReference()) {
                argFFIType.setRequirePointer();
                argTypeName = getPointerOfPointer(argFFIType.javaType);
            }
            argTypeNames.add(argTypeName);
        });

        return ParameterizedTypeName.get(baseClassName, argTypeNames.toArray(new TypeName[0]));
    }

    private TypeName getJavaTypeForTemplateSpecializationType(TemplateSpecializationType type) {
        TemplateName templateName = type.getTemplateName();
        TemplateDecl templateDecl = templateName.getAsTemplateDecl();
        verifyVisibility(templateDecl);

        ClassName baseClassName = null;

        // See also: https://clang.llvm.org/doxygen/classclang_1_1TemplateDecl.html
        switch (templateDecl.getKind()) {
            case ClassTemplate: {
                ClassTemplateDecl classTemplateDecl = ClassTemplateDecl.dyn_cast(templateDecl);
                CXXRecordDecl cxxRecordDecl = classTemplateDecl.getTemplatedDecl();
                baseClassName = getJavaClassName(cxxRecordDecl);
                break;
            }
            case TypeAliasTemplate: {
                TypeAliasTemplateDecl typeAliasTemplateDecl = TypeAliasTemplateDecl.dyn_cast(templateDecl);
                TypeAliasDecl typeAliasDecl = typeAliasTemplateDecl.getTemplatedDecl();
                baseClassName = getJavaClassName(typeAliasDecl);
                break;
            }
            default: {
                throw unsupportedAST("Unsupported template: " + templateDecl.getKind() + ", " + templateDecl);
            }
        }

        int numArgs = type.getNumArgs();
        if (numArgs == 0) {
            return baseClassName;
        }
        List<TypeName> argTypeNames = new ArrayList<>();

        forEachTemplateArgument(type, argType -> {
            FFIType argFFIType = typeToFFIType(argType);
            TypeName argTypeName = argFFIType.javaType;
            if (argTypeName.isPrimitive()) {
                argFFIType.setRequirePointer();
                argTypeName = getPointerOfPrimitive(getCXXName(argFFIType.cxxQualType), argFFIType.javaType);
            } else if (argFFIType.isPointer() || argFFIType.isReference()) {
                argFFIType.setRequirePointer();
                argTypeName = getPointerOfPointer(argFFIType.javaType);
            }
            argTypeNames.add(argTypeName);
        });

        return ParameterizedTypeName.get(baseClassName, argTypeNames.toArray(new TypeName[0]));
    }

    private TypeName getJavaTypeForTemplateTypeParamType(TemplateTypeParmType type) {
        if (type.getDecl().hasDefaultArgument()) {
            throw unsupportedAST("TODO: no support of template argument with default argument: " + type.getAsString());
        }
        IdentifierInfo identifierInfo = type.getIdentifier();
        if (identifierInfo == null) {
            throw unsupportedAST("TemplateTypeParamType does not have an identifier: " + type.getAsString());
        }
        return TypeVariableName.get(identifierInfo.getName().toJavaString());
    }

    private TypeName getJavaTypeForPointerType(PointerType type) {
        return getJavaTypeForPointeeType(type.getPointeeType());
    }

    private TypeName getJavaTypeForEnumType(EnumType type) {
        EnumDecl enumDecl = type.getDecl();
        verifyVisibility(enumDecl);

        if (enumAsInteger) {
            return TypeName.INT;
        }
        return getJavaClassName(enumDecl);
    }

    private TypeName getJavaTypeForRecordType(RecordType type) {
        RecordDecl recordDecl = type.getDecl();
        verifyVisibility(recordDecl);
        return tryApplyTypeParameters(recordDecl, getJavaClassName(recordDecl));
    }

    private TypeName getJavaTypeForBuiltinType(BuiltinType type) {
        TypeName typeName = getJavaTypeName(type);
        if (typeName == null) {
            throw unsupportedAST("Unsupported builtint type: " + type);
        }
        return typeName;
    }

    private QualType simplifyCXXType(QualType qualType) {
        Type type = qualType.getTypePtr();
        switch (type.getTypeClass()) {
            case Typedef: {
                TypedefType typedefType = TypedefType.dyn_cast(type);
                TypedefNameDecl typedefNameDecl = typedefType.getDecl();
                QualType underlyingQualType = typedefNameDecl.getUnderlyingType();
                QualType rtype = simplifyCXXType(underlyingQualType);
                switch (rtype.getTypePtr().getTypeClass()) {
                    // acceptable
                    case Builtin:
                    case TemplateTypeParm:
                    case InjectedClassName:
                        return rtype;
                    default:
                        return qualType;
                }
            }
            case Elaborated: {
                return ElaboratedType.dyn_cast(type).desugar();
            }
            case SubstTemplateTypeParm: {
                SubstTemplateTypeParmType substTemplateTypeParmType = SubstTemplateTypeParmType.dyn_cast(type);
                return simplifyCXXType(substTemplateTypeParmType.getReplacementType());
            }
            case Record: {
                RecordType recordType = RecordType.dyn_cast(type);
                RecordDecl recordDecl = recordType.getDecl();
                switch (recordDecl.getKind()) {
                    case ClassTemplateSpecialization: {
                        ClassTemplateSpecializationDecl templateSpecializationDecl = ClassTemplateSpecializationDecl.dyn_cast(recordDecl);
                        return recordDecl.getASTContext().getTemplateSpecializationType(
                                TemplateName.create(templateSpecializationDecl.getSpecializedTemplate()),
                                templateSpecializationDecl.getTemplateArgs().asArray(),
                                qualType);
                    }
                    default: {
                        return qualType;
                    }
                }
            }
            case Decltype: {
                return DecltypeType.dyn_cast(type).getUnderlyingType();
            }
            default: {
                return qualType;
            }
        }
    }

    FFIType typeToFFIType(QualType qualType) {
        QualType simplifyQualType = simplifyCXXType(qualType);
        Type type = simplifyQualType.getTypePtr();
        TypeName typeName = typeToTypeName(type);
        FFIType key = new FFIType(simplifyQualType, typeName);

        // ensure the type is generatable.
        verifyDecl(key);

        // no need to cache:
        //  - template parameter
        //  - injected class name, which has already been generated.
        switch (type.getTypeClass()) {
            case TemplateTypeParm:
            case InjectedClassName:
                return key;
        }

        FFIType ffiType = ffiTypeDictionary.get(key);
        if (ffiType != null) {
            if (!key.cxxType.getTypeClass().equals(ffiType.cxxType.getTypeClass())) {
                Logger.warn("different types are registered with the same name: %s -> %s", ffiType, key);
            }
            return ffiType;
        }
        ffiTypeDictionary.put(key, key);
        return key;
    }

    FFIType typedefToFFIType(TypedefNameDecl typedefNameDecl) {
        ASTContext astContext = typedefNameDecl.getASTContext();
        QualType type = astContext.getTypedefType(typedefNameDecl, typedefNameDecl.getUnderlyingType());
        return typeToFFIType(type);
    }

    private void verifyVisibility(Decl decl) {
        if (!decl.getAccess().isPublicOrNone()) {
            NamedDecl namedDecl = NamedDecl.dyn_cast(decl);
            if (namedDecl != null && !namedDecl.isNull()) {
                throw unsupportedAST("Cannot find non-public decl: " + namedDecl.getDeclName().getAsString());
            } else {
                throw unsupportedAST("Cannot find non-public decl: " + decl.getKind());
            }
        }
    }

    private void verifyDecl(Decl decl) {
        verifyVisibility(decl);
        Decl.Kind kind = decl.getKind();
        switch (kind) {
            case Enum:
            case Record:
            case CXXRecord:{
                TagDecl tagDecl = TagDecl.dyn_cast(decl);
                if (!tagDecl.isThisDeclarationADefinition()) {
                    tagDecl = tagDecl.getDefinition();
                }
                if (tagDecl == null) {
                    tagDecl = TagDecl.dyn_cast(decl);
                    String name = tagDecl.getNameAsString().toJavaString();
                    throw unsupportedAST("TODO: cannot get the definition of a tag decl: " + name);
                }
                ClassName javaClassName = getJavaClassName(tagDecl);
                if (exclude(javaClassName.canonicalName())) {
                    throw excludedException("TODO: exclude " + javaClassName);
                }
                break;
            }
            case ClassTemplateSpecialization: {
                ClassTemplateSpecializationDecl classTemplateSpecializationDecl = ClassTemplateSpecializationDecl.dyn_cast(decl);
                verifyDecl(classTemplateSpecializationDecl.getSpecializedTemplate());
                TemplateArgumentList templateArgumentList = classTemplateSpecializationDecl.getTemplateArgs();
                for (int i = 0; i < templateArgumentList.size(); i++) {
                    TemplateArgument templateArgument = templateArgumentList.get(i);
                    switch (templateArgument.getKind()) {
                        case Type: {
                            verifyDecl(templateArgument.getAsType().getTypePtr());
                            break;
                        }
                        case Integral: {
                            break;
                        }
                        default: {
                            throw unsupportedAST("TODO: unsupported template argument type: " + templateArgument.getKind());
                        }
                    }
                }
                break;
            }
            case ClassTemplate: {
                ClassTemplateDecl classTemplateDecl = ClassTemplateDecl.dyn_cast(decl);
                verifyDecl(classTemplateDecl.getTemplatedDecl());
                {
                    TemplateParameterList templateParameterList = classTemplateDecl.getTemplateParameters();
                    if (templateParameterList.hasParameterPack()) {
                        throw unsupportedAST("TODO: parameter pack is not supported on " + getJavaClassName(classTemplateDecl));
                    }
                }
                break;
            }
            case TypeAlias: {
                TypeAliasDecl typeAliasDecl = TypeAliasDecl.dyn_cast(decl);
                ClassName javaClassName = getJavaClassName(typeAliasDecl);
                if (exclude(javaClassName.canonicalName())) {
                    throw excludedException("TODO: exclude type alias " + javaClassName);
                }
                break;
            }
            case TypeAliasTemplate: {
                TypeAliasTemplateDecl typeAliasTemplateDecl = TypeAliasTemplateDecl.dyn_cast(decl);
                verifyDecl(typeAliasTemplateDecl.getTemplatedDecl());
                {
                    TemplateParameterList templateParameterList = typeAliasTemplateDecl.getTemplateParameters();
                    if (templateParameterList.hasParameterPack()) {
                        throw unsupportedAST("TODO: parameter pack is not supported on type alias " + getJavaClassName(typeAliasTemplateDecl));
                    }
                }
                break;
            }
            case Typedef: {
                TypedefNameDecl typedefNameDecl = TypedefNameDecl.dyn_cast(decl);
                ClassName javaClassName = getJavaClassName(typedefNameDecl);
                if (exclude(javaClassName.canonicalName())) {
                    throw excludedException("TODO: exclude type alias " + javaClassName);
                }
                break;
            }
        }
    }

    private void verifyDecl(Type type) {
        TypeClass typeClass = type.getTypeClass();
        switch (typeClass) {
            case Enum: {
                EnumType enumType = EnumType.dyn_cast(type);
                verifyDecl(enumType.getDecl());
                break;
            }
            case Record: {
                RecordType recordType = RecordType.dyn_cast(type);
                verifyDecl(recordType.getDecl());
                break;
            }
            case TemplateSpecialization: {
                TemplateSpecializationType templateSpecializationType = TemplateSpecializationType.dyn_cast(type);
                forEachTemplateArgument(templateSpecializationType, argType -> {
                    verifyDecl(argType.getTypePtr());
                });
                verifyDecl(templateSpecializationType.getTemplateName().getAsTemplateDecl());
                break;
            }
            case Pointer: {
                PointerType pointerType = PointerType.dyn_cast(type);
                verifyDecl(pointerType.getPointeeType().getTypePtr());
                break;
            }
            case LValueReference: {
                LValueReferenceType lValueReferenceType = LValueReferenceType.dyn_cast(type);
                verifyDecl(lValueReferenceType.getPointeeType().getTypePtr());
                break;
            }
            case RValueReference: {
                RValueReferenceType rValueReferenceType = RValueReferenceType.dyn_cast(type);
                verifyDecl(rValueReferenceType.getPointeeType().getTypePtr());
                break;
            }
            case Typedef: {
                TypedefType typedefType = TypedefType.dyn_cast(type);
                verifyDecl(typedefType.getDecl());
                break;
            }
        }
    }

    private void verifyDecl(FFIType ffiType) {
        if (exclude(ffiType.javaType.toString())) {
            throw excludedException("Excluded java type: " + ffiType.javaType);
        }
        // N.B.: verify the cxxType itself first (in case of it has been exclueded), and verify
        // the underlying canonical type if it has a different type class.
        verifyDecl(ffiType.cxxType);
        Type canonical = ffiType.cxxType.getCanonicalTypeInternal().getTypePtr();
        if (!ffiType.cxxType.getTypeClass().equals(canonical.getTypeClass())) {
            verifyDecl(canonical);
        }
    }

    ClassName getClassName(String packageName, String simpleName, String... simpleNames) {
        return ClassName.get(packageName, simpleName, simpleNames);
    }

    static String getCXXTypeName(TypeName javaType) {
        if (javaType == null) {
            return null;
        }
        String javaTypeName = javaType.toString();
        switch (javaTypeName) {
            case "void":
                return "void";
            case "boolean":
                return "bool";
            case "byte":
                return "int8_t";
            case "char":
                return "char";
            case "short":
                return "int16_t";
            case "int":
                return "int";
            case "long":
                return "int64_t";
            case "float":
                return "float";
            case "double":
                return "double";
            default:
                return null;
        }
    }

    static String getCXXTypeName(BuiltinType type) {
        return getCXXTypeName(getJavaTypeName(type));
    }

    static TypeName getJavaTypeName(BuiltinType type) {
        BuiltinType.Kind kind = type.getKind();
        switch (kind) {
            case Void:
                return TypeName.VOID;
            case Bool:
                return TypeName.BOOLEAN;
            case Char8:
            case Char_U:
            case UChar:
            case Char_S: // N.B. "char" will has kind Char_S
                return TypeName.CHAR;
            case SChar:
                return TypeName.BYTE;
            case UShort:
            case Short:
            case WChar_S:
            case WChar_U:
            case Char16:
                return TypeName.SHORT;
            case UInt:
            case Int:
            case Char32:
                return TypeName.INT;
            case ULong:
            case Long:
            case ULongLong:
            case LongLong:
                return TypeName.LONG;
            case Float:
                return TypeName.FLOAT;
            case Double:
                return TypeName.DOUBLE;
            default:
                return null;
        }
    }

    void buildParamTypes(MethodSpec.Builder methodBuilder, FunctionDecl functionDecl) {
        // TODO:
        if (functionDecl.getDescribedFunctionTemplate() != null) {
            throw unsupportedAST("TODO: function template is not supported yet");
        }
        int numParams = functionDecl.getNumParams();
        for (int i = 0; i < numParams; i++) {
            ParmVarDecl parmVarDecl = functionDecl.getParamDecl(i);
            String parmName;
            if (parmVarDecl.getIdentifier() != null) {
                parmName = parmVarDecl.getIdentifier().getName().toJavaString();
            } else {
                parmName = "arg" + i;
            }
            QualType paramType = parmVarDecl.getTypeSourceInfo().getType();
            FFIType ffiType = typeToFFIType(paramType);
            TypeName javaType = ffiType.javaType;
            TypeName aliased = isAliasedPrimitive(ffiType);
            if (ffiType.isValue() && aliased != null) {
                javaType = aliased;
            }
            ParameterSpec.Builder paramBuilder = ParameterSpec.builder(javaType, parmName);
            if (ffiType.isConst()) {
                paramBuilder.addAnnotation(FFIConst.class);
            }
            if (!enumAsInteger && ffiType.isEnum()) {
                paramBuilder.addAnnotation(CXXValue.class);
            } else if (ffiType.isReference()) {
                paramBuilder.addAnnotation(CXXReference.class);
            } else if (ffiType.isValue() && aliased == null) {
                paramBuilder.addAnnotation(CXXValue.class);
            }
            if (ffiType.needFFITypeAlias()) {
                paramBuilder.addAnnotation(getFFITypeAlias(ffiType));
            }
            methodBuilder.addParameter(paramBuilder.build());
        }
    }

    void generateRecord(RecordDecl recordDecl) {
        recordDecl = recordDecl.getDefinition();
        if (!isSupportedRecordDecl(recordDecl)) {
            return;
        }

        if (!(recordDecl.isStruct() || recordDecl.isUnion())) {
            throw unsupportedAST("Not a struct or union");
        }
        generateFFIPointer(recordDecl);
    }

    void generateCXXRecord(CXXRecordDecl cxxRecordDecl) {
        cxxRecordDecl = cxxRecordDecl.getDefinition();
        if (!isSupportedCXXRecordDecl(cxxRecordDecl)) {
            return;
        }

        if (!(cxxRecordDecl.isStruct() || cxxRecordDecl.isUnion() || cxxRecordDecl.isClass())) {
            throw unsupportedAST("Not a struct union or class");
        }
        generateFFIPointer(cxxRecordDecl);
    }

    void generateClassTemplate(ClassTemplateDecl classTemplateDecl) {
        generate(classTemplateDecl.getTemplatedDecl());
    }

    void generateClassTemplateSpecialization(ClassTemplateSpecializationDecl specializationDecl) {
        ClassTemplateDecl templateDecl = specializationDecl.getSpecializedTemplate();
        generate(templateDecl);
        TemplateName templateName = TemplateName.create(templateDecl);
        QualType specializationType = specializationDecl.getASTContext()
                .getCanonicalTemplateSpecializationType(
                        templateName, specializationDecl.getTemplateArgs().asArray());
        checkTemplateInstantiation(typeToFFIType(specializationType));
    }

    private void generateFFIPointer(RecordDecl recordDecl) {
        TypeGen typeGen = getOrCreateTypeGen(recordDecl);
        TypeSpec.Builder classBuilder = typeGen.builder;
        generateFields(recordDecl, classBuilder);

        addCXXHeader(typeGen);
        // mark successful
        typeGen.succ();
    }

    private void generateFFIPointer(CXXRecordDecl cxxRecordDecl) {
        ClassName className = getJavaClassName(cxxRecordDecl);
        if (exclude(className.canonicalName())) {
            return;
        }
        TypeGen typeGen = getOrCreateTypeGen(className, cxxRecordDecl);
        TypeSpec.Builder classBuilder = typeGen.builder;
        List<TypeVariableName> typeVariableNames = getJavaTypeVariablesInContext(cxxRecordDecl.getDeclContext());
        getJavaTypeVariablesOnDecl(cxxRecordDecl, typeVariableNames);
        {
            if (cxxRecordDecl.getNumBases() > 0) {
                // build base class
                CXXRecordDecl.base_class_iterator begin = cxxRecordDecl.bases_begin();
                CXXRecordDecl.base_class_iterator end = cxxRecordDecl.bases_end();
                for (; begin.notEquals(end); begin.next()) {
                    try {
                        FFIType baseFFIType = typeToFFIType(begin.get().getType());
                        // ensure the baseType has been generated
                        generateValue(baseFFIType);
                        addSuperinterface(classBuilder, baseFFIType);

                        // merge the methods inside the base class
                        TypeGen baseTypeGen = getTypeGen(baseFFIType);
                        if (baseTypeGen != null) {
                            typeGen.addMethods(baseTypeGen.getMethods());
                        }
                    } catch (UnsupportedASTException e) {
                        // ignore this super if we cannot generate it
                    }
                }
            }
            {
                if (cxxRecordDecl.hasUserDeclaredDestructor()) {
                    CXXDestructorDecl destructorDecl = cxxRecordDecl.getDestructor();
                    if (destructorDecl.getAccess().isPublicOrNone()) {
                        classBuilder.addSuperinterface(ClassName.get(CXXPointer.class));
                    } else {
                        classBuilder.addSuperinterface(ClassName.get(FFIPointer.class));
                    }
                } else {
                    classBuilder.addSuperinterface(ClassName.get(FFIPointer.class));
                }
            }
        }
        // build ctors
        generateCtorMethods(cxxRecordDecl, typeGen, typeVariableNames);
        // build fields
        generateFields(cxxRecordDecl, classBuilder);
        // build methods
        List<CXXMethodDecl> staticMethods = new ArrayList<>();
        generateMethods(cxxRecordDecl, typeGen, staticMethods);
        // build static methods
        generateStaticMethods(cxxRecordDecl, typeGen, staticMethods, typeVariableNames);

        // associate the type variables
        associateTypeParameters(typeGen, typeVariableNames);

        addCXXHeader(typeGen);
        // mark successful
        typeGen.succ();
    }

    private boolean isJavaObjectFinal(String name, String desc) {
        switch (name) {
            case "wait":
                return desc.equals("") || desc.equals("J") || desc.equals("JI");
            case "notify":
                return desc.equals("");
            case "notifyAll":
                return desc.equals("");
            case "getClass":
                return desc.equals("");
        }
        return false;
    }

    private void generateFields(RecordDecl recordDecl, TypeGen typeGen) {
        generateFields(recordDecl, typeGen.builder);
    }

    private void generateFields(RecordDecl recordDecl, TypeSpec.Builder classBuilder) {
        if (!recordDecl.isCompleteDefinition()) {
            return;
        }
        RecordDecl.field_iterator begin = recordDecl.field_begin();
        RecordDecl.field_iterator end = recordDecl.field_end();
        for (; begin.notEquals(end); begin.next()) {
            FieldDecl fieldDecl = begin.get();
            if (!fieldDecl.getAccess().isPublicOrNone()) {
                continue;
            }
            if (fieldDecl.isImplicit()) {
                continue;
            }
            try {
                String name = fieldDecl.getIdentifier().getName().toJavaString();
                String using = name;
                boolean renamed = false;
                if (isJavaObjectFinal(name, "")) {
                    using = "_" + name;
                    renamed = true;
                }
                FFIType ffiType = typeToFFIType(fieldDecl.getTypeSourceInfo().getType());

                TypeName javaType = ffiType.javaType;
                TypeName alias = isAliasedPrimitive(ffiType.cxxType);
                if (ffiType.isValue() && alias != null) {
                    javaType = alias;
                }
                MethodSpec.Builder setter = MethodSpec.methodBuilder(using).addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT).addAnnotation(FFISetter.class);
                ParameterSpec.Builder paramBuilder = ParameterSpec.builder(javaType, "value");
                MethodSpec.Builder getter = MethodSpec.methodBuilder(using).addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT).addAnnotation(FFIGetter.class);
                getter.returns(javaType);
                setter.returns(TypeName.VOID);
                if (renamed) {
                    getter.addAnnotation(getFFINameAlias(name));
                    setter.addAnnotation(getFFINameAlias(name));
                }
                if (!enumAsInteger && ffiType.isEnum()) {
                    getter.addAnnotation(CXXValue.class);
                    paramBuilder.addAnnotation(CXXValue.class);
                } else if (ffiType.isReference() || (ffiType.isValue() && alias == null)) {
                    getter.addAnnotation(CXXReference.class);
                    paramBuilder.addAnnotation(CXXReference.class);
                }
                if (ffiType.needFFITypeAlias()) {
                    getter.addAnnotation(getFFITypeAlias(ffiType));
                    paramBuilder.addAnnotation(getFFITypeAlias(ffiType));
                }
                setter.addParameter(paramBuilder.build());
                TypeSpec.Builder addTo = classBuilder;
                if (fieldDecl.isMutable() && allowCopyAssignment(ffiType)) classBuilder.addMethod(setter.build());
                classBuilder.addMethod(getter.build());
            } catch (UnsupportedASTException e) {
                // simply ignore the field
            }
        }
    }

    private void generateCtorMethods(CXXRecordDecl cxxRecordDecl, TypeGen typeGen, List<TypeVariableName> typeVariableNames) {
        TypeSpec.Builder factoryClassBuilder = null;
        if (!cxxRecordDecl.isCompleteDefinition() || cxxRecordDecl.isAbstract()) {
            return;
        }
        // build constructors
        CXXRecordDecl.ctor_iterator begin = cxxRecordDecl.ctor_begin();
        CXXRecordDecl.ctor_iterator end = cxxRecordDecl.ctor_end();
        TypeName returnType = typeGen.className;
        if (!typeVariableNames.isEmpty()) {
            returnType = ParameterizedTypeName.get(typeGen.className, typeVariableNames.stream().toArray(TypeName[]::new));
        }
        Set<String> methods = new HashSet<>();
        for (; begin.notEquals(end); begin.next()) {
            try {
                CXXConstructorDecl ctorDecl = begin.get();
                if (!ctorDecl.getAccess().isPublicOrNone()) {
                    continue;
                }
                if (!ctorDecl.isUserProvided()) {
                    continue;
                }
                if (ctorDecl.isDeleted()) {
                    continue;
                }
                if (ctorDecl.isMoveConstructor()) {
                    // TODO: move factor needs special handling, see std::move
                    continue;
                }

                // generate a method declaration in the "Library" interface.
                MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("create")
                        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT);
                buildReturnAndParamTypes(methodBuilder, returnType, ctorDecl);

                if (factoryClassBuilder == null) {
                    factoryClassBuilder = typeGen.getFactoryBuilder();
                }
                MethodSpec methodSpec = methodBuilder.build();
                if (!methods.add(typeGen.generateMethodTag(methodSpec.parameters))) {
                    continue;
                }
                factoryClassBuilder.addMethod(methodSpec);
            }
            catch (UnsupportedASTException e) {
            }
        }
    }

    private void generateMethods(CXXRecordDecl cxxRecordDecl, TypeGen typeGen, List<CXXMethodDecl> staticMethods) {
        if (!cxxRecordDecl.isCompleteDefinition()) {
            return;
        }
        // build methods
        CXXRecordDecl.method_iterator begin = cxxRecordDecl.method_begin();
        CXXRecordDecl.method_iterator end = cxxRecordDecl.method_end();
        for (; begin.notEquals(end); begin.next()) {
            try {
                CXXMethodDecl methodDecl = begin.get();
                if (!methodDecl.getAccess().isPublicOrNone()) {
                    continue;
                }
                if (!methodDecl.isUserProvided()) {
                    continue;
                }
                if (methodDecl.isDeleted() || methodDecl.isPure()) {
                    continue;
                }
                if (Decl.Kind.CXXConstructor.equals(methodDecl.getDeclKind())) {
                    continue;
                }
                if (Decl.Kind.CXXDestructor.equals(methodDecl.getDeclKind())) {
                    continue;
                }
                if (Decl.Kind.CXXConversion.equals(methodDecl.getDeclKind())) {
                    continue;
                }
                if (RefQualifierKind.RValue.equals(methodDecl.getRefQualifier())) {
                    // rvalue ref qualifier is not supported
                    Logger.warn("the rvalue ref qualifier on method is not supported: %s",
                            methodDecl.getNameAsString().toJavaString());
                    continue;
                }
                if (methodDecl.isStatic()) {
                    if (staticMethods != null) {
                        staticMethods.add(methodDecl);
                    }
                    continue;
                }
                MethodSpec.Builder methodBuilder;
                String methodName;
                if (methodDecl.isOverloadedOperator()) {
                    OverloadedOperatorKind kind = methodDecl.getOverloadedOperator();
                    methodName = kind.toString();
                    methodBuilder = MethodSpec.methodBuilder(methodName);
                    methodBuilder.addAnnotation(AnnotationSpec.builder(CXXOperator.class)
                            .addMember("value", "$S", kind.getOperatorSpelling()).build());
                    continue; // TODO: skip operator
                } else {
                    methodName = methodDecl.getNameAsString().toJavaString();
                    methodBuilder = MethodSpec.methodBuilder(methodName);
                }
                methodBuilder.addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT);
                TypeName returnType = buildReturnAndParamTypes(methodBuilder, methodDecl);

                String methodTag = typeGen.generateMethodTag(methodBuilder.parameters);
                String methodNameCoded = methodName;
                int methodNameIndex = 1;
                while (!typeGen.addMethod(methodNameCoded, methodTag)) {
                    methodNameCoded = methodName + "_" + methodNameIndex++;
                }
                methodBuilder.setName(methodNameCoded);
                methodBuilder.returns(returnType);  // `setName` will clear the `returns`.
                if (!methodName.equals(methodNameCoded)) {
                    methodBuilder.addAnnotation(AnnotationSpec.builder(FFINameAlias.class)
                            .addMember("value", "$S", methodName)
                            .build());
                }

                MethodSpec methodSpec = methodBuilder.build();
                if (exclude(typeGen.className.canonicalName(), methodName)) {
                    continue;
                }
                typeGen.builder.addMethod(methodSpec);
            } catch (ExcludedException e) {
            } catch (UnsupportedASTException e) {
                Logger.error("Failed to generate for method %s::%s, %s",
                        typeGen.className.canonicalName(), begin.get().getNameAsString(), e.getMessage());
                if (Logger.verbose()) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void generateStaticMethods(CXXRecordDecl cxxRecordDecl, TypeGen typeGen, List<CXXMethodDecl> staticMethods, List<TypeVariableName> typeVariableNames) {
        if (!cxxRecordDecl.isCompleteDefinition()) {
            return;
        }
        if (staticMethods.isEmpty()) {
            return;
        }
        // static methods in template classes are not supported, see GH-24, add a logger
        if (!typeVariableNames.isEmpty()) {
            Logger.warn("TODO: static methods inside template clasess are not supported: %s", typeGen.className);
            return;
        }
        String cxxName = getCXXName(cxxRecordDecl);
        TypeSpec.Builder libraryBuilder = typeGen.getLibraryBuilder(cxxName);
        for (CXXMethodDecl methodDecl : staticMethods) {
            String methodName = methodDecl.getNameAsString().toJavaString();
            try {
                MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(methodName);
                methodBuilder.addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT);
                {
                    FFIType ffiType = typeToFFIType(methodDecl.getReturnType());
                    if (ffiType.isTemplateVariableDependent()) {
                        throw unsupportedAST("TODO: static methods with type variables are not supported.");
                    }

                    int numParams = methodDecl.getNumParams();
                    for (int i = 0; i < numParams; i++) {
                        ParmVarDecl parmVarDecl = methodDecl.getParamDecl(i);
                        FFIType paramFFIType = typeToFFIType(parmVarDecl.getTypeSourceInfo().getType());
                        if (paramFFIType.isTemplateVariableDependent()) {
                            throw unsupportedAST("TODO: static methods with type variables are not supported.");
                        }
                    }
                }

                TypeName returnType = buildReturnAndParamTypes(methodBuilder, methodDecl);

                String methodTag = typeGen.generateMethodTag(methodBuilder.parameters);
                String methodNameCoded = methodName;
                int methodNameIndex = 1;
                while (!typeGen.addMethod(methodNameCoded, methodTag)) {
                    methodNameCoded = methodName + "_" + methodNameIndex++;
                }
                methodBuilder.setName(methodNameCoded);
                methodBuilder.returns(returnType);  // `setName` will clear the `returns`.
                if (!methodName.equals(methodNameCoded)) {
                    methodBuilder.addAnnotation(AnnotationSpec.builder(FFINameAlias.class)
                            .addMember("value", "$S", methodName)
                            .build());
                }

                MethodSpec methodSpec = methodBuilder.build();
                if (exclude(typeGen.className.canonicalName(), methodName)) {
                    continue;
                }
                libraryBuilder.addMethod(methodSpec);
            } catch (UnsupportedASTException e) {
                Logger.debug("Failed to generate static method %s::%s: %s", cxxName, methodName, e);
            }
        }
    }

    private void associateTypeParameters(TypeGen typeGen, List<TypeVariableName> typeVariableNames) {
        TypeSpec.Builder classBuilder = typeGen.builder;
        classBuilder.addTypeVariables(typeVariableNames);

        TypeSpec.Builder factoryClassBuilder = typeGen.getFactoryBuilderOrNull();
        if (factoryClassBuilder != null) {
            factoryClassBuilder.addTypeVariables(typeVariableNames);
        }
    }

    private boolean allowCopyAssignment(FFIType ffiType) {
        return allowCopyAssignment(ffiType.cxxType.getCanonicalTypeInternal().getTypePtr());
    }

    private boolean allowCopyAssignment(Type type) {
        TypeClass typeClass = type.getTypeClass();
        CXXRecordDecl cxxRecordDecl = null;
        switch (typeClass) {
            case Record: {
                RecordType recordType = RecordType.dyn_cast(type);
                RecordDecl recordDecl = recordType.getDecl();
                cxxRecordDecl = CXXRecordDecl.dyn_cast(recordDecl);
                break;
            }
            case TemplateSpecialization: {
                TemplateSpecializationType templateSpecializationType = TemplateSpecializationType.dyn_cast(type);
                TemplateDecl templateDecl = templateSpecializationType.getTemplateName().getAsTemplateDecl();
                ClassTemplateDecl classTemplateDecl = ClassTemplateDecl.dyn_cast(templateDecl);
                if (classTemplateDecl != null) {
                    cxxRecordDecl = classTemplateDecl.getTemplatedDecl();
                }
                break;
            }
        }
        if (cxxRecordDecl != null && !cxxRecordDecl.hasSimpleCopyAssignment()) {
            return false;
        }
        return true;
    }

    private void addSuperinterface(TypeSpec.Builder classBuilder, QualType superType) {
        FFIType superFFIType = typeToFFIType(superType);
        addSuperinterface(classBuilder, superFFIType);
    }

    private void addSuperTemplate(TypeSpec.Builder classBuilder, FFIType superFFIType) {
        TypeClass typeClass = superFFIType.cxxType.getTypeClass();
        switch (typeClass) {
            case TemplateSpecialization: {
                if (!(superFFIType.javaType instanceof ParameterizedTypeName)) {
                    return; // a template specialization with zero arguments
                }
                ParameterizedTypeName typeName = (ParameterizedTypeName) superFFIType.javaType;
                AnnotationSpec.Builder templateBuilder = AnnotationSpec.builder(CXXTemplate.class);
                for (TypeName typeArg : typeName.typeArguments) {
                    templateBuilder.addMember("java", "$S", typeArg.toString().replace(" ", ""));
                }
                TemplateSpecializationType templateSpecializationType = TemplateSpecializationType.dyn_cast(superFFIType.cxxType);
                int size = templateSpecializationType.getNumArgs();
                for (int i = 0; i < size; i++) {
                    templateBuilder.addMember("cxx", "$S", getCXXName(templateSpecializationType.getArg(i).getAsType()));
                }
                classBuilder.addAnnotation(AnnotationSpec.builder(CXXSuperTemplate.class)
                        .addMember("type", "$S", typeName.rawType.toString())
                        .addMember("template", "$L", templateBuilder.build())
                        .build());
                break;
            }
            case Record:
                break;
            case Elaborated:
                addSuperTemplate(classBuilder, typeToFFIType(ElaboratedType.dyn_cast(superFFIType.cxxType).desugar()));
                break;
            default:
                throw unsupportedAST("Unsupported type: " + superFFIType);
        }
    }

    private void addSuperinterface(TypeSpec.Builder classBuilder, FFIType superFFIType) {
        classBuilder.addSuperinterface(superFFIType.javaType);
        // disable the super template, see GH-23
        // addSuperTemplate(classBuilder, superFFIType);
        Logger.warn("TODO: @CXXSuperTemplate doesn't work");
    }

    List<NamedDecl> collectTypeParameters(TemplateParameterList templateParameterList) {
        if (templateParameterList.hasParameterPack()) {
            throw unsupportedAST("TODO: template has parameter pack");
        }
        List<NamedDecl> array = new ArrayList<>();
        int size = templateParameterList.getMinRequiredArguments();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                NamedDecl param = templateParameterList.getParam(i);
                if (param.getKind() != Decl.Kind.TemplateTypeParm && param.getKind() != Decl.Kind.NonTypeTemplateParm) {
                    if (!hasDefaultArgument(param)) {
                        throw unsupportedAST(String.format(
                                "TODO: non type template parameter (without default argument) is not supported: %s, %s",
                                param.getKind(), param.dump()));
                    }
                    continue;
                }
                if (param.getIdentifier() == null) {
                    throw unsupportedAST("TODO: type parameter has no name: " + param.dump());
                }
                array.add(param);
            }
        }
        return array;
    }

    private void tryInstantiateTemplateSpecialization(CXXRecordDecl cxxRecordDecl) {
        if (!cxxRecordDecl.isCompleteDefinition() &&
                cxxRecordDecl.getDeclKind().equals(Decl.Kind.ClassTemplateSpecialization)) {
            tryInstantiateTemplateSpecialization(ClassTemplateSpecializationDecl.dyn_cast(cxxRecordDecl));
        }
    }

    private void tryInstantiateTemplateSpecialization(ClassTemplateSpecializationDecl templateSpecializationDecl) {
        Sema sema = this.getCurrentSema();
        if (sema != null && templateSpecializationDecl != null) {
            TemplateSpecializationKind specializationKind = templateSpecializationDecl.getSpecializationKind();
            if (specializationKind.equals(TemplateSpecializationKind.TSK_ImplicitInstantiation) ||
                    specializationKind.equals(TemplateSpecializationKind.TSK_Undeclared)) {
                sema.InstantiateClassTemplateSpecialization(
                        templateSpecializationDecl.getLocation(),
                        templateSpecializationDecl,
                        TemplateSpecializationKind.TSK_ExplicitInstantiationDefinition, true);
            }
        }
    }

    private boolean hasDefaultArgument(NamedDecl param) {
        Decl.Kind kind = param.getKind();
        switch (kind) {
            case TemplateTypeParm: {
                return TemplateTypeParmDecl.dyn_cast(param).hasDefaultArgument();
            }
            case NonTypeTemplateParm: {
                return NonTypeTemplateParmDecl.dyn_cast(param).hasDefaultArgument();
            }
            case TemplateTemplateParm: {
                return TemplateTemplateParmDecl.dyn_cast(param).hasDefaultArgument();
            }
            default:
                throw new IllegalStateException("TODO: unknown template parameter: " + param);
        }
    }

    /**
     * context should not contain any TagDecl.
     * @param context
     * @return
     */
    private List<String> getNames(DeclContext context) {
        LinkedList<String> names = new LinkedList<>();
        outer: while (true) {
            Decl contextDecl = DeclContext.cast(context);
            Decl.Kind kind = contextDecl.getKind();
            switch (kind) {
                case Namespace: {
                    NamespaceDecl current = NamespaceDecl.dyn_cast(contextDecl);
                    if (!current.isAnonymousNamespace() && !current.isInline()) {
                        names.addFirst(current.getIdentifier().getName().toJavaString());
                    }
                    break;
                }
                case Record:
                case CXXRecord:
                case Enum: {
                    NamedDecl namedDecl = NamedDecl.dyn_cast(contextDecl);
                    if (namedDecl != null) {
                        names.addFirst(namedDecl.getNameAsString().toJavaString());
                    }
                    break;
                }
                default:
                    break;
            }
            context = context.getParent();
            if (context == null) {
                break;
            }
        }
        return names;
    }

    private String prependRootPackage(String packageName) {
        if (rootPackage == null || rootPackage.isEmpty()) {
            return packageName;
        }
        return rootPackage + "." + packageName;
    }

    private String getPackageName(DeclContext declContext) {
        String packageName = String.join(".", getNames(declContext));
        return prependRootPackage(packageName);
    }

    private boolean isSupportedCXXRecordDecl(CXXRecordDecl recordDecl) {
        return isSupportedRecordDecl(recordDecl);
    }


    private boolean isSupportedRecordDecl(RecordDecl recordDecl) {
        return isSupportedTagDecl(recordDecl);
    }

    private boolean isSupportedTagDecl(TagDecl tagDecl) {
        if (tagDecl == null) {
            return false;
        }
        if (!tagDecl.isThisDeclarationADefinition()) {
            return false;
        }
        if (tagDecl.getIdentifier() == null) {
            return false; // no anonymous record supported
        }
        if (!tagDecl.getAccess().isPublicOrNone()) {
            return false; // not a public decl;
        }
        NestedNameSpecifier qualifier = tagDecl.getQualifier();
        if (qualifier != null) {
            throw unsupportedAST("TODO: handle nested name specifier");
        }
        return tagDecl.isStruct() || tagDecl.isUnion() || tagDecl.isClass() || tagDecl.isEnum();
    }

    private void getJavaTypeVariablesOnDecl(Decl decl, List<TypeVariableName> variables) {
        Decl.Kind declKind = decl.getKind();
        TemplateParameterList templateParameterList = null;
        // collect current template variables
        switch (declKind) {
            case CXXRecord: {
                CXXRecordDecl recordDecl = CXXRecordDecl.dyn_cast(decl);
                ClassTemplateDecl classTemplateDecl = recordDecl.getDescribedClassTemplate();
                if (classTemplateDecl != null) {
                    templateParameterList = classTemplateDecl.getTemplateParameters();
                }
                break;
            }
            case TypeAlias: {
                TypeAliasDecl typeAliasDecl = TypeAliasDecl.dyn_cast(decl);
                TypeAliasTemplateDecl typeAliasTemplateDecl = typeAliasDecl.getDescribedAliasTemplate();
                if (typeAliasTemplateDecl != null) {
                    templateParameterList = typeAliasTemplateDecl.getTemplateParameters();
                }
                break;
            }
        }
        if (templateParameterList != null) {
            for (NamedDecl param : collectTypeParameters(templateParameterList)) {
                String name = param.getIdentifier().getName().toJavaString();
                if (debug) {
                    for (TypeVariableName variable: variables) {
                        if (variable.name.equals(name)) {
                            throw unsupportedAST("TODO: duplicate type variable is not supported: " + name);
                        }
                    }
                }
                variables.add(TypeVariableName.get(name));
            }
        }
    }

    private void getJavaTypeVariablesInContext(DeclContext context, List<TypeVariableName> variables) {
        Decl contextDecl = DeclContext.cast(context);
        Decl.Kind contextKind = contextDecl.getKind();
        DeclContext parentContext = context.getParent();
        switch (contextKind) {
            case Enum:
            case Record:
            case ClassTemplateSpecialization:
            case Block:
            case Namespace: {
                getJavaTypeVariablesInContext(parentContext, variables);
                return;
            }
            case CXXRecord: {
                getJavaTypeVariablesInContext(parentContext, variables);
                getJavaTypeVariablesOnDecl(contextDecl, variables);
                return;
            }
            default: {
                Logger.verbose("Type variables in context '%s' are not processed", contextKind);
                return;
            }
        }
    }

    private List<TypeVariableName> getJavaTypeVariablesInContext(DeclContext context) {
        List<TypeVariableName> variables = new ArrayList<>();
        getJavaTypeVariablesInContext(context, variables);
        return variables;
    }

    private String getCXXContextName(DeclContext context) {
        Decl contextDecl = DeclContext.cast(context);
        Decl.Kind contextKind = contextDecl.getKind();
        DeclContext parentContext = context.getParent();
        switch (contextKind) {
            case Enum:
            case Record:
            case Namespace: {
                String parentName = getCXXContextName(parentContext);
                {
                    // skip inline namespace
                    NamespaceDecl namespaceDecl = NamespaceDecl.dyn_cast(contextDecl);
                    if (namespaceDecl != null && (namespaceDecl.isAnonymousNamespace() || namespaceDecl.isInline())) {
                        return parentName;
                    }
                }
                NamedDecl namedDecl = (NamedDecl) contextDecl;
                if (namedDecl.getIdentifier() != null) {
                    String name = namedDecl.getIdentifier().getName().toJavaString();

                    // ensure the context class are generated
                    if (contextKind.equals(Decl.Kind.Record)) {
                        RecordDecl recordDecl = RecordDecl.dyn_cast(contextDecl);
                        typeToFFIType(recordDecl.getTypeForDecl().getCanonicalTypeInternal());
                    }

                    if (parentName.isEmpty()) {
                        return name;
                    }
                    return parentName + "::" + name;
                } else {
                    return parentName;
                }
            }
            case CXXRecord: {
                // process nested declartions, see GH-20.
                String parentName = getCXXContextName(parentContext);
                NamedDecl namedDecl = (NamedDecl) contextDecl;
                if (namedDecl.getIdentifier() != null) {
                    String name = namedDecl.getIdentifier().getName().toJavaString();
                    CXXRecordDecl recordDecl = CXXRecordDecl.dyn_cast(contextDecl);

                    // ensure the context class are generated
                    typeToFFIType(recordDecl.getTypeForDecl().getCanonicalTypeInternal());

                    ClassTemplateDecl classTemplateDecl = recordDecl.getDescribedClassTemplate();
                    if (classTemplateDecl != null) {
                        List<NamedDecl> templateTypeParmDeclList = collectTypeParameters(classTemplateDecl.getTemplateParameters());
                        if (!templateTypeParmDeclList.isEmpty()) {
                            name = name + "<" + String.join(",", Collections.nCopies(templateTypeParmDeclList.size(), "%s")) + ">";
                        }
                    }
                    if (parentName.isEmpty()) {
                        return name;
                    }
                    return parentName + "::" + name;
                } else {
                    return parentName;
                }
            }
            case TranslationUnit:
                return "";
            case ExternCContext:
                return getCXXContextName(parentContext);
            case ClassTemplateSpecialization:
                String parentName = getCXXContextName(parentContext);
                ClassTemplateSpecializationDecl templateSpecializationDecl = ClassTemplateSpecializationDecl.dyn_cast(contextDecl);
                ClassTemplateDecl classTemplateDecl = templateSpecializationDecl.getSpecializedTemplate();

                // ensure the context class are generated
                CXXRecordDecl recordDecl = classTemplateDecl.getTemplatedDecl();
                if (recordDecl != null) {
                    typeToFFIType(recordDecl.getTypeForDecl().getCanonicalTypeInternal());
                }

                TemplateArgumentList templateArgumentList = templateSpecializationDecl.getTemplateArgs();
                List<String> args = new ArrayList<>();
                for (int i = 0; i < templateArgumentList.size(); ++i) {
                    TemplateArgument templateArgument = templateArgumentList.get(i);
                    switch (templateArgument.getKind()) {
                        case Type: {
                            args.add(getCXXNameInternal(templateArgument.getAsType(), false));
                            break;
                        }
                        case Integral: {
                            args.add(String.valueOf(templateArgument.getAsIntegral().getExtValue()));
                            break;
                        }
                        default: {
                            throw unsupportedAST("TODO: unsupported template argument type: " + templateArgument.getKind());
                        }
                    }
                }
                String name = classTemplateDecl.getName().toString();
                return parentName + "::" + name + "<" + String.join(", ", args) + ">";
            default:
                throw unsupportedAST("Not a supported DeclContext: " + contextDecl);
        }
    }

    private String getCXXName(FFIType ffiType) {
        return getCXXName(ffiType.cxxQualType);
    }

    private String getCXXName(FFIType ffiType, boolean addConst) {
        return getCXXName(ffiType.cxxQualType, addConst);
    }

    private String getCXXName(QualType type) {
        return getCXXName(type, false);
    }

    private String getCXXName(QualType type, boolean addConst) {
        String cxxName = getCXXNameInternal(type, addConst);
        if (addConst && type.isConstQualified()) {
            return "const " + cxxName;
        }
        return cxxName;
    }

    private String getCXXNameInternal(QualType qualType, boolean addConst) {
        QualType simplifyQualType = simplifyCXXType(qualType);
        Type type = simplifyQualType.getTypePtr();
        TypeClass typeClass = type.getTypeClass();
        switch (typeClass) {
            case Builtin: {
                BuiltinType builtinType = (BuiltinType) type;
                BuiltinType.Kind kind = builtinType.getKind();
                switch (kind) {
                    case Bool:
                        return "bool";
                }
                return builtinType.getAsString().toJavaString();
            }
            case Typedef: {
                TypedefType typedefType = (TypedefType) type;
                TypedefNameDecl typedefNameDecl = typedefType.getDecl();
                Type underlyingType = typedefNameDecl.getUnderlyingType().getTypePtr();
                TypeClass underlyingClass = underlyingType.getTypeClass();
                switch (underlyingClass) {
                    case Enum:
                    case Builtin:
                    case Typedef:
                    case Record:
                    case Pointer:
                    case LValueReference:
                    case RValueReference:
                    case Elaborated:  // elaborated type inside typedef
                    case TemplateSpecialization:
                    case DependentName:
                    case SubstTemplateTypeParm:
                    case TemplateTypeParm:
                        return getCXXName(typedefNameDecl);
                    default: {
                        throw unsupportedAST("Unsupported typedef type: " + typeClass + ", underlying " + underlyingClass);
                    }
                }
            }
            case Enum: {
                EnumType enumType = (EnumType) type;
                EnumDecl enumDecl = enumType.getDecl();
                return getCXXName(enumDecl);
            }
            case Record: {
                RecordType recordType = (RecordType) type;
                RecordDecl recordDecl = recordType.getDecl();
                return getCXXName(recordDecl);
            }
            case Pointer: {
                return getCXXName(PointerType.dyn_cast(type).getPointeeType(), addConst) + "*";
            }
            case LValueReference: {
                return getCXXName(LValueReferenceType.dyn_cast(type).getPointeeType(), addConst) + "&";
            }
            case RValueReference: {
                return getCXXName(RValueReferenceType.dyn_cast(type).getPointeeType(), addConst) + "&&";
            }
            case Elaborated: {
                return getCXXName(ElaboratedType.dyn_cast(type).desugar(), addConst);
            }
            case TemplateSpecialization: {
                TemplateSpecializationType templateSpecializationType = TemplateSpecializationType.dyn_cast(type);
                StringBuilder sb = new StringBuilder();
                sb.append(getCXXName(templateSpecializationType.getTemplateName().getAsTemplateDecl()));
                int size = templateSpecializationType.getNumArgs();
                if (size > 0) {
                    sb.append("<");
                    sb.append(getCXXName(templateSpecializationType.getArg(0).getAsType(), addConst));
                    for (int i = 1; i < size; i++) {
                        sb.append(",");
                        sb.append(getCXXName(templateSpecializationType.getArg(i).getAsType(), addConst));
                    }
                    sb.append(">");
                }
                return sb.toString();
            }
            case TemplateTypeParm: {
                TemplateTypeParmType templateTypeParmType = TemplateTypeParmType.dyn_cast(type);
                return templateTypeParmType.getIdentifier().getName().toJavaString();
            }
            case SubstTemplateTypeParm: {
                SubstTemplateTypeParmType substTemplateTypeParmType = SubstTemplateTypeParmType.dyn_cast(type);
                return getCXXNameInternal(substTemplateTypeParmType.getReplacementType(), addConst);
            }
            default:
                throw unsupportedAST("Unsupported CXX type: " + type);
        }
    }

    private String getLocalCXXName(NamedDecl namedDecl) {
        return namedDecl.getIdentifier().getName().toJavaString();
    }

    private String getCXXName(NamedDecl namedDecl) {
        String simpleName = getLocalCXXName(namedDecl);
        DeclContext context = namedDecl.getDeclContext();
        String contextName = getCXXContextName(context);
        if (!contextName.isEmpty()) {
            return contextName + "::" + simpleName;
        }
        return simpleName;
    }

    private String getCXXName(RecordDecl recordDecl) {
        Decl.Kind declKind = recordDecl.getKind();
        switch (declKind) {
            case Record: {
                return getCXXName((NamedDecl) recordDecl);
            }
            case CXXRecord: {
                CXXRecordDecl cxxRecordDecl = CXXRecordDecl.dyn_cast(recordDecl);
                String name = getCXXName((NamedDecl) cxxRecordDecl);
                if (cxxRecordDecl.getDescribedClassTemplate() != null) {
                    throw unsupportedAST("Unspecialized template class cannot be a concrete type: " + name);
                }
                return name;
            }
            case ClassTemplateSpecialization: {
                ClassTemplateSpecializationDecl classTemplateSpecializationDecl = ClassTemplateSpecializationDecl.dyn_cast(recordDecl);
                CXXRecordDecl cxxRecordDecl = classTemplateSpecializationDecl.getSpecializedTemplate().getTemplatedDecl();
                StringBuilder name = new StringBuilder(getCXXName((NamedDecl) cxxRecordDecl));
                TemplateArgumentList templateArgumentList = classTemplateSpecializationDecl.getTemplateArgs();
                name.append("<");
                for (int i = 0; i < templateArgumentList.size(); ++i) {
                    if (i > 0) {
                        name.append(", ");
                    }
                    TemplateArgument templateArgument = templateArgumentList.get(i);
                    name.append(getCXXNameInternal(templateArgument.getAsType(), false));
                }
                name.append(">");
                return name.toString();
            }
            default: {
                throw unsupportedAST("Unsupported record declaration: " + declKind);
            }
        }
    }

    private TypeName tryApplyTypeParameters(RecordDecl recordDecl, ClassName className) {
        switch (recordDecl.getKind()) {
            case ClassTemplateSpecialization: {
                ClassTemplateSpecializationDecl templateSpecializationDecl = ClassTemplateSpecializationDecl.dyn_cast(recordDecl);
                return getJavaTypeForTemplateSpecializationType(templateSpecializationDecl);
            }
            default: {
                return className;
            }
        }
    }

    private ClassName getJavaClassName(NamedDecl namedDecl) {
        if (namedDecl.getIdentifier() == null) {
            throw unsupportedAST("Cannot get Java class name for anonymous NamedDecl: " + namedDecl.getKind());
        }
        String simpleClassName = namedDecl.getIdentifier().getName().toJavaString();
        DeclContext context = namedDecl.getDeclContext();
        Decl contextDecl = DeclContext.cast(context);
        Decl.Kind contextKind = contextDecl.getKind();
        if (contextKind.equals(Decl.Kind.Enum) || contextKind.equals(Decl.Kind.CXXRecord) || contextKind.equals(Decl.Kind.Record)) {
            TagDecl parentDecl = TagDecl.dyn_cast(contextDecl);
            ClassName parentClassName = getJavaClassName(parentDecl);
            String packageName = parentClassName.packageName();
            List<String> simpleNames = new ArrayList<>(parentClassName.simpleNames());
            String simpleName = simpleNames.remove(0);
            simpleNames.add(simpleClassName);
            return getClassName(packageName, simpleName, simpleNames.toArray(new String[0]));
        } else if (contextKind.equals(Decl.Kind.ClassTemplateSpecialization)) {
            TagDecl parentDecl = TagDecl.dyn_cast(contextDecl);
            ClassName parentClassName = getJavaClassName(parentDecl);

            // generate template specialization as pkg.impl.class.specialization, e.g.,
            // vineyard.impl.converttoarrowtype.ArrayTypeBool
            StringBuilder packageName = new StringBuilder(parentClassName.packageName());

            packageName.append(".impl");
            for (String name: parentClassName.simpleNames()) {
                packageName.append(".");
                packageName.append(name.toLowerCase());
            }

            // Generate a unique name for each template instantiation type.
            StringBuilder instantiation = new StringBuilder(capitalizeCXXName(simpleClassName));
            ClassTemplateSpecializationDecl templateSpecializationDecl = ClassTemplateSpecializationDecl.dyn_cast(contextDecl);
            TemplateArgumentList templateArgumentList = templateSpecializationDecl.getTemplateArgs();
            for (int i = 0; i < templateArgumentList.size(); ++i) {
                TemplateArgument templateArgument = templateArgumentList.get(i);
                switch (templateArgument.getKind()) {
                    case Type: {
                        instantiation.append(capitalizeCXXName(getCXXNameInternal(templateArgument.getAsType(), false)));
                        break;
                    }
                    case Integral: {
                        instantiation.append(templateArgument.getAsIntegral().getExtValue());
                        break;
                    }
                    default: {
                        throw unsupportedAST("TODO: unsupported template argument type: " + templateArgument.getKind());
                    }
                }
            }

            return getClassName(packageName.toString(), instantiation.toString());
        } else {
            String packageName = getPackageName(context);
            return getClassName(packageName, simpleClassName);
        }
    }

    private ClassName getJavaClassName(TypedefNameDecl typedefNameDecl) {
        Type canonicalType = typedefNameDecl.getUnderlyingType().getTypePtr();
        if (typedefNameDecl.getDeclContext().getDeclKind().equals(Decl.Kind.TranslationUnit)) {
            switch (canonicalType.getTypeClass()) {
                case Builtin: {
                    // put builtin types (e.g., size_t), into the `std` namespace.
                    return getClassName(prependRootPackage("std"), typedefNameDecl.getIdentifier().getName().toJavaString());
                }
                default: {
                    return getJavaClassName((NamedDecl) typedefNameDecl);
                }
            }
        } else {
            return getJavaClassName((NamedDecl) typedefNameDecl);
        }
    }

    private TypeGen getTypeGen(ClassName className) {
        return classBuilderMap.get(className.canonicalName());
    }

    private void addTypeGen(ClassName className, TypeGen typeGen) {
        classBuilderMap.put(className.canonicalName(), typeGen);
    }

    private TypeGen getOrCreateTypeGen(TagDecl tagDecl) {
        ClassName className = getJavaClassName(tagDecl);
        return getOrCreateTypeGen(className, tagDecl);
    }

    private TypeGen getOrCreateTypeGen(TypedefNameDecl typedefNameDecl) {
        ClassName className = getJavaClassName(typedefNameDecl);
        return getOrCreateTypeGen(className, typedefNameDecl);
    }

    private TypeGen getOrCreateTypeGen(ClassName className, TagDecl tagDecl) {
        if (!tagDecl.isThisDeclarationADefinition()) {
            throw new IllegalStateException("Must be definition!");
        }
        TypeGen typeGen = getTypeGen(className);
        if (typeGen == null) {
            TypeSpec.Builder builder = createTypeBuilder(className, tagDecl);
            {
                String cxxName = getCXXName(tagDecl);
                builder.addAnnotation(getFFITypeAlias(cxxName));
                if (!tagDecl.isEnum()) {
                    builder.addAnnotation(FFIGen.class);
                }
            }
            typeGen = new TypeGen(className, builder, tagDecl, fileComment);
            addTypeGen(className, typeGen);
        }
        return typeGen;
    }

    private TypeGen getOrCreateTypeGen(ClassName className, TypedefNameDecl typedefNameDecl) {
        TypeGen typeGen = getTypeGen(className);
        if (typeGen == null) {
            TypeSpec.Builder builder = TypeSpec.interfaceBuilder(className).addModifiers(Modifier.PUBLIC, Modifier.STATIC);
            {
                builder.addAnnotation(getFFITypeAlias(getCXXName(typedefNameDecl)));
                builder.addAnnotation(FFIGen.class);
            }
            typeGen = new TypeGen(className, builder, typedefNameDecl, fileComment);
            addTypeGen(className, typeGen);
        }
        return typeGen;
    }

    private TypeSpec.Builder createTypeBuilder(ClassName className, TagDecl tagDecl) {
        if (tagDecl.isStruct() || tagDecl.isClass() || tagDecl.isUnion()) {
            return TypeSpec.interfaceBuilder(className).addModifiers(Modifier.PUBLIC, Modifier.STATIC);
        }
        if (tagDecl.isEnum()) {
            return TypeSpec.enumBuilder(className).addModifiers(Modifier.PUBLIC, Modifier.STATIC);
        }
        throw unsupportedAST("Unsupported record: " + className);
    }

    /**
     * See also StringUtils.capitalize.
     */
    private String capitalizeCXXName(final String name) {
        if (name == null) {
            return null;
        }
        final int strLen = name.length();
        if (strLen == 0) {
            return name;
        }

        StringBuilder builder = new StringBuilder();
        boolean toflip = true;
        final int comma = Character.codePointAt(",", 0);
        final int colon = Character.codePointAt(":", 0);
        final int letter_than = Character.codePointAt("<", 0);
        final int greater_than = Character.codePointAt(">", 0);
        final int underscore = Character.codePointAt("_", 0);
        final int star = Character.codePointAt("*", 0);
        for (int i = 0; i < name.length(); ++i) {
            final int codepoint = name.codePointAt(i);
            if (Character.isWhitespace(codepoint) ||
                    codepoint == comma || codepoint == colon ||
                    codepoint == letter_than || codepoint == greater_than ||
                    codepoint == underscore || codepoint == star) {
                toflip = true;
            } else {
                if (toflip) {
                    builder.append(Character.toChars(Character.toTitleCase(codepoint)));
                    toflip = false;
                } else {
                    builder.append(Character.toChars(codepoint));
                }
            }
        }
        return builder.toString();
    }

    static void dump(String indent, DeclContext context) {
        DeclContext.decl_iterator begin = context.decls_begin();
        DeclContext.decl_iterator end = context.decls_end();
        for (; begin.notEquals(end); begin.next()) {
            Decl decl = begin.get();
            ASTContext astContext = decl.getASTContext();
            if (!astContext.getSourceManager().isInMainFile(decl.getBeginLoc())) {
                continue;
            }
            if (!decl.isExplicitlyDeclaredInMainFile()) {
                continue;
            }
            System.out.format("%s%s", indent, decl.getKind());
            if (decl instanceof CXXRecordDecl) {
                CXXRecordDecl cxxRecordDecl = (CXXRecordDecl) decl;
                System.out.format(" %s", cxxRecordDecl.isThisDeclarationADefinition() ? "Definition" : "");
                System.out.format(" %s", cxxRecordDecl.isInjectedClassName() ? " InjectedClassName" : "");
                if (cxxRecordDecl.getTypeForDecl() != null) {
                    if (cxxRecordDecl.getTypeForDecl().getCanonicalTypeInternal().getBaseTypeIdentifier() != null) {
                        System.out.format(" %s", cxxRecordDecl.getTypeForDecl().getCanonicalTypeInternal().getBaseTypeIdentifier().getName().toJavaString());
                    }
                }
            }
            if (decl instanceof CXXMethodDecl) {
                CXXMethodDecl methodDecl = (CXXMethodDecl) decl;
                System.out.format(" %s", methodDecl.getNameAsString().toString());
            }
            if (decl instanceof NamespaceDecl) {
                NamespaceDecl namespaceDecl = (NamespaceDecl) decl;
                System.out.format(" %s", namespaceDecl.getNameAsString().toString());
            }
            if (decl instanceof ClassTemplateDecl) {
                System.out.println();
                dump("  " + indent, ((ClassTemplateDecl) decl).getTemplatedDecl());
            }

            System.out.println();
            if (decl instanceof DeclContext) {
                dump("  " + indent, (DeclContext) decl);
            }
        }
    }

}
