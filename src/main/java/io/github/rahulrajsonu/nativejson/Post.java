package io.github.rahulrajsonu.nativejson;

import jdk.incubator.json.JsonValue;

public record Post(int userId, int id, String title, String body) {
    static Post from(JsonValue json){
        return new Post(
                json.get("userId").asInt(),
                json.get("id").asInt(),
                json.get("title").asString(),
                json.get("body").asString()
        );
    }
}
