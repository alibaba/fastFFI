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
package com.alibaba.fastffi.annotation.test;

import com.alibaba.fastffi.annotation.AnnotationProcessor;
import com.alibaba.fastffi.FFITypeFactory;
import com.google.testing.compile.Compilation;
import com.google.testing.compile.JavaFileObjects;
import org.junit.Test;

import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static com.google.testing.compile.CompilationSubject.assertThat;
import static com.google.testing.compile.Compiler.javac;
import static org.junit.Assert.assertTrue;

public class FunctionTemplateTest {

    @Test
    public void test() {
        TestFunctionTemplate.Factory factory = FFITypeFactory.getFactory("test::TestFunctionTemplate");
        TestFunctionTemplate test = factory.create();

        {
            Byte ret = test.getValueNoSuffix(Byte.valueOf((byte) 2));
            assertTrue(ret != null && ret.intValue() == 4);
        }
        {
            Integer ret = test.getValueNoSuffix(Integer.valueOf((byte) 2));
            assertTrue(ret != null && ret.intValue() == 4);
        }

        TestFunctionTemplateGen testGen = (TestFunctionTemplateGen) test;
        {
            Byte ret = testGen.getValueNoSuffix(Byte.valueOf((byte) 2));
            assertTrue(ret != null && ret.intValue() == 4);
        }
        {
            Integer ret = testGen.getValueNoSuffix(Integer.valueOf((byte) 2));
            assertTrue(ret != null && ret.intValue() == 4);
        }
        {
            Byte ret = testGen.getValueSkip((Byte) null);
            assertTrue(ret != null && ret.intValue() == 16);
        }
        {
            Integer ret = testGen.getValueSkip((Integer) null);
            assertTrue(ret != null && ret.intValue() == 16);
        }
        test.delete();
    }

    @Test
    public void testPerformance() {
        TestFunctionTemplate.Factory factory = FFITypeFactory.getFactory("test::TestFunctionTemplate");
        TestFunctionTemplate test = factory.create();

        long t1 = System.nanoTime();
        int total = 10_000_000;
        for (int i = 0; i < total; i++) {
            test.getValueNoSuffix(new Byte((byte) 2));
            test.getValueNoSuffix(new Integer(2));
        }
        long t2 = System.nanoTime();
        TestFunctionTemplateGen testGen = (TestFunctionTemplateGen) test;
        for (int i = 0; i < total; i++) {
            testGen.getValueNoSuffix(new Byte((byte) 2));
            testGen.getValueNoSuffix(new Integer(2));
        }
        long t3 = System.nanoTime();
        System.out.format("instanceof(%sms) vs. interface (%sms): %d%%\n", TimeUnit.NANOSECONDS.toMillis(t2 - t1),
                TimeUnit.NANOSECONDS.toMillis(t3 - t2), ((t2 - t1) - (t3 - t2)) * 100 / (t3 - t2));
        test.delete();
    }

    @Test
    public void testFFIFunGen() throws IOException {

        JavaFileObject file1 = JavaFileObjects.forSourceLines(
                "test.FFIFunGenTest",
                "package test;",
                "import com.alibaba.fastffi.*;",
                "@FFIGenBatch({",
                "  @FFIGen(type = \"" + TestFunctionTemplate.class.getName() +"\",",
                "    functionTemplates = { @FFIFunGen(",
                "    name = \"getValueNoSuffix\",",
                "    returnType = \"T\",",
                "    parameterTypes = {\"T\"},",
                "    templates = {@CXXTemplate(cxx=\"double\", java=\"Double\")}",
                "    )}", // end of functionTemplates and FFIFunGen
                "  )", // end of FFIGen
                "})", // end of FFIGenBatch
                "public interface FFIFunGenTest {",
                "}");

        {
            Compilation compilation =
                    javac()
                            .withProcessors(new AnnotationProcessor())
                            .compile(file1);
            checkCompilation(compilation);
        }
    }

