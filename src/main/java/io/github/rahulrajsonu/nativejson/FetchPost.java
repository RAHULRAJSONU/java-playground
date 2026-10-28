package io.github.rahulrajsonu.nativejson;

import jdk.incubator.json.Json;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class FetchPost {
    void main() throws IOException, InterruptedException {
        try(HttpClient client = HttpClient.newHttpClient()){
            HttpRequest request = HttpRequest.newBuilder(URI.create("https://jsonplaceholder.typicode.com/posts")).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            List<Post> posts = Json.parse(response.body()).asList().stream()
                    .map(Post::from)
                    .toList();
            IO.println("Fetched "+posts.size()+" posts.");
            posts.stream()
                    .limit(10)
                    .forEach(p -> IO.println(p.id()+" : "+p.title()));
        }
    }
}
