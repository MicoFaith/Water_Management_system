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
										"""
										Backend API for customer, meter, billing, payment, and notification management.

										## How to authenticate
										1. **POST /api/auth/login** with a test account below
										2. **POST /api/auth/verify-otp** with the OTP sent to the account email
										3. Click **Authorize** (top right) and enter: `Bearer <your-jwt-token>`

										## Test accounts by role
										| Role | Email | Password |
										|------|-------|----------|
										| ADMIN | faithmico4@gmail.com | Admin123 |
										| OPERATOR | buterafaith@gmail.com | Operator123 |
										| FINANCE | isimbihyguette24@gmail.com | Finance123 |
										| CUSTOMER | faithmico25@gmail.com | Customer123 |

										Each endpoint description lists the role(s) required. Use the matching account before calling protected APIs.
										""")
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
