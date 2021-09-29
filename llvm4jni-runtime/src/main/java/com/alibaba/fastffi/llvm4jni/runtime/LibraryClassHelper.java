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
package com.alibaba.fastffi.llvm4jni.runtime;

import com.kenai.jffi.Library;
import jnr.ffi.provider.Invoker;
import jnr.ffi.provider.jffi.LLVMIndirectInvokerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

public class LibraryClassHelper {

    static final LLVMIndirectInvokerFactory factory;

    static {
        factory = new LLVMIndirectInvokerFactory();
    }

    /**
     * TODO: This method is invoked at runtime. So we need to try global first.
     * Not sure why global cannot always find all symbols.
     * @param library
     * @param symbol
     * @return
     */
    public static long getSymbolAddress(Library library, String symbol) {
        long address = Library.getDefault().getSymbolAddress(symbol);
        if (address != 0L) {
            return address;
        }
        if (library == null) {
            throw new IllegalStateException("Cannot find symbol " + symbol + " in default library.");
        }
        address = library.getSymbolAddress(symbol);
        if (address == 0L) {
            throw new IllegalStateException("Cannot find symbol " + symbol + " in library " + library.toString());
        }
        return address;
    }

    public static void initializeLibraryClass(Class<?> cls) {
        Library library = null;
        {
            LibrarySymbol librarySymbol = cls.getAnnotation(LibrarySymbol.class);
            if (librarySymbol != null) {
                String libraryName = librarySymbol.value();
                library = Library.getCachedInstance(libraryName, Library.LAZY | Library.GLOBAL);
                if (library == null) {
                    throw new IllegalStateException("Cannot load library " + libraryName + " due to " + Library.getLastError());
                }
            } // will use default library only
        }
        long currentGlobalTop = 0L;
        Field[] fields = cls.getDeclaredFields();
        List<Field> constants = new ArrayList<>();
        for (Field field : fields) {
            int modifiers = field.getModifiers();
            if (!Modifier.isStatic(modifiers)) {
                continue;
            }
            GlobalSymbol globalSymbol = field.getAnnotation(GlobalSymbol.class);
            if (globalSymbol != null) {
                long address = getSymbolAddress(library, globalSymbol.value());
                setField(field, new RuntimeGlobal(globalSymbol.value(), address));
                continue;
            }
            ConstantSymbol constantSymbol = field.getAnnotation(ConstantSymbol.class);
            if (constantSymbol != null) {
                currentGlobalTop += constantSymbol.size();
                constants.add(field);
                continue;
            }
            FunctionSymbol functionSymbol = field.getAnnotation(FunctionSymbol.class);
            if (functionSymbol != null) {
                long address = getSymbolAddress(library, functionSymbol.value());
                setField(field, address);
                continue;
            }
            InvokerMark invokerMark = field.getAnnotation(InvokerMark.class);
            if (invokerMark != null) {
                Class<?> type = field.getType();
                Method invokeMethod = null;
                for (Method method : type.getDeclaredMethods()) {
                    if (method.getName().equals("invoke")) {
                        if (invokeMethod == null) {
                            invokeMethod = method;
                        } else {
                            throw new IllegalStateException("An invoker class " + type + " cannot have two invoke methods.");
                        }
                    }
                }
                if (invokeMethod == null) {
                    throw new IllegalStateException("No invoke method is found in " + type);
                }
                Invoker invoker = factory.createInvoker(invokeMethod);
                Object proxy =  Proxy.newProxyInstance(type.getClassLoader(),
                        new Class[]{type},
                        new DefaultInvokerInvocationHandler(invoker));
                setField(field, proxy);
                continue;
            }
            // TODO: simply ignore since other instrumentation framework may add fields to a library class
            // throw new IllegalStateException("Should not reach here: an unknown field " + field);
        }

        long memoryBase = JavaRuntime.allocate(currentGlobalTop);
        long newTop = 0;
        for (Field field : constants) {
            ConstantSymbol constantSymbol = field.getAnnotation(ConstantSymbol.class);
            long size = constantSymbol.size();
            long pointer = newTop + memoryBase;
            newTop += size;
            setField(field, RuntimeConstant.create(constantSymbol.value(), pointer));
        }
        if (newTop != currentGlobalTop) {
            throw new IllegalStateException("Top are different: got " + newTop +  ", expected " + currentGlobalTop);
        }
        // TODO: Add hooks to unload the memory.
    }

    static void setField(Field field, Object value) {
        try {
            field.setAccessible(true);
            field.set(null, value);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Cannot set value to " + field);
        }
    }
}
