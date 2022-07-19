package bit.quantum.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class ProjectConfig {
    @Value("${client.id}")
    private String clientId;

    @Value("${client.secret}")
    private String clientSecret;

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.sessionManagement().disable();
        http.authorizeRequests()
                .mvcMatchers("/secret/**")
                .hasAuthority("business_server")
                .anyRequest()
                .authenticated()
                .and()
                .httpBasic();

        //   By default Spring Security disables rendering within an iframe because allowing a webpage
        //   to be added to a frame can be a security issue, for example Clickjacking.
        //   Since H2 console runs within a frame so while Spring security is enabled, frame options has
        //   to be disabled explicitly, in order to get the H2 console working.
        http.headers().frameOptions().disable();

        return http.build();
    }

    @Bean
    UserDetailsService userDetailsService() {
        InMemoryUserDetailsManager userDetailsManager = new InMemoryUserDetailsManager();
        UserDetails client = User.withUsername(clientId)
                .password(passwordEncoder().encode(clientSecret))
                .authorities("client")
                .build();
        UserDetails businessServer = User.withUsername("businessServer")
                .password(passwordEncoder().encode("businessServer"))
                .authorities("business_server")
                .build();
        userDetailsManager.createUser(client);
        userDetailsManager.createUser(businessServer);
        return userDetailsManager;
    }
}
