package com.example.gateway.config;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.function.HandlerFilterFunction;
import org.springframework.web.servlet.function.HandlerFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

@Component
public class ApiKeyFilterFunction implements HandlerFilterFunction<ServerResponse, ServerResponse> {

    private static final String EXPECTED_KEY = "281c5ca1";

    @Override
    public ServerResponse filter(ServerRequest request, HandlerFunction<ServerResponse> next) throws Exception {

        if (!EXPECTED_KEY.equals(request.headers().firstHeader("X-api-key"))) {
            return ServerResponse.status(HttpStatus.UNAUTHORIZED).build();
        }

        return next.handle(request);
    }
}
