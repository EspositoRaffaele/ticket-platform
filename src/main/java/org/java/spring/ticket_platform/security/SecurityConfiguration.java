package org.java.spring.ticket_platform.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfiguration {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(requests -> requests
                .requestMatchers("/tickets", "/tickets/index", "/tickets/create").hasAuthority("ADMIN")
                .requestMatchers(HttpMethod.POST, "/tickets/**").permitAll()
                .requestMatchers("/tickets/{id}").hasAnyAuthority("OPERATORE", "ADMIN")
                .requestMatchers("/operatori", "/operatori/**", "/tickets/{id}").hasAuthority("OPERATORE")
                .requestMatchers("/tickets", "/tickets/**").hasAnyAuthority("OPERATORE", "ADMIN")
                .requestMatchers("/**").permitAll())
                .formLogin(Customizer.withDefaults());

        return http.build();
    }

    // ! Authentication Provider

    @Bean
    DaoAuthenticationProvider authenticationProvider() {
        // inseriamo direttamente nel costruttore il nostro userDetailsService;
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    DatabaseUserDetailsService userDetailsService() {
        return new DatabaseUserDetailsService(); // @ Strumento per andare a cercare utente by username
    }
    // * Al suo interno chiamerà:
    // * UserDetailService per cercare lo username
    // * PasswordEncoder per verificare la correttezza della password

    // ?UserDetailService => lo strumento che verifica la presenza degli utenti

    // ? PasswordEncoder => lo strumento che gestisce la verifica e l'inserimento
    // delle password

    @Bean
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder(); // in base a quello che mettiamo nel DB prima
                                                                           // della password, stabilisci quale sia la
                                                                           // funzione di hashing che userai.
    }
}
