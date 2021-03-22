package com.bossabox.supervuttr;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SuperVuttrApplication {

	public static void main(String[] args) {
		SpringApplication.run(SuperVuttrApplication.class, args);
	}

	@Bean
	public OpenAPI customOpenAPI() {
		return new OpenAPI()
				.components(new Components()
						.addSecuritySchemes("bearer-key",
						new SecurityScheme()
								.type(SecurityScheme.Type.HTTP)
								.scheme("bearer")
								.bearerFormat("JWT")))
				.info(new Info()
						.title("Super VUTTR API")
						.description("This server stores Very Useful Tools To Remember for developers.")
						.license(new License().name("MIT").url("https://choosealicense.com/licenses/mit/"))

				);
	}


}
