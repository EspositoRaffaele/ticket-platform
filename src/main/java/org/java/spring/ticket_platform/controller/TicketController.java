package org.java.spring.ticket_platform.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.java.spring.ticket_platform.Repository.CategoriaRepository;
import org.java.spring.ticket_platform.Repository.NotaRepository;
import org.java.spring.ticket_platform.Repository.RuoloRepository;
import org.java.spring.ticket_platform.Repository.StatoRepository;
import org.java.spring.ticket_platform.Repository.TicketRepository;
import org.java.spring.ticket_platform.Repository.UtenteRepository;
import org.java.spring.ticket_platform.model.Nota;
import org.java.spring.ticket_platform.model.Stato;
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

import jakarta.validation.Valid;

@Controller
@RequestMapping("/tickets")
public class TicketController {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private UtenteRepository utenteRepository;

    @Autowired
    private StatoRepository statoRepository;

    @Autowired
    private NotaRepository notaRepository;

    @Autowired
    private RuoloRepository ruoloRepository;

    @GetMapping
    public String index(Model model, @RequestParam(name = "keyword", required = false) String keyword) {
        List<Ticket> tickets;
        // Logica di ricerca ticket in index tramite keyword richiesta come parametro
        if (keyword != null && !keyword.isEmpty() && !keyword.isBlank()) {
            tickets = ticketRepository.findByTitoloContainingIgnoreCase(keyword);
        } else {
            tickets = ticketRepository.findAll();
        }
        model.addAttribute("operatori", utenteRepository.findByRuoliNomeRuolo("OPERATORE"));
        model.addAttribute("tickets", tickets);

        return "tickets/index";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable Integer id, Model model) {

        model.addAttribute("ticket", ticketRepository.findById(id).get());

        return "tickets/show";
    }

    @GetMapping("/create")
    public String create(Model model) {

        // Query su utenti - ricerca per nome_ruolo e stato - Ritorna una lista con
        // operatori in stato attivo
        List<Utente> operatori = utenteRepository.findByRuoliNomeRuoloAndStatoTrue("OPERATORE");
        // Recupero lo stato del ticket di tipo 1 (Da Fare) e lo imposto come default
        // alla creazione
        Optional<Stato> stato = statoRepository.findById(1);

        Ticket ticket = new Ticket();
        ticket.setStato(stato.get());
        model.addAttribute("operatoriDisponibili", operatori);
        model.addAttribute("categorie", categoriaRepository.findAll());
        model.addAttribute("stato", stato.get());
        model.addAttribute("ticket", ticket);

        return "tickets/create";
    }

    @PostMapping("/create")
    public String store(@Valid @ModelAttribute("ticket") Ticket formTicket, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("categorie", categoriaRepository.findAll());
            model.addAttribute("operatoriDisponibili", utenteRepository.findByRuoliNomeRuoloAndStatoTrue("OPERATORE"));
            return "tickets/create";
        }
        ticketRepository.save(formTicket);
        return "redirect:/tickets";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Integer id, Model model) {
        List<Utente> utente;
        utente = utenteRepository.findByRuoliNomeRuoloAndStatoTrue("OPERATORE");
        model.addAttribute("operatoriDisponibili", utente);
        model.addAttribute("ticket", ticketRepository.findById(id).get());
        model.addAttribute("categorie", categoriaRepository.findAll());
        model.addAttribute("stati", statoRepository.findAll());
        return "/tickets/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(Authentication authentication, @PathVariable Integer id,
            @Valid @ModelAttribute("ticket") Ticket formTicket, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("categorie", categoriaRepository.findAll());
            model.addAttribute("opertoriDisponibili", utenteRepository.findAll());
            model.addAttribute("stati", statoRepository.findAll());
            return "/tickets/edit";
        }

        ticketRepository.save(formTicket);

        if (authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("OPERATORE"))) {
            // Se è un Operatore, reindirizza a /operatori/index
            return "redirect:/operatori";
        } else

            return "redirect:/tickets";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Integer id, Model model) {
        Ticket ticketDaCancellare = ticketRepository.findById(id).get();

        for (Nota nota : ticketDaCancellare.getNote()) {
            notaRepository.delete(nota);
        }

        ticketRepository.delete(ticketDaCancellare);

        return "redirect:/tickets";
    }

    @GetMapping("/nota/{id}")
    public String creaNota(Authentication authentication, @PathVariable("id") Integer id, Model model) {

        Optional<Ticket> ticketOptional = ticketRepository.findById(id);
        if (ticketOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Impossibile creare una nota: Non esiste un ticket con id " + id);
        }

        String username = authentication.getName();
        Optional<Utente> utenteCorrente = utenteRepository.findByEmail(username);
        if (utenteCorrente.isPresent()) {
            model.addAttribute("utenteCorrente", utenteCorrente.get());

        } else {

            return "redirect:/login";
        }

        model.addAttribute("ticket", ticketOptional.get());
        Nota nota = new Nota();
        nota.setTicket(ticketOptional.get());
        nota.setUtente(utenteCorrente.get());
        nota.setDataCreazione(LocalDate.now());
        model.addAttribute("nota", nota);

        return "note/create";
    }

}
