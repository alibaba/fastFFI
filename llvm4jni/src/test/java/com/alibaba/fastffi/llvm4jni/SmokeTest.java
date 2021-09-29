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

import com.alibaba.fastffi.llvm4jni.runtime.JavaRuntime;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;

import static com.alibaba.fastffi.llvm.TestUtils.deleteDirectory;

public class SmokeTest {

    private static final String TARGET = "smokeTest";

    private static Products products;
    private static File output;
    private static Class<?> transformedClass;
    @BeforeClass
    public static void prepare() throws Exception {
        products = TestUtils.prepareBitcodeAndLibrary(TARGET);
        // convert
        output = Files.createTempDirectory("fastffi-smoke-test").toAbsolutePath().toFile();
        try {
            System.out.println("Bitcode at: " + products.bitcode);
            Main.main(new String[]{"-v", "ERROR", "-output", output.toString(),"-bc", products.bitcode, "-cp", TestUtils.RESOURCE_PATH, "-lib", products.library});
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw e;
        }

        // load new class
        GeneratedTestClassLoader cl = new GeneratedTestClassLoader(new URL[]{output.toURI().toURL()}, SmokeTest.class.getClassLoader());
        Class<?> clazz = cl.loadClass(SmokeTest.class.getName());
        Assert.assertNotEquals(clazz, SmokeTest.class);
        transformedClass = clazz;
    }

    @AfterClass
    public static void teardown() {
        if (output != null) {
            deleteDirectory(output);
        }
    }

    @Test
    public void testLoad() throws Exception {
        // load new class
        GeneratedTestClassLoader cl = new GeneratedTestClassLoader(new URL[]{output.toURI().toURL()}, SmokeTest.class.getClassLoader());
        Class<?> clazz = cl.loadClass(SmokeTest.class.getName());
        Assert.assertNotEquals(clazz, SmokeTest.class);
    }

    // FIXME
    @Test
    public void test() throws Exception {
        // check
        Object instance = transformedClass.newInstance();
        Method method = transformedClass.getMethod("test", int.class, int.class);
        Assert.assertFalse(Modifier.isNative(method.getModifiers()));
        int result = (Integer) method.invoke(instance, 10, 2);
        Assert.assertEquals(new SmokeTest().test(10, 2), result);
    }

    @Test
    public void testRemoveNullElements() throws Exception {
        Object instance = transformedClass.newInstance();
        Method method = transformedClass.getMethod("removeNullElements", Object[].class);
        Assert.assertFalse(Modifier.isNative(method.getModifiers()));
        Object[] input = new Object[]{
                "1",
                1,
                null,
                "2"
        };
        Object[] result = (Object[]) method.invoke(instance, (Object) input);
        Object[] resultJNI = new SmokeTest().removeNullElements(input);
        Assert.assertArrayEquals(resultJNI, result);
    }


    @Test
    public void testIsJavaUtilList() throws Exception {
        Object instance = transformedClass.newInstance();
        Method method = transformedClass.getMethod("isJavaUtilList", Object.class);
        Assert.assertFalse(Modifier.isNative(method.getModifiers()));
        {
            ArrayList<Object> input = new ArrayList<Object>();
            boolean result = (Boolean) method.invoke(instance, (Object) input);
            boolean resultJNI = new SmokeTest().isJavaUtilList(input);
            Assert.assertTrue(resultJNI == result);
        }
        {
            Object input = new Object();
            boolean result = (Boolean) method.invoke(instance, (Object) input);
            boolean resultJNI = new SmokeTest().isJavaUtilList(input);
            Assert.assertTrue(resultJNI == result);
        }
    }

    @Test
    public void testCreateArray() throws Exception {
        testCreateArray("createBooleanArray", boolean[].class);
        testCreateArray("createByteArray", byte[].class);
        testCreateArray("createCharArray", char[].class);
        testCreateArray("createShortArray", short[].class);
        testCreateArray("createIntArray", int[].class);
        testCreateArray("createLongArray", long[].class);
        testCreateArray("createFloatArray", float[].class);
        testCreateArray("createDoubleArray", double[].class);
    }

