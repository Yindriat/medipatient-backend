-- Création d'un utilisateur admin par défaut
-- Mot de passe : admin123 (à changer en production !)

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
    '$2a$10$rKvLm9H4YYrROTOlNCpfG.PTHD3aqmYD5RdCjxb/W.Ct5NhKS1TtG', -- admin123
    'ADMIN',
    true,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    0
) ON CONFLICT (email) DO NOTHING;

-- Ajout d'un commentaire pour documentation
COMMENT ON TABLE profiles IS 'Table des profils utilisateurs avec admin par défaut (admin@medipatient.com / admin123)';