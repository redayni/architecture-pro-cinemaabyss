package com.github.redayni.cinemaabyss.proxy.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RequestPredicate;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.uri;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.cloud.gateway.server.mvc.predicate.GatewayRequestPredicates.path;
import static org.springframework.cloud.gateway.server.mvc.predicate.GatewayRequestPredicates.weight;

@Configuration
public class GatewayRoutesConfig {

    @Bean
    RouterFunction<ServerResponse> gatewayRoutes(
        @Value("${MONOLITH_URL:http://monolith:8080}") String monolithUrl,
        @Value("${MOVIES_SERVICE_URL:http://movies-service:8081}") String moviesUrl,
        @Value("${EVENTS_SERVICE_URL:http://events-service:8082}") String eventsServiceUrl,
        @Value("${GRADUAL_MIGRATION:true}") boolean gradualMigration,
        @Value("${MOVIES_MIGRATION_PERCENT:0}") int moviesPercent
    ) {
        String apiEventsToEventsService = "api_events_to_events_service";
        RouterFunction<ServerResponse> gatewayRouterFunction = route(apiEventsToEventsService)
            .route(path("/api/events/**"), http())
            .before(uri(eventsServiceUrl))
            .build();

        String moviesHealthToMoviesService = "api_movies_health_to_movies_service";
        RouterFunction<ServerResponse> moviesHealthRouterFunction = route(moviesHealthToMoviesService)
            .route(path("/api/movies/health"), http())
            .before(uri(moviesUrl))
            .build();
        gatewayRouterFunction = gatewayRouterFunction.and(moviesHealthRouterFunction);

        if (gradualMigration) {
            RouterFunction<ServerResponse> moviesRouterFunction;
            RequestPredicate moviesPath = path("/api/movies").or(path("/api/movies/**"));
            if (moviesPercent <= 0) {
                String moviesToMonolith = "api_movies_to_monolith";
                moviesRouterFunction = route(moviesToMonolith)
                    .route(moviesPath, http())
                    .before(uri(monolithUrl))
                    .build();
            } else if (moviesPercent >= 100) {
                String moviesToMoviesService = "api_movies_to_movies_service";
                moviesRouterFunction = route(moviesToMoviesService)
                    .route(moviesPath, http())
                    .before(uri(moviesUrl))
                    .build();
            } else {
                moviesRouterFunction = getWeightedMoviesRouter(
                    moviesPath,
                    monolithUrl,
                    moviesUrl,
                    moviesPercent
                );
            }
            gatewayRouterFunction = gatewayRouterFunction.and(moviesRouterFunction);
        }

        String apiAllToMonolith = "api_all_to_monolith";
        RouterFunction<ServerResponse> monolithFunction = route(apiAllToMonolith)
            .route(path("/api/**"), http())
            .before(uri(monolithUrl))
            .build();

        return gatewayRouterFunction.and(monolithFunction);
    }

    private RouterFunction<ServerResponse> getWeightedMoviesRouter(
        RequestPredicate moviesPath,
        String monolithUrl,
        String moviesUrl,
        int moviesPercent
    ) {
        int pct = Math.max(0, Math.min(100, moviesPercent));
        int monolithWeight = 100 - pct;
        String moviesWeightToMoviesService = "api_movies_weight_to_movies_service";
        String moviesWeightToMonolith = "api_movies_weight_to_monolith";
        String migrationGroup = "movies-migration";
        return route(moviesWeightToMoviesService)
            .route(weight(migrationGroup, pct).and(moviesPath), http())
            .before(uri(moviesUrl))
            .build()
            .and(
                route(moviesWeightToMonolith)
                    .route(weight(migrationGroup, monolithWeight).and(moviesPath), http())
                    .before(uri(monolithUrl))
                    .build()
            );
    }
}
