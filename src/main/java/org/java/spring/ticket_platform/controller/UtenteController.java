package org.java.spring.ticket_platform.controller;

import java.util.List;
import java.util.Optional;

import org.java.spring.ticket_platform.Repository.TicketRepository;
import org.java.spring.ticket_platform.Repository.UtenteRepository;
import org.java.spring.ticket_platform.model.Ticket;
import org.java.spring.ticket_platform.model.Utente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

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
        if (utenteCorrente.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Impossibile trovare l'utente");
        }
        List<Ticket> ticketAssegnati = ticketRepository.findByUtente(utenteCorrente);
        model.addAttribute("operatore", utenteCorrente.get());

        if (keyword != null && !keyword.isEmpty() && !keyword.isBlank()) {
            ticketAssegnati = ticketRepository.findByTitoloContainingIgnoreCase(keyword);
            model.addAttribute("ticketAssegnati", ticketAssegnati);
        } else {
            model.addAttribute("ticketAssegnati", ticketAssegnati);
        }
        return "operatori/index";
    }

    @PostMapping("/update-stato")
    public String updateStato(@Valid @ModelAttribute("operatore") Utente operatoreForm, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        // if (bindingResult.hasErrors()) {
        // model.addAttribute("operatore", operatoreForm);
        // return "operatori/index";
        // }

        Optional<Utente> utenteOptional = utenteRepository.findById(operatoreForm.getId());
        if (utenteOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nessun utente trovato");
        }
        Integer numeroTicket = ticketRepository.countByUtenteId(operatoreForm.getId());
        if (numeroTicket > 0) {
            model.addAttribute("operatore", operatoreForm);
            redirectAttributes.addFlashAttribute("errore", "Non puoi disattivarti se hai ticket aperti.");
            return "redirect:/operatori/index";
        }

        Utente utente = utenteOptional.get();
        utente.setStato(operatoreForm.getStato());
        utenteRepository.save(utente);
        return "redirect:/operatori/index";

    }

    @GetMapping("/{id}")
    public String show(Authentication authentication, @PathVariable Integer id, Model model) {
        // Recupero lo username dell'utente loggato in pagina
        String username = authentication.getName();
        // Cerco l'utente tramite lo username e ne recupero l'id
        Integer idUtente = utenteRepository.findByUsername(username).get().getId();
        // Cerco il ticket per id del ticket e utente_id
        Optional<Ticket> ticket = ticketRepository.findByIdAndUtenteId(id, idUtente);
        // Se l'id dell'operatore associato al ticket non corrisponde alla rotta, torna
        // alla index
        if (ticket.isEmpty()) {
            return "redirect:/operatori/index";
        } else
            // altrimenti mostrami il ticket
            model.addAttribute("ticket", ticket.get());
        return "operatori/show";
    }
}
