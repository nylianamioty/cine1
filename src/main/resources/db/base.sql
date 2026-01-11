CREATE TABLE cinema(
   id SERIAL PRIMARY KEY,
   nom VARCHAR(50) NOT NULL,
   adresse VARCHAR(50) NOT NULL,
   ville VARCHAR(50) NOT NULL,
   telephone VARCHAR(50) NOT NULL,
   email VARCHAR(50)
);

CREATE TABLE typesalle(
   id SERIAL PRIMARY KEY,
   libelle VARCHAR(50) NOT NULL
);

CREATE TABLE salle(
   id SERIAL PRIMARY KEY,
   num INT NOT NULL,
   idcinema INT NOT NULL,
   capacite INT,
   idtypesalle INT NOT NULL,
   acces_pmr BOOLEAN DEFAULT FALSE,
   FOREIGN KEY (idcinema) REFERENCES cinema(id) ON DELETE CASCADE,
   FOREIGN KEY (idtypesalle) REFERENCES typesalle(id) ON DELETE RESTRICT
);

CREATE TABLE typesiege(
   id SERIAL PRIMARY KEY,
   libelle VARCHAR(50) NOT NULL
);

CREATE TABLE siege(
   id SERIAL PRIMARY KEY,
   idSalle INT NOT NULL,
   rangee VARCHAR(50),
   numero INT NOT NULL,
   idTypeSiege INT NOT NULL,
   FOREIGN KEY (idSalle) REFERENCES salle(id) ON DELETE CASCADE,
   FOREIGN KEY (idTypeSiege) REFERENCES typesiege(id) ON DELETE RESTRICT
);

CREATE TABLE classification(
   id SERIAL PRIMARY KEY,
   libelle VARCHAR(50) NOT NULL
);

CREATE TABLE genre(
   id SERIAL PRIMARY KEY,
   libelle VARCHAR(50) NOT NULL
);

CREATE TABLE film(
   id SERIAL PRIMARY KEY,
   titre VARCHAR(50) NOT NULL,
   duree INT NOT NULL,
   idGenre INT NOT NULL,
   langue VARCHAR(50) NOT NULL,
   synopsis TEXT,
   idClassification INT NOT NULL,
   date_de_sortie DATE NOT NULL,
   FOREIGN KEY (idGenre) REFERENCES genre(id) ON DELETE RESTRICT,
   FOREIGN KEY (idClassification) REFERENCES classification(id) ON DELETE RESTRICT
);

CREATE TABLE seance(
   id SERIAL PRIMARY KEY,
   idFilm INT NOT NULL,
   idSalle INT NOT NULL,
   date_heure_debut TIMESTAMP NOT NULL,
   version VARCHAR(50),
   prix DECIMAL(8, 2) NOT NULL,
   FOREIGN KEY (idFilm) REFERENCES film(id) ON DELETE CASCADE,
   FOREIGN KEY (idSalle) REFERENCES salle(id) ON DELETE CASCADE
);

CREATE TABLE client(
   id SERIAL PRIMARY KEY,
   nom VARCHAR(50) NOT NULL,
   prenom VARCHAR(50) NOT NULL,
   email VARCHAR(50),
   telephone VARCHAR(50) NOT NULL,
   fidelitePoints INT DEFAULT 0
);

CREATE TABLE tarif(
   id SERIAL PRIMARY KEY,
   nom VARCHAR(50) NOT NULL,
   age_min INT,
   age_max INT NOT NULL,
   prix DECIMAL(8, 2) NOT NULL,
   idTypeSalle INT NOT NULL,
   actif BOOLEAN DEFAULT TRUE,
   FOREIGN KEY (idTypeSalle) REFERENCES typesalle(id) ON DELETE RESTRICT
);

CREATE TABLE tarif_typesiege(
   id SERIAL PRIMARY KEY,
   idtarif INT NOT NULL,
   idtypesiege INT NOT NULL,
   prix DECIMAL(8, 2),
   FOREIGN KEY (idtarif) REFERENCES tarif(id) ON DELETE CASCADE,
   FOREIGN KEY (idtypesiege) REFERENCES typesiege(id) ON DELETE CASCADE,
   UNIQUE(idtarif, idtypesiege)
);

CREATE TABLE billet(
   id SERIAL PRIMARY KEY,
   idSeance INT NOT NULL,
   idSiege INT NOT NULL,
   idClient INT NOT NULL,
   idtarif_typesiege INT NOT NULL,
   prix DECIMAL(8, 2) NOT NULL,
   statut BOOLEAN DEFAULT FALSE,
   FOREIGN KEY (idSeance) REFERENCES seance(id) ON DELETE CASCADE,
   FOREIGN KEY (idSiege) REFERENCES siege(id) ON DELETE RESTRICT,
   FOREIGN KEY (idClient) REFERENCES client(id) ON DELETE CASCADE,
   FOREIGN KEY (idtarif_typesiege) REFERENCES tarif_typesiege(id) ON DELETE RESTRICT
);

