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

public class Context {

    public static boolean HANDLE_EXCEPTION = Boolean.getBoolean("com.alibaba.fastffi.handleException");

    /**
     * Manual boxing will replace Integer.valueOf to new Integer
     * Enabled by default.
     */
    public static boolean MANUAL_BOXING = Boolean.parseBoolean(System.getProperty("com.alibaba.fastffi.manualBoxing", "true"));

    public static boolean CC_TO_CLASS_PATH = Boolean.getBoolean("CC2CLASSPATH");

    public static boolean STRICT_TYPE_CHECK = Boolean.getBoolean("com.alibaba.fastffi.strictTypeCheck");
    public static boolean NULL_RETURN_VALUE_CHECK = Boolean.parseBoolean(System.getProperty("com.alibaba.fastffi.nullReturnValueCheck", "true"));

}