    void checkCompilation(Compilation compilation) throws IOException {
        assertThat(compilation).succeeded();
        {
            String content = compilation.generatedFile(StandardLocation.SOURCE_OUTPUT, "com/alibaba/fastffi/annotation/test/TestFunctionTemplateGen.java")
                    .get()
                    .getCharContent(true)
                    .toString();

            String expected = "package com.alibaba.fastffi.annotation.test;\n" +
                    "\n" +
                    "import com.alibaba.fastffi.CXXHead;\n" +
                    "import com.alibaba.fastffi.CXXTemplate;\n" +
                    "import com.alibaba.fastffi.CXXTemplates;\n" +
                    "import com.alibaba.fastffi.CXXValue;\n" +
                    "import com.alibaba.fastffi.FFIFunGen;\n" +
                    "import com.alibaba.fastffi.FFIGen;\n" +
                    "import com.alibaba.fastffi.FFINameAlias;\n" +
                    "import com.alibaba.fastffi.FFISkip;\n" +
                    "import com.alibaba.fastffi.FFISynthetic;\n" +
                    "import com.alibaba.fastffi.FFITypeAlias;\n" +
                    "import java.lang.Byte;\n" +
                    "import java.lang.Double;\n" +
                    "import java.lang.Integer;\n" +
                    "\n" +
                    "@CXXHead(\"ffitest.h\")\n" +
                    "@FFITypeAlias(\"test::TestFunctionTemplate\")\n" +
                    "@FFIGen(\n" +
                    "    type = \"com.alibaba.fastffi.annotation.test.TestFunctionTemplate\",\n" +
                    "    functionTemplates = @FFIFunGen(name = \"getValueNoSuffix\", returnType = \"T\", parameterTypes = \"T\", templates = @CXXTemplate(java = \"Double\", cxx = \"double\"))\n" +
                    ")\n" +
                    "@FFISynthetic(\"com.alibaba.fastffi.annotation.test.TestFunctionTemplate\")\n" +
                    "public abstract interface TestFunctionTemplateGen extends TestFunctionTemplate {\n" +
                    "  @CXXTemplates({\n" +
                    "      @CXXTemplate(cxx = \"char\", java = \"java.lang.Byte\"),\n" +
                    "      @CXXTemplate(cxx = \"int\", java = \"java.lang.Integer\")\n" +
                    "  })\n" +
                    "  @CXXValue\n" +
                    "  default <T> T getValueNoSuffix(T arg0) {\n" +
                    "    if (arg0 == null) {\n" +
                    "      throw new NullPointerException(\"arg0 must not be null\");\n" +
                    "    }\n" +
                    "    if (arg0 instanceof Byte)  {\n" +
                    "      return (T) getValueNoSuffix((java.lang.Byte) arg0);\n" +
                    "    }\n" +
                    "    if (arg0 instanceof Integer)  {\n" +
                    "      return (T) getValueNoSuffix((java.lang.Integer) arg0);\n" +
                    "    }\n" +
                    "    if (arg0 instanceof Double)  {\n" +
                    "      return (T) getValueNoSuffix((java.lang.Double) arg0);\n" +
                    "    }\n" +
                    "    throw new RuntimeException(\"Cannot call TestFunctionTemplateGen.getValueNoSuffix, no template instantiation for the type arguments.\");\n" +
                    "  }\n" +
                    "\n" +
                    "  @FFITypeAlias(\"char\")\n" +
                    "  @CXXValue\n" +
                    "  @CXXTemplate(\n" +
                    "      cxx = \"char\",\n" +
                    "      java = \"java.lang.Byte\"\n" +
                    "  )\n" +
                    "  @FFINameAlias(\"getValueNoSuffix\")\n" +
                    "  Byte getValueNoSuffix(@FFITypeAlias(\"char\") Byte arg0);\n" +
                    "\n" +
                    "  @FFITypeAlias(\"int\")\n" +
                    "  @CXXValue\n" +
                    "  @CXXTemplate(\n" +
                    "      cxx = \"int\",\n" +
                    "      java = \"java.lang.Integer\"\n" +
                    "  )\n" +
                    "  @FFINameAlias(\"getValueNoSuffix\")\n" +
                    "  Integer getValueNoSuffix(@FFITypeAlias(\"int\") Integer arg0);\n" +
                    "\n" +
                    "  @FFITypeAlias(\"double\")\n" +
                    "  @CXXValue\n" +
                    "  @CXXTemplate(\n" +
                    "      cxx = \"double\",\n" +
                    "      java = \"Double\"\n" +
                    "  )\n" +
                    "  @FFINameAlias(\"getValueNoSuffix\")\n" +
                    "  Double getValueNoSuffix(@FFITypeAlias(\"double\") Double arg0);\n" +
                    "\n" +
                    "  @CXXTemplates({\n" +
                    "      @CXXTemplate(cxx = \"char\", java = \"java.lang.Byte\"),\n" +
                    "      @CXXTemplate(cxx = \"int\", java = \"java.lang.Integer\")\n" +
                    "  })\n" +
                    "  @CXXValue\n" +
                    "  default <T> T getValueSkip(@FFISkip T arg0) {\n" +
                    "    if (arg0 == null) {\n" +
                    "      throw new NullPointerException(\"arg0 must not be null\");\n" +
                    "    }\n" +
                    "    if (arg0 instanceof Byte)  {\n" +
                    "      return (T) getValueSkip((java.lang.Byte) arg0);\n" +
                    "    }\n" +
                    "    if (arg0 instanceof Integer)  {\n" +
                    "      return (T) getValueSkip((java.lang.Integer) arg0);\n" +
                    "    }\n" +
                    "    throw new RuntimeException(\"Cannot call TestFunctionTemplateGen.getValueSkip, no template instantiation for the type arguments.\");\n" +
                    "  }\n" +
                    "\n" +
                    "  @FFITypeAlias(\"char\")\n" +
                    "  @CXXValue\n" +
                    "  @CXXTemplate(\n" +
                    "      cxx = \"char\",\n" +
                    "      java = \"java.lang.Byte\"\n" +
                    "  )\n" +
                    "  @FFINameAlias(\"getValueSkip\")\n" +
                    "  Byte getValueSkip(@FFITypeAlias(\"char\") @FFISkip Byte arg0);\n" +
                    "\n" +
                    "  @FFITypeAlias(\"int\")\n" +
                    "  @CXXValue\n" +
                    "  @CXXTemplate(\n" +
                    "      cxx = \"int\",\n" +
                    "      java = \"java.lang.Integer\"\n" +
                    "  )\n" +
                    "  @FFINameAlias(\"getValueSkip\")\n" +
                    "  Integer getValueSkip(@FFITypeAlias(\"int\") @FFISkip Integer arg0);\n" +
                    "\n" +
                    "  @CXXTemplates({\n" +
                    "      @CXXTemplate(cxx = \"char\", java = \"java.lang.Byte\"),\n" +
                    "      @CXXTemplate(cxx = \"int\", java = \"java.lang.Integer\")\n" +
                    "  })\n" +
                    "  @FFINameAlias(\"getValueSkip\")\n" +
                    "  @CXXValue\n" +
                    "  default <T> T testNameAliasSkip(@FFISkip T arg0) {\n" +
                    "    if (arg0 == null) {\n" +
                    "      throw new NullPointerException(\"arg0 must not be null\");\n" +
                    "    }\n" +
                    "    if (arg0 instanceof Byte)  {\n" +
                    "      return (T) testNameAliasSkip((java.lang.Byte) arg0);\n" +
                    "    }\n" +
                    "    if (arg0 instanceof Integer)  {\n" +
                    "      return (T) testNameAliasSkip((java.lang.Integer) arg0);\n" +
                    "    }\n" +
                    "    throw new RuntimeException(\"Cannot call TestFunctionTemplateGen.testNameAliasSkip, no template instantiation for the type arguments.\");\n" +
                    "  }\n" +
                    "\n" +
                    "  @FFITypeAlias(\"char\")\n" +
                    "  @FFINameAlias(\"getValueSkip\")\n" +
                    "  @CXXValue\n" +
                    "  @CXXTemplate(\n" +
                    "      cxx = \"char\",\n" +
                    "      java = \"java.lang.Byte\"\n" +
                    "  )\n" +
                    "  Byte testNameAliasSkip(@FFITypeAlias(\"char\") @FFISkip Byte arg0);\n" +
                    "\n" +
                    "  @FFITypeAlias(\"int\")\n" +
                    "  @FFINameAlias(\"getValueSkip\")\n" +
                    "  @CXXValue\n" +
                    "  @CXXTemplate(\n" +
                    "      cxx = \"int\",\n" +
                    "      java = \"java.lang.Integer\"\n" +
                    "  )\n" +
                    "  Integer testNameAliasSkip(@FFITypeAlias(\"int\") @FFISkip Integer arg0);\n" +
                    "}\n";
            assertEquals(content, expected);
        }
    }

    static void assertEquals(String str1, String str2) {
        if (str1.length() != str2.length()) {
            throw new IllegalArgumentException("Expected length " + str1.length() + ", got "
                    + str2.length());
        }
        for (int i = 0; i < str1.length(); i++) {
            if (str1.charAt(i) != str2.charAt(i)) {
                throw new IllegalArgumentException(
                        "Expected char at " + i + " = " + str1.charAt(i) + ", got " + str2.charAt(i)
                                + ", compared = " + str1.substring(0, i + 1) + " vs. " + str2.substring(0, i + 1));
            }
        }
    }
}
