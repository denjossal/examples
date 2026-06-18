package com.denjossal.study.aws.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import java.util.Map;

public class HelloHandler implements RequestHandler<Map<String, String>, String> {

    @Override
    public String handleRequest(Map<String, String> input, Context context) {
        String name = input.getOrDefault("name", "World");
        context.getLogger().log("Processing request for: " + name);
        return "Hello, " + name + "!";
    }
}
