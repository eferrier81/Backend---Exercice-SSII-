package ssii.dao;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ssii.entity.Participation;
import ssii.entity.Personne;
import ssii.entity.Projet;

import java.time.LocalDate;

public interface ParticipationRepository extends JpaRepository<Participation, Integer> {

    boolean existsByPersonneAndProjet(Personne personne, Projet projet);

    @Query("SELECT COALESCE(SUM(p.pourcentage), 0) " +
            "FROM Participation p " +
            "WHERE p.personne = :personne " +
            "AND p.projet.fin IS NULL OR p.projet.fin >= :date")
    Float sumPourcentageByPersonneAndProjetDate(Personne personne, LocalDate date);

    @Query("SELECT COUNT(DISTINCT p.projet) " +
            "FROM Participation p " +
            "WHERE p.personne = :personne " +
            "AND (p.projet.fin IS NULL OR p.projet.fin >= :date)")
    long countProjetsEnCoursByPersonne(Personne personne, LocalDate date);
}
