package ssii.service;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ssii.dao.ParticipationRepository;
import ssii.dao.PersonneRepository;
import ssii.dao.ProjetRepository;
import ssii.entity.Participation;
import ssii.entity.Personne;
import ssii.entity.Projet;

import java.time.LocalDate;
import java.util.List;

@Service
public class ParticipationService {

    private final ParticipationRepository participationRepository;
    private final PersonneRepository personneRepository;
    private final ProjetRepository projetRepository;

    public ParticipationService(ParticipationRepository participationRepository,
                                PersonneRepository personneRepository,
                                ProjetRepository projetRepository) {
        this.participationRepository = participationRepository;
        this.personneRepository = personneRepository;
        this.projetRepository = projetRepository;
    }

    @Transactional
    public void ajouterParticipation(Integer matricule, Integer codeProjet, String role, Float pourcentage) {
        Personne personne = personneRepository.findById(matricule)
                .orElseThrow(() -> new IllegalArgumentException("Personne introuvable avec le matricule : " + matricule));

        Projet projet = projetRepository.findById(codeProjet)
                .orElseThrow(() -> new IllegalArgumentException("Projet introuvable avec le code : " + codeProjet));

        // Règle 1 : Une personne ne peut pas participer plusieurs fois au même projet
        boolean dejaParticipant = participationRepository.existsByPersonneAndProjet(personne, projet);
        if (dejaParticipant) {
            throw new IllegalStateException("La personne participe déjà à ce projet.");
        }

        // Règle 2 : Une personne ne peut pas être occupée à plus de 100% sur des projets en cours
        LocalDate today = LocalDate.now();
        Float totalPourcentage = participationRepository.sumPourcentageByPersonneAndProjetDate(personne, today);
        if (totalPourcentage + pourcentage > 100) {
            throw new IllegalStateException("La personne serait occupée à plus de 100%.");
        }

        // Règle 3 : Une personne ne peut pas participer à plus de trois projets en même temps
        long projetsEnCours = participationRepository.countProjetsEnCoursByPersonne(personne, today);
        if (projetsEnCours >= 3) {
            throw new IllegalStateException("La personne participe déjà à 3 projets en même temps.");
        }

        // Ajouter la participation
        Participation participation = new Participation();
        participation.setPersonne(personne);
        participation.setProjet(projet);
        participation.setRole(role);
        participation.setPourcentage(pourcentage);
        participationRepository.save(participation);
    }
}
