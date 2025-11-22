package urfu.student.helper.security.jwt;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.time.temporal.ChronoUnit;

@Data
@Configuration
@ConfigurationProperties("secret.jwt")
public class JwtConfig {
    private String signingKey;
    private LifeTime lifeTime;

    @Data
    public static class LifeTime {
        private Long value;
        private ChronoUnit unit;
    }

    @PostConstruct
    private void check() {
        if (!StringUtils.hasText(signingKey)) {
            throw new IllegalArgumentException("secret.jwt.signing-key must be sat");
        }
        if (lifeTime == null || lifeTime.getValue() == null) {
            throw new IllegalArgumentException("secret.jwt.life-time.value must be sat");
        }
        if (lifeTime.getUnit() == null) {
            lifeTime.setUnit(ChronoUnit.MILLIS);
        }
    }
}
