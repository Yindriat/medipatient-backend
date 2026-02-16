-- Suppression et recréation de l'utilisateur admin avec un hash BCrypt correct
-- Le mot de passe sera "admin123"

-- Supprimer l'ancien utilisateur admin
DELETE FROM profiles WHERE email = 'admin@medipatient.com';

-- Recréer l'utilisateur admin avec un hash BCrypt valide pour "admin123"
-- Hash généré avec: BCryptPasswordEncoder avec strength 10
INSERT INTO profiles (
    id,
    first_name,
    last_name,
    email,
    phone,
    password,
    role,
    enabled,
    created_at,
    updated_at,
    version
) VALUES (
    gen_random_uuid(),
    'Admin',
    'System',
    'admin@medipatient.com',
    '+33123456789',
    '$2a$10$CwTycUXWue0Thq9StjUM0uyhHS5KtVn5gOrxrGS.LkMYF.83Y.FoS', -- admin123
    'ADMIN',
    true,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    0
) ON CONFLICT (email) DO NOTHING;