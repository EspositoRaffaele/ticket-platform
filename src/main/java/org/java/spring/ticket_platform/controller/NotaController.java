package org.java.spring.ticket_platform.controller;

import org.java.spring.ticket_platform.Repository.NotaRepository;
import org.java.spring.ticket_platform.model.Nota;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/note")
public class NotaController {
    @Autowired
    private NotaRepository notaRepository;

    @PostMapping
    public String store(Authentication authentication, @Valid @ModelAttribute("nota") Nota notaForm,
            BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("nota", notaForm);
            return "note/create";
        }

        notaRepository.save(notaForm);

        // Recupero dalle Authority il ruolo associato all'utente attualmente loggato e verifico quale dei due sia per poi reindirizzarli alla pagina di appartenenza
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