    private void testCreateArray(String methodName, Class<?> resultClass) throws Exception {
        Object instance = transformedClass.newInstance();
        Method method = transformedClass.getMethod(methodName, int.class);
        Assert.assertFalse(Modifier.isNative(method.getModifiers()));
        int arrayLength = 5;
        {
            Object result = method.invoke(instance, arrayLength);
            resultClass.isInstance(result);
            Assert.assertTrue(Array.getLength(result) == arrayLength);
        }
    }

    @Test
    public void testField() throws Exception {
        testField("Boolean", boolean.class, true, false);
        testField("Byte", byte.class, (byte) 1, (byte) 2);
        testField("Short", short.class, (short) 1, (short) 2);
        testField("Char", char.class, (char) 1, (char) 2);
        testField("Int", int.class, 1, 2);
        testField("Float", float.class, 1.0F, 2.0F);
        testField("Double", double.class,  1.0, 2.0);
        testField("Long", long.class, 1L, 2L);
        testField("Object", Object.class, new Object(), "2");
    }

    private void testField(String fieldTypeName, Class<?> fieldType, Object value1, Object value2) throws Exception {
        {
            Field field = transformedClass.getDeclaredField("instance" + fieldTypeName + "Field");
            Method get = transformedClass.getMethod("get" + fieldTypeName + "Field");
            Method set = transformedClass.getMethod("set" + fieldTypeName + "Field", fieldType);
            Object object = transformedClass.newInstance();
            testField(object, field, get, set, value1, value2);
        }
        {
            Field field = transformedClass.getDeclaredField("static" + fieldTypeName + "Field");
            Method get = transformedClass.getMethod("getStatic" + fieldTypeName + "Field");
            Method set = transformedClass.getMethod("setStatic" + fieldTypeName + "Field", fieldType);
            testField(null, field, get, set, value1, value2);
        }
    }

    private void testField(Object object, Field field, Method get, Method set, Object value1, Object value2) throws Exception {
        Assert.assertFalse(Modifier.isNative(get.getModifiers()));
        Assert.assertFalse(Modifier.isNative(set.getModifiers()));
        field.set(object, value1);
        Assert.assertTrue(value1.equals(get.invoke(object)));
        set.invoke(object, value2);
        Assert.assertTrue(value2.equals(field.get(object)));
    }

    @Test
    public void testMethod() throws Exception {
        testMethod("Boolean", boolean.class, true, false);
        testMethod("Byte", byte.class, (byte) 1, (byte) 2);
        testMethod("Short", short.class, (short) 1, (short) 2);
        testMethod("Char", char.class, (char) 1, (char) 2);
        testMethod("Int", int.class, 1, 2);
        testMethod("Float", float.class, 1.0F, 2.0F);
        testMethod("Double", double.class,  1.0, 2.0);
        testMethod("Long", long.class, 1L, 2L);
        testMethod("Object", Object.class, new Object(), "2");
    }

    private void testMethod(String fieldTypeName, Class<?> fieldType, Object value1, Object value2) throws Exception {
        {
            Field field = transformedClass.getDeclaredField("instance" + fieldTypeName + "Field");
            Method get = transformedClass.getMethod("get" + fieldTypeName);
            Method set = transformedClass.getMethod("set" + fieldTypeName, fieldType);
            Object object = transformedClass.newInstance();
            testField(object, field, get, set, value1, value2);
        }
        {
            Field field = transformedClass.getDeclaredField("instance" + fieldTypeName + "Field");
            Method get = transformedClass.getMethod("getNonvirtual" + fieldTypeName);
            Method set = transformedClass.getMethod("setNonvirtual" + fieldTypeName, fieldType);
            Object object = transformedClass.newInstance();
            testField(object, field, get, set, value1, value2);
        }
        {
            Field field = transformedClass.getDeclaredField("static" + fieldTypeName + "Field");
            Method get = transformedClass.getMethod("getStatic" + fieldTypeName);
            Method set = transformedClass.getMethod("setStatic" + fieldTypeName, fieldType);
            testField(null, field, get, set, value1, value2);
        }
    }

