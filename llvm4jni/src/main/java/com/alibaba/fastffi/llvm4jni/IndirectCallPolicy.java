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

import com.alibaba.fastffi.llvm.CallBase;

public interface IndirectCallPolicy {

    static IndirectCallPolicy createIndirectCallPolicy(Options options) {
        String policy = options.supportIndirectCall();
        switch (policy) {
            case "simple":
                return new SimpleIndirectCallPolicy();
            case "true":
            case "all":
                return IndirectCallPolicy.ALL;
            case "false":
            case "none":
            default:
                return IndirectCallPolicy.NONE;
        }
    }

    IndirectCallPolicy ALL = new IndirectCallPolicy() {
        @Override
        public boolean supportIndirectCall(Universe universe, CallBase inst) {
            return true;
        }
    };

    IndirectCallPolicy NONE = new IndirectCallPolicy() {
        @Override
        public boolean supportIndirectCall(Universe universe, CallBase inst) {
            return false;
        }
    };

    boolean supportIndirectCall(Universe universe, CallBase inst);
}
