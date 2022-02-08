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
