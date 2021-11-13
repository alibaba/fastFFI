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
#ifndef BENCHMAKRS_H
#define BENCHMAKRS_H

#include "simdjson.h"
#include <vector>
#include <string>

namespace simdjson {

template<typename StringType=std::string_view>
struct twitter_user {
  uint64_t id{};
  StringType screen_name{};

  template<typename OtherStringType>
  bool operator==(const twitter_user<OtherStringType> &other) const {
    return id == other.id &&
           screen_name == other.screen_name;
  }
};

template<typename StringType=std::string_view>
struct tweet {
  StringType created_at{};
  uint64_t id{};
  StringType result{};
  uint64_t in_reply_to_status_id{};
  twitter_user<StringType> user{};
  uint64_t retweet_count{};
  uint64_t favorite_count{};
  template<typename OtherStringType>
  simdjson_really_inline bool operator==(const tweet<OtherStringType> &other) const {
    return created_at == other.created_at &&
           id == other.id &&
           result == other.result &&
           in_reply_to_status_id == other.in_reply_to_status_id &&
           user == other.user &&
           retweet_count == other.retweet_count &&
           favorite_count == other.favorite_count;
  }
  template<typename OtherStringType>
  simdjson_really_inline bool operator!=(const tweet<OtherStringType> &other) const { return !(*this == other); }
};

template<typename StringType>
struct top_tweet_result {
  int64_t retweet_count{};
  StringType screen_name{};
  StringType text{};
  template<typename OtherStringType>
  simdjson_really_inline bool operator==(const top_tweet_result<OtherStringType> &other) const {
    return retweet_count == other.retweet_count &&
           screen_name == other.screen_name &&
           text == other.text;
  }
  template<typename OtherStringType>
  simdjson_really_inline bool operator!=(const top_tweet_result<OtherStringType> &other) const { return !(*this == other); }
};

struct point {
    double x;
    double y;
    double z;

    point() : x(0), y(0), z(0) {}
    point(double xv, double yv, double zv) : x(xv), y(yv), z(zv) {}

    void set(double xv, double yv, double zv) {
        x = xv;
        y = yv;
        z = zv;
    }
};

class Benchmarks {

public:
    static void distinctuserid_recursive(simdjson::simdjson_result<simdjson::dom::element> & result, std::vector<int64_t> & answer);

    static void distinctuserid(simdjson::simdjson_result<simdjson::dom::element> & result, std::vector<int64_t> & answer);
    static void distinctuserid(simdjson::padded_string & content, std::vector<int64_t> & answer);
    static void distinctuserid(simdjson::dom::parser &parser, simdjson::padded_string & content, std::vector<int64_t> & answer);

    static void kostya(simdjson::simdjson_result<simdjson::dom::element> & dom, std::vector<simdjson::point> & result);
    static void kostya(simdjson::padded_string & content, std::vector<simdjson::point> & result);
    static void kostya(simdjson::dom::parser &parser, simdjson::padded_string & content, std::vector<simdjson::point> & result);

    static void top_tweet(simdjson::simdjson_result<simdjson::dom::element> &dom, int64_t max_retweet_count, top_tweet_result<std::string_view> &result);
    static void top_tweet(simdjson::padded_string & content, int64_t max_retweet_count, top_tweet_result<std::string_view> &result);
    static void top_tweet(simdjson::dom::parser &parser, simdjson::padded_string & content, int64_t max_retweet_count, top_tweet_result<std::string_view> &result);

    static void partial_tweets(simdjson::simdjson_result<simdjson::dom::element> &dom, std::vector<tweet<std::string_view>> &result);
    static void partial_tweets(simdjson::padded_string & content, std::vector<tweet<std::string_view>> &result);
    static void partial_tweets(simdjson::dom::parser &parser, simdjson::padded_string & content, std::vector<tweet<std::string_view>> &result);

};

}

#endif
