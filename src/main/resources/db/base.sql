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
   -- age_min INT,
   -- age_max INT NOT NULL,
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
CREATE TABLE tarif_typesiege_seance(
   id SERIAL PRIMARY KEY,
   idtarif_typesiege INT NOT NULL,
   idseance INT NOT NULL,
   prix DECIMAL(8, 2),
   FOREIGN KEY (idtarif_typesiege) REFERENCES tarif_typesiege(id) ON DELETE CASCADE,
   FOREIGN KEY (idseance) REFERENCES seance(id) ON DELETE CASCADE,
   UNIQUE(idtarif_typesiege, idseance)   
);
CREATE TABLE categorie_age(
   id SERIAL PRIMARY KEY,
   categ VARCHAR(50) NOT NULL,
   age_min INT,
   age_max INT NOT NULL
);
CREATE TABLE tarif_typesiege_categorie(
   id SERIAL PRIMARY KEY,
   idtarif_typesiege INT NOT NULL,
   idcategorie_age INT NOT NULL,
   prix DECIMAL(8, 2),
   FOREIGN KEY (idtarif_typesiege) REFERENCES tarif_typesiege(id) ON DELETE CASCADE,
   FOREIGN KEY (idcategorie_age) REFERENCES categorie_age(id) ON DELETE CASCADE,
   UNIQUE(idtarif_typesiege, idcategorie_age)   
);
CREATE TABLE billet(
   id SERIAL PRIMARY KEY,
   idSeance INT NOT NULL,
   idSiege INT NOT NULL,
   idClient INT NOT NULL,
   idcategorie_age INT,
   prix DECIMAL(8, 2) NOT NULL,
   statut VARCHAR(20) NOT NULL DEFAULT 'RESERVE',
   FOREIGN KEY (idSeance) REFERENCES seance(id) ON DELETE CASCADE,
   FOREIGN KEY (idSiege) REFERENCES siege(id) ON DELETE RESTRICT,
   FOREIGN KEY (idClient) REFERENCES client(id) ON DELETE CASCADE,
   FOREIGN KEY (idcategorie_age) REFERENCES categorie_age(id) ON DELETE RESTRICT
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
CREATE TABLE societe(
   id SERIAL PRIMARY KEY,
   nom VARCHAR(50) NOT NULL,
   email VARCHAR(50) NOT NULL,
   telephone VARCHAR(50) NOT NULL
);
CREATE TABLE publicite(
   id SERIAL PRIMARY KEY,
   titre VARCHAR(100) NOT NULL,
   duree INT NOT NULL,
   idSociete INT NOT NULL,
   FOREIGN KEY (idSociete) REFERENCES societe(id) ON DELETE CASCADE
);
CREATE TABLE diffusion_publicite(
   id SERIAL PRIMARY KEY,
   idPublicite INT NOT NULL,
   idSeance INT NOT NULL,
   prix DECIMAL(8, 2) NOT NULL,
   FOREIGN KEY (idPublicite) REFERENCES publicite(id) ON DELETE CASCADE,
   FOREIGN KEY (idSeance) REFERENCES seance(id) ON DELETE CASCADE,
   UNIQUE(idPublicite, idSeance)
);