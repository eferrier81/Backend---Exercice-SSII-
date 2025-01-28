package ssii.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ssii.dao.ParticipationRepository;
import ssii.dao.PersonneRepository;
import ssii.dao.ProjetRepository;
import ssii.entity.Participation;
import ssii.entity.Personne;
import ssii.entity.Projet;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ParticipationServiceTest {

    @InjectMocks
    private ParticipationService participationService;

    @Mock
    private ParticipationRepository participationRepository;

    @Mock
    private PersonneRepository personneRepository;

    @Mock
    private ProjetRepository projetRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testPersonneCannotParticipateTwiceInSameProject() {
        // Arrange
        Integer matricule = 1;
        Integer codeProjet = 1;

        Personne personne = new Personne();
        Projet projet = new Projet();

        when(personneRepository.findById(matricule)).thenReturn(Optional.of(personne));
        when(projetRepository.findById(codeProjet)).thenReturn(Optional.of(projet));
        when(participationRepository.existsByPersonneAndProjet(personne, projet)).thenReturn(true);

        // Act & Assert
        Exception exception = assertThrows(IllegalStateException.class, () ->
                participationService.ajouterParticipation(matricule, codeProjet, "Développeur", 50f)
        );

        assertEquals("La personne participe déjà à ce projet.", exception.getMessage());
        verify(participationRepository, times(1)).existsByPersonneAndProjet(personne, projet);
    }

    @Test
    void testPersonneCannotBeOverloadedMoreThan100Percent() {
        // Arrange
        Integer matricule = 1;
        Integer codeProjet = 1;

        Personne personne = new Personne();
        Projet projet = new Projet();

        when(personneRepository.findById(matricule)).thenReturn(Optional.of(personne));
        when(projetRepository.findById(codeProjet)).thenReturn(Optional.of(projet));
        when(participationRepository.existsByPersonneAndProjet(personne, projet)).thenReturn(false);
        when(participationRepository.sumPourcentageByPersonneAndProjetDate(personne, LocalDate.now())).thenReturn(80f);

        // Act & Assert
        Exception exception = assertThrows(IllegalStateException.class, () ->
                participationService.ajouterParticipation(matricule, codeProjet, "Chef de Projet", 30f)
        );

        assertEquals("La personne serait occupée à plus de 100%.", exception.getMessage());
        verify(participationRepository, times(1)).sumPourcentageByPersonneAndProjetDate(personne, LocalDate.now());
    }

    @Test
    void testPersonneCannotParticipateInMoreThanThreeProjectsSimultaneously() {
        // Arrange
        Integer matricule = 1;
        Integer codeProjet = 1;

        Personne personne = new Personne();
        Projet projet = new Projet();

        when(personneRepository.findById(matricule)).thenReturn(Optional.of(personne));
        when(projetRepository.findById(codeProjet)).thenReturn(Optional.of(projet));
        when(participationRepository.existsByPersonneAndProjet(personne, projet)).thenReturn(false);
        when(participationRepository.sumPourcentageByPersonneAndProjetDate(personne, LocalDate.now())).thenReturn(50f);
        when(participationRepository.countProjetsEnCoursByPersonne(personne, LocalDate.now())).thenReturn(3L);

        // Act & Assert
        Exception exception = assertThrows(IllegalStateException.class, () ->
                participationService.ajouterParticipation(matricule, codeProjet, "Développeur", 30f)
        );

        assertEquals("La personne participe déjà à 3 projets en même temps.", exception.getMessage());
        verify(participationRepository, times(1)).countProjetsEnCoursByPersonne(personne, LocalDate.now());
    }

    @Test
    void testSuccessfulParticipationRegistration() {
        // Arrange
        Integer matricule = 1;
        Integer codeProjet = 1;

        Personne personne = new Personne();
        Projet projet = new Projet();

        when(personneRepository.findById(matricule)).thenReturn(Optional.of(personne));
        when(projetRepository.findById(codeProjet)).thenReturn(Optional.of(projet));
        when(participationRepository.existsByPersonneAndProjet(personne, projet)).thenReturn(false);
        when(participationRepository.sumPourcentageByPersonneAndProjetDate(personne, LocalDate.now())).thenReturn(70f);
        when(participationRepository.countProjetsEnCoursByPersonne(personne, LocalDate.now())).thenReturn(2L);

        Participation expectedParticipation = new Participation();
        expectedParticipation.setPersonne(personne);
        expectedParticipation.setProjet(projet);
        expectedParticipation.setRole("Analyste");
        expectedParticipation.setPourcentage(30f);

        when(participationRepository.save(any(Participation.class))).thenReturn(expectedParticipation);

        // Act
        participationService.ajouterParticipation(matricule, codeProjet, "Analyste", 30f);

        // Assert
        verify(participationRepository, times(1)).save(any(Participation.class));
    }
}
