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
package com.alibaba.llvm4jni.simdjson.benchmark;

import com.alibaba.fastffi.CXXValueScope;
import com.alibaba.fastffi.FFITypeFactory;
import com.alibaba.llvm4jni.simdjson.Benchmarks;
import com.alibaba.llvm4jni.simdjson.Point;
import com.alibaba.llvm4jni.simdjson.document.SimdElement;
import com.alibaba.llvm4jni.simdjson.document.SimdPaddedString;
import com.alibaba.llvm4jni.simdjson.document.SimdParser;
import com.alibaba.llvm4jni.simdjson.document.SimdResult;
import com.alibaba.llvm4jni.simdjson.stdcxx.StdString;
import com.alibaba.llvm4jni.simdjson.stdcxx.StdVector;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.ChainedOptionsBuilder;
import org.openjdk.jmh.runner.options.CommandLineOptionException;
import org.openjdk.jmh.runner.options.CommandLineOptions;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Fork(value = 1)
@Warmup(iterations = 10, time = 1000, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 10, time = 1000, timeUnit = TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
public class Kostya {

    static StdString COORDINATES = StdString.create("coordinates");
    static long COORDINATES_ADDR = COORDINATES.c_str();
    static StdString X = StdString.create("x");
    static long X_ADDR = X.c_str();
    static StdString Y = StdString.create("y");
    static long Y_ADDR = Y.c_str();
    static StdString Z = StdString.create("z");
    static long Z_ADDR = Z.c_str();

    static StdVector.Factory<Point> factory = FFITypeFactory.getFactory(StdVector.class, "std::vector<simdjson::point>");
    static StdVector<Point> createVector() {
        return factory.create();
    }

    SimdParser parser;
    SimdResult<SimdElement> root;
    SimdPaddedString content;

    String json;
    JsonNode jsonNode;

    StdVector<Point> answer;
    List<JavaPoint> javaAnswer;

    StdVector<Point> answerRead;
    List<JavaPoint> javaAnswerRead;

    @Setup(Level.Iteration)
    public void setup() throws IOException {
        json = generateJson();
        content = SimdPaddedString.create(json);
        parser = SimdParser.create();
        root = parser.parse(content);
        answer = createVector();
        answerRead = createVector();
        ObjectMapper objectMapper = new ObjectMapper();
        jsonNode = objectMapper.readTree(json);
        javaAnswer = new ArrayList<>();
        javaAnswerRead = new ArrayList<>();

        {
            kostyaFFI(root, answerRead);
            kostyaJava(jsonNode, javaAnswerRead);
            if (answerRead.size() != javaAnswerRead.size()) {
                throw new IllegalStateException();
            }
            for (int i = 0; i < answerRead.size(); i++) {
                JavaPoint jp = javaAnswerRead.get(i);
                Point fp = answerRead.get(i);
                if (jp.x != fp.x() || jp.y != fp.y() || jp.z != fp.z()) {
                    throw new IllegalStateException();
                }
            }
        }
    }


    void FFIPoint2JavaPoint(StdVector<Point> ffi, List<JavaPoint> java) {
        long size = ffi.size();
        for (long i = 0; i < size; i++) {
            Point ffiPoint = ffi.get(i);
            java.add(new JavaPoint(ffiPoint.x(), ffiPoint.y(), ffiPoint.z()));
        }
    }

    @TearDown(Level.Trial)
    public void tearDown() {
        content.delete();
        parser.delete();
        answer.delete();
        answerRead.delete();
    }

    static final int SIZE = 100_000; // 524_288;

    static String generateJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"coordinates\":[");
        Random rand = new Random(2021);
        for (int i = 0; i < SIZE; i++) {
            sb.append(String.format("{\"x\":%f,\"y\":%f,\"z\":%f},", rand.nextDouble(),
                    rand.nextDouble(), rand.nextDouble()));
        }
        sb.append(String.format("{\"x\":%f,\"y\":%f,\"z\":%f}]}", rand.nextDouble(),
                rand.nextDouble(), rand.nextDouble()));
        return sb.toString();
    }

    @Benchmark
    public void testKostyaFFI() {
        answer.clear();
        try (CXXValueScope scope = new CXXValueScope()) {
            kostyaFFI(root, answer);
        }
    }

    @Benchmark
    public void testFullKostyaFFI() {
        answer.clear();
        try (CXXValueScope scope = new CXXValueScope()) {
            kostyaFFI(content, answer);
        }
    }

    public void kostyaFFI(SimdPaddedString content, StdVector<Point> result) {
        SimdParser parser = SimdParser.create();
        kostyaFFI(parser.parse(content), result);
        parser.delete();
    }

    public void kostyaFFI(SimdResult<SimdElement> dom, StdVector<Point> result) {
        SimdElement doc = dom.value_unsafe();
        Point local = Point.factory.createStack();
        for (SimdElement point : doc.get_unsafe_array(COORDINATES_ADDR)) {
            double x = point.get_unsafe_double(X_ADDR);
            double y = point.get_unsafe_double(Y_ADDR);
            double z = point.get_unsafe_double(Z_ADDR);
            local.set(x, y, z);
            result.emplace_back(local);
        }
    }

    public void kostyaFFIJava(SimdPaddedString content, List<JavaPoint> result) {
        SimdParser parser = SimdParser.create();
        kostyaFFIJava(parser.parse(content), result);
        parser.delete();
    }

    public void kostyaFFIJava(SimdResult<SimdElement> dom, List<JavaPoint> result) {
        SimdElement doc = dom.value_unsafe();
        for (SimdElement point : doc.get_unsafe_array(COORDINATES_ADDR)) {
            double x = point.get_unsafe_double(X_ADDR);
            double y = point.get_unsafe_double(Y_ADDR);
            double z = point.get_unsafe_double(Z_ADDR);
            result.add(new JavaPoint(x, y, z));
        }
    }

    @Benchmark
    public void testKostyaFFIJava() {
        javaAnswer.clear();
        try (CXXValueScope scope = new CXXValueScope()) {
            kostyaFFIJava(root, javaAnswer);
        }
    }

    @Benchmark
    public void testFullKostyaFFIJava() throws IOException {
        javaAnswer.clear();
        try (CXXValueScope scope = new CXXValueScope()) {
            kostyaFFIJava(content, javaAnswer);
        }
    }

    @Benchmark
    public void testKostyaJavaFFI() {
        answer.clear();
        try (CXXValueScope scope = new CXXValueScope()) {
            kostyaJavaFFI(jsonNode, answer);
        }
    }

    @Benchmark
    public void testFullKostyaJavaFFI() throws IOException {
        answer.clear();
        try (CXXValueScope scope = new CXXValueScope()) {
            kostyaJavaFFI(json, answer);
        }
    }

    /**
     * Java Input and FFI Output
     * @param content
     * @param result
     * @throws IOException
     */
    public void kostyaJavaFFI(String content, StdVector<Point> result) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode doc = objectMapper.readTree(content);
        Point local = Point.factory.createStack();
        for (JsonNode node : doc.get("coordinates")) {
            double x = node.get("x").asDouble();
            double y = node.get("y").asDouble();
            double z = node.get("z").asDouble();
            local.set(x, y, z);
            result.emplace_back(local);
        }
    }

    /**
     * Java Input and FFI Output
     * @param doc
     * @param result
     */
    public void kostyaJavaFFI(JsonNode doc, StdVector<Point> result) {
        Point local = Point.factory.createStack();
        for (JsonNode node : doc.get("coordinates")) {
            double x = node.get("x").asDouble();
            double y = node.get("y").asDouble();
            double z = node.get("z").asDouble();
            local.set(x, y, z);
            result.push_back(local);
        }
    }

    @Benchmark
    public void testKostyaJava() {
        javaAnswer.clear();
        try (CXXValueScope scope = new CXXValueScope()) {
            kostyaJava(jsonNode, javaAnswer);
        }
    }

    @Benchmark
    public void testFullKostyaJava() throws IOException {
        javaAnswer.clear();
        try (CXXValueScope scope = new CXXValueScope()) {
            kostyaJava(json, javaAnswer);
        }
    }

    public void kostyaJava(String content, List<JavaPoint> result) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode doc = objectMapper.readTree(content);
        for (JsonNode node : doc.get("coordinates")) {
            double x = node.get("x").asDouble();
            double y = node.get("y").asDouble();
            double z = node.get("z").asDouble();
            result.add(new JavaPoint(x, y, z));
        }
    }

    public void kostyaJava(JsonNode doc, List<JavaPoint> result) {
        for (JsonNode node : doc.get("coordinates")) {
            double x = node.get("x").asDouble();
            double y = node.get("y").asDouble();
            double z = node.get("z").asDouble();
            result.add(new JavaPoint(x, y, z));
        }
    }

    @Benchmark
    public void testKostyaNative() {
        answer.clear();
        try (CXXValueScope scope = new CXXValueScope()) {
            kostyaNative(root, answer);
        }
    }

    @Benchmark
    public void testFullKostyaNative() {
        answer.clear();
        try (CXXValueScope scope = new CXXValueScope()) {
            kostyaNative(content, answer);
        }
    }

    @Benchmark
    public void testKostyaNativeJava() {
        answer.clear();
        javaAnswer.clear();
        try (CXXValueScope scope = new CXXValueScope()) {
            kostyaNative(root, answer);
            FFIPoint2JavaPoint(answer, javaAnswer);
        }
    }

    @Benchmark
    public void testFullKostyaNativeJava() {
        answer.clear();
        javaAnswer.clear();
        try (CXXValueScope scope = new CXXValueScope()) {
            SimdParser parser = SimdParser.create();
            kostyaNative(content, answer);
            FFIPoint2JavaPoint(answer, javaAnswer);
            parser.delete();
        }
    }

    public void kostyaNative(SimdParser parser, SimdPaddedString content, StdVector<Point> result) {
        Benchmarks.INSTANCE.kostya(parser, content, result);
    }

    public void kostyaNative(SimdPaddedString content, StdVector<Point> result) {
        Benchmarks.INSTANCE.kostya(content, result);
    }

    public void kostyaNative(SimdResult<SimdElement> dom, StdVector<Point> result) {
        Benchmarks.INSTANCE.kostya(dom, result);
    }

    public static void main(String[] args) throws RunnerException, CommandLineOptionException {
        CommandLineOptions commandLineOptions = new CommandLineOptions(args);
        ChainedOptionsBuilder builder = new OptionsBuilder().parent(commandLineOptions)
                .include(Kostya.class.getSimpleName());

        new Runner(builder.build()).run();
    }
}
