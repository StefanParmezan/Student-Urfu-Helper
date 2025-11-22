package urfu.student.helper.security.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties("urfu.api")
public class UrfuApiConfig {
    private String baseUrl;
    private String authEndpoint;
    private String profileEndpoint;

}
