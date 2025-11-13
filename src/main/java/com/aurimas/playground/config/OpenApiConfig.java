package com.aurimas.playground.config;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
    info = @Info(
        title = "OpenAPI documentation",
        description = "Open API documentation for playground manager",
        version = "1.0"
    ),
    servers = {
        @Server(
            description = "Local",
            url = "http://localhost:8080"
        )
    }
)
public class OpenApiConfig {
}
