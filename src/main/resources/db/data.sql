INSERT INTO typesalle (libelle)
VALUES ('Standard');

INSERT INTO typesiege (libelle)
VALUES
('Standard'),
('VIP'),
('premium');

INSERT INTO cinema (nom, adresse, ville, telephone, email)
VALUES ('Cinéma Mada', 'Analakely', 'Antananarivo', '0340000000', 'contact@cinemamada.mg');

INSERT INTO salle (num, idcinema, capacite, idtypesalle, acces_pmr)
VALUES (1, 1, 24, 1, true);

-- Rangée A (Standard)
INSERT INTO siege (idSalle, rangee, numero, idTypeSiege)
SELECT 1, 'A', generate_series(1,6), 1;

-- Rangée B (Standard)
INSERT INTO siege (idSalle, rangee, numero, idTypeSiege)
SELECT 1, 'B', generate_series(1,6), 1;

-- Rangée C (VIP)
INSERT INTO siege (idSalle, rangee, numero, idTypeSiege)
SELECT 1, 'C', generate_series(1,6), 2;

-- Rangée D (PMR)
INSERT INTO siege (idSalle, rangee, numero, idTypeSiege)
SELECT 1, 'D', generate_series(1,6), 3;

INSERT INTO categorie_age (categ, age_min, age_max)
VALUES
('Enfant', 0, 11),
('Adolescent', 12, 17),
('Adulte', 18, 99);

INSERT INTO tarif (nom, prix, idTypeSalle)
VALUES ('Tarif adulte', 0, 1);

INSERT INTO tarif_typesiege (idtarif, idtypesiege, prix)
VALUES
(1, 1, 30000), -- Standard
(1, 2, 50000), -- VIP
(1, 3, 40000); -- PMR

INSERT INTO tarif (nom, prix, idTypeSalle)
VALUES ('Tarif ado', 0, 1);

INSERT INTO tarif_typesiege (idtarif, idtypesiege, prix)
VALUES
(2, 1, 20000),
(2, 2, 45000),
(2, 3, 30000);

INSERT INTO tarif (nom, prix, idTypeSalle)
VALUES ('Tarif enfant', 0, 1);

INSERT INTO tarif_typesiege (idtarif, idtypesiege, prix)
VALUES
(3, 1, 15000),
(3, 2, 25000),
(3, 3, 20000);

INSERT INTO genre (libelle) VALUES ('Romance');
INSERT INTO classification (libelle) VALUES ('Tous publics');

INSERT INTO film (titre, duree, idGenre, langue, synopsis, idClassification, date_de_sortie)
VALUES (
  'Titanic',
  195,
  1,
  'Anglais',
  'Une histoire d amour tragique à bord du Titanic.',
  1,
  '1997-01-01'
);

INSERT INTO seance (idFilm, idSalle, date_heure_debut, version)
VALUES
(1, 1, '2026-01-20 10:00:00', 'VO'),
(1, 1, '2026-01-21 10:00:00', 'VO'),
(1, 1, '2026-01-21 15:00:00', 'VO');

INSERT INTO client (nom, prenom, telephone)
VALUES
('Rakoto', 'Jean', '0341111111'),
('Rabe', 'Marie', '0342222222'),
('Andry', 'Paul', '0343333333');

-- Adulte, siège VIP
INSERT INTO billet (idSeance, idSiege, idClient, idcategorie_age, prix, statut)
VALUES (1, 13, 1, 3, 50000, 'PAYE');

-- Ado, siège Standard
INSERT INTO billet (idSeance, idSiege, idClient, idcategorie_age, prix, statut)
VALUES (1, 2, 2, 2, 20000, 'PAYE');

-- Enfant, siège PMR
INSERT INTO billet (idSeance, idSiege, idClient, idcategorie_age, prix, statut)
VALUES (1, 19, 3, 1, 20000, 'PAYE');

INSERT INTO societe (nom, email, telephone)
VALUES
('Vanilla', 'contact@vanilla.mg', '0344444444'),
('Lewis', 'contact@lewis.mg', '0345555555'),
('Scobibs', 'contact@scobibs.mg', '0346666666');

INSERT INTO publicite (titre, duree, idSociete)
VALUES
('Pub Vaniala', 30, 1),
('Pub Lewis', 25, 2),
('Pub Socobis', 20, 3);

-- 20 janvier 2026 - 10h
INSERT INTO diffusion_publicite (idPublicite, idSeance, prix)
VALUES
(1, 1, 20000),
(2, 1, 20000);

-- 21 janvier 2026 - 10h
INSERT INTO diffusion_publicite (idPublicite, idSeance, prix)
VALUES
(1, 2, 20000),
(3, 2, 20000);

-- 21 janvier 2026 - 15h : aucune pub
INSERT INTO categorie (categ)
VALUES ('snack'), ('boison');

INSERT INTO produit (nom, idCategorie, prix, stock)
VALUES
('Pop corn', 1, 10000, 200),
('Tablette de chocolat', 1, 5000, 100),
('eau vive', 2, 5000, 50),
('Coca cola', 2, 8000, 80);

INSERT INTO modePaiement (libelle)
VALUES
('Espèces'),
('Carte bancaire'),
('Mobile Money');

INSERT INTO paiement (idClient, idModePaiement, montant, datePaiement)
VALUES
(1, 1, 50000, '2026-01-20'),  -- Espèces
(2, 2, 20000, '2026-01-20'),  -- Carte
(3, 3, 20000, '2026-01-20');  -- Mobile money

INSERT INTO paiement_billet (idpaiement, idbillet)
VALUES
(1, 1),
(2, 2),
(3, 3);

INSERT INTO vente_produit (idproduit, idClient, quantite, dateVente)
VALUES
(5, 1, 2, '2026-01-20'), -- 2 pop corn
(6, 2, 1, '2026-01-20'), -- 1 chocolat
(7, 3, 1, '2026-01-21'); -- 1 rano

INSERT INTO paiement (idClient, idModePaiement, montant, datePaiement)
VALUES
(1, 1, 20000, '2026-01-20'),
(2, 2, 5000, '2026-01-20'),
(3, 3, 5000, '2026-01-21');

INSERT INTO vente_produit_paiement (idvente_produit, idpaiement)
VALUES
(7, 4),
(8, 5),
(9, 6);