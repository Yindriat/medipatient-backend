-- Création de 5 patients de démonstration
-- Mot de passe pour tous : patient123
-- Hash BCrypt généré pour "patient123"

INSERT INTO profiles (id, first_name, last_name, email, phone, password, role, enabled, created_at, updated_at, version)
VALUES 
    -- Patient 1: Aïcha Diabaté
    (gen_random_uuid(), 'Aïcha', 'Diabaté', 'aicha.diabate@medipatient.com', '+225 07 12 34 56 78',
     '$2a$10$aLd8PX80du1ICHEe1IEaleVqZWw29Q0Kj1opYuvngvVQcbP6K6h/u', 'PATIENT', true, 
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
    
    -- Patient 2: Ibrahim Koné
    (gen_random_uuid(), 'Ibrahim', 'Koné', 'ibrahim.kone@medipatient.com', '+225 01 23 45 67 89',
     '$2a$10$aLd8PX80du1ICHEe1IEaleVqZWw29Q0Kj1opYuvngvVQcbP6K6h/u', 'PATIENT', true, 
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
    
    -- Patient 3: Mariam Bamba
    (gen_random_uuid(), 'Mariam', 'Bamba', 'mariam.bamba@medipatient.com', '+225 05 87 65 43 21',
     '$2a$10$aLd8PX80du1ICHEe1IEaleVqZWw29Q0Kj1opYuvngvVQcbP6K6h/u', 'PATIENT', true, 
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
    
    -- Patient 4: Fatou Sanogo
    (gen_random_uuid(), 'Fatou', 'Sanogo', 'fatou.sanogo@medipatient.com', '+225 07 55 66 77 88',
     '$2a$10$aLd8PX80du1ICHEe1IEaleVqZWw29Q0Kj1opYuvngvVQcbP6K6h/u', 'PATIENT', true, 
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
    
    -- Patient 5: Moussa Coulibaly
    (gen_random_uuid(), 'Moussa', 'Coulibaly', 'moussa.coulibaly@medipatient.com', '+225 01 99 88 77 66',
     '$2a$10$aLd8PX80du1ICHEe1IEaleVqZWw29Q0Kj1opYuvngvVQcbP6K6h/u', 'PATIENT', true, 
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);

COMMENT ON TABLE profiles IS 'Table des profils utilisateurs - Patients démo ajoutés avec mot de passe: patient123';
