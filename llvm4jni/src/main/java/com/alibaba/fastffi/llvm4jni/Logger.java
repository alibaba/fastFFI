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

public class Logger {

    public static void initialize(Options options) {
        if (options.isDebug()) {
            setLevel(Level.DEBUG);
            return;
        }
        String loggerLevel = options.getLoggerLevel();
        Level level = Level.valueOf(loggerLevel);
        setLevel(level);
    }

    enum Level {
        NONE,
        ERROR,
        WARN,
        INFO,
        DEBUG;
    }

    private static Level level = Level.ERROR;

    public static Level getLevel() {
        return level;
    }

    public static void setLevel(Level level) {
        Logger.level = level;
    }

    public static boolean info() {
        return level.ordinal() >= Level.INFO.ordinal();
    }

    public static boolean warn() {
        return level.ordinal() >= Level.WARN.ordinal();
    }

    public static boolean error() {
        return level.ordinal() >= Level.ERROR.ordinal();
    }

    public static boolean debug() {
        return level.ordinal() >= Level.DEBUG.ordinal();
    }

    public static void info(String format, Object... args) {
        if (info()) {
            System.out.format("[INFO] " + format + "\n", args);
        }
    }

    public static void warn(String format, Object... args) {
        if (warn()) {
            System.out.format("[WARN] " + format + "\n", args);
        }
    }

    public static void error(String format, Object... args) {
        if (error()) {
            System.out.format("[ERROR] " + format + "\n", args);
        }
    }

    public static void debug(String format, Object... args) {
        if (debug()) {
            System.out.format("[DEBUG] " + format + "\n", args);
        }
    }

}
