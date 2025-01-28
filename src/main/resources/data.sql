-- Initialisation de la table Personne
INSERT INTO personne (matricule, nom, prenom, poste) VALUES
(1, 'Dupont', 'Jean', 'Développeur'),
(2, 'Durand', 'Marie', 'Chef de projet'),
(3, 'Martin', 'Paul', 'Analyste');

-- Initialisation de la table Projet
INSERT INTO projet (code, nom, debut, fin) VALUES
    (101, 'Projet Alpha', '2025-01-01', '2025-06-30'),
(102, 'Projet Beta', '2025-02-15', NULL),
(103, 'Projet Gamma', '2025-03-01', '2025-12-31');

-- Initialisation de la table Participation
INSERT INTO participation (id, role, pourcentage, personne_id, projet_id) VALUES
(1, 'Développeur', 50, 1, 101),
(2, 'Analyste', 30, 3, 101),
(3, 'Chef de projet', 100, 2, 102);