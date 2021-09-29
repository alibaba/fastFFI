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

import com.alibaba.fastffi.llvm4jni.type.FieldTypeDef;
import com.alibaba.fastffi.llvm4jni.type.MethodTypeDef;
import com.alibaba.fastffi.llvm4jni.type.TypeDef;
import com.alibaba.fastffi.llvm4jni.type.TypeDefException;
import com.alibaba.fastffi.llvm4jni.type.TypeDefFactory;
import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Path;

public class TypeDefTest {

    void testExact(TypeDef def) {
        TypeDef exactDef = def.getExact();
        if (def.isPrimitive()) {
            Assert.assertTrue(def == exactDef);
            return;
        }
        Assert.assertTrue(def != exactDef);
        Assert.assertFalse(def.equals(exactDef));
        // TODO: need proof
        Assert.assertTrue(def.hashCode() != exactDef.hashCode());
        Assert.assertTrue(exactDef.isExact());
        Assert.assertFalse(def.isExact());
        Assert.assertEquals(def.getName(), exactDef.getName());
        Assert.assertEquals(def.getDescriptor(), exactDef.getDescriptor());
        Assert.assertEquals(def.getInternalName(), exactDef.getInternalName());
        Assert.assertTrue(exactDef.getExact() == exactDef);
        Assert.assertTrue(exactDef.isSameType(def));
    }

