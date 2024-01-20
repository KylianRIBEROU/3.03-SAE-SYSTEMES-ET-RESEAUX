-- USE reseausocial if exists;


-- Création de la table pour l'entité Utilisateur
CREATE TABLE utilisateur (
    utilisateur_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    pseudonyme VARCHAR(100) NOT NULL UNIQUE,
    motdepasse VARCHAR(100) NOT NULL
);

-- Création de la table pour l'entité Publication
CREATE TABLE publication (
    publi_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    publi_contenu VARCHAR(255) NOT NULL,
    publi_dateheure TIMESTAMP NOT NULL,
    nb_likes INT DEFAULT 0,
    utilisateur_id BIGINT,
    FOREIGN KEY (utilisateur_id) REFERENCES utilisateur(utilisateur_id)
);

-- Création de la table pour la relation Many-to-Many (utilisateur_like_publication)
CREATE TABLE utilisateur_like_publication (
    utilisateur_id BIGINT,
    publication_id BIGINT,
    PRIMARY KEY (utilisateur_id, publication_id),
    FOREIGN KEY (utilisateur_id) REFERENCES utilisateur(utilisateur_id),
    FOREIGN KEY (publication_id) REFERENCES publication(publi_id)
);

-- Création de la table pour la relation Many-to-Many (suivre)
CREATE TABLE suivre (
    utilisateur_id BIGINT,
    abonne_id BIGINT,
    PRIMARY KEY (utilisateur_id, abonne_id),
    FOREIGN KEY (utilisateur_id) REFERENCES utilisateur(utilisateur_id),
    FOREIGN KEY (abonne_id) REFERENCES utilisateur(utilisateur_id)
);
