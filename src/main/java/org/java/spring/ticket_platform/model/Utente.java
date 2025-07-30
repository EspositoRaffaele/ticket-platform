package org.java.spring.ticket_platform.model;

import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "utenti", uniqueConstraints = {@UniqueConstraint(columnNames = "email")})
public class Utente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Il nome non può essere vuoto o null")
    private String nome;

    @Email(message = "Formato non valido")
    @NotBlank(message = "L'email non può essere vuoto o null")
    private String email;

    @NotBlank(message = "La password nome non può essere vuoto o null")
    @Size(min = 5, message = "La password deve essere lunga almeno 5 caratteri")
    private String password;

    private boolean stato;

    @OneToMany(mappedBy = "utente")
    @JsonBackReference
    private List<Nota> note;

    @OneToMany(mappedBy = "utente")
    @JsonBackReference
    private List<Ticket> tickets;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "utente_ruolo", joinColumns = @JoinColumn(name = "utente_id"), inverseJoinColumns = @JoinColumn(name = "ruolo_id"))
    @JsonBackReference
    private Set<Ruolo> ruoli;

    //*GETTER E SETTER */
    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return this.nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isStato() {
        return this.stato;
    }

    public boolean getStato() {
        return this.stato;
    }

    public void setStato(boolean stato) {
        this.stato = stato;
    }

    public List<Nota> getNote() {
        return this.note;
    }

    public void setNote(List<Nota> note) {
        this.note = note;
    }

    public Set<Ruolo> getRuoli() {
        return this.ruoli;
    }

    public void setRuoli(Set<Ruolo> ruoli) {
        this.ruoli = ruoli;
    }

}
