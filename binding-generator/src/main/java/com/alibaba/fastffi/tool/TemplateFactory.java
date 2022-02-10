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

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TemplateFactory
{
    private Map<ClassName, Set<FFIType>> templates;
    private Map<ClassName, Set<ClassName>> depends;

    public TemplateFactory() {
        this.templates = new HashMap<>();
        this.depends = new HashMap<>();
    }

    public Map<ClassName, Set<FFIType>> getTemplates() {
        return this.templates;
    }

    public boolean isEmpty() {
        return this.templates.isEmpty();
    }

    public void add(ClassName template, FFIType args) {
        addToMapSet(templates, template, args);
    }

    private static <K,V> boolean addToMapSet(Map<K, Set<V>> map, K key, V value) {
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
}
