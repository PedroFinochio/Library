package com.mcnz.spring.app.config;

import com.mcnz.spring.app.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private CustomAuthenticationSuccessHandler successHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // Recursos públicos
                        .requestMatchers(
                                "/login",
                                "/registro",
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/error",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/swagger-resources/**",
                                "/webjars/**",
                                "/admin-tools/**",
                                "/api/test-queries/**"  // ← ENDPOINT DE TESTE (REMOVER EM PRODUÇÃO!)
                        ).permitAll()
                        // Rotas exclusivas para ADMIN
                        .requestMatchers("/admin/**").hasAnyRole("ADMIN", "BIBLIOTECARIO")
                        // Rotas para usuários comuns
                        .requestMatchers("/user/**").hasAnyRole("USER", "ADMIN", "BIBLIOTECARIO")
                        // Rotas de biblioteca para ADMIN e BIBLIOTECARIO
                        .requestMatchers(
                                "/biblioteca/novo",
                                "/biblioteca/salvar",
                                "/biblioteca/editar/**",
                                "/biblioteca/deletar/**"
                        ).hasAnyRole("ADMIN", "BIBLIOTECARIO")
                        // Todas as outras rotas autenticadas
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler(successHandler)
                        .failureUrl("/login?erro=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout=true")
                        .permitAll()
                )
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/livros/**", "/admin-tools/**", "/api/**")
                )
                .exceptionHandling(exception -> exception
                        .accessDeniedPage("/acesso-negado")
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}