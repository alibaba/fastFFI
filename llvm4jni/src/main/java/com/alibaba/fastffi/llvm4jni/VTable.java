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

import com.alibaba.fastffi.llvm.Constant;
import com.alibaba.fastffi.llvm.ConstantArray;
import com.alibaba.fastffi.llvm.ConstantExpr;
import com.alibaba.fastffi.llvm.ConstantPointerNull;
import com.alibaba.fastffi.llvm.ConstantStruct;
import com.alibaba.fastffi.llvm.Function;
import com.alibaba.fastffi.llvm.GlobalObject;
import com.alibaba.fastffi.llvm.GlobalVariable;
import com.alibaba.fastffi.llvm.Module;
import com.alibaba.fastffi.llvm.StructType;
import com.alibaba.fastffi.llvm.Value;

import java.util.HashMap;
import java.util.Map;
import java.util.function.ObjIntConsumer;

public class VTable {

    static boolean printVTable = true; // Boolean.getBoolean("llvm4jni.printVTable");

    static Map<String, VTable> readVTable(Module module) {
        Map<String, VTable> nameToVTable = new HashMap<>();
        module.forEachGlobal( g -> {
            String name = g.getName();
            if (name.startsWith("_ZTV") && g.hasInitializer()) {
                VTable vTable = readVTable(g);
                if (vTable != null) {
                    nameToVTable.put(name, vTable);
                }
            }
        });
        return nameToVTable;
    }

    static VTable readVTable(GlobalVariable g) {
        String vtableSymbol = g.getName();
        Constant constant = g.getInitializer();
        if (ConstantStruct.isa(constant)) {
            ConstantStruct constantStruct = ConstantStruct.cast(constant);
            StructType structType = StructType.cast(constantStruct.getType());
            if (structType.getNumContainedTypes() != 1) {
                // throw new IllegalStateException("Not supported yet");
                return null;
            }
            Value v = constantStruct.getOperand(0);
            ConstantArray constantArray = ConstantArray.dyn_cast(v);
            if (constantArray.isNull()) {
                // throw new IllegalStateException("Not supported yet");
                return null;
            }

            int length = constantArray.getNumOperands();
            if (length <= 0) {
                // throw new IllegalStateException("Not supported yet");
                return null;
            }
            GlobalObject[] symbols = new GlobalObject[length];
            int typeInfoIndex = -1;
            String typeInfoName = null;
            for (int i = 0; i < length; i++) {
                v = constantArray.getOperand(i);
                if (v instanceof ConstantPointerNull) {
                    continue;
                } else if (v instanceof ConstantExpr) {
                    ConstantExpr constantExpr = (ConstantExpr) v;
                    if (constantExpr.getNumOperands() != 1) {
                        // throw new IllegalStateException("Not supported yet");
                        return null;
                    }
                    v = constantExpr.getOperand(0);
                    if (v instanceof GlobalVariable) {
                        String name = v.getName();
                        symbols[i] = (GlobalVariable) v;
                        if (name.startsWith("_ZTI")) {
                            if (typeInfoName == null) {
                                // _ZTIxxx vs _ZTVxxx
                                if (name.substring(4).equals(vtableSymbol.substring(4))) {
                                    typeInfoName = v.getName();
                                    typeInfoIndex = i;
                                } else {
                                    // throw new IllegalStateException("Unexpected TypeInfo: " + name + " for " + vtableSymbol);
                                    return null;
                                }
                            } else {
                                // throw new IllegalStateException("Two TypeInfo: " + name + " vs. " + typeInfoName);
                                return null;
                            }
                        }
                    } else if (v instanceof Function) {
                        symbols[i] = (Function) v;
                    } else {
                        // throw new IllegalStateException("Not supported yet");
                        return null;
                    }
                }
            }
            if (typeInfoIndex == -1) {
                // throw new IllegalStateException("Cannot find type info for " + vtableSymbol);
                return null;
            }
            if (printVTable) {
                Logger.debug("VTable: " + g.getName());
                for (int i = 0; i < length; i++) {
                    Logger.debug("%3d: %s", i, symbols[i] == null ? null : symbols[i].getName());
                }
            }
            return new VTable(g, typeInfoIndex, symbols);
        }
        return null;
    }

    private GlobalVariable vtableSymbol;
    /**
     * typeInfoIndex is the index in the LLVM constant.
     * According ABI, typeinfo has the -1 vtable index.
     */
    private int typeInfoIndex;
    /**
     * VTable index to Symbol
     */
    private GlobalObject[] symbols;

    public VTable(GlobalVariable vtableSymbol, int typeInfoIndex, GlobalObject[] symbols) {
        this.vtableSymbol = vtableSymbol;
        this.typeInfoIndex = typeInfoIndex;
        this.symbols = symbols;
    }

    public GlobalVariable getVTable() {
        return vtableSymbol;
    }

    public GlobalVariable getTypeInfo() {
        return (GlobalVariable) symbols[typeInfoIndex];
    }

    public void forEachFunction(ObjIntConsumer<Function> consumer) {
        for (int i = 0; i < symbols.length; i++) {
            GlobalObject go = symbols[i];
            if (go instanceof Function) {
                int index = i - (this.typeInfoIndex + 1);
                if (index < 0) {
                    throw new IllegalStateException("All vtable index must be non-negative.");
                }
                consumer.accept((Function) go, index);
            }
        }
    }

    public Function getFunctionAt(int vindex) {
        int cindex = vindex + this.typeInfoIndex + 1;
        GlobalObject object = symbols[cindex];
        if (object instanceof Function) {
            return (Function) object;
        }
        return null;
    }
}
