package leandro.dev.gestao_obras.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI(){
        return new OpenAPI()
                .info(new Info()
                        .title("Gestão  de obras - API de gestão de obras")
                        .version("1.0.0")
                        .description("API REST para gerenciamento de obra , etapas , cronogramas e checklists")
                        .contact(new Contact()
                                .name(" LEANDRO BARRETO DE BRITO")
                                .email("leandrobarreto.barrto@gmail.com")
                                .url("https://github.com/LeandroBryto/obrafacil-gestao-obras"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")));
    }
}
