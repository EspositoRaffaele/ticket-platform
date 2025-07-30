package org.java.spring.ticket_platform.controller.api;

import java.util.List;

import org.java.spring.ticket_platform.Repository.CategoriaRepository;
import org.java.spring.ticket_platform.Repository.StatoRepository;
import org.java.spring.ticket_platform.Repository.TicketRepository;
import org.java.spring.ticket_platform.model.Ticket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/testApi")
public class TicketRestController {

    @Autowired
    TicketRepository ticketRepository;
    @Autowired
    CategoriaRepository categoriaRepository;
    @Autowired
    StatoRepository statoRepository;

    @GetMapping("/allTickets")
    public List<Ticket> getAllTickets(){
        return ticketRepository.findAll();
    }

    @GetMapping("/TicketsByCategory")
    public List<Ticket> getTicketsByCategory(@RequestParam String nomeCategoria){
        return ticketRepository.findByCategoria(categoriaRepository.findByNome(nomeCategoria));
    }

    @GetMapping("/TicketsByStato")
    public List<Ticket> getTicketsByStato(@RequestParam String nomeStato){
        return ticketRepository.findByStato(statoRepository.findByNomeStato(nomeStato));
    }
}
