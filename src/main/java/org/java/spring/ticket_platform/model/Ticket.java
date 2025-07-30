package org.java.spring.ticket_platform.model;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "tickets")
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Il titolo non deve essere vuoto o null")
    private String titolo;

    @ManyToOne
    @JoinColumn(name = "categoria_id", nullable = false)
    @NotNull(message = "Il campo non può essere vuoto")
    private Categoria categoria;

    @ManyToOne
    @JoinColumn(name = "stato_id", nullable = false)
    private Stato stato;

    @ManyToOne
    @JoinColumn(name = "utente_id", nullable = false)
    @NotNull(message = "Il campo non può essere vuoto")
    private Utente utente;

    @OneToMany(mappedBy = "ticket")
    private List<Nota> note;

    //*GETTER E SETTER */
    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitolo() {
        return this.titolo;
    }

    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public Categoria getCategoria() {
        return this.categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public Stato getStato() {
        return this.stato;
    }

    public void setStato(Stato stato) {
        this.stato = stato;
    }

    public List<Nota> getNote() {
        return this.note;
    }

    public void setNote(List<Nota> note) {
        this.note = note;
    }

    public Utente getUtente() {
        return this.utente;
    }

    public void setUtente(Utente utente) {
        this.utente = utente;
    }

}
