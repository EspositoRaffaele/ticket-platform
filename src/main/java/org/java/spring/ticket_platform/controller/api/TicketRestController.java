package org.java.spring.ticket_platform.controller.api;

import java.util.List;

import org.java.spring.ticket_platform.Repository.CategoriaRepository;
import org.java.spring.ticket_platform.Repository.StatoRepository;
import org.java.spring.ticket_platform.Repository.TicketRepository;
import org.java.spring.ticket_platform.model.Ticket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/tickets")
public class TicketRestController {

    @Autowired
    TicketRepository ticketRepository;
    @Autowired
    CategoriaRepository categoriaRepository;
    @Autowired
    StatoRepository statoRepository;

    @GetMapping()
    public ResponseEntity<List<Ticket>> tickets(){
        List<Ticket> tickets = ticketRepository.findAll();

        if (tickets.size() == 0){
            return new ResponseEntity<>(tickets, HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(tickets, HttpStatus.OK);
    }

    @GetMapping("/categoria/{nomeCategoria}")
    public ResponseEntity<List<Ticket>> ticketsPerCategoria(@PathVariable String nomeCategoria){
        List<Ticket> ticketsPerCategoria = ticketRepository.findByCategoria(categoriaRepository.findByNome(nomeCategoria));

            if (ticketsPerCategoria.size() == 0){
            return new ResponseEntity<>(ticketsPerCategoria, HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(ticketsPerCategoria, HttpStatus.OK);
    }

    @GetMapping("/stato/{nomeStato}")
    public ResponseEntity<List<Ticket>> getTicketsByStato(@PathVariable String nomeStato){
        List<Ticket> ticketsPerStato = ticketRepository.findByStato(statoRepository.findByNomeStato(nomeStato));
        
        if (ticketsPerStato.isEmpty()) {
            return new ResponseEntity<>(ticketsPerStato, HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(ticketsPerStato, HttpStatus.OK);
    }
}
