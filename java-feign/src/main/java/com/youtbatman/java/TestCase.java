package com.youtbatman.java;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.youtbatman.java.client.DemoClient;
import feign.*;
import lombok.Data;

import java.util.List;

public class TestCase {

    // public static void main(String[] args) {
    //     DemoClient client = Feign.builder()
    //             .logger(new Logger.ErrorLogger()).logLevel(Logger.Level.FULL)
    //             .retryer(Retryer.NEVER_RETRY)
    //             .target(DemoClient.class, "http://localhost:8080");
    //     String result = client.getDemo1("YourBatman");
    //     System.out.println(result);
    // }
    interface GitHub {
        @RequestLine("GET /repos/{owner}/{repo}/contributors")
        List<Contributor> contributors(@Param("owner") String owner, @Param("repo") String repo);

        @RequestLine("POST /repos/{owner}/{repo}/issues")
        void createIssue(Issue issue, @Param("owner") String owner, @Param("repo") String repo);

    }
@Data
    public static class Contributor {
        String login;
        int contributions;
    }

    public static class Issue {
        String title;
        String body;
        List<String> assignees;
        int milestone;
        List<String> labels;
    }

    static class MyApp {
        public static void main(String... args) {
            GitHub github = Feign.builder() .logger(new Logger.ErrorLogger())
                    .logLevel(Logger.Level.FULL)
                    .decoder((response, type) -> {

                        ObjectMapper objectMapper = new ObjectMapper();
                        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                        return objectMapper.readValue(response.body().asInputStream(),new TypeReference<List<Contributor>>(){});
                    })
                    .target(GitHub.class, "https://api.github.com");

            // Fetch and print a list of the contributors to this library.
            List<Contributor> contributors = github.contributors("OpenFeign", "feign");
            for (Contributor contributor : contributors) {
                System.out.println(contributor.login + " (" + contributor.contributions + ")");
            }
        }
    }
}


