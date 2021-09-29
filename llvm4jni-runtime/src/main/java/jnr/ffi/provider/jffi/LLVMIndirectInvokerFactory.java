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
/*
 * Copyright (C) 2008-2012 Wayne Meissner
 *
 * This file is part of the JNR project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jnr.ffi.provider.jffi;

import com.kenai.jffi.CallContext;
import com.kenai.jffi.HeapInvocationBuffer;
import jnr.ffi.CallingConvention;
import jnr.ffi.Runtime;
import jnr.ffi.mapper.DefaultSignatureType;
import jnr.ffi.mapper.FromNativeContext;
import jnr.ffi.mapper.MethodResultContext;
import jnr.ffi.mapper.SignatureType;
import jnr.ffi.mapper.SignatureTypeMapper;
import jnr.ffi.provider.InvocationSession;
import jnr.ffi.provider.Invoker;
import jnr.ffi.provider.NullTypeMapper;
import jnr.ffi.provider.ParameterType;
import jnr.ffi.provider.ResultType;

import java.lang.reflect.Method;

import static jnr.ffi.provider.jffi.DefaultInvokerFactory.getMarshaller;
import static jnr.ffi.provider.jffi.InvokerUtil.getCallContext;
import static jnr.ffi.provider.jffi.InvokerUtil.getParameterTypes;

public class LLVMIndirectInvokerFactory {

    private final Runtime runtime;

    public LLVMIndirectInvokerFactory() {
        runtime = NativeRuntime.getInstance();
    }

    public Invoker createInvoker(Method method) {
        SignatureTypeMapper typeMapper = new NullTypeMapper();
        FromNativeContext resultContext = new MethodResultContext(NativeRuntime.getInstance(), method);
        SignatureType signatureType = DefaultSignatureType.create(method.getReturnType(), resultContext);
        ResultType resultType = InvokerUtil.getResultType(runtime, method.getReturnType(),
                resultContext.getAnnotations(), typeMapper.getFromNativeType(signatureType, resultContext),
                resultContext);

        IndirectInvoker functionInvoker = getIndirectInvoker(resultType);

        ParameterType[] parameterTypes = getParameterTypes(runtime, typeMapper, method);
        //Allow individual methods to set the calling convention to stdcall
        CallingConvention callingConvention = CallingConvention.DEFAULT;

        boolean saveError = false; // LibraryLoader.saveError(libraryOptions, NativeFunction.hasSaveError(method), NativeFunction.hasIgnoreError(method));

        if (method.isVarArgs()) {
            throw new RuntimeException();
        } else {
            CallContext callContext = getCallContext(resultType, parameterTypes, callingConvention, saveError);

            DefaultInvokerFactory.Marshaller[] marshallers = new DefaultInvokerFactory.Marshaller[parameterTypes.length];
            for (int i = 0; i < marshallers.length; ++i) {
                marshallers[i] = getMarshaller(parameterTypes[i]);
            }

            return new DefaultInvoker(runtime, callContext, functionInvoker, marshallers);
        }
    }

    interface IndirectInvoker {
        Object invoke(long functionAddress, CallContext callContext, HeapInvocationBuffer buffer);
    }

    static abstract class BaseIndirectInvoker implements IndirectInvoker {
        static com.kenai.jffi.Invoker invoker = com.kenai.jffi.Invoker.getInstance();
    }

    static class VoidInvoker extends BaseIndirectInvoker {
        static IndirectInvoker INSTANCE = new VoidInvoker();
        public final Object invoke(long functionAddress, CallContext callContext, HeapInvocationBuffer buffer) {
            invoker.invokeInt(callContext, functionAddress, buffer);
            return null;
        }
    }

    static class BooleanInvoker extends BaseIndirectInvoker {
        static IndirectInvoker INSTANCE = new BooleanInvoker();
        public final Object invoke(long functionAddress, CallContext callContext, HeapInvocationBuffer buffer) {
            return ((byte) invoker.invokeInt(callContext, functionAddress, buffer)) != 0;
        }
    }

    static class IntInvoker extends BaseIndirectInvoker {
        static final IndirectInvoker INSTANCE = new IntInvoker();
        public final Object invoke(long functionAddress, CallContext callContext, HeapInvocationBuffer buffer) {
            return invoker.invokeInt(callContext, functionAddress, buffer);
        }
    }

    static class ByteInvoker extends BaseIndirectInvoker {
        static final IndirectInvoker INSTANCE = new ByteInvoker();
        public final Object invoke(long functionAddress, CallContext callContext, HeapInvocationBuffer buffer) {
            return (byte) invoker.invokeInt(callContext, functionAddress, buffer);
        }
    }

    static class ShortInvoker extends BaseIndirectInvoker {
        static final IndirectInvoker INSTANCE = new ShortInvoker();
        public final Object invoke(long functionAddress, CallContext callContext, HeapInvocationBuffer buffer) {
            return (short) invoker.invokeInt(callContext, functionAddress, buffer);
        }
    }

    static class LongInvoker extends BaseIndirectInvoker {
        static final IndirectInvoker INSTANCE = new LongInvoker();
        public final Object invoke(long functionAddress, CallContext callContext, HeapInvocationBuffer buffer) {
            return invoker.invokeLong(callContext, functionAddress, buffer);
        }
    }

    static class FloatInvoker extends BaseIndirectInvoker {
        static final IndirectInvoker INSTANCE = new FloatInvoker();
        public final Object invoke(long functionAddress, CallContext callContext, HeapInvocationBuffer buffer) {
            return invoker.invokeFloat(callContext, functionAddress, buffer);
        }
    }
    static class DoubleInvoker extends BaseIndirectInvoker {
        static final IndirectInvoker INSTANCE = new DoubleInvoker();
        public final Object invoke(long functionAddress, CallContext callContext, HeapInvocationBuffer buffer) {
            return invoker.invokeDouble(callContext, functionAddress, buffer);
        }
    }

    private static IndirectInvoker getIndirectInvoker(ResultType resultType) {
        Class returnType = resultType.effectiveJavaType();
        if (Void.class.isAssignableFrom(returnType) || void.class == returnType) {
            return VoidInvoker.INSTANCE;
        } else if (Boolean.class.isAssignableFrom(returnType) || boolean.class == returnType) {
            return BooleanInvoker.INSTANCE;
        } else if (Byte.class.isAssignableFrom(returnType) || byte.class == returnType) {
            return ByteInvoker.INSTANCE;
        } else if (Short.class.isAssignableFrom(returnType) || short.class == returnType) {
            return ShortInvoker.INSTANCE;
        } else if (Integer.class.isAssignableFrom(returnType) || int.class == returnType) {
            return IntInvoker.INSTANCE;
        } else if (Long.class.isAssignableFrom(returnType) || long.class == returnType) {
            return LongInvoker.INSTANCE;
        } else if (Float.class.isAssignableFrom(returnType) || float.class == returnType) {
            return FloatInvoker.INSTANCE;
        } else if (Double.class.isAssignableFrom(returnType) || double.class == returnType) {
            return DoubleInvoker.INSTANCE;
        } else {
            throw new IllegalArgumentException("Unknown return type: " + returnType);
        }
    }

    static class DefaultInvoker implements jnr.ffi.provider.Invoker {
        protected final jnr.ffi.Runtime runtime;
        final CallContext callContext;
        final IndirectInvoker functionInvoker;
        final DefaultInvokerFactory.Marshaller[] marshallers;

        DefaultInvoker(jnr.ffi.Runtime runtime, CallContext callContext, IndirectInvoker invoker, DefaultInvokerFactory.Marshaller[] marshallers) {
            this.runtime = runtime;
            this.callContext = callContext;
            this.functionInvoker = invoker;
            this.marshallers = marshallers;
        }

        public final Object invoke(Object self, Object[] parameters) {
            InvocationSession session = new InvocationSession();
            HeapInvocationBuffer buffer = new HeapInvocationBuffer(callContext);
            long functionAddress = ((Long) parameters[0]).longValue();
            try {
                if (parameters != null) {
                    // skip the first parameter of function
                    for (int i = 1; i < parameters.length; ++i) {
                        marshallers[i].marshal(session, buffer, parameters[i]);
                    }
                }

                return functionInvoker.invoke(functionAddress, callContext, buffer);
            } finally {
                session.finish();
            }
        }
    }
}
