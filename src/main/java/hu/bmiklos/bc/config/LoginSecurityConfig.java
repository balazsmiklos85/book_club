package hu.bmiklos.bc.config;

import static hu.bmiklos.bc.service.security.BookClubAuthority.BOOKCLUB_ADMIN;
import static hu.bmiklos.bc.service.security.BookClubAuthority.BOOKCLUB_USER;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class LoginSecurityConfig {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(CsrfConfigurer::disable)
                .authorizeHttpRequests(
                        authorizeHttpRequests -> authorizeHttpRequests.requestMatchers("/login*").permitAll()
                                .requestMatchers("/register*").permitAll()
                                .requestMatchers("/admin/**").hasAuthority(BOOKCLUB_ADMIN.getAuthority())
                                .anyRequest().hasAnyAuthority(BOOKCLUB_USER.getAuthority()))
                .formLogin(login -> login.loginPage("/login")
                        .defaultSuccessUrl("/", true)
                        .failureUrl("/login?error=true"))
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .deleteCookies("JSESSIONID"));
        return http.build();
    }
}
