package national_exam.Java.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

	@Bean
	public OpenAPI utilityBillingOpenAPI() {
		final String securitySchemeName = "Bearer Authentication";
		return new OpenAPI()
				.info(
						new Info()
								.title("WASAC/REG Utility Billing System API")
								.description(
										"Backend API for customer, meter, billing, payment, and notification management")
								.version("1.0.0")
								.contact(new Contact().name("National Exam").email("support@wasac.rw")))
				.addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
				.components(
						new Components()
								.addSecuritySchemes(
										securitySchemeName,
										new SecurityScheme()
												.name(securitySchemeName)
												.type(SecurityScheme.Type.HTTP)
												.scheme("bearer")
												.bearerFormat("JWT")));
	}
}
