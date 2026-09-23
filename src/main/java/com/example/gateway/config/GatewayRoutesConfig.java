package com.example.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.stripPrefix;
import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.uri;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.cloud.gateway.server.mvc.predicate.GatewayRequestPredicates.path;

/**
 * Java-based route definition — needed because Gateway Server MVC cannot reference
 * a custom HandlerFilterFunction from application.yaml (confirmed limitation, not a
 * config mistake). Your existing YAML route for /api/books/** can stay as-is for
 * comparison, or be removed once this bean covers the same path — having both active
 * at once would double-register the route, so pick one.
 */
@Configuration
public class GatewayRoutesConfig {

    @Bean
    public RouterFunction<ServerResponse> booksRouteWithRateLimit(RateLimitFilterFunction rateLimitFilterFunction) {
        return route("books")
                .route(path("/api/books/**"), http())
                .before(uri("http://localhost:7979"))
                .before(stripPrefix(1))
                .filter(rateLimitFilterFunction)
                .build();

        // TODO: verify this compiles against your exact Spring Cloud Gateway version —
        // the Java Routes API has shifted slightly across recent releases (e.g.
        // HandlerFunctions.http(String) was deprecated in favor of http() + before(uri(...))
        // per the docs — worth checking which era your dependency is on).
    }
}
