package ssii.entity;

import jakarta.persistence.*;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@ToString
@Entity
public class Personne {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer matricule;

    private String nom;
    private String prenom;
    private String poste;

    @OneToMany(mappedBy = "personne", cascade = CascadeType.ALL)
    private Set<Participation> participations;
}
