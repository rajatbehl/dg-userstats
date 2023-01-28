package com.dangalgames.userstats.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

/**
 * @author Ritika A.
 * @author rajat behl
 *
 */
@Configuration
public class OpenAPIConfig {

	@Bean
    public OpenAPI springShopOpenAPI() {
		return new OpenAPI()
                .info(new Info().title("User Stats Service APIs")
                .description("User Stats Service")
                .version("v1.0"))
                .externalDocs(new ExternalDocumentation()
                .description("User Stats Service Wiki Documentation")
                .url("https://dangalgamesinc.atlassian.net/wiki/spaces/EN/pages/542539806/User+Stats+Service"));
    }
}
