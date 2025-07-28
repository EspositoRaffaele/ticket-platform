package org.java.spring.ticket_platform.security;

import java.util.Optional;

import org.java.spring.ticket_platform.model.Utente;
import org.java.spring.ticket_platform.Repository.UtenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class DatabaseUserDetailsService implements UserDetailsService{
    
    @Autowired
    private UtenteRepository utenteRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
            
        Optional<Utente> utente = utenteRepository.findByUsername(username);
        if (utente.isPresent()) {
            return new DatabaseUserDetails(utente.get());
        } else {
            throw new UsernameNotFoundException("Username non trovato");
        }
    }
}