    @Test
    public void test() {
        TypeDefFactory typeDefFactory = TypeDefFactory.create(new Path[0]);
        TypeDef javaLangObject = typeDefFactory.getJavaLangObject();
        TypeDef javaLangString = typeDefFactory.getJavaLangString();
        TypeDef javaLangThrowable = typeDefFactory.getJavaLangThrowable();
        TypeDef javaLangClass = typeDefFactory.getJavaLangClass();
        {
            try {
                typeDefFactory.getTypeDef("LNotExist;");
                Assert.fail("should throw exception");
            } catch (TypeDefException e) {}
        }
        {
            testExact(javaLangClass);
        }
        {
            TypeDef def = typeDefFactory.getTypeDef("I");
            testExact(def);
            Assert.assertFalse(def.isAbstract());
            Assert.assertFalse(def.isArray());
            Assert.assertTrue(def.isPrimitive());
            Assert.assertFalse(def.isInterface());
            Assert.assertTrue(def.getInternalName().equals("I"));
            Assert.assertTrue(def.getName().equals("int"));
            Assert.assertNull(def.getSuperType());
            Assert.assertTrue(def.isAssignableFrom(def));
            Assert.assertTrue(def.getArrayType().isSameType(typeDefFactory.getTypeDef("[I")));
            Assert.assertTrue(def.getComponentTypeDef() == null);
        }
        {
            TypeDef def = typeDefFactory.getTypeDef("[I");
            testExact(def);
            Assert.assertFalse(def.isAbstract());
            Assert.assertTrue(def.isArray());
            Assert.assertFalse(def.isPrimitive());
            Assert.assertFalse(def.isInterface());
            Assert.assertTrue(def.getInternalName().equals("[I"));
            Assert.assertTrue(def.getName().equals("[I"));
            Assert.assertTrue(def.getSuperType().isSameType(javaLangObject));
            Assert.assertTrue(def.isAssignableFrom(def));
            Assert.assertTrue(def.getComponentTypeDef().isSameType(typeDefFactory.getTypeDef("I")));
        }
        {
            TypeDef def = typeDefFactory.getTypeDef("Ljava/lang/String;");
            testExact(def);
            Assert.assertTrue(def.isSameType(javaLangString));
            Assert.assertFalse(def.isAbstract());
            Assert.assertFalse(def.isArray());
            Assert.assertFalse(def.isPrimitive());
            Assert.assertFalse(def.isInterface());
            Assert.assertTrue(def.getInternalName().equals("java/lang/String"));
            Assert.assertTrue(def.getName().equals("java.lang.String"));
            Assert.assertTrue(def.getSuperType().isSameType(javaLangObject));
            Assert.assertTrue(def.isAssignableFrom(def));
            Assert.assertTrue(def.getComponentTypeDef() == null);
        }
        {
            TypeDef def = typeDefFactory.getTypeDef("[Ljava/lang/String;");
            testExact(def);
            Assert.assertFalse(def.isAbstract());
            Assert.assertTrue(def.isArray());
            Assert.assertFalse(def.isPrimitive());
            Assert.assertFalse(def.isInterface());
            Assert.assertTrue(def.getInternalName().equals("[Ljava/lang/String;"));
            Assert.assertTrue(def.getName().equals("[Ljava.lang.String;"));
            Assert.assertTrue(def.getSuperType().isSameType(javaLangObject));
            Assert.assertTrue(def.isAssignableFrom(def));
            Assert.assertTrue(def.getComponentTypeDef().isSameType(javaLangString));
        }
        {
            TypeDef def = typeDefFactory.getTypeDef("Ljava/lang/RuntimeException;");
            testExact(def);
            Assert.assertFalse(def.isAbstract());
            Assert.assertFalse(def.isArray());
            Assert.assertFalse(def.isPrimitive());
            Assert.assertFalse(def.isInterface());
            Assert.assertTrue(def.getInternalName().equals("java/lang/RuntimeException"));
            Assert.assertTrue(def.getName().equals("java.lang.RuntimeException"));
            Assert.assertTrue(def.isAssignableFrom(def));
            Assert.assertTrue(javaLangThrowable.isAssignableFrom(def));
            Assert.assertTrue(def.getComponentTypeDef() == null);
        }
        {
            TypeDef def = typeDefFactory.getTypeDef("Ljava/lang/CharSequence;");
            Assert.assertTrue(def.isAbstract());
            Assert.assertFalse(def.isArray());
            Assert.assertFalse(def.isPrimitive());
            Assert.assertTrue(def.isInterface());
            Assert.assertTrue(def.getInternalName().equals("java/lang/CharSequence"));
            Assert.assertTrue(def.getName().equals("java.lang.CharSequence"));
            Assert.assertTrue(def.isAssignableFrom(typeDefFactory.getJavaLangString()));
            Assert.assertTrue(def.getComponentTypeDef() == null);
        }
        {
            MethodTypeDef methodTypeDef = javaLangObject.getMethodTypeDef("getClass", "()Ljava/lang/Class;");
            Assert.assertFalse(methodTypeDef.isPrivate());
            Assert.assertFalse(methodTypeDef.isStatic());
            Assert.assertFalse(methodTypeDef.isVariadic());
            Assert.assertTrue(methodTypeDef.name().equals("getClass"));
            Assert.assertTrue(methodTypeDef.descriptor().equals("()Ljava/lang/Class;"));
            Assert.assertTrue(methodTypeDef.owner().isSameType(javaLangObject));
            Assert.assertTrue(methodTypeDef.parameterTypes().length == 0);
            Assert.assertTrue(javaLangClass.isSameType(methodTypeDef.returnType()));
        }
        {
            TypeDef def = typeDefFactory.getTypeDef("Ljava/lang/System;");
            MethodTypeDef methodTypeDef = def.getMethodTypeDef("nanoTime", "()J");
            Assert.assertFalse(methodTypeDef.isPrivate());
            Assert.assertTrue(methodTypeDef.isStatic());
            Assert.assertFalse(methodTypeDef.isVariadic());
            Assert.assertTrue(methodTypeDef.name().equals("nanoTime"));
            Assert.assertTrue(methodTypeDef.descriptor().equals("()J"));
            Assert.assertTrue(methodTypeDef.parameterTypes().length == 0);
            Assert.assertTrue(methodTypeDef.owner().isSameType(def));
            FieldTypeDef fieldTypeDef = def.getFieldTypeDef("out", "Ljava/io/PrintStream;");
            Assert.assertFalse(fieldTypeDef.isPrivate());
            Assert.assertTrue(fieldTypeDef.isStatic());
            Assert.assertTrue(fieldTypeDef.name().equals("out"));
            Assert.assertTrue(fieldTypeDef.descriptor().equals("Ljava/io/PrintStream;"));
            Assert.assertTrue(fieldTypeDef.type().isSameType(typeDefFactory.getTypeDef("Ljava/io/PrintStream;")));
            Assert.assertTrue(fieldTypeDef.owner().isSameType(def));
            Assert.assertTrue(def.getComponentTypeDef() == null);
        }
        {
            MethodTypeDef methodTypeDef = javaLangString.getMethodTypeDef("format", "(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;");
            Assert.assertFalse(methodTypeDef.isPrivate());
            Assert.assertTrue(methodTypeDef.isStatic());
            Assert.assertTrue(methodTypeDef.isVariadic());
            Assert.assertTrue(methodTypeDef.name().equals("format"));
            Assert.assertTrue(methodTypeDef.descriptor().equals("(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;"));
            Assert.assertTrue(methodTypeDef.parameterTypes().length == 2);
            Assert.assertTrue(methodTypeDef.owner().isSameType(javaLangString));
        }
    }
}
