-- Script d'initialisation de la base de données MediPatient
-- Ce script s'exécute automatiquement au premier démarrage du container PostgreSQL

-- Création de la base de données (déjà fait par POSTGRES_DB)
-- Mais on s'assure qu'elle existe
SELECT 'CREATE DATABASE medipatient'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'medipatient')\gexec

-- Connexion à la base medipatient
\c medipatient;

-- Création de l'extension pour les UUID si nécessaire
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Ajout de commentaires sur la base
COMMENT ON DATABASE medipatient IS 'Base de données pour l''application MediPatient - Gestion des patients et consultations médicales';

-- Création d'un schéma pour les audit logs (optionnel)
CREATE SCHEMA IF NOT EXISTS audit;
COMMENT ON SCHEMA audit IS 'Schéma pour les logs d''audit et l''historique des modifications';

-- Message de confirmation
DO $$
BEGIN
    RAISE NOTICE 'Base de données MediPatient initialisée avec succès !';
    RAISE NOTICE 'Utilisateur: medipatient';
    RAISE NOTICE 'Base de données: medipatient';
    RAISE NOTICE 'Port: 5432';
END$$;