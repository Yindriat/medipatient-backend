-- Mise à jour du mot de passe admin avec le hash généré par l'application
-- Hash généré par Spring Security BCryptPasswordEncoder pour "admin123"

UPDATE profiles 
SET password = '$2a$10$aLd8PX80du1ICHEe1IEaleVqZWw29Q0Kj1opYuvngvVQcbP6K6h/u',
    updated_at = CURRENT_TIMESTAMP,
    version = version + 1
WHERE email = 'admin@medipatient.com';