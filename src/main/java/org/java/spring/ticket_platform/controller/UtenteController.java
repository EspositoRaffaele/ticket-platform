package org.java.spring.ticket_platform.controller;

import java.util.List;
import java.util.Optional;

import org.java.spring.ticket_platform.Repository.CategoriaRepository;
import org.java.spring.ticket_platform.Repository.NotaRepository;
import org.java.spring.ticket_platform.Repository.StatoRepository;
import org.java.spring.ticket_platform.Repository.TicketRepository;
import org.java.spring.ticket_platform.Repository.UtenteRepository;
import org.java.spring.ticket_platform.model.Nota;
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
    @Autowired
    private NotaRepository notaRepository;
    @Autowired
    private CategoriaRepository categoriaRepository;
    @Autowired
    private StatoRepository statoRepository;

    @GetMapping
    public String index(Authentication authentication, @RequestParam(name = "keyword", required = false) String keyword,
            Model model) {
        // Recupero nome dell'utente autenticato
        String operatore = authentication.getName();
        // Recupero utente da db con query find by username
        Optional<Utente> utenteCorrente = utenteRepository.findByEmail(operatore);
        if (utenteCorrente.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Impossibile trovare l'utente");
        }
        // Recupero della lista dei ticket con operatore(attualmente loggato) assegnato grazie alla query find by utente
        List<Ticket> ticketAssegnati = ticketRepository.findByUtente(utenteCorrente);
        model.addAttribute("operatore", utenteCorrente.get());

        // Ricerca del ticket grazie alla query find by titolo e id utente
        if (keyword != null && !keyword.isEmpty() && !keyword.isBlank()) {
            ticketAssegnati = ticketRepository.findByTitoloContainingIgnoreCaseAndUtenteId(keyword, utenteCorrente.get().getId());
            model.addAttribute("ticketAssegnati", ticketAssegnati);
        } else {
            model.addAttribute("ticketAssegnati", ticketAssegnati);
        }
        return "operatori/index";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Integer id, Model model) {
        List<Utente> utente;
        utente = utenteRepository.findByRuoliNomeRuoloAndStatoTrue("OPERATORE");
        model.addAttribute("operatoriDisponibili", utente);
        model.addAttribute("ticket", ticketRepository.findById(id).get());
        model.addAttribute("categorie", categoriaRepository.findAll());
        model.addAttribute("stati", statoRepository.findAll());
        return "operatori/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(Authentication authentication, @PathVariable Integer id, @Valid @ModelAttribute("ticket") Ticket formTicket, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            return "operatori/edit";
        }

        ticketRepository.save(formTicket);

        return "redirect:/operatori";
    }

    @PostMapping("/update-stato")
    public String updateStato(Authentication authentication, @ModelAttribute("operatore") Utente operatoreForm, RedirectAttributes redirectAttributes,
            Model model) {

        // Recupero l'utente dal form tramite Id
        Optional<Utente> utenteOptional = utenteRepository.findByEmail(authentication.getName());
        if (utenteOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nessun utente trovato");
        }
        // Ricerca con query su ticket in DB -> find by not id stato(Cerco ticket che non abbiano id=3 ovvero stato=completato) e id utente
        List<Ticket> ticketIncompleti = ticketRepository.findByUtenteIdAndStatoIdNot(utenteOptional.get().getId(), 3);
        // L'utente non può disattivarsi se la lista dei ticket non completati non è vuota, ovvero sono presenti ticket DA_FARE e IN_CORSO 
        // a quel punto reindirizza ad /operatori e invia un messaggio di errore in pagina
        if (!ticketIncompleti.isEmpty()) {
        model.addAttribute("operatore", operatoreForm);
        redirectAttributes.addFlashAttribute("errore", "Non puoi disattivarti se hai ticket aperti.");
            return "redirect:/operatori";
        }

        Utente utente = utenteOptional.get();
        utente.setStato(operatoreForm.getStato());
        utenteRepository.save(utente);
        redirectAttributes.addFlashAttribute("successo", "Stato aggiornato con successo.");
        return "redirect:/operatori";

    }

    @GetMapping("/{id}")
    public String show(Authentication authentication, @PathVariable Integer id, Model model) {
        // Recupero lo username dell'utente loggato in pagina
        String username = authentication.getName();
        // Cerco l'utente tramite lo username e ne recupero l'id
        Integer idUtente = utenteRepository.findByEmail(username).get().getId();
        // Cerco il ticket per id del ticket e utente_id
        Optional<Ticket> ticket = ticketRepository.findByIdAndUtenteId(id, idUtente);
        // Se l'id dell'operatore associato al ticket non corrisponde alla rotta, torna
        // alla index
        if (ticket.isEmpty()) {
            return "redirect:/operatori";
        } else
            // altrimenti mostrami il ticket
            model.addAttribute("ticket", ticket.get());
        return "operatori/show";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Integer id, Model model) {
        Ticket ticketDaCancellare = ticketRepository.findById(id).get();

        // Per ogni nota presente nel ticket da cancellare, cancella la nota
        for (Nota nota : ticketDaCancellare.getNote()) {
            notaRepository.delete(nota);
        }

        ticketRepository.delete(ticketDaCancellare);

        return "redirect:/operatori";
    }
}
