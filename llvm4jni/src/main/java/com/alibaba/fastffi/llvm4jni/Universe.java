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
package com.alibaba.fastffi.llvm4jni;

import com.alibaba.fastffi.llvm.Function;
import com.alibaba.fastffi.llvm.FunctionType;
import com.alibaba.fastffi.llvm.GlobalValue;
import com.alibaba.fastffi.llvm.GlobalVariable;
import com.alibaba.fastffi.llvm.Module;
import com.alibaba.fastffi.llvm4jni.body.InputClass;
import com.alibaba.fastffi.llvm4jni.body.InvokerClass;
import com.alibaba.fastffi.llvm4jni.body.LibraryClass;
import com.alibaba.fastffi.llvm4jni.body.LibraryMethod;
import com.alibaba.fastffi.llvm4jni.type.TypeDef;
import com.alibaba.fastffi.llvm4jni.type.TypeDefFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An Java Scope is a namespace of all classes, including input classes with native Java methods
 * and the generated library classes for methods generated from native functions.
 *
 */
public class Universe {

    private Options options;
    /**
     * The module being processed
     */
    private Module module;
    private String moduleName;
    /**
     * A set of classes
     */
    private Map<String, InputClass> inputClasses;
    /**
     * The Java FFI library class for the module
     */
    private LibraryClass libraryClass;
    /**
     * The map between ZTI and vtable definition
     */
    private Map<String, VTable> vtableMap;
    /**
     * Map between ZTI global variable to vtable
     */
    private Map<GlobalVariable, List<VTable>> typeInfoToVTable;
    /**
     * Map between function type and possible vtable entry
     */
    private Map<FunctionType, List<VTableEntry>> functionTypeToVTableEntry;
    private Map<String, Function> functionMap;
    /**
     * The factory used to do type flow analysis over JNI functions.
     */
    private TypeDefFactory typeDefFactory;

    public Universe(Options options, Module module, String moduleName, Map<String, InputClass> classes, Path libraryPath, Path[] classpath) {
        this.options = options;
        this.module = module;
        this.moduleName = moduleName;
        inputClasses = classes;
        this.libraryClass = new LibraryClass(this, module.getDataLayout(), libraryPath);
        vtableMap = VTable.readVTable(module);
        functionMap = new HashMap<>();
        module.forEachFunction(f -> functionMap.put(f.getName(), f));
        collectVTableEntry();
        typeDefFactory = TypeDefFactory.create(classpath);
    }

    private void collectVTableEntry() {
        functionTypeToVTableEntry = new HashMap<>();
        typeInfoToVTable = new HashMap<>();
        vtableMap.values().forEach( v -> {
            Utils.addToMapList(typeInfoToVTable, v.getTypeInfo(), v);
            v.forEachFunction( (f, i) -> {
                FunctionType functionType = f.getFunctionType();
                int index = i;
                VTableEntry vTableEntry = new VTableEntry(v, index);
                Utils.addToMapList(functionTypeToVTableEntry, functionType, vTableEntry);
            });
        });
    }

    public List<VTableEntry> getVTableEntryList(FunctionType functionType) {
        List<VTableEntry> results = functionTypeToVTableEntry.get(functionType);
        if (results == null) {
            return Collections.emptyList();
        }
        return results;
    }

    public Module getModule() {
        return this.module;
    }

    public String getModuleName() {
        return this.moduleName;
    }

    public boolean isAvailableInELF(GlobalValue globalValue) {
        if (!globalValue.hasDefaultVisibility()) {
            return false;
        }

        GlobalValue.LinkageTypes linkageTypes = globalValue.getLinkage();
        switch (linkageTypes) {
            case ExternalLinkage:
            case AvailableExternallyLinkage:
            case ExternalWeakLinkage:
                return true;
            case LinkOnceAnyLinkage:
            case WeakAnyLinkage:
            case WeakODRLinkage:
            case LinkOnceODRLinkage:
                // These linking will merge definitions.
                // The module may create a copy of the global definition.
                // We cannot use the address in the module for global usage.
                if (globalValue.hasAtLeastLocalUnnamedAddr()) {
                    return false;
                }
                return true;
            default:
        }
        /*
        String symbol = globalValue.getName();
        return library.getSymbolAddress(symbol) != 0L;
         */
        return false;
    }

    public LibraryClass getLibraryClass() {
        return libraryClass;
    }

    public void lookupNativeMethods(Map<String, Function> nameToFunctions) {
        for (InputClass cls : inputClasses.values()) {
            cls.lookupNativeMethods(this, nameToFunctions);
        }
    }

    public void save(Path root) throws IOException {
        List<String> classNameList = new ArrayList<>();
        for (InputClass cls : inputClasses.values()) {
            if (cls.needsSaving()) {
                cls.save(root);
                classNameList.add(cls.getName());
            }
        }
        if (libraryClass.needsSaving()) {
            libraryClass.save(root);
            classNameList.add(libraryClass.getName());
        }
        for (InvokerClass invokerClass : libraryClass.getInvokerClasses()) {
            if (invokerClass.needsSaving()) {
                invokerClass.save(root);
                classNameList.add(invokerClass.getName());
            }
        }
        Logger.info("Saved %d classes into %s", classNameList.size(), root);
        if (options.verifyBytecode()) {
            BytecodeVerifier.verify(options, root, classNameList);
        }
    }

    public void dump() {
        libraryClass.dump();
    }

    public TypeDef getTypeDef(String name) {
        return typeDefFactory.getTypeDef(name);
    }

    public TypeDef getJavaLangObject() {
        return typeDefFactory.getJavaLangObject();
    }

    public TypeDef getJavaLangClass() {
        return typeDefFactory.getJavaLangClass();
    }

    public Options getOptions() {
        return options;
    }


    public boolean supportDirectCall(Function callee) {
        FunctionType functionType = callee.getFunctionType();
        if (functionType.isVarArg()) {
            return false;
        }
        LibraryClass libraryClass = getLibraryClass();
        LibraryMethod methodBody = libraryClass.getOrCreateDirectFunctionMethod(callee);
        LLVMToBytecode.generateLLVMFunctionMethod(this, methodBody, callee);
        return methodBody.getBytecodeType() == LibraryMethod.BytecodeType.Direct;
    }

    public boolean isJavaLangObject(TypeDef typeDef) {
        return typeDefFactory.getJavaLangObject().isSameType(typeDef);
    }

    public boolean isJavaLangClass(TypeDef typeDef) {
        return typeDefFactory.getJavaLangClass().isSameType(typeDef);
    }

    public boolean isJavaLangString(TypeDef typeDef) {
        return typeDefFactory.getJavaLangString().isSameType(typeDef);
    }
}