    @Test
    public void testSameObject() throws Exception {
        Method method = transformedClass.getMethod("isSameObject", Object.class, Object.class);
        Assert.assertFalse(Modifier.isNative(method.getModifiers()));
        Assert.assertTrue((Boolean) method.invoke(null, this, this));
        Assert.assertFalse((Boolean) method.invoke(null, this, new Object()));
    }

    @Test
    public void testCreateObject() throws Exception {
        {
            Method method = transformedClass.getMethod("newObject");
            Assert.assertFalse(Modifier.isNative(method.getModifiers()));
            ObjectTest result = (ObjectTest) method.invoke(null);
            Assert.assertTrue(result != null);
            Assert.assertTrue(result.nonNull != null);
            Assert.assertTrue(result.object == null);
        }
        {
            Method method = transformedClass.getMethod("newObject2", Object.class);
            Assert.assertFalse(Modifier.isNative(method.getModifiers()));
            ObjectTest result = (ObjectTest) method.invoke(null, this);
            Assert.assertTrue(result != null);
            Assert.assertTrue(result.getClass().equals(ObjectTest.class));
            Assert.assertTrue(result.nonNull != null);
            Assert.assertTrue(result.object == this);
        }
        {
            Method method = transformedClass.getMethod("allocObject");
            Assert.assertFalse(Modifier.isNative(method.getModifiers()));
            ObjectTest result = (ObjectTest) method.invoke(null);
            Assert.assertTrue(result != null);
            Assert.assertTrue(result.getClass().equals(ObjectTest.class));
            Assert.assertTrue(result.nonNull == null);
            Assert.assertTrue(result.object == null);
        }
    }

    public static class ObjectTest {
        public final Object nonNull = new Object();
        public Object object;
        public ObjectTest() {}
        public ObjectTest(Object o) { this.object = o; }
    }

    public static native ObjectTest newObject();
    public static native ObjectTest newObject2(Object o);
    public static native ObjectTest allocObject();

    // Native stubs
    public native int test(int i, int j);

    /**
     * Test
     * <ul>
     *     <li>FindClass</li>
     *     <li>GetArrayLength</li>
     *     <li>GetObjectArrayElement</li>
     *     <li>FindClass</li>
     *     <li>NewObjectArray</li>
     *     <li>SetObjectArrayElement</li>
     * </ul>
     *
     * @param array
     * @return
     */
    public native Object[] removeNullElements(Object[] array);

    /**
     * test IsInstanceOf
     * @param o
     * @return
     */
    public native boolean isJavaUtilList(Object o);

    // test New<NativeType>Array
    public native boolean[] createBooleanArray(int size);
    public native char[] createCharArray(int size);
    public native float[] createFloatArray(int size);
    public native double[] createDoubleArray(int size);
    public native byte[] createByteArray(int size);
    public native short[] createShortArray(int size);
    public native int[] createIntArray(int size);
    public native long[] createLongArray(int size);

    public static boolean staticBooleanField;
    public static byte staticByteField;
    public static char staticCharField;
    public static short staticShortField;
    public static int staticIntField;
    public static long staticLongField;
    public static float staticFloatField;
    public static double staticDoubleField;
    public static Object staticObjectField;


    public static native boolean getStaticBooleanField();
    public static native void setStaticBooleanField(boolean v);
    public static native byte getStaticByteField();
    public static native void setStaticByteField(byte v);
    public static native char getStaticCharField();
    public static native void setStaticCharField(char v);
    public static native short getStaticShortField();
    public static native void setStaticShortField(short v);
    public static native int getStaticIntField();
    public static native void setStaticIntField(int size);
    public static native long getStaticLongField();
    public static native void setStaticLongField(long v);
    public static native float getStaticFloatField();
    public static native void setStaticFloatField(float v);
    public static native double getStaticDoubleField();
    public static native void setStaticDoubleField(double v);
    public static native Object getStaticObjectField();
    public static native void setStaticObjectField(Object v);

    public boolean instanceBooleanField;
    public byte instanceByteField;
    public char instanceCharField;
    public short instanceShortField;
    public int instanceIntField;
    public long instanceLongField;
    public float instanceFloatField;
    public double instanceDoubleField;
    public Object instanceObjectField;

