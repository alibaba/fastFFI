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

public class Logger
{

    public static void initialize(String loggerLevel) {
        initialize(Level.valueOf(loggerLevel));
    }

    public static void initialize(Level loggerLevel) {
        setLevel(loggerLevel);
    }

    enum Level {
        NONE,
        ERROR,
        WARN,
        INFO,
        DEBUG,
        VERBOSE;
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

    public static boolean verbose() {
        return level.ordinal() >= Level.VERBOSE.ordinal();
    }

    public static void info(String format, Object... args) {
        if (info()) {
            System.err.format("[INFO] " + format + "\n", args);
        }
    }

    public static void warn(String format, Object... args) {
        if (warn()) {
            System.err.format("[WARN] " + format + "\n", args);
        }
    }

    public static void error(String format, Object... args) {
        if (error()) {
            System.err.format("[ERROR] " + format + "\n", args);
        }
    }

    public static void debug(String format, Object... args) {
        if (debug()) {
            System.err.format("[DEBUG] " + format + "\n", args);
        }
    }

    public static void verbose(String format, Object... args) {
        if (verbose()) {
            System.err.format("[VERBOSE] " + format + "\n", args);
        }
    }
}
