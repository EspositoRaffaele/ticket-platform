package org.java.spring.ticket_platform.controller;

import java.util.List;
import java.util.Optional;

import org.java.spring.ticket_platform.Repository.TicketRepository;
import org.java.spring.ticket_platform.Repository.UtenteRepository;
import org.java.spring.ticket_platform.model.Ticket;
import org.java.spring.ticket_platform.model.Utente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/operatori")
public class UtenteController {
    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private UtenteRepository utenteRepository;

    @GetMapping("/index")
    public String index(Authentication authentication, @RequestParam(name = "keyword", required = false) String keyword,
            Model model) {
        String operatore = authentication.getName();
        Optional<Utente> utenteCorrente = utenteRepository.findByUsername(operatore);
        List<Ticket> ticketAssegnati = ticketRepository.findByUtente(utenteCorrente);

        if (keyword != null && !keyword.isEmpty() && !keyword.isBlank()) {
            ticketAssegnati = ticketRepository.findByTitoloContainingIgnoreCase(keyword);
            model.addAttribute("ticketAssegnati", ticketAssegnati);
        } else {
            model.addAttribute("ticketAssegnati", ticketAssegnati);
        }
        return "operatori/index";
    }

    @GetMapping("/{id}")
    public String show(Authentication authentication, @PathVariable Integer id, Model model) {
        // Recupero lo username dell'utente loggato in pagina
        String username = authentication.getName();
        // Cerco l'utente tramite lo username e ne recupero l'id 
        Integer idUtente = utenteRepository.findByUsername(username).get().getId();
        // Cerco il ticket per id del ticket e utente_id
        Optional<Ticket> ticket = ticketRepository.findByIdAndUtenteId(id, idUtente);
        // Se l'id dell'operatore associato al ticket non corrisponde alla rotta, torna alla index
        if (ticket.isEmpty()) {
            return "redirect:/operatori/index";
        }else 
        // altrimenti mostrami il ticket
        model.addAttribute("ticket", ticket.get());
        return "operatori/show";
    }
}