    public native boolean getBooleanField();
    public native void setBooleanField(boolean v);
    public native byte getByteField();
    public native void setByteField(byte v);
    public native char getCharField();
    public native void setCharField(char v);
    public native short getShortField();
    public native void setShortField(short v);
    public native int getIntField();
    public native void setIntField(int size);
    public native long getLongField();
    public native void setLongField(long v);
    public native float getFloatField();
    public native void setFloatField(float v);
    public native double getDoubleField();
    public native void setDoubleField(double v);
    public native Object getObjectField();
    public native void setObjectField(Object v);

    public static native boolean isSameObject(Object obj1, Object obj2);

    @Test
    public void testGetObjectClass() throws Exception {
        Method method = transformedClass.getMethod("getObjectClass", Object.class);
        Assert.assertFalse(Modifier.isNative(method.getModifiers()));
        Assert.assertTrue(method.invoke(null, "12345") == String.class);
    }
    public static native Class<?> getObjectClass(Object obj);

    @Test
    public void testGetSuperClass() throws Exception {
        {
            Method method = transformedClass.getMethod("getSuperClass", Class.class);
            Assert.assertFalse(Modifier.isNative(method.getModifiers()));
            Assert.assertTrue(method.invoke(null, String.class) == Object.class);
            Assert.assertTrue(method.invoke(null, Object.class) == null);
        }
        {
            Method method = transformedClass.getMethod("getStringSuperClass");
            Assert.assertFalse(Modifier.isNative(method.getModifiers()));
            Assert.assertTrue(method.invoke(null) == Object.class);
        }
        {
            Method method = transformedClass.getMethod("getObjectSuperClass");
            Assert.assertFalse(Modifier.isNative(method.getModifiers()));
            Assert.assertTrue(method.invoke(null) == null);
        }
    }

    public static native Class<?> getObjectSuperClass();
    public static native Class<?> getStringSuperClass();
    public static native Class<?> getSuperClass(Class<?> cls);

    // test CallXX
    public static native boolean getStaticBoolean();
    public static native void setStaticBoolean(boolean v);
    public static native byte getStaticByte();
    public static native void setStaticByte(byte v);
    public static native char getStaticChar();
    public static native void setStaticChar(char v);
    public static native short getStaticShort();
    public static native void setStaticShort(short v);
    public static native int getStaticInt();
    public static native void setStaticInt(int size);
    public static native long getStaticLong();
    public static native void setStaticLong(long v);
    public static native float getStaticFloat();
    public static native void setStaticFloat(float v);
    public static native double getStaticDouble();
    public static native void setStaticDouble(double v);
    public static native Object getStaticObject();
    public static native void setStaticObject(Object v);

    public native boolean getBoolean();
    public native void setBoolean(boolean v);
    public native byte getByte();
    public native void setByte(byte v);
    public native char getChar();
    public native void setChar(char v);
    public native short getShort();
    public native void setShort(short v);
    public native int getInt();
    public native void setInt(int size);
    public native long getLong();
    public native void setLong(long v);
    public native float getFloat();
    public native void setFloat(float v);
    public native double getDouble();
    public native void setDouble(double v);
    public native Object getObject();
    public native void setObject(Object v);

    public native boolean getNonvirtualBoolean();
    public native void setNonvirtualBoolean(boolean v);
    public native byte getNonvirtualByte();
    public native void setNonvirtualByte(byte v);
    public native char getNonvirtualChar();
    public native void setNonvirtualChar(char v);
    public native short getNonvirtualShort();
    public native void setNonvirtualShort(short v);
    public native int getNonvirtualInt();
    public native void setNonvirtualInt(int size);
    public native long getNonvirtualLong();
    public native void setNonvirtualLong(long v);
    public native float getNonvirtualFloat();
    public native void setNonvirtualFloat(float v);
    public native double getNonvirtualDouble();
    public native void setNonvirtualDouble(double v);
    public native Object getNonvirtualObject();
    public native void setNonvirtualObject(Object v);

    public static native int getJNIVersion();

    @Test
    public void testGetVersion() throws Exception {
        Method method = transformedClass.getMethod("getJNIVersion");
        Assert.assertFalse(Modifier.isNative(method.getModifiers()));
        Assert.assertTrue(method.invoke(null).equals(JavaRuntime.jniGetVersion()));
    }
}
