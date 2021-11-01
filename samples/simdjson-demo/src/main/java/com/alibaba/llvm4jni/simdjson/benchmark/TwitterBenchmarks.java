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
import com.alibaba.llvm4jni.simdjson.Benchmarks;
import com.alibaba.llvm4jni.simdjson.ErrorCode;
import com.alibaba.llvm4jni.simdjson.TopTweetResult;
import com.alibaba.llvm4jni.simdjson.Tweet;
import com.alibaba.llvm4jni.simdjson.TwitterUser;
import com.alibaba.llvm4jni.simdjson.document.SimdElement;
import com.alibaba.llvm4jni.simdjson.document.SimdPaddedString;
import com.alibaba.llvm4jni.simdjson.document.SimdParser;
import com.alibaba.llvm4jni.simdjson.document.SimdResult;
import com.alibaba.llvm4jni.simdjson.stdcxx.StdString;
import com.alibaba.llvm4jni.simdjson.stdcxx.StdVector;
import com.alibaba.llvm4jni.simdjson.stdcxx.StringView;
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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Fork(value = 1)
@Warmup(iterations = 10, time = 1000, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 10, time = 1000, timeUnit = TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
public class TwitterBenchmarks {

    SimdParser parser;
    SimdResult<SimdElement> root;
    SimdPaddedString content;

    StdVector<Long> distinctUserIDResult;
    List<Long> distinctUserIDResultJava;

    long max_retweet_count = 60;
    TopTweetResult<StringView> topTweetResult;
    JavaTopTweetResult topTweetResultJava;

    StdVector<Tweet<StringView>> partialTweetsResult;
    List<JavaTweet> partialTweetsResultJava;

    String json;
    JsonNode jsonNode;

    @Setup(Level.Iteration)
    public void setup() throws IOException {
        InputStream inputStream = TwitterBenchmarks.class.getClassLoader().getResourceAsStream("twitter.json");
        json = Utils.readInputStream(inputStream);
        content = SimdPaddedString.create(json);
        parser = SimdParser.create();
        root = parser.parse(content);

        distinctUserIDResult = StdVector.LONG_FACTORY.create();
        topTweetResult = TopTweetResult.STRING_VIEW_FACTORY.create();
        partialTweetsResult = StdVector.TWEET_STRING_VIEW_FACTORY.create();

        {
            ObjectMapper objectMapper = new ObjectMapper();
            jsonNode = objectMapper.readTree(json);
            distinctUserIDResultJava = new ArrayList<>();
            topTweetResultJava = new JavaTopTweetResult();
            partialTweetsResultJava = new ArrayList<>();
        }
        {
            List<Long> distinctUserIDResultJava = new ArrayList<>();
            StdVector<Long> distinctUserIDResult = StdVector.LONG_FACTORY.create();
            distinctUserIDFFI(root, distinctUserIDResult);
            distinctUserIDJava(jsonNode, distinctUserIDResultJava);
            if (distinctUserIDResultJava.size() != distinctUserIDResult.size()) {
                throw new IllegalStateException();
            }
            for (int i = 0; i < distinctUserIDResultJava.size(); i++) {
                if (distinctUserIDResultJava.get(i).longValue() != distinctUserIDResult.get(i).longValue()) {
                    throw new IllegalStateException();
                }
            }
        }
    }

    @TearDown(Level.Trial)
    public void tearDown() {
        content.delete();
        parser.delete();
        distinctUserIDResult.delete();
        topTweetResult.delete();
        partialTweetsResult.delete();
    }


    static StdString STATUSES = StdString.create("statuses");
    static long STATUSES_ADDR = STATUSES.c_str();
    static StdString USER = StdString.create("user");
    static long USER_ADDR = USER.c_str();
    static StdString ID = StdString.create("id");
    static long ID_ADDR = ID.c_str();
    static StdString RETWEETED_STATUSES = StdString.create("retweeted_status");
    static long RETWEETED_STATUSES_ADDR = RETWEETED_STATUSES.c_str();
    static StdString RETWEET_COUNT = StdString.create("retweet_count");
    static long RETWEET_COUNT_ADDR = RETWEET_COUNT.c_str();
    static StdString FAVORITE_COUNT = StdString.create("favorite_count");
    static long FAVORITE_COUNT_ADDR = FAVORITE_COUNT.c_str();
    static StdString TEXT = StdString.create("text");
    static long TEXT_ADDR = TEXT.c_str();
    static StdString IN_REPLY_TO_STATUS_ID = StdString.create("in_reply_to_status_id");
    static long IN_REPLY_TO_STATUS_ID_ADDR = IN_REPLY_TO_STATUS_ID.c_str();
    static StdString SCREEN_NAME = StdString.create("screen_name");
    static long SCREEN_NAME_ADDR = SCREEN_NAME.c_str();
    static StdString CREATED_AT = StdString.create("created_at");
    static long CREATED_AT_ADDR = CREATED_AT.c_str();

    static int NO_SUCH_FIELD = ErrorCode.NO_SUCH_FIELD.getValue();

    static void distinctUserIDFFI(SimdPaddedString content, StdVector<Long> answer) {
        SimdParser parser = SimdParser.create();
        distinctUserIDFFI(parser.parse(content), answer);
        parser.delete();
    }

    static void distinctUserIDFFI(SimdResult<SimdElement> result, StdVector<Long> answer) {
        SimdElement doc = result.value_unsafe();
        for (SimdElement tweet : doc.get_unsafe(STATUSES_ADDR).get_array_unsafe()) {
            // long id = tweet.get_unsafe(USER_ADDR).get_unsafe_int64(ID_ADDR);
            long id = tweet.get_unsafe_int64(USER_ADDR, ID_ADDR);
            answer.push_back(id);
            SimdResult<SimdElement> retweet = tweet.get(RETWEETED_STATUSES_ADDR);
            // if (retweet.error() != ErrorCode.NO_SUCH_FIELD) {
            if (retweet.error_code() != NO_SUCH_FIELD) {
                // answer.push_back(retweet.value_unsafe().get_unsafe(USER_ADDR).get_unsafe_int64(ID_ADDR));
                answer.push_back(retweet.value_unsafe().get_unsafe_int64(USER_ADDR, ID_ADDR));
            }
        }
    }

    static void distinctUserIDFFIJava(SimdPaddedString content, List<Long> answer) {
        SimdParser parser = SimdParser.create();
        distinctUserIDFFIJava(parser.parse(content), answer);
        parser.delete();
    }

    static void distinctUserIDFFIJava(SimdResult<SimdElement> result, List<Long> answer) {
        SimdElement doc = result.value_unsafe();
        for (SimdElement tweet : doc.get_unsafe(STATUSES_ADDR).get_array_unsafe()) {
            // long id = tweet.get_unsafe(USER_ADDR).get_unsafe_int64(ID_ADDR);
            long id = tweet.get_unsafe_int64(USER_ADDR, ID_ADDR);
            answer.add(id);
            SimdResult<SimdElement> retweet = tweet.get(RETWEETED_STATUSES_ADDR);
            // if (retweet.error() != ErrorCode.NO_SUCH_FIELD) {
            if (retweet.error_code() != NO_SUCH_FIELD) {
                // answer.push_back(retweet.value_unsafe().get_unsafe(USER_ADDR).get_unsafe_int64(ID_ADDR));
                answer.add(retweet.value_unsafe().get_unsafe_int64(USER_ADDR, ID_ADDR));
            }
        }
    }

    static void distinctUserIDJava(String content, List<Long> answer) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        distinctUserIDJava(objectMapper.readTree(content), answer);
    }

    static void distinctUserIDJava(JsonNode result, List<Long> answer) {
        for (JsonNode tweet : result.get("statuses")) {
            long id = tweet.get("user").get("id").asLong();
            answer.add(id);
            JsonNode retweet = tweet.get("retweeted_status");
            if (retweet != null) {
                answer.add(retweet.get("user").get("id").asLong());
            }
        }
    }

    static void distinctUserIDJavaFFI(String content, StdVector<Long> answer) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        distinctUserIDJavaFFI(objectMapper.readTree(content), answer);
    }

    static void distinctUserIDJavaFFI(JsonNode result, StdVector<Long> answer) {
        for (JsonNode tweet : result.get("statuses")) {
            long id = tweet.get("user").get("id").asLong();
            answer.push_back(id);
            JsonNode retweet = tweet.get("retweeted_status");
            if (retweet != null) {
                answer.push_back(retweet.get("user").get("id").asLong());
            }
        }
    }

    static void distinctUserIDNative(SimdResult<SimdElement> result, StdVector<Long> answer) {
        Benchmarks.INSTANCE.distinctuserid(result, answer);
    }

    static void distinctUserIDNative(SimdPaddedString content, StdVector<Long> answer) {
        Benchmarks.INSTANCE.distinctuserid(content, answer);
    }

    static void distinctUserIDNative(SimdParser parser, SimdPaddedString content, StdVector<Long> answer) {
        Benchmarks.INSTANCE.distinctuserid(parser, content, answer);
    }

    @Benchmark
    public void testDistinctUserIDFFI() {
        distinctUserIDResult.clear();
        try (CXXValueScope scope = new CXXValueScope()) {
            distinctUserIDFFI(root, distinctUserIDResult);
        }
    }

    @Benchmark
    public void testFullDistinctUserIDFFI() {
        distinctUserIDResult.clear();
        try (CXXValueScope scope = new CXXValueScope()) {
            distinctUserIDFFI(content, distinctUserIDResult);
        }
    }

    @Benchmark
    public void testDistinctUserIDJava() {
        distinctUserIDResultJava.clear();
        try (CXXValueScope scope = new CXXValueScope()) {
            distinctUserIDJava(jsonNode, distinctUserIDResultJava);
        }
    }

    @Benchmark
    public void testFullDistinctUserIDJava() throws IOException {
        distinctUserIDResultJava.clear();
        try (CXXValueScope scope = new CXXValueScope()) {
            distinctUserIDJava(json, distinctUserIDResultJava);
        }
    }

    @Benchmark
    public void testDistinctUserIDNative() {
        distinctUserIDResult.clear();
        try (CXXValueScope scope = new CXXValueScope()) {
            distinctUserIDNative(root, distinctUserIDResult);
        }
    }

    @Benchmark
    public void testFullDistinctUserIDNative() {
        distinctUserIDResult.clear();
        try (CXXValueScope scope = new CXXValueScope()) {
            distinctUserIDNative(content, distinctUserIDResult);
        }
    }

    @Benchmark
    public void testDistinctUserIDNativeJava() {
        distinctUserIDResult.clear();
        distinctUserIDResultJava.clear();
        try (CXXValueScope scope = new CXXValueScope()) {
            distinctUserIDNative(root, distinctUserIDResult);
            FFI2Java(distinctUserIDResult, distinctUserIDResultJava);
        }
    }

    @Benchmark
    public void testFullDistinctUserIDNativeJava() {
        distinctUserIDResult.clear();
        distinctUserIDResultJava.clear();
        SimdParser parser = SimdParser.create();
        try (CXXValueScope scope = new CXXValueScope()) {
            distinctUserIDNative(parser, content, distinctUserIDResult);
            FFI2Java(distinctUserIDResult, distinctUserIDResultJava);
            parser.delete();
        }
    }

    @Benchmark
    public void testDistinctUserIDFFIJava() {
        distinctUserIDResultJava.clear();
        try (CXXValueScope scope = new CXXValueScope()) {
            distinctUserIDFFIJava(root, distinctUserIDResultJava);
        }
    }

    @Benchmark
    public void testFullDistinctUserIDFFIJava() {
        distinctUserIDResultJava.clear();
        try (CXXValueScope scope = new CXXValueScope()) {
            distinctUserIDFFIJava(content, distinctUserIDResultJava);
        }
    }

    @Benchmark
    public void testDistinctUserIDJavaFFI() {
        distinctUserIDResult.clear();
        try (CXXValueScope scope = new CXXValueScope()) {
            distinctUserIDJavaFFI(jsonNode, distinctUserIDResult);
        }
    }

    @Benchmark
    public void testFullDistinctUserIDJavaFFI() throws IOException {
        distinctUserIDResult.clear();
        try (CXXValueScope scope = new CXXValueScope()) {
            distinctUserIDJavaFFI(json, distinctUserIDResult);
        }
    }

    static void topTweetResultFFI(SimdPaddedString content, long max_retweet_count, TopTweetResult<StringView> result) {
        SimdParser parser = SimdParser.create();
        topTweetResultFFI(parser.parse(content), max_retweet_count, result);
        parser.delete();
    }

    static void topTweetResultFFI(SimdResult<SimdElement> dom, long max_retweet_count, TopTweetResult<StringView> result) {
        result.retweet_count(-1L);
        SimdElement top_tweet = null;

        for (SimdElement tweet : dom.value_unsafe().get_unsafe_array(STATUSES_ADDR)) {
            long retweet_count = tweet.get_unsafe_int64(RETWEET_COUNT_ADDR);
            if (retweet_count <= max_retweet_count && retweet_count >= result.retweet_count()) {
                result.retweet_count(retweet_count);
                top_tweet = tweet;
            }
        }

        if (top_tweet != null) {
            result.text(top_tweet.get_unsafe_string(TEXT_ADDR));
            result.screen_name(top_tweet.get_unsafe(USER_ADDR).get_unsafe_string(SCREEN_NAME_ADDR));
        }
    }

    static void topTweetResultFFIJava(SimdPaddedString content, long max_retweet_count, JavaTopTweetResult result) {
        SimdParser parser = SimdParser.create();
        topTweetResultFFIJava(parser.parse(content), max_retweet_count, result);
        parser.delete();
    }

    static void topTweetResultFFIJava(SimdResult<SimdElement> dom, long max_retweet_count, JavaTopTweetResult result) {
        result.retweet_count = -1L;
        SimdElement top_tweet = null;

        for (SimdElement tweet : dom.value_unsafe().get_unsafe_array(STATUSES_ADDR)) {
            long retweet_count = tweet.get_unsafe_int64(RETWEET_COUNT_ADDR);
            if (retweet_count <= max_retweet_count && retweet_count >= result.retweet_count) {
                result.retweet_count = retweet_count;
                top_tweet = tweet;
            }
        }

        if (top_tweet != null) {
            result.text = top_tweet.get_unsafe_string(TEXT_ADDR).toJavaString();
            result.screen_name = top_tweet.get_unsafe(USER_ADDR).get_unsafe_string(SCREEN_NAME_ADDR).toJavaString();
        }
    }

    static void topTweetResultJava(String content, long max_retweet_count, JavaTopTweetResult result) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        topTweetResultJava(objectMapper.readTree(content), max_retweet_count, result);
    }

    static void topTweetResultJava(JsonNode dom, long max_retweet_count, JavaTopTweetResult result) {
        result.retweet_count = -1L;
        JsonNode top_tweet = null;

        for (JsonNode tweet : dom.get("statuses")) {
            long retweet_count = tweet.get("retweet_count").asLong();
            if (retweet_count <= max_retweet_count && retweet_count >= result.retweet_count) {
                result.retweet_count = retweet_count;
                top_tweet = tweet;
            }
        }

        if (top_tweet != null) {
            result.text = top_tweet.get("text").asText();
            result.screen_name = top_tweet.get("user").get("screen_name").asText();
        }
    }

    static void topTweetResultJavaFFI(String content, long max_retweet_count, TopTweetResult<StringView> result) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        topTweetResultJavaFFI(objectMapper.readTree(content), max_retweet_count, result);
    }

    static void topTweetResultJavaFFI(JsonNode dom, long max_retweet_count, TopTweetResult<StringView>  result) {
        result.retweet_count(-1L);
        JsonNode top_tweet = null;

        for (JsonNode tweet : dom.get("statuses")) {
            long retweet_count = tweet.get("retweet_count").asLong();
            if (retweet_count <= max_retweet_count && retweet_count >= result.retweet_count()) {
                result.retweet_count(retweet_count);
                top_tweet = tweet;
            }
        }

        if (top_tweet != null) {
            result.text(StringView.create(top_tweet.get("text").asText()));
            result.screen_name(StringView.create(top_tweet.get("user").get("screen_name").asText()));
        }
    }

    static void topTweetResultNative(SimdPaddedString content, long max_retweet_count, TopTweetResult<StringView> result) {
        Benchmarks.INSTANCE.top_tweet(content, max_retweet_count, result);
    }

    static void topTweetResultNative(SimdParser parser, SimdPaddedString content, long max_retweet_count, TopTweetResult<StringView> result) {
        Benchmarks.INSTANCE.top_tweet(parser, content, max_retweet_count, result);
    }

    static void topTweetResultNative(SimdResult<SimdElement> dom, long max_retweet_count, TopTweetResult<StringView> result) {
        Benchmarks.INSTANCE.top_tweet(dom, max_retweet_count, result);
    }

    @Benchmark
    public void testTopTweetResultFFI() {
        try (CXXValueScope scope = new CXXValueScope()) {
            topTweetResultFFI(root, max_retweet_count, topTweetResult);
        }
    }

    @Benchmark
    public void testFullTopTweetResultFFI() {
        try (CXXValueScope scope = new CXXValueScope()) {
            topTweetResultFFI(content, max_retweet_count, topTweetResult);
        }
    }

    @Benchmark
    public void testTopTweetResultFFIJava() {
        try (CXXValueScope scope = new CXXValueScope()) {
            topTweetResultFFIJava(root, max_retweet_count, topTweetResultJava);
        }
    }

    @Benchmark
    public void testFullTopTweetResultFFIJava() {
        try (CXXValueScope scope = new CXXValueScope()) {
            topTweetResultFFIJava(content, max_retweet_count, topTweetResultJava);
        }
    }

    void FFI2Java(TopTweetResult<StringView> ffi, JavaTopTweetResult java) {
        java.retweet_count = ffi.retweet_count();
        java.text = ffi.text().toJavaString();
        java.screen_name = ffi.screen_name().toJavaString();
    }

    void FFI2Java(StdVector<Long> ffi, List<Long> java) {
        long size = ffi.size();
        for (long i = 0; i < size; i++) {
            java.add(ffi.get(i));
        }
    }

    void FFITweet2JavaTweet(StdVector<Tweet<StringView>> ffi, List<JavaTweet> java) {
        long size = ffi.size();
        for (long i = 0; i < size; i++) {
            Tweet<StringView> ffiTweet = ffi.get(i);
            JavaTweet t = new JavaTweet();
            java.add(t);
            t.created_at = ffiTweet.created_at().toJavaString();
            t.id = ffiTweet.id();
            t.result = ffiTweet.result().toJavaString();
            t.in_reply_to_status_id = ffiTweet.in_reply_to_status_id();
            JavaTwitterUser u = new JavaTwitterUser();
            TwitterUser<StringView> ffiUser = ffiTweet.user();
            t.user = u;
            u.id = ffiTweet.id();
            u.screen_name = ffiUser.screen_name().toJavaString();
            t.retweet_count = ffiTweet.retweet_count();
            t.favorite_count = ffiTweet.favorite_count();
        }
    }

    @Benchmark
    public void testTopTweetResultNativeJava() {
        try (CXXValueScope scope = new CXXValueScope()) {
            topTweetResultNative(root, max_retweet_count, topTweetResult);
            FFI2Java(topTweetResult, topTweetResultJava);
        }
    }

    @Benchmark
    public void testFullTopTweetResultNativeJava() {
        try (CXXValueScope scope = new CXXValueScope()) {
            SimdParser parser = SimdParser.create();
            topTweetResultNative(parser, content, max_retweet_count, topTweetResult);
            FFI2Java(topTweetResult, topTweetResultJava);
            parser.delete();
        }
    }

    @Benchmark
    public void testTopTweetResultNative() {
        try (CXXValueScope scope = new CXXValueScope()) {
            topTweetResultNative(root, max_retweet_count, topTweetResult);
        }
    }

    @Benchmark
    public void testFullTopTweetResultNative() {
        try (CXXValueScope scope = new CXXValueScope()) {
            topTweetResultNative(content, max_retweet_count, topTweetResult);
        }
    }

    @Benchmark
    public void testTopTweetResultJava() {
        try (CXXValueScope scope = new CXXValueScope()) {
            topTweetResultJava(jsonNode, max_retweet_count, topTweetResultJava);
        }
    }

    @Benchmark
    public void testFullTopTweetResultJava() throws IOException {
        try (CXXValueScope scope = new CXXValueScope()) {
            topTweetResultJava(json, max_retweet_count, topTweetResultJava);
        }
    }

    @Benchmark
    public void testTopTweetResultJavaFFI() {
        try (CXXValueScope scope = new CXXValueScope()) {
            topTweetResultJavaFFI(jsonNode, max_retweet_count, topTweetResult);
        }
    }

    @Benchmark
    public void testFullTopTweetResultJavaFFI() throws IOException {
        try (CXXValueScope scope = new CXXValueScope()) {
            topTweetResultJavaFFI(json, max_retweet_count, topTweetResult);
        }
    }

    @Benchmark
    public void testPartialTweetsFFI() {
        partialTweetsResult.clear();
        try (CXXValueScope scope = new CXXValueScope()) {
            partialTweetsFFI(root, partialTweetsResult);
        }
    }

    @Benchmark
    public void testFullPartialTweetsFFI() {
        partialTweetsResult.clear();
        try (CXXValueScope scope = new CXXValueScope()) {
            partialTweetsFFI(content, partialTweetsResult);
        }
    }

    @Benchmark
    public void testPartialTweetsFFIJava() {
        partialTweetsResultJava.clear();
        try (CXXValueScope scope = new CXXValueScope()) {
            partialTweetsFFIJava(root, partialTweetsResultJava);
        }
    }

    @Benchmark
    public void testFullPartialTweetsFFIJava() {
        partialTweetsResultJava.clear();
        try (CXXValueScope scope = new CXXValueScope()) {
            partialTweetsFFIJava(content, partialTweetsResultJava);
        }
    }

    @Benchmark
    public void testPartialTweetsNative() {
        partialTweetsResult.clear();
        try (CXXValueScope scope = new CXXValueScope()) {
            partialTweetsNative(root, partialTweetsResult);
        }
    }

    @Benchmark
    public void testFullPartialTweetsNative() {
        partialTweetsResult.clear();
        try (CXXValueScope scope = new CXXValueScope()) {
            partialTweetsNative(content, partialTweetsResult);
        }
    }

    @Benchmark
    public void testPartialTweetsNativeJava() {
        partialTweetsResult.clear();
        partialTweetsResultJava.clear();
        try (CXXValueScope scope = new CXXValueScope()) {
            partialTweetsNative(root, partialTweetsResult);
            FFITweet2JavaTweet(partialTweetsResult, partialTweetsResultJava);
        }
    }

    @Benchmark
    public void testFullPartialTweetsNativeJava() {
        partialTweetsResult.clear();
        partialTweetsResultJava.clear();
        try (CXXValueScope scope = new CXXValueScope()) {
            SimdParser parser = SimdParser.create();
            partialTweetsNative(parser, content, partialTweetsResult);
            FFITweet2JavaTweet(partialTweetsResult, partialTweetsResultJava);
            parser.delete();
        }
    }

    @Benchmark
    public void testPartialTweetsJava() {
        partialTweetsResultJava.clear();
        try (CXXValueScope scope = new CXXValueScope()) {
            partialTweetsJava(jsonNode, partialTweetsResultJava);
        }
    }

    @Benchmark
    public void testFullPartialTweetsJava() throws IOException {
        partialTweetsResultJava.clear();
        try (CXXValueScope scope = new CXXValueScope()) {
            partialTweetsJava(json, partialTweetsResultJava);
        }
    }

    @Benchmark
    public void testPartialTweetsJavaFFI() {
        partialTweetsResult.clear();
        try (CXXValueScope scope = new CXXValueScope()) {
            partialTweetsJavaFFI(jsonNode, partialTweetsResult);
        }
    }

    @Benchmark
    public void testFullPartialTweetsJavaFFI() throws IOException {
        partialTweetsResult.clear();
        try (CXXValueScope scope = new CXXValueScope()) {
            partialTweetsJavaFFI(json, partialTweetsResult);
        }
    }

    static void partialTweetsJava(String content, List<JavaTweet> result) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        partialTweetsJava(objectMapper.readTree(content), result);
    }

    static void partialTweetsJava(JsonNode dom, List<JavaTweet> result) {
        for (JsonNode tweet : dom.get("statuses")) {
            JsonNode user = tweet.get("user");
            JavaTweet t = new JavaTweet();
            result.add(t);
            t.created_at = tweet.get("created_at").asText();
            t.id = tweet.get("id").asLong();
            t.result = tweet.get("text").asText();
            JsonNode in = tweet.get("in_reply_to_status_id");
            t.in_reply_to_status_id = in.isNull() ? 0L : in.asLong();
            JavaTwitterUser u = new JavaTwitterUser();
            t.user = u;
            u.id = user.get("id").asLong();
            u.screen_name = user.get("screen_name").asText();
            t.retweet_count = tweet.get("retweet_count").asLong();
            t.favorite_count = tweet.get("favorite_count").asLong();
        }
    }

    static void partialTweetsJavaFFI(String content, StdVector<Tweet<StringView>> result) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        partialTweetsJavaFFI(objectMapper.readTree(content), result);
    }

    static void partialTweetsJavaFFI(JsonNode dom, StdVector<Tweet<StringView>> result) {
        Tweet<StringView> t = Tweet.STRING_VIEW_FACTORY.createStack();
        for (JsonNode tweet : dom.get("statuses")) {
            JsonNode user = tweet.get("user");
            // Tweet<StringView> t = result.append();
            t.created_at(StringView.create(tweet.get("created_at").asText()));
            t.id(tweet.get("id").asLong());
            t.result(StringView.create(tweet.get("text").asText()));
            JsonNode in = tweet.get("in_reply_to_status_id");
            t.in_reply_to_status_id(in.isNull() ? 0L : in.asLong());
            TwitterUser<StringView> u = t.user();
            u.id(user.get("id").asLong());
            u.screen_name(StringView.create(user.get("screen_name").asText()));
            t.retweet_count(tweet.get("retweet_count").asLong());
            t.favorite_count(tweet.get("favorite_count").asLong());
            result.emplace_back(t);
        }
    }

    static void partialTweetsFFI(SimdPaddedString content, StdVector<Tweet<StringView>> result) {
        SimdParser parser = SimdParser.create();
        partialTweetsFFI(parser.parse(content), result);
        parser.delete();
    }

    static void partialTweetsFFI(SimdResult<SimdElement> dom, StdVector<Tweet<StringView>> result) {
        Tweet<StringView> t = Tweet.STRING_VIEW_FACTORY.createStack();
        for (SimdElement tweet : dom.value_unsafe().get_unsafe_array(STATUSES_ADDR)) {
            SimdElement user = tweet.get_unsafe(USER_ADDR);
            // Tweet<StringView> t = result.append();
            t.created_at(tweet.get_unsafe_string(CREATED_AT_ADDR));
            t.id(tweet.get_unsafe_uint64(ID_ADDR));
            t.result(tweet.get_unsafe_string(TEXT_ADDR));
            SimdElement in = tweet.get_unsafe(IN_REPLY_TO_STATUS_ID_ADDR);
            t.in_reply_to_status_id(in.is_null() ? 0L : in.get_uint64());
            TwitterUser<StringView> u = t.user();
            u.id(user.get_unsafe_uint64(ID_ADDR));
            u.screen_name(user.get_unsafe_string(SCREEN_NAME_ADDR));
            t.retweet_count(tweet.get_unsafe_uint64(RETWEET_COUNT_ADDR));
            t.favorite_count(tweet.get_unsafe_uint64(FAVORITE_COUNT_ADDR));
            result.emplace_back(t);
        }
    }

    static void partialTweetsFFIJava(SimdPaddedString content, List<JavaTweet> result) {
        SimdParser parser = SimdParser.create();
        partialTweetsFFIJava(parser.parse(content), result);
        parser.delete();
    }

    static void partialTweetsFFIJava(SimdResult<SimdElement> dom, List<JavaTweet>  result) {
        for (SimdElement tweet : dom.value_unsafe().get_unsafe_array(STATUSES_ADDR)) {
            SimdElement user = tweet.get_unsafe(USER_ADDR);
            JavaTweet t = new JavaTweet();
            result.add(t);
            t.created_at = tweet.get_unsafe_string(CREATED_AT_ADDR).toJavaString();
            t.id = tweet.get_unsafe_uint64(ID_ADDR);
            t.result = tweet.get_unsafe_string(TEXT_ADDR).toJavaString();
            SimdElement in = tweet.get_unsafe(IN_REPLY_TO_STATUS_ID_ADDR);
            t.in_reply_to_status_id = in.is_null() ? 0L : in.get_uint64();
            JavaTwitterUser u = new JavaTwitterUser();
            t.user = u;
            u.id = user.get_unsafe_uint64(ID_ADDR);
            u.screen_name = user.get_unsafe_string(SCREEN_NAME_ADDR).toJavaString();
            t.retweet_count = tweet.get_unsafe_uint64(RETWEET_COUNT_ADDR);
            t.favorite_count = tweet.get_unsafe_uint64(FAVORITE_COUNT_ADDR);
        }
    }

    static void partialTweetsNative(SimdParser parser, SimdPaddedString content, StdVector<Tweet<StringView>> result) {
        Benchmarks.INSTANCE.partial_tweets(parser, content, result);
    }

    static void partialTweetsNative(SimdPaddedString content, StdVector<Tweet<StringView>> result) {
        Benchmarks.INSTANCE.partial_tweets(content, result);
    }

    static void partialTweetsNative(SimdResult<SimdElement> dom, StdVector<Tweet<StringView>> result) {
        Benchmarks.INSTANCE.partial_tweets(dom, result);
    }

    private static void assertEquals(StdVector<Long> answer1, StdVector<Long> answer2) {
        if (answer1.size() != answer2.size()) {
            throw new IllegalStateException();
        }
        long size = answer1.size();
        for (long i = 0; i < size; i++) {
            if (!answer1.get(i).equals(answer2.get(i))) {
                throw new IllegalStateException();
            }
        }
    }

    public static void main(String[] args) throws RunnerException, CommandLineOptionException {
        CommandLineOptions commandLineOptions = new CommandLineOptions(args);
        ChainedOptionsBuilder builder = new OptionsBuilder().parent(commandLineOptions)
                .include(TwitterBenchmarks.class.getSimpleName());

        new Runner(builder.build()).run();
    }
}
