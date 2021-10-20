#include "benchmarks.h"

namespace simdjson {

void Benchmarks::distinctuserid(simdjson::dom::parser &parser, simdjson::padded_string &content, std::vector<int64_t> & result) {
    simdjson::simdjson_result<simdjson::dom::element> dom = parser.parse(content);
    distinctuserid(dom, result);
}

void Benchmarks::distinctuserid(simdjson::padded_string &content, std::vector<int64_t> & result) {
    simdjson::dom::parser parser;
    simdjson::simdjson_result<simdjson::dom::element> dom = parser.parse(content);
    distinctuserid(dom, result);
}

void Benchmarks::distinctuserid(simdjson::simdjson_result<simdjson::dom::element> & doc, std::vector<int64_t> & result) {
    for (dom::element tweet : doc["statuses"].get_array().value_unsafe()) {
        // We believe that all statuses have a matching
        // user, and we are willing to throw when they do not.
        result.push_back(tweet["user"]["id"].get_int64().value_unsafe());
        // Not all tweets have a "retweeted_status", but when they do
        // we want to go and find the user within.
        auto retweet = tweet["retweeted_status"];
        if (retweet.error() != NO_SUCH_FIELD) {
            result.push_back(retweet["user"]["id"].get_int64().value_unsafe());
        }
    }
}

void simdjson_recurse(std::vector<int64_t> & v, simdjson::dom::element element) {
    if (element.is_array()) {
        auto array = element.get_array().value_unsafe();
        for (auto child : array) {
            if (child.is_array() || child.is_object()) {
                simdjson_recurse(v, child);
            }
        }
    } else if (element.is_object()) {
        auto object = element.get_object().value_unsafe();
        for (auto [key, value] : object) {
            if((key.size() == 4) && (memcmp(key.data(), "user", 4) == 0)) {
                // we are in an object under the key "user"
                if(value.is_object()) {
                    auto child_object = value.get_object().value_unsafe();
                    for (auto [child_key, child_value] : child_object) {
                        if((child_key.size() == 2) && (memcmp(child_key.data(), "id", 2) == 0)) {
                            if(child_value.is_int64()) {
                                v.push_back(child_value.get_int64().value_unsafe());
                            }
                        }
                        if (child_value.is_array() || child_value.is_object()) {
                            simdjson_recurse(v, child_value);
                        }
                    }
                } else if (value.is_array()) {
                    simdjson_recurse(v, value);
                }
                // end of: we are in an object under the key "user"
            } else if (value.is_array() || value.is_object()) {
                simdjson_recurse(v, value);
            }
        }
    }
}

void Benchmarks::distinctuserid_recursive(simdjson::simdjson_result<simdjson::dom::element> & doc, std::vector<int64_t> & result) {
    simdjson_recurse(result, doc.value_unsafe());
}

void Benchmarks::kostya(simdjson::padded_string &content, std::vector<simdjson::point> & result) {
    simdjson::dom::parser parser;
    simdjson::simdjson_result<simdjson::dom::element> dom = parser.parse(content);
    kostya(dom, result);
}

void Benchmarks::kostya(simdjson::dom::parser &parser, simdjson::padded_string &content, std::vector<simdjson::point> & result) {
    simdjson::simdjson_result<simdjson::dom::element> dom = parser.parse(content);
    kostya(dom, result);
}

void Benchmarks::kostya(simdjson::simdjson_result<simdjson::dom::element> & dom, std::vector<simdjson::point> & result) {
    for (auto point : dom.value_unsafe()["coordinates"].get_array().value_unsafe()) {
        result.emplace_back(simdjson::point{point["x"].get_double().value_unsafe(), point["y"].get_double().value_unsafe(), point["z"].get_double().value_unsafe()});
    }
}

void Benchmarks::top_tweet(simdjson::padded_string &content, int64_t max_retweet_count, top_tweet_result<std::string_view> &result) {
    simdjson::dom::parser parser;
    simdjson::simdjson_result<simdjson::dom::element> dom = parser.parse(content);
    top_tweet(dom, max_retweet_count, result);
}

void Benchmarks::top_tweet(simdjson::dom::parser &parser, simdjson::padded_string &content, int64_t max_retweet_count, top_tweet_result<std::string_view> &result) {
    simdjson::simdjson_result<simdjson::dom::element> dom = parser.parse(content);
    top_tweet(dom, max_retweet_count, result);
}

void Benchmarks::top_tweet(simdjson::simdjson_result<simdjson::dom::element> &dom, int64_t max_retweet_count, top_tweet_result<std::string_view> &result) {
    result.retweet_count = -1;
    simdjson::dom::element top_tweet{};

    for (auto tweet : dom.value_unsafe()["statuses"].value_unsafe().get_array().value_unsafe()) {
        int64_t retweet_count = tweet["retweet_count"].value_unsafe().get_int64().value_unsafe();
        if (retweet_count <= max_retweet_count && retweet_count >= result.retweet_count) {
            result.retweet_count = retweet_count;
            top_tweet = tweet;
        }
    }

    result.text = top_tweet["text"].value_unsafe().get_string().value_unsafe();
    result.screen_name = top_tweet["user"]["screen_name"].value_unsafe().get_string().value_unsafe();
}

uint64_t nullable_int(dom::element element) {
    if (element.is_null()) { return 0; }
    return element.get_uint64().value_unsafe();
}

void Benchmarks::partial_tweets(simdjson::dom::parser &parser, simdjson::padded_string &content, std::vector<simdjson::tweet<std::string_view>> &result) {
    simdjson::simdjson_result<simdjson::dom::element> dom = parser.parse(content);
    partial_tweets(dom, result);
}

void Benchmarks::partial_tweets(simdjson::padded_string &content, std::vector<simdjson::tweet<std::string_view>> &result) {
    simdjson::dom::parser parser;
    simdjson::simdjson_result<simdjson::dom::element> dom = parser.parse(content);
    partial_tweets(dom, result);
}

void Benchmarks::partial_tweets(simdjson::simdjson_result<simdjson::dom::element> &dom, std::vector<simdjson::tweet<std::string_view>> &result) {
    for (dom::element tweet : dom.value_unsafe()["statuses"].get_array().value_unsafe()) {
        auto user = tweet["user"].value_unsafe();
        result.emplace_back(simdjson::tweet<std::string_view>{
                tweet["created_at"].get_string().value_unsafe(),
                tweet["id"].get_uint64().value_unsafe(),
                tweet["text"].get_string().value_unsafe(),
                nullable_int(tweet["in_reply_to_status_id"].value_unsafe()),
                { user["id"].get_uint64().value_unsafe(), user["screen_name"].get_string().value_unsafe() },
                tweet["retweet_count"].get_uint64().value_unsafe(),
                tweet["favorite_count"].get_uint64().value_unsafe()
                });
    }
}


} // end of namespace
