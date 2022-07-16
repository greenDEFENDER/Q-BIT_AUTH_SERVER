package bit.quantum.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class ProjectConfig {

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.sessionManagement().disable();
        http.authorizeRequests().anyRequest().permitAll();

        //   By default Spring Security disables rendering within an iframe because allowing a webpage
        //   to be added to a frame can be a security issue, for example Clickjacking.
        //   Since H2 console runs within a frame so while Spring security is enabled, frame options has
        //   to be disabled explicitly, in order to get the H2 console working.
        http.headers().frameOptions().disable();

        return http.build();
    }
}
