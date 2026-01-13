-- Création des enregistrements patients pour les profils de démonstration
-- Cette migration crée les entrées dans la table patients pour les profils créés dans V5

-- Insérer les données médicales pour les patients de démonstration
INSERT INTO patients (user_id, date_of_birth, gender, blood_type, allergies, chronic_conditions, emergency_contact_name, emergency_contact_phone, emergency_contact_relationship)
SELECT
    p.id,
    CASE
        WHEN p.email = 'aicha.diabate@medipatient.com' THEN '1990-05-15'::date
        WHEN p.email = 'ibrahim.kone@medipatient.com' THEN '1985-08-22'::date
        WHEN p.email = 'mariam.bamba@medipatient.com' THEN '1992-12-03'::date
        WHEN p.email = 'fatou.sanogo@medipatient.com' THEN '1988-03-10'::date
        WHEN p.email = 'moussa.coulibaly@medipatient.com' THEN '1995-07-28'::date
    END,
    CASE
        WHEN p.email = 'aicha.diabate@medipatient.com' THEN 'female'
        WHEN p.email = 'ibrahim.kone@medipatient.com' THEN 'male'
        WHEN p.email = 'mariam.bamba@medipatient.com' THEN 'female'
        WHEN p.email = 'fatou.sanogo@medipatient.com' THEN 'female'
        WHEN p.email = 'moussa.coulibaly@medipatient.com' THEN 'male'
    END,
    CASE
        WHEN p.email = 'aicha.diabate@medipatient.com' THEN 'O+'
        WHEN p.email = 'ibrahim.kone@medipatient.com' THEN 'A+'
        WHEN p.email = 'mariam.bamba@medipatient.com' THEN 'B+'
        WHEN p.email = 'fatou.sanogo@medipatient.com' THEN 'AB+'
        WHEN p.email = 'moussa.coulibaly@medipatient.com' THEN 'O-'
    END,
    CASE
        WHEN p.email = 'aicha.diabate@medipatient.com' THEN ARRAY['Pénicilline', 'Arachides']
        WHEN p.email = 'ibrahim.kone@medipatient.com' THEN ARRAY['Aspirine']
        WHEN p.email = 'mariam.bamba@medipatient.com' THEN ARRAY[]::text[]
        WHEN p.email = 'fatou.sanogo@medipatient.com' THEN ARRAY['Lactose']
        WHEN p.email = 'moussa.coulibaly@medipatient.com' THEN ARRAY['Sulfamides']
    END,
    CASE
        WHEN p.email = 'aicha.diabate@medipatient.com' THEN ARRAY['Asthme']
        WHEN p.email = 'ibrahim.kone@medipatient.com' THEN ARRAY['Hypertension']
        WHEN p.email = 'mariam.bamba@medipatient.com' THEN ARRAY[]::text[]
        WHEN p.email = 'fatou.sanogo@medipatient.com' THEN ARRAY['Diabète de type 2']
        WHEN p.email = 'moussa.coulibaly@medipatient.com' THEN ARRAY[]::text[]
    END,
    CASE
        WHEN p.email = 'aicha.diabate@medipatient.com' THEN 'Kofi Diabaté'
        WHEN p.email = 'ibrahim.kone@medipatient.com' THEN 'Aminata Koné'
        WHEN p.email = 'mariam.bamba@medipatient.com' THEN 'Abdoulaye Bamba'
        WHEN p.email = 'fatou.sanogo@medipatient.com' THEN 'Mamadou Sanogo'
        WHEN p.email = 'moussa.coulibaly@medipatient.com' THEN 'Adama Coulibaly'
    END,
    CASE
        WHEN p.email = 'aicha.diabate@medipatient.com' THEN '+225 07 12 34 56 79'
        WHEN p.email = 'ibrahim.kone@medipatient.com' THEN '+225 01 23 45 67 90'
        WHEN p.email = 'mariam.bamba@medipatient.com' THEN '+225 05 87 65 43 22'
        WHEN p.email = 'fatou.sanogo@medipatient.com' THEN '+225 07 55 66 77 89'
        WHEN p.email = 'moussa.coulibaly@medipatient.com' THEN '+225 01 99 88 77 67'
    END,
    CASE
        WHEN p.email = 'aicha.diabate@medipatient.com' THEN 'Frère'
        WHEN p.email = 'ibrahim.kone@medipatient.com' THEN 'Épouse'
        WHEN p.email = 'mariam.bamba@medipatient.com' THEN 'Père'
        WHEN p.email = 'fatou.sanogo@medipatient.com' THEN 'Mari'
        WHEN p.email = 'moussa.coulibaly@medipatient.com' THEN 'Mère'
    END
FROM profiles p
WHERE p.role = 'PATIENT'
AND p.email IN (
    'aicha.diabate@medipatient.com',
    'ibrahim.kone@medipatient.com',
    'mariam.bamba@medipatient.com',
    'fatou.sanogo@medipatient.com',
    'moussa.coulibaly@medipatient.com'
)
AND NOT EXISTS (
    SELECT 1 FROM patients pt WHERE pt.user_id = p.id
);

COMMENT ON TABLE patients IS 'Table des informations médicales des patients - Enregistrements démo ajoutés';