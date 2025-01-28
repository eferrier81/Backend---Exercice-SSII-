package ssii.entity;

import jakarta.persistence.*;

import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Getter
@Setter
@ToString
public class Projet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer code;

    private String nom;

    @Column(nullable = false)
    private LocalDate debut;

    @Column(nullable = true)
    private LocalDate fin;

    @OneToMany(mappedBy = "projet", cascade = CascadeType.ALL)
    private Set<Participation> participations;
}