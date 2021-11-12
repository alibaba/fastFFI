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
import com.alibaba.fastffi.FFIExpr;
import com.alibaba.fastffi.FFIFactory;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFIGenBatch;
import com.alibaba.fastffi.FFIGetter;
import com.alibaba.fastffi.FFILibrary;
import com.alibaba.fastffi.FFIPointer;
import com.alibaba.fastffi.FFISetter;
import com.alibaba.fastffi.FFITypeAlias;
import com.alibaba.fastffi.FFITypeFactory;
import com.alibaba.fastffi.FFITypeRefiner;
import com.alibaba.fastffi.clang.ASTContext;
import com.alibaba.fastffi.clang.ASTUnit;
import com.alibaba.fastffi.clang.BuiltinType;
import com.alibaba.fastffi.clang.CXXConstructorDecl;
import com.alibaba.fastffi.clang.CXXMethodDecl;
import com.alibaba.fastffi.clang.CXXRecordDecl;
import com.alibaba.fastffi.clang.ClassTemplateDecl;
import com.alibaba.fastffi.clang.Decl;
import com.alibaba.fastffi.clang.DeclContext;
import com.alibaba.fastffi.clang.DeclTypeRefiner;
import com.alibaba.fastffi.clang.DependentNameType;
import com.alibaba.fastffi.clang.DirectoryLookup;
import com.alibaba.fastffi.clang.ElaboratedType;
import com.alibaba.fastffi.clang.EnumConstantDecl;
import com.alibaba.fastffi.clang.EnumDecl;
import com.alibaba.fastffi.clang.FieldDecl;
import com.alibaba.fastffi.clang.FunctionDecl;
import com.alibaba.fastffi.clang.HeaderSearch;
import com.alibaba.fastffi.clang.IdentifierInfo;
import com.alibaba.fastffi.clang.LValueReferenceType;
import com.alibaba.fastffi.clang.NamedDecl;
import com.alibaba.fastffi.clang.NamespaceDecl;
import com.alibaba.fastffi.clang.NestedNameSpecifier;
import com.alibaba.fastffi.clang.OverloadedOperatorKind;
import com.alibaba.fastffi.clang.ParmVarDecl;
import com.alibaba.fastffi.clang.PointerType;
import com.alibaba.fastffi.clang.RValueReferenceType;
import com.alibaba.fastffi.clang.RecordDecl;
import com.alibaba.fastffi.clang.RecordType;
import com.alibaba.fastffi.clang.ReferenceType;
import com.alibaba.fastffi.clang.TagDecl;
import com.alibaba.fastffi.clang.TemplateArgument;
import com.alibaba.fastffi.clang.TemplateDecl;
import com.alibaba.fastffi.clang.TemplateName;
import com.alibaba.fastffi.clang.TemplateParameterList;
import com.alibaba.fastffi.clang.TemplateSpecializationType;
import com.alibaba.fastffi.clang.TemplateTypeParmType;
import com.alibaba.fastffi.clang.TranslationUnitDecl;
import com.alibaba.fastffi.clang.Type;
import com.alibaba.fastffi.clang.TypeClass;
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
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
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
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class FFIBindingGenerator {

    static boolean debug = false;

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
            String relative = file.substring(directory.length() + File.separator.length());
            return relative;
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
    static List<Path> inputFiles;

    static String[] processCommandLines(String[] input) {
        int i = 0;
        List<String> searchDirectory = new ArrayList<>();
        String searchPattern = ".*[.]h(pp)?";
        List<String> additional = new ArrayList<>();
        while (i < input.length) {
            String cur = input[i++];
            switch (cur) {
                case "--":
                    for (int j = i; j < input.length; j++) {
                        additional.add(input[j]);
                    }
                    break;
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
                case "--output-directory": {
                    outputDirectory = Paths.get(nextOperand(input, i++, cur));
                    continue;
                }
            }
        }
        inputFiles = collectInputFiles(searchDirectory, searchPattern);
        if (outputDirectory == null) {
            outputDirectory = Paths.get("output");
        }
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

    /**
     * Use -Dkey=value to pass Java options
     * Use application parameters
     * @param args
     */
    public static void main(String[] args) throws IOException {
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

            ClangTool clangTool;
            if (inputFiles.isEmpty()) {
                clangTool = optionsParser.createClangTool();
            } else {
                StdVector<StdString> sourcePathList = optionsParser.getSourcePathList();
                inputFiles.forEach(p -> {
                    StdString stdString = StdString.create(p.toAbsolutePath().toString());
                    memoryHelper.add(stdString);
                    sourcePathList.push_back(stdString);
                });
                clangTool = optionsParser.createClangTool(sourcePathList);
            }

            memoryHelper.add(clangTool);

            StdVectorLite.Factory<UniquePtr<ASTUnit>> vectorFactory = FFITypeFactory.getFactory(StdVectorLite.class, "std::vector<std::unique_ptr<clang::ASTUnit>>");
            StdVectorLite<UniquePtr<ASTUnit>> results = vectorFactory.create();
            memoryHelper.add(results);

            clangTool.buildASTs(results);

            FFIBindingGenerator generator = new FFIBindingGenerator(results);
            generator.generate();
        }
    }

    enum Status {
        None,
        Succ,
        Fail;
    }

    static class TypeGen {
        final ClassName className;
        final TypeSpec.Builder builder;
        final Decl decl;
        TypeSpec typeSpec;
        Status status;

        TypeGen enclosingTypeGen;
        List<TypeGen> enclosedTypeGenList;

        private TypeSpec.Builder factoryBuilder;
        private TypeSpec.Builder libraryBuilder;
        TypeGen(ClassName className, TypeSpec.Builder builder, Decl decl) {
            this.className = className;
            this.builder = builder;
            this.decl = decl;
            this.status = Status.None;
        }

        List<TypeGen> getEnclosedTypeGenList() {
            if (enclosedTypeGenList == null) {
                return Collections.emptyList();
            }
            return enclosedTypeGenList;
        }

        void addEnclosedTypeGen(TypeGen typeGen) {
            if (enclosedTypeGenList == null) {
                enclosedTypeGenList = new ArrayList<>(3);
            }
            if (enclosedTypeGenList.contains(typeGen)) {
                throw new IllegalStateException("Oops");
            }
            if (typeGen.enclosedTypeGenList != null) {
                throw new IllegalStateException("Oops");
            }
            typeGen.enclosingTypeGen = this;
            enclosedTypeGenList.add(typeGen);
        }

        TypeGen getEnclosingTypeGen() {
            return enclosingTypeGen;
        }

        boolean isSucc() {
            return status == Status.Succ;
        }

        boolean isFail() {
            return status == Status.Fail;
        }

        void fail() {
            if (debug) System.out.format("Mark failed: %s@%d\n", className, hashCode());
            if (status != Status.None) {
                throw new IllegalStateException("Oops, expected None, got " + status);
            }
            status = Status.Fail;
        }

        void succ() {
            if (debug) System.out.format("Mark successful: %s@%d\n", className, hashCode());
            if (status != Status.None) {
                throw new IllegalStateException("Oops, expected None, got " + status);
            }
            status = Status.Succ;
        }

        void build(Path output) throws IOException {
            if (debug) System.out.format("Build %s@%d\n", className, hashCode());
            if (!isSucc()) {
                throw new IllegalStateException("Oops, expected Succ, got " + status);
            }
            if (typeSpec != null) {
                throw new IllegalStateException("Cannot build twice " + className);
            }
            if (factoryBuilder != null) {
                builder.addType(factoryBuilder.build());
            }
            if (libraryBuilder != null) {
                builder.addType(libraryBuilder.build());
            }
            processEnclosedTypes();
            typeSpec = builder.build();

            if (getEnclosingTypeGen() == null) {
                JavaFile javaFile = JavaFile.builder(className.packageName(), typeSpec)
                        .build();
                if (debug) System.out.println(javaFile);
                javaFile.writeTo(output);
            }
        }

        void processEnclosedTypes() {
            if (isFail()) {
                return;
            }
            if (!isSucc()) {
                throw new IllegalStateException("Oops, expected ongoing, got " + status);
            }
            if (enclosedTypeGenList != null) {
                for (TypeGen child : enclosedTypeGenList) {
                    if (child.isSucc()) {
                        builder.addType(child.typeSpec);
                    }
                }
            }
        }

        public boolean isNone() {
            return status == Status.None;
        }

        public TypeSpec.Builder getFactoryBuilder() {
            if (factoryBuilder == null) {
                ClassName factoryClassName = className.nestedClass("Factory");
                factoryBuilder = TypeSpec.interfaceBuilder(factoryClassName).addModifiers(Modifier.PUBLIC, Modifier.STATIC);
                factoryBuilder.addAnnotation(FFIFactory.class);
            }
            return factoryBuilder;
        }

        public TypeSpec.Builder getLibraryBuilder(String cxxName) {
            if (libraryBuilder == null) {
                ClassName libraryClassName = className.nestedClass("Library");
                libraryBuilder = TypeSpec.interfaceBuilder(libraryClassName).addModifiers(Modifier.PUBLIC, Modifier.STATIC);
                libraryBuilder.addAnnotation(FFIGen.class);
                libraryBuilder.addAnnotation(AnnotationSpec.builder(FFILibrary.class)
                                .addMember("value", "$S", cxxName)
                                .addMember("namespace", "$S", cxxName)
                                .build());
                libraryBuilder.addField(FieldSpec.builder(libraryClassName, "INSTANCE", Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                        .initializer("$T.getLibrary($T.class)", FFITypeFactory.class, libraryClassName)
                        .build());
            }
            return libraryBuilder;
        }
    }

    StdVectorLite<UniquePtr<ASTUnit>> astUnits;
    Map<String, TypeGen> classBuilderMap;

    Map<FFIType, FFIType> ffiTypeDictionary = new HashMap<>();

    Map<ClassName, Set<FFIType>> baseClassToFFIType = new HashMap<>();

    HeaderManager headerManager = null;

    FFIBindingGenerator(StdVectorLite<UniquePtr<ASTUnit>> astUnits) {
        this.astUnits = astUnits;
        this.classBuilderMap = new HashMap<>();
    }

    static <K,V> boolean addToMapSet(Map<K, Set<V>> map, K key, V value) {
        Set<V> values = map.get(key);
        if (values == null) {
            values = Collections.singleton(value);
            map.put(key, values);
            return true;
        } else if (values.size() == 1) {
            if (values.contains(value)) {
                return false;
            }
            values = new HashSet<>(values);
            map.put(key, values);
            values.add(value);
            return true;
        } else {
            return values.add(value);
        }
    }

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
                throw new IllegalStateException("Sanity check failed");
            }
            if (!ffiType.javaType.equals(pointeeType.javaType)) {
                throw new IllegalStateException("Sanity check failed");
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

    static Decl getTypeDecl(Type type) {
        TypeClass typeClass = type.getTypeClass();
        switch (typeClass) {
            case Builtin:
                return null;
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
            case Record:
                return true;
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
            case Record: {
                ClassName javaName = (ClassName) ffiType.javaType;
                // tricky part: cxxName used in FFITypeAlias is the pointee type.
                String cxxName = getCXXName(ffiType.cxxType);
                TypeGen typeGen = getOrCreateTypeGenForFFIPointer(javaName, cxxName, ffiType.cxxType);
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
                // use desugared version
                ElaboratedType elaboratedType = ElaboratedType.dyn_cast(ffiType.cxxType);
                FFIType desugaredType = typeToFFIType(elaboratedType.desugar().getTypePtr());
                return process(desugaredType);
            }
            case TemplateSpecialization: {
                TemplateSpecializationType templateSpecializationType = TemplateSpecializationType.dyn_cast(ffiType.cxxType);
                TemplateDecl templateDecl = templateSpecializationType.getTemplateName().getAsTemplateDecl();
                boolean ret = generate(DeclTypeRefiner.refine(templateDecl));

                return ret;
            }
            default:
                throw unsupportedAST("Unsupported pointee type: " + ffiType);
        }
    }

    private AnnotationSpec ffiExpr(String expr) {
        return AnnotationSpec.builder(FFIExpr.class).addMember("value", "$S", expr).build();
    }

    private boolean generateTypedef(FFIType type) {
        TypedefType typedefType = TypedefType.dyn_cast(type.cxxType);
        TypedefNameDecl typedefNameDecl = typedefType.getDecl();

        ClassName aliasJavaName = getJavaClassName(typedefNameDecl);
        String aliasCXXName = getCXXName(typedefNameDecl);
        TypeGen typeGen = getOrCreateTypeGenForFFIPointer(aliasJavaName, aliasCXXName, typedefType);
        if (!typeGen.isNone()) {
            return false;
        }

        try {
            FFIType underlyingFFIType = typeToFFIType(typedefNameDecl.getUnderlyingType().getTypePtr());
            if (underlyingFFIType.isVoid()) {
                throw unsupportedAST("TODO: alias of void is not supported.");
            }

            MethodSpec.Builder getter = MethodSpec.methodBuilder("get").addModifiers(Modifier.ABSTRACT, Modifier.PUBLIC);
            if (underlyingFFIType.isPointer() || underlyingFFIType.isReference()) {
                // cast the dereference of `this' to the aliased type pointer
                getter.returns(underlyingFFIType.javaType);
                getter.addAnnotation(ffiExpr("(*{0})"));
            } else if (underlyingFFIType.isPrimitive()) {
                // return the primitive dereference value
                getter.returns(underlyingFFIType.javaType);
                getter.addAnnotation(ffiExpr("(*{0})"));
            } else {
                if (underlyingFFIType.javaType.isPrimitive()) {
                    throw new IllegalStateException("Must be a FFIPointer");
                }
                // cast `this' to the aliased type pointer
                getter.returns(underlyingFFIType.javaType);
                getter.addAnnotation(ffiExpr("{0}"));
            }
            typeGen.builder.addMethod(getter.build());
            typeGen.builder.addSuperinterface(CXXPointer.class);
        } catch (UnsupportedASTException e) {
            typeGen.fail();
            return true; // return true since we have made some changes to the global state.
        }

        // mark succ
        addCXXHeader(typeGen);
        typeGen.succ();
        checkDeclContext(typeGen, typedefNameDecl);
        return true;
    }

    private boolean generatePointerOfPrimitiveOrPointer(FFIType ffiType, FFIType pointeeType) {
        ClassName javaName = (ClassName) ffiType.javaType;
        // tricky part: cxxName used in FFITypeAlias is the pointee type.
        String cxxName = getCXXName(pointeeType.cxxType);
        TypeGen typeGen = getOrCreateTypeGenForFFIPointer(javaName, cxxName, pointeeType.cxxType);
        if (typeGen.isFail()) {
            throw new IllegalStateException("Oops: we can always generate pointer of primitives");
        }
        if (typeGen.isSucc()) {
            return false;
        }
        TypeSpec.Builder classBuilder = typeGen.builder;
        classBuilder.addSuperinterface(CXXPointer.class);

        // generate getter and setter
        classBuilder.addMethod(MethodSpec.methodBuilder("get")
                .returns(pointeeType.javaType).addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .addAnnotation(AnnotationSpec.builder(FFIExpr.class)
                        .addMember("value", "$S", "*{0}").build())
                .build());
        classBuilder.addMethod(MethodSpec.methodBuilder("set")
                .returns(TypeName.VOID).addParameter(pointeeType.javaType, "value")
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .addAnnotation(AnnotationSpec.builder(FFIExpr.class)
                        .addMember("value", "$S", "*{0} = {1}").build())
                .build());

        // factory
        TypeSpec.Builder factoryBuilder = typeGen.getFactoryBuilder();
        factoryBuilder.addMethod(MethodSpec.methodBuilder("create")
                .returns(ffiType.javaType).addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT).build());

        addCXXHeader(typeGen);
        typeGen.succ();
        return true;
    }

    TypeGen getOrCreateTypeGenForFFIPointer(ClassName className, String cxxName, Type cxxType) {
        String canonicalName = className.canonicalName();
        TypeGen typeGen = classBuilderMap.get(canonicalName);
        if (typeGen == null) {
            TypeSpec.Builder builder = TypeSpec.interfaceBuilder(className).addModifiers(Modifier.PUBLIC, Modifier.STATIC);
            {
                builder.addAnnotation(AnnotationSpec.builder(FFITypeAlias.class).addMember("value", "$S", cxxName).build());
                builder.addAnnotation(FFIGen.class);
            }
            typeGen = new TypeGen(className, builder, getTypeDecl(cxxType));
            classBuilderMap.put(canonicalName, typeGen);
        }
        return typeGen;
    }

    boolean process(FFIType ffiType) {
        System.out.println("Processing " + ffiType);
        try {
            if (ffiType.isPointer() || ffiType.isReference()) {
                return processPointerOrReference(ffiType);
            }
            if (ffiType.isValue()) {
                boolean ret = generateValue(ffiType);
                checkTemplateInstantiation(ffiType);
                return ret;
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
        TypeGen typeGen = getTypeGen(ffiType);
        if (typeGen == null) {
            // TODO: must not be null???
            return;
        }
        if (!typeGen.isSucc()) {
            return; // not generated
        }
        if (ffiType.cxxType.getTypeClass() == TypeClass.TemplateSpecialization) {
            TemplateSpecializationType templateSpecializationType = TemplateSpecializationType.dyn_cast(ffiType.cxxType);
            try {
                forEachTemplateArgument(templateSpecializationType, argType -> {
                    // do sanity check first
                    getCXXName(argType);
                    typeToTypeName(argType);
                });
                ParameterizedTypeName parameterizedTypeName = (ParameterizedTypeName) ffiType.javaType;
                addToMapSet(baseClassToFFIType, parameterizedTypeName.rawType, ffiType);
            } catch (UnsupportedASTException e) {}
        } // else may be sugared type
    }

    void iterate() {
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
            if (typeGen.getEnclosingTypeGen() == null) {
                visit(typeGen, sorted, added);
            }
        }
        ListIterator<TypeGen> iterator = sorted.listIterator(sorted.size());
        while (iterator.hasPrevious()) {
            TypeGen prev = iterator.previous();
            if (prev.isSucc()) {
                prev.build(outputDirectory);
            }
        }
        {
            instantiateTemplates();
        }
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

    private void collectInclude(Type type, Set<IncludeEntry> includes) {
        if (type.getTypeClass() == TypeClass.TemplateSpecialization) {
            TemplateSpecializationType templateSpecializationType = TemplateSpecializationType.dyn_cast(type);
            forEachTemplateArgument(templateSpecializationType, argType -> {
                collectInclude(argType, includes);
            });
        } else {
            IncludeEntry entry = getIncludeEntry(type);
            if (entry != null) {
                includes.add(entry);
            }
        }
    }

    void forEachTemplateArgument(TemplateSpecializationType templateSpecializationType, Consumer<Type> consumer) {
        int size = templateSpecializationType.getNumArgs();
        for (int i = 0; i < size; i++) {
            TemplateArgument arg = templateSpecializationType.getArg(i);
            if (arg.getKind() != TemplateArgument.ArgKind.Type) {
                throw unsupportedAST("TODO: unsupported template argument " + arg.getKind());
            }
            Type argType = arg.getAsType().getTypePtr();
            consumer.accept(argType);
        }
    }

    private void instantiateTemplates() throws IOException {
        if (baseClassToFFIType.isEmpty()) {
            return;
        }
        TypeSpec.Builder builder = TypeSpec.interfaceBuilder("Templates");
        AnnotationSpec.Builder ffiBatchGen = AnnotationSpec.builder(FFIGenBatch.class);
        for (Map.Entry<ClassName, Set<FFIType>> entry : baseClassToFFIType.entrySet()) {
            ClassName base = entry.getKey();
            Set<FFIType> ffiTypes = entry.getValue();
            AnnotationSpec.Builder ffiGen = AnnotationSpec.builder(FFIGen.class);
            ffiGen.addMember("type", "$S", base.canonicalName());
            for (FFIType ffiType : ffiTypes) {
                AnnotationSpec.Builder cxxTemplate = AnnotationSpec.builder(CXXTemplate.class);
                TemplateSpecializationType templateSpecializationType = TemplateSpecializationType.dyn_cast(ffiType.cxxType);
                forEachTemplateArgument(templateSpecializationType, argType -> {
                    String cxxName = getCXXName(argType);
                    TypeName javaName = typeToTypeName(argType);
                    cxxTemplate.addMember("cxx", "$S", cxxName);
                    cxxTemplate.addMember("java", "$S", javaName);
                    collectCXXHead(argType).forEach(c -> {
                        cxxTemplate.addMember("include", "$L", c);
                    });
                });
                ffiGen.addMember("templates", "$L", cxxTemplate.build());
            }
            ffiBatchGen.addMember("value", "$L", ffiGen.build());
        }
        builder.addAnnotation(ffiBatchGen.build());

        JavaFile javaFile = JavaFile.builder("", builder.build()).build();
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
            System.out.println("#ASTUnit: " + astUnit.getOriginalSourceFileName().toJavaString() + " has uncompilable errors.");
            return;
        }

        initHeaderManager(astUnit);
        generate(astUnit.getASTContext().getTranslationUnitDecl());
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
            } catch (UnsupportedASTException e) {
                e.printStackTrace();
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
                default:
                    break;
            }
            if (decl instanceof DeclContext) {
                DeclContext declContext = (DeclContext) decl;
                DeclContext.decl_iterator begin = declContext.decls_begin();
                DeclContext.decl_iterator end = declContext.decls_end();
                for (; begin.notEquals(end); begin.next()) {
                    // mark parent changed of any enclosing element is changed
                    // TODO: begin.get() may be null.
                    if (begin.get() == null) {
                        continue;
                    }
                    changed |= generate(begin.get());
                }
            }
        } catch (UnsupportedASTException e) {
            if (debug) e.printStackTrace();
            if (typeGen != null) {
                typeGen.fail();
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
                    return null; // e.g., indirect class name
                }
                if (tagDecl.getIdentifier() == null) {
                    return null;
                }
                return getOrCreateTypeGen(tagDecl);
            case ClassTemplate:
                return getOrCreateTypeGenIfNecessary(((ClassTemplateDecl) decl).getTemplatedDecl());
            default:
                return null;
        }
    }

    private void generateEnum(EnumDecl decl) {
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
            classBuilder.addField(TypeName.INT, "$value");
            classBuilder.addMethod(MethodSpec.methodBuilder("getValue")
                    .addModifiers(Modifier.PUBLIC).returns(TypeName.INT).addStatement("return $$value")
                    .build());
            classBuilder.addMethod(MethodSpec.constructorBuilder()
                    .addStatement("$$value = value").addParameter(TypeName.INT, "value")
                    .build());

            ClassName CXXEnumMapName = ClassName.get(CXXEnumMap.class);
            TypeName mapName = ParameterizedTypeName.get(CXXEnumMapName, className);
            classBuilder.addField(FieldSpec.builder(mapName, "$map", Modifier.STATIC, Modifier.FINAL, Modifier.PRIVATE)
                    .initializer("new $T<>(values())", CXXEnumMap.class).build());

            classBuilder.addMethod(MethodSpec.methodBuilder("get")
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .addParameter(TypeName.INT, "value")
                    .returns(className)
                    .addStatement("return $$map.get(value)").build());
            classBuilder.addAnnotation(AnnotationSpec.builder(FFITypeRefiner.class)
                    .addMember("value", "$S", className.toString() + ".get").build());
        }
        addCXXHeader(typeGen);
        typeGen.succ();
        checkDeclContext(typeGen, decl);
    }

    private void addCXXHeader(TypeGen typeGen) {
        AnnotationSpec annotationSpec = createCXXHeader(typeGen.decl);
        if (annotationSpec != null) {
            typeGen.builder.addAnnotation(annotationSpec);
        }
    }

    private AnnotationSpec createCXXHeader(Type type) {
        Decl decl = getTypeDecl(type);
        return createCXXHeader(decl);
    }

    private HeaderDirectoryEntry getHeaderEntry(Type type) {
        Decl decl = getTypeDecl(type);
        return getHeaderEntry(decl);
    }

    private HeaderDirectoryEntry getHeaderEntry(Decl decl) {
        if (decl == null) {
            return null;
        }
        String fileName = decl.getASTContext().getSourceManager().getFilename(decl.getLocation()).toJavaString();
        return headerManager.getIncludeDirectory(fileName);
    }

    private IncludeEntry getIncludeEntry(Type type) {
        Decl decl = getTypeDecl(type);
        return getIncludeEntry(decl);
    }

    private IncludeEntry getIncludeEntry(Decl decl) {
        if (decl == null) {
            return null;
        }
        String fileName = decl.getASTContext().getSourceManager().getFilename(decl.getLocation()).toJavaString();
        HeaderDirectoryEntry entry = headerManager.getIncludeDirectory(fileName);
        if (entry == null) {
            return null;
        }
        return new IncludeEntry(entry.getIncludePath(fileName), entry.isSystemDir);
    }

    private AnnotationSpec createCXXHeader(Decl decl) {
        if (decl == null) {
            return null;
        }
        String fileName = decl.getASTContext().getSourceManager().getFilename(decl.getLocation()).toJavaString();
        HeaderDirectoryEntry entry = getHeaderEntry(decl);
        if (entry == null) {
            return null;
        }
        String key = entry.isSystemDir ? "system" : "value";
        return AnnotationSpec.builder(CXXHead.class)
                .addMember(key, "$S", entry.getIncludePath(fileName))
                .build();
    }

    private void checkDeclContext(TypeGen typeGen, Decl decl) {
        Decl parent = DeclContext.cast(decl.getDeclContext());
        if (parent instanceof CXXRecordDecl) {
            CXXRecordDecl parentCXXRecord = (CXXRecordDecl) parent;
            if (!parentCXXRecord.isThisDeclarationADefinition()) {
                throw new IllegalStateException("Must be a definition");
            }
            TypeGen parentTypeGen = getOrCreateTypeGen(parentCXXRecord);
            parentTypeGen.addEnclosedTypeGen(typeGen);
        }
    }

    void generateClassTemplate(ClassTemplateDecl classTemplateDecl) {
        generate(classTemplateDecl.getTemplatedDecl());
    }

    UnsupportedASTException unsupportedAST(String message) {
        return new UnsupportedASTException(message);
    }

    void buildReturnAndParamTypes(MethodSpec.Builder methodBuilder, TypeName returnType, CXXConstructorDecl methodDecl) {
        methodBuilder.returns(returnType);
        buildParamTypes(methodBuilder, methodDecl);
    }

    void buildReturnAndParamTypes(MethodSpec.Builder methodBuilder, CXXMethodDecl methodDecl) {
        Type returnType = methodDecl.getReturnType().getTypePtr();
        FFIType ffiType = typeToFFIType(returnType);
        methodBuilder.returns(ffiType.javaType);
        if (ffiType.isReference()) {
            methodBuilder.addAnnotation(CXXReference.class);
        } else if (ffiType.isValue()) {
            methodBuilder.addAnnotation(CXXValue.class);
        }
        buildParamTypes(methodBuilder, methodDecl);
    }

    TypeName typeToTypeName(Type type) {
        TypeClass typeClass = type.getTypeClass();
        switch (typeClass) {
            case Builtin: {
                return getJavaTypeForBuiltinType((BuiltinType) type);
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
            case LValueReference:
            case RValueReference: {
                return getJavaTypeForReferenceType((ReferenceType) type);
            }
            case Typedef: {
                return getJavaTypeForTypedefType((TypedefType) type);
            }
            case DependentName: {
                return getJavaTypeForDependentNameType((DependentNameType) type);
            }
            case Elaborated: {
                return getJavaTypeForElaboratedType((ElaboratedType) type);
            }
            default:
                throw unsupportedAST("Unsupported type: " + type);
        }
    }

    private TypeName getJavaTypeForElaboratedType(ElaboratedType type) {
        Type desugared = type.desugar().getTypePtr();
        return typeToTypeName(desugared);
    }

    private TypeName getJavaTypeForDependentNameType(DependentNameType type) {
        throw unsupportedAST("Unsupported dependent name type: " + type);
    }

    /**
     * <ul>
     *     <li>If the typedef is defined from a primitve, then use the primitive type name</li>
     *     <li>otherwise, get the name from the TypedefNameDecl.
     *     In this case, we may create redundant Java bindings for aliased types,
     *     where each type alias has a Java binding. Java does not support type alias
     *     so we use inheritance to support type alias.</li>
     * </ul>
     * @param type
     * @return
     */
    private TypeName getJavaTypeForTypedefType(TypedefType type) {
        TypedefNameDecl typedefNameDecl = type.getDecl();
        Type underlyingType = typedefNameDecl.getUnderlyingType().getTypePtr();
        if (isJavaDeclarable(underlyingType)) {
            return getJavaClassName(typedefNameDecl);
        }
        // other cases: only value, pointer and reference of type parameter is allowed.
        FFIType underlyingFFIType = typeToFFIType(underlyingType);
        if (underlyingFFIType.isTemplateVariableDependent()) {
            return underlyingFFIType.javaType;
        }
        throw unsupportedAST("Unsupported TypedefType: " + type);
    }

    /**
     * TODO: We currently does not support pointer of pointer
     * unless the pointer type is redefined as a new type via typedef or using.
     * @param pointeeType
     * @return
     */
    private TypeName getJavaTypeForPointeeType(Type pointeeType) {
        FFIType pointeeFFIType = typeToFFIType(pointeeType);
        if (pointeeFFIType.isVoid()) {
            return TypeName.LONG;
        }
        if (pointeeFFIType.isReference()) {
            throw new IllegalStateException("Sanity check");
        }
        if (pointeeFFIType.isPointer()) {
            if (pointeeFFIType.isTemplateVariableDependent()) {
                throw new UnsupportedASTException("TODO: no pointer to pointer of template variable is supported");
            }
            // here is a little bit tricky;
            // we need to create a pointer to the pointer
            return makePointerOfPointer(pointeeFFIType.javaType);
        }
        if (pointeeFFIType.isPrimitive()) {
            return makePointerOfPrimitive(pointeeFFIType.javaType);
        }
        if (pointeeFFIType.isTemplateVariableDependent()) {
            return pointeeFFIType.javaType;
        }
        return pointeeFFIType.javaType;
    }

    private TypeName makePointerOfPrimitive(TypeName pointerTypeName) {
        if (!pointerTypeName.isPrimitive()) {
            throw new IllegalStateException("Sanity check");
        }
        return ClassName.get("", pointerTypeName.toString() + "Pointer");
    }

    private TypeName makePointerOfPointer(TypeName pointerTypeName) {
        ClassName className = (ClassName) pointerTypeName;
        String simpleName = className.simpleName();
        ClassName enclosingClassName = className.enclosingClassName();
        if (enclosingClassName == null) {
            String packageName = className.packageName();
            return ClassName.get(packageName, simpleName + "P");
        } else {
            return enclosingClassName.nestedClass(simpleName + "P");
        }
    }

    private TypeName getJavaTypeForReferenceType(ReferenceType type) {
        Type pointeeType = type.getPointeeType().getTypePtr();
        return getJavaTypeForPointeeType(pointeeType);
    }

    FFIType typeToFFIType(Type type) {
        TypeName typeName = typeToTypeName(type);
        FFIType key = new FFIType(type, typeName);
        FFIType ffiType = ffiTypeDictionary.get(key);
        if (ffiType != null) {
            return ffiType;
        }
        if (type.getTypeClass() == TypeClass.TemplateTypeParm) {
            return key; // no need to cache template parameter
        }
        if (key.javaType.toString().endsWith("reverse_iterator")) {
            if (isJavaDeclarable(key.cxxType)) {
                System.out.println("Declarable");
            }
        }
        ffiTypeDictionary.put(key, key);
        return key;
    }

    private TypeName getJavaTypeForTemplateSpecializationType(TemplateSpecializationType type) {
        TemplateName templateName = type.getTemplateName();
        TemplateDecl templateDecl = templateName.getAsTemplateDecl();
        if (templateDecl.getKind() != Decl.Kind.ClassTemplate) {
            throw unsupportedAST("Unsupported template: " + templateDecl);
        }
        ClassTemplateDecl classTemplateDecl = ClassTemplateDecl.dyn_cast(templateDecl);
        CXXRecordDecl cxxRecordDecl = classTemplateDecl.getTemplatedDecl();
        ClassName baseClassName = getJavaClassName(cxxRecordDecl);

        int numArgs = type.getNumArgs();
        if (numArgs == 0) {
            return baseClassName;
        }
        TypeName[] argTypeNames = new TypeName[numArgs];

        for (int i = 0; i < numArgs; i++) {
            TemplateArgument templateArgument = type.getArg(i);
            if (templateArgument.getKind() != TemplateArgument.ArgKind.Type) {
                throw unsupportedAST("Unsupported template argument: " + templateArgument);
            }
            Type argType = templateArgument.getAsType().getTypePtr();
            argTypeNames[i] = typeToTypeName(argType);
            if (argTypeNames[i].isPrimitive()) {
                argTypeNames[i] = argTypeNames[i].box();
            }
        }
        return ParameterizedTypeName.get(baseClassName, argTypeNames);
    }

    private TypeName getJavaTypeForTemplateTypeParamType(TemplateTypeParmType type) {
        if (type.getDecl().hasDefaultArgument()) {
            throw unsupportedAST("TODO: no support of template argument with default argument.");
        }
        IdentifierInfo identifierInfo = type.getIdentifier();
        if (identifierInfo == null) {
            throw unsupportedAST("TemplateTypeParamType does not have an identifier");
        }
        return TypeVariableName.get(identifierInfo.getName().toJavaString());
    }

    private TypeName getJavaTypeForPointerType(PointerType type) {
        Type pointeeType = type.getPointeeType().getTypePtr();
        return getJavaTypeForPointeeType(pointeeType);
    }

    private TypeName getJavaTypeForRecordType(RecordType type) {
        ClassName className = getJavaClassName(type.getDecl());
        return className;
    }

    static TypeName getJavaTypeName(BuiltinType type) {
        BuiltinType.Kind kind = type.getKind();
        switch (kind) {
            case Void:
                return TypeName.VOID;
            case Bool:
                return TypeName.BOOLEAN;
            case Char8:
            case Char_S:
            case SChar:
            case Char_U:
            case UChar:
                return TypeName.BYTE;
            case UShort:
            case Short:
            case WChar_S:
            case WChar_U:
            case Char16:
                return TypeName.SHORT;
            case UInt:
            case Int:
            case ULong:
            case Long:
            case Char32:
                return TypeName.INT;
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

    private TypeName getJavaTypeForBuiltinType(BuiltinType type) {
        TypeName typeName = getJavaTypeName(type);
        if (typeName == null) {
            throw unsupportedAST("Unsupported builtint type: " + type);
        }
        return typeName;
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
            Type paramType = parmVarDecl.getTypeSourceInfo().getType().getTypePtr();
            FFIType ffiType = typeToFFIType(paramType);
            ParameterSpec.Builder paramBuilder = ParameterSpec.builder(ffiType.javaType, parmName);
            if (ffiType.isReference()) {
                paramBuilder.addAnnotation(CXXReference.class);
            } else if (ffiType.isValue()) {
                paramBuilder.addAnnotation(CXXValue.class);
            }
            if (ffiType.javaType instanceof ParameterizedTypeName) {
                paramBuilder.addAnnotation(AnnotationSpec.builder(FFITypeAlias.class)
                        .addMember("value", "$S", getCXXName(ffiType.cxxType)).build());
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
        if (!isSupportedRecordDecl(cxxRecordDecl)) {
            return;
        }

        if (!(cxxRecordDecl.isStruct() || cxxRecordDecl.isUnion() || cxxRecordDecl.isClass())) {
            throw unsupportedAST("Not a struct union or class");
        }
        generateFFIPointer(cxxRecordDecl);
    }

    private void generateFFIPointer(RecordDecl recordDecl) {
        TypeGen typeGen = getOrCreateTypeGen(recordDecl);
        TypeSpec.Builder classBuilder = typeGen.builder;
        generateFields(recordDecl, classBuilder);

        addCXXHeader(typeGen);
        // mark successful
        typeGen.succ();
        checkDeclContext(typeGen, recordDecl);
    }

    private void generateFields(RecordDecl recordDecl, TypeSpec.Builder classBuilder) {
        RecordDecl.field_iterator begin = recordDecl.field_begin();
        RecordDecl.field_iterator end = recordDecl.field_end();
        for (; begin.notEquals(end); begin.next()) {
            FieldDecl fieldDecl = begin.get();
            if (!fieldDecl.getAccess().isPublic()) {
                continue;
            }
            if (fieldDecl.isImplicit()) {
                continue;
            }
            String name = fieldDecl.getIdentifier().getName().toJavaString();
            FFIType ffiType = typeToFFIType(fieldDecl.getTypeSourceInfo().getType().getTypePtr());

            TypeName javaType = ffiType.javaType;
            MethodSpec.Builder setter = MethodSpec.methodBuilder(name).addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT).addAnnotation(FFISetter.class);
            ParameterSpec.Builder paramBuilder = ParameterSpec.builder(javaType, "value");
            MethodSpec.Builder getter = MethodSpec.methodBuilder(name).addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT).addAnnotation(FFIGetter.class);
            getter.returns(javaType);
            setter.returns(TypeName.VOID);
            if (ffiType.isReference() || ffiType.isValue()) {
                getter.addAnnotation(CXXReference.class);
                paramBuilder.addAnnotation(CXXReference.class);
            }
            setter.addParameter(paramBuilder.build());
            classBuilder.addMethod(setter.build());
            classBuilder.addMethod(getter.build());
        }
    }

    private void addSuperinterface(TypeSpec.Builder classBuilder, Type superType) {
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
                    templateBuilder.addMember("cxx", "$S", getCXXName(templateSpecializationType.getArg(i).getAsType().getTypePtr()));
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
                addSuperTemplate(classBuilder, typeToFFIType(ElaboratedType.dyn_cast(superFFIType.cxxType).desugar().getTypePtr()));
                break;
            default:
                throw unsupportedAST("Unsupported type: " + superFFIType);
        }
    }

    private void addSuperinterface(TypeSpec.Builder classBuilder, FFIType superFFIType) {
        classBuilder.addSuperinterface(superFFIType.javaType);
        addSuperTemplate(classBuilder, superFFIType);
    }

    private void generateFFIPointer(CXXRecordDecl cxxRecordDecl) {
        ClassName className = getJavaClassName(cxxRecordDecl);
        TypeGen typeGen = getOrCreateTypeGen(className, cxxRecordDecl);
        TypeSpec.Builder classBuilder = typeGen.builder;
        TypeSpec.Builder factoryClassBuilder = null;
        ClassTemplateDecl classTemplateDecl = cxxRecordDecl.getDescribedClassTemplate();
        {
            if (cxxRecordDecl.getNumBases() > 0) {
                // build base class
                CXXRecordDecl.base_class_iterator begin = cxxRecordDecl.bases_begin();
                CXXRecordDecl.base_class_iterator end = cxxRecordDecl.bases_end();
                for (; begin.notEquals(end); begin.next()) {
                    try {
                        addSuperinterface(classBuilder, begin.get().getType().getTypePtr());
                    } catch (UnsupportedASTException e) {
                        // ignore this super if we cannot generate it
                    }
                }
            } else {
                if (cxxRecordDecl.hasUserDeclaredDestructor()) {
                    classBuilder.addSuperinterface(ClassName.get(CXXPointer.class));
                } else {
                    classBuilder.addSuperinterface(ClassName.get(FFIPointer.class));
                }
            }
        }
        {
            // build methods
            CXXRecordDecl.method_iterator begin = cxxRecordDecl.method_begin();
            CXXRecordDecl.method_iterator end = cxxRecordDecl.method_end();
            for (; begin.notEquals(end); begin.next()) {
                try {
                    CXXMethodDecl methodDecl = begin.get();
                    if (!methodDecl.getAccess().isPublic()) {
                        continue;
                    }
                    if (methodDecl.isStatic()) {
                        continue;
                    }
                    if (!methodDecl.isUserProvided()) {
                        continue;
                    }
                    if (methodDecl.getDeclKind() == Decl.Kind.CXXConstructor) {
                        continue;
                    }
                    if (methodDecl.getDeclKind() == Decl.Kind.CXXDestructor) {
                        continue;
                    }
                    if (methodDecl.getDeclKind() == Decl.Kind.CXXConversion) {
                        continue;
                    }

                    MethodSpec.Builder methodBuilder;
                    if (methodDecl.isOverloadedOperator()) {
                        OverloadedOperatorKind kind = methodDecl.getOverloadedOperator();
                        methodBuilder = MethodSpec.methodBuilder("$" + kind);
                        methodBuilder.addAnnotation(AnnotationSpec.builder(CXXOperator.class)
                                .addMember("value", "$S", kind.getOperatorSpelling()).build());
                    } else {
                        String methodName = methodDecl.getNameAsString().toJavaString();
                        methodBuilder = MethodSpec.methodBuilder(methodDecl.getNameAsString().toJavaString());
                    }
                    methodBuilder.addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT);
                    buildReturnAndParamTypes(methodBuilder, methodDecl);
                    classBuilder.addMethod(methodBuilder.build());
                } catch (UnsupportedASTException e) {

                }
            }
        }
        {
            List<MethodSpec.Builder> ctorBuilders = new ArrayList<>();
            // build constructors
            CXXRecordDecl.ctor_iterator begin = cxxRecordDecl.ctor_begin();
            CXXRecordDecl.ctor_iterator end = cxxRecordDecl.ctor_end();
            TypeName returnType = className;
            if (classTemplateDecl != null) {
                TemplateParameterList templateParameterList = classTemplateDecl.getTemplateParameters();
                int size = templateParameterList.getMinRequiredArguments();
                if (size > 0) {
                    TypeName[] typeArgs = new TypeName[size];
                    for (int i = 0; i < size; i++) {
                        NamedDecl param = templateParameterList.getParam(i);
                        String name = param.getIdentifier().getName().toJavaString();
                        typeArgs[i] = TypeVariableName.get(name);
                    }
                    returnType = ParameterizedTypeName.get(className, typeArgs);
                }
            }
            for (; begin.notEquals(end); begin.next()) {
                try {
                    CXXConstructorDecl ctorDecl = begin.get();
                    if (!ctorDecl.getAccess().isPublic()) {
                        continue;
                    }
                    if (!ctorDecl.isUserProvided()) {
                        continue;
                    }
                    MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("create")
                            .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT);
                    buildReturnAndParamTypes(methodBuilder, returnType, ctorDecl);
                    ctorBuilders.add(methodBuilder);
                } catch (UnsupportedASTException e) {

                }
            }
            if (!ctorBuilders.isEmpty()) {
                factoryClassBuilder = typeGen.getFactoryBuilder();
                for (MethodSpec.Builder methodBuilder : ctorBuilders) {
                    factoryClassBuilder.addMethod(methodBuilder.build());
                }
            }
        }
        {
            // build fields
            generateFields(cxxRecordDecl, classBuilder);
        }
        {
            if (classTemplateDecl != null) {
                TemplateParameterList templateParameterList = classTemplateDecl.getTemplateParameters();
                int size = templateParameterList.getMinRequiredArguments();
                for (int i = 0; i < size; i++) {
                    NamedDecl param = templateParameterList.getParam(i);
                    String name = param.getIdentifier().getName().toJavaString();
                    TypeVariableName variableName = TypeVariableName.get(name);
                    classBuilder.addTypeVariable(variableName);
                    if (factoryClassBuilder != null) {
                        factoryClassBuilder.addTypeVariable(variableName);
                    }
                }
            }
        }

        addCXXHeader(typeGen);
        // mark successful
        typeGen.succ();
        checkDeclContext(typeGen, cxxRecordDecl);
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
                    if (!current.isAnonymousNamespace()) {
                        names.addFirst(current.getIdentifier().getName().toJavaString());
                    }
                    break;
                }
                case Record:
                case CXXRecord:
                case Enum: {
                    throw new IllegalStateException("Oops: should not call this method");
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

    private String getPackageName(DeclContext declContext) {
        return getNames(declContext).stream().collect(Collectors.joining("."));
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
        NestedNameSpecifier qualifier = tagDecl.getQualifier();
        if (qualifier != null) {
            throw unsupportedAST("TODO: handle nested name specifier");
        }
        return tagDecl.isStruct() || tagDecl.isUnion() || tagDecl.isClass() || tagDecl.isEnum();
    }

    private String getCXXContextName(DeclContext context) {
        Decl contextDecl = DeclContext.cast(context);
        Decl.Kind contextKind = contextDecl.getKind();
        DeclContext parentContext = context.getParent();
        switch (contextKind) {
            case Enum:
            case CXXRecord:
            case Record:
            case Namespace: {
                NamedDecl namedDecl = (NamedDecl) contextDecl;
                String parentName = getCXXContextName(parentContext);
                if (namedDecl.getIdentifier() != null) {
                    String name = namedDecl.getIdentifier().getName().toJavaString();
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
            default:
                throw unsupportedAST("Not a supported DeclContext: " + contextDecl);
        }
    }

    private String getCXXName(Type type) {
        TypeClass typeClass = type.getTypeClass();
        switch (typeClass) {
            case Builtin: {
                BuiltinType builtinType = (BuiltinType) type;
                return builtinType.getCanonicalTypeInternal().getAsString().toJavaString();
            }
            case Typedef: {
                TypedefType typedefType = (TypedefType) type;
                TypedefNameDecl typedefNameDecl = typedefType.getDecl();
                Type underlyingType = typedefNameDecl.getUnderlyingType().getTypePtr();
                TypeClass underlyingClass = underlyingType.getTypeClass();
                switch (underlyingClass) {
                    //case DependentName: {
                    //    return getCXXName(typedefNameDecl);
                    //}
                    case Builtin:
                    case Typedef:
                    case Record:
                    case Pointer:
                    case LValueReference:
                    case RValueReference:
                    case Elaborated:
                    case TemplateSpecialization:
                        return getCXXName(typedefNameDecl);
                    default:
                        throw unsupportedAST("Unsupported typedef type: " + type);
                }
            }
            case Record: {
                RecordType recordType = (RecordType) type;
                RecordDecl recordDecl = recordType.getDecl();
                return getCXXName(recordDecl);
            }
            case Pointer: {
                return getCXXName(PointerType.dyn_cast(type).getPointeeType().getTypePtr()) + "*";
            }
            case LValueReference: {
                return getCXXName(LValueReferenceType.dyn_cast(type).getPointeeType().getTypePtr()) + "&";
            }
            case RValueReference: {
                return getCXXName(RValueReferenceType.dyn_cast(type).getPointeeType().getTypePtr()) + "&&";
            }
            case Elaborated: {
                return getCXXName(ElaboratedType.dyn_cast(type).desugar().getTypePtr());
            }
            case TemplateSpecialization: {
                TemplateSpecializationType templateSpecializationType = TemplateSpecializationType.dyn_cast(type);
                StringBuilder sb = new StringBuilder();
                sb.append(getCXXName(templateSpecializationType.getTemplateName().getAsTemplateDecl()));
                sb.append("<");
                int size = templateSpecializationType.getNumArgs();
                if (size > 0) {
                    sb.append(getCXXName(templateSpecializationType.getArg(0).getAsType().getTypePtr()));
                    for (int i = 1; i < size; i++) {
                        sb.append(",");
                        sb.append(getCXXName(templateSpecializationType.getArg(i).getAsType().getTypePtr()));
                    }
                }
                sb.append(">");
                return sb.toString();
            }
            default:
                throw unsupportedAST("Unsupported CXX type: " + type);
        }
    }

    private String getCXXName(NamedDecl tagDecl) {
        String simpleName = tagDecl.getIdentifier().getName().toJavaString();
        DeclContext context = tagDecl.getDeclContext();
        String contextName = getCXXContextName(context);
        if (!contextName.isEmpty()) {
            return contextName + "::" + simpleName;
        }
        return simpleName;
    }

    private ClassName getJavaClassName(NamedDecl namedDecl) {
        if (namedDecl.getIdentifier() == null) {
            throw unsupportedAST("Cannot get Java class name for anonymous NamedDecl: " + namedDecl);
        }
        DeclContext context = namedDecl.getDeclContext();
        Decl contextDecl = DeclContext.cast(context);
        Decl.Kind contextKind = contextDecl.getKind();
        if (contextKind == Decl.Kind.Enum || contextKind == Decl.Kind.CXXRecord || contextKind == Decl.Kind.Record) {
            TagDecl parentDecl = TagDecl.dyn_cast(contextDecl);
            ClassName parentClassName = getJavaClassName(parentDecl);
            String packageName = parentClassName.packageName();
            List<String> simpleNames = new ArrayList<>(parentClassName.simpleNames());
            String simpleName = simpleNames.remove(0);
            simpleNames.add(namedDecl.getIdentifier().getName().toJavaString());
            return ClassName.get(packageName, simpleName, simpleNames.toArray(new String[0]));
        } else {
            String packageName = getPackageName(context);
            String simpleName = namedDecl.getIdentifier().getName().toJavaString();
            return ClassName.get(packageName, simpleName);
        }
    }

    private TypeGen getTypeGen(ClassName className) {
        return classBuilderMap.get(className.canonicalName());
    }

    private TypeGen getOrCreateTypeGen(TagDecl tagDecl) {
        ClassName className = getJavaClassName(tagDecl);
        return getOrCreateTypeGen(className, tagDecl);
    }

    private TypeGen getOrCreateTypeGen(ClassName className, TagDecl tagDecl) {
        if (!tagDecl.isThisDeclarationADefinition()) {
            throw new IllegalStateException("Must be definition!");
        }
        String canonicalName = className.canonicalName();
        TypeGen typeGen = classBuilderMap.get(canonicalName);
        if (typeGen == null) {
            TypeSpec.Builder builder = createTypeBuilder(className, tagDecl);
            {
                String cxxName = getCXXName(tagDecl);
                builder.addAnnotation(AnnotationSpec.builder(FFITypeAlias.class).addMember("value", "$S", cxxName).build());
                if (!tagDecl.isEnum()) builder.addAnnotation(FFIGen.class);
            }
            typeGen = new TypeGen(className, builder, tagDecl);
            classBuilderMap.put(canonicalName, typeGen);
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
