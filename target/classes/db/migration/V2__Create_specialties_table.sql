-- Create specialties table
CREATE TABLE specialties (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) UNIQUE NOT NULL,
    description TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0
);

-- Insert default specialties
INSERT INTO specialties (name, description) VALUES
('Médecine Générale', 'Médecine générale et consultations de première ligne'),
('Cardiologie', 'Spécialité médicale qui traite les maladies du cœur et des vaisseaux'),
('Dermatologie', 'Spécialité médicale qui traite les maladies de la peau'),
('Pédiatrie', 'Médecine spécialisée dans le soin des enfants'),
('Gynécologie', 'Spécialité médicale qui traite l''appareil génital féminin'),
('Orthopédie', 'Spécialité chirurgicale qui traite les affections de l''appareil locomoteur'),
('Ophtalmologie', 'Spécialité médicale qui traite les maladies de l''œil'),
('ORL', 'Spécialité médicale qui traite les maladies de l''oreille, du nez et de la gorge');