CREATE TABLE modePaiement(
   id SERIAL PRIMARY KEY,
   libelle VARCHAR(50) NOT NULL
);

CREATE TABLE paiement(
   id SERIAL PRIMARY KEY,
   idClient INT NOT NULL,
   idModePaiement INT NOT NULL,
   montant DECIMAL(8, 2) NOT NULL,
   datePaiement DATE NOT NULL,
   FOREIGN KEY (idClient) REFERENCES client(id) ON DELETE CASCADE,
   FOREIGN KEY (idModePaiement) REFERENCES modePaiement(id) ON DELETE RESTRICT
);

CREATE TABLE paiement_billet(
   id SERIAL PRIMARY KEY,
   idpaiement INT NOT NULL,
   idbillet INT NOT NULL,
   FOREIGN KEY (idpaiement) REFERENCES paiement(id) ON DELETE CASCADE,
   FOREIGN KEY (idbillet) REFERENCES billet(id) ON DELETE CASCADE,
   UNIQUE(idpaiement, idbillet)
);

CREATE TABLE categorie(
   id SERIAL PRIMARY KEY,
   categ VARCHAR(50) NOT NULL
);

CREATE TABLE produit(
   id SERIAL PRIMARY KEY,
   nom VARCHAR(50) NOT NULL,
   idCategorie INT NOT NULL,
   prix DECIMAL(8, 2) NOT NULL,
   stock INT NOT NULL,
   FOREIGN KEY (idCategorie) REFERENCES categorie(id) ON DELETE RESTRICT
);

CREATE TABLE vente_produit(
   id SERIAL PRIMARY KEY,
   idproduit INT NOT NULL,
   idClient INT NOT NULL,
   quantite INT NOT NULL,
   dateVente DATE NOT NULL,
   FOREIGN KEY (idproduit) REFERENCES produit(id) ON DELETE CASCADE,
   FOREIGN KEY (idClient) REFERENCES client(id) ON DELETE CASCADE
);

CREATE TABLE vente_produit_paiement(
   id SERIAL PRIMARY KEY,
   idvente_produit INT NOT NULL,
   idpaiement INT NOT NULL,
   FOREIGN KEY (idvente_produit) REFERENCES vente_produit(id) ON DELETE CASCADE,
   FOREIGN KEY (idpaiement) REFERENCES paiement(id) ON DELETE CASCADE,
   UNIQUE(idvente_produit, idpaiement)
);

-- Exemple de données pour tester l'achat d'un billet (Avatar 10 Jan 2026 10:00)
INSERT INTO typesalle(libelle) VALUES ('Standard');
INSERT INTO typesiege(libelle) VALUES ('Normal');
INSERT INTO tarif(nom, age_min, age_max, prix, idTypeSalle, actif) VALUES ('Normal', 0, 120, 8.50, 1, TRUE);
INSERT INTO tarif_typesiege(idtarif, idtypesiege, prix) VALUES (1, 1, 8.50);

INSERT INTO cinema(nom, adresse, ville, telephone, email) VALUES ('Cinema Central', '1 rue Exemple', 'Ville', '0123456789', 'contact@cinema.test');
INSERT INTO salle(num, idcinema, capacite, idtypesalle, acces_pmr) VALUES (1, 1, 100, 1, FALSE);

-- Crée 100 sièges pour la salle 1
DO $$
DECLARE i INT := 1;
BEGIN
   WHILE i <= 100 LOOP
      INSERT INTO siege(idSalle, rangee, numero, idTypeSiege) VALUES (1, 'A', i, 1);
      i := i + 1;
   END LOOP;
END $$;

INSERT INTO genre(libelle) VALUES ('Action');
INSERT INTO classification(libelle) VALUES ('PG-13');
INSERT INTO film(titre, duree, idGenre, langue, synopsis, idClassification, date_de_sortie) VALUES ('Avatar', 162, 1, 'VO', 'Film de science-fiction', 1, '2009-12-18');

-- Seance Avatar le 10 janvier 2026 à 10:00
INSERT INTO seance(idFilm, idSalle, date_heure_debut, version, prix) VALUES (1, 1, '2026-01-10 10:00:00', '2D', 8.50);

-- Client exemple
INSERT INTO client(nom, prenom, email, telephone) VALUES ('Dupont', 'Jean', 'jean.dupont@example.com', '0600000000');

-- Mode de paiement exemple
INSERT INTO modePaiement(libelle) VALUES ('Carte');