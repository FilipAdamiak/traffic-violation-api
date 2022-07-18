package pl.kurs.config.security.configuration;

import com.auth0.jwt.algorithms.Algorithm;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "jwt")
@Getter
@Setter
public class JwtConfigurationProperties {

    private String secret;
    private long expirationTime;

    @Bean
    public Algorithm algorithm() {
        return Algorithm.HMAC256(getSecret().getBytes());
    }

}
