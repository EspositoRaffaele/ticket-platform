package org.java.spring.ticket_platform.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
public class HomeController {

    @GetMapping
    public String home(Authentication authentication) {
        if (authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ADMIN"))) {
            // Se è un Admin, reindirizza a /tickets
            return "redirect:/tickets";
        } else {
            // Se è un Operatore, reindirizza a /operatori/index
            return "redirect:/operatori";
        }
    }
}