INSERT INTO roles (name, description) VALUES
    ('ROLE_ADMIN',   'Administrateur de la plateforme'),
    ('ROLE_AGENT',   'Agent traitant les réclamations'),
    ('ROLE_CITIZEN', 'Citoyen déposant des réclamations')
ON CONFLICT (name) DO UPDATE SET description = EXCLUDED.description;

INSERT INTO users (full_name, email, password, phone, enabled , demo_password_hint) VALUES
    ('Admin Principal', 'admin@platform.com',   '$2b$10$4S5V.ofEyQKKdoZsq8v4VOUQNy5sVkEXbJyxP65Dolz551Pfvp5za', '0600000001', TRUE,'Admin@123'),
    ('Agent Municipal', 'agent@platform.com',   '$2b$10$E4HT8PVCUmWV0uTYxgf0BOS11uy6bWp8t2y4wxf6b1WEwIEoVIGLG', '0600000002', TRUE,'Agent@123'),
    ('Citoyen Test',    'citizen@platform.com', '$2b$10$EydSJiO3CwtDVVHQrp4f7ODc/MHqCB7.SX16aotSRs69Oo.i2koG.', '0600000003', TRUE,'Citizen@123')
ON CONFLICT (email) DO UPDATE SET full_name = EXCLUDED.full_name;

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r WHERE u.email = 'admin@platform.com'   AND r.name = 'ROLE_ADMIN'
ON CONFLICT (user_id, role_id) DO NOTHING;
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r WHERE u.email = 'agent@platform.com'   AND r.name = 'ROLE_AGENT'
ON CONFLICT (user_id, role_id) DO NOTHING;
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r WHERE u.email = 'citizen@platform.com' AND r.name = 'ROLE_CITIZEN'
ON CONFLICT (user_id, role_id) DO NOTHING;

INSERT INTO categories (name, description) VALUES
    ('Voirie',           'Nids-de-poule, chaussées endommagées, signalisation'),
    ('Éclairage public', 'Lampadaires en panne ou défectueux'),
    ('Propreté',         'Collecte des déchets, dépôts sauvages'),
    ('Espaces verts',    'Parcs, jardins, arbres'),
    ('Sécurité',         'Incidents de sécurité sur la voie publique'),
    ('Autre',            'Toute autre demande de service')
ON CONFLICT (name) DO UPDATE SET description = EXCLUDED.description;

INSERT INTO complaints (title, description, location, status, citizen_id, assigned_agent_id, category_id)
SELECT 'Nid-de-poule dangereux Avenue Hassan II',
       'Un grand nid-de-poule s''est formé près du carrefour, risque pour les deux-roues.',
       'Avenue Hassan II, Casablanca', 'IN_PROGRESS',
       (SELECT id FROM users WHERE email = 'citizen@platform.com'),
       (SELECT id FROM users WHERE email = 'agent@platform.com'),
       (SELECT id FROM categories WHERE name = 'Voirie')
WHERE NOT EXISTS (SELECT 1 FROM complaints WHERE title = 'Nid-de-poule dangereux Avenue Hassan II');

INSERT INTO complaints (title, description, location, status, citizen_id, category_id)
SELECT 'Lampadaire éteint depuis une semaine',
       'Le lampadaire au coin de la rue ne fonctionne plus, la rue est très sombre la nuit.',
       'Rue des Fleurs, Casablanca', 'NEW',
       (SELECT id FROM users WHERE email = 'citizen@platform.com'),
       (SELECT id FROM categories WHERE name = 'Éclairage public')
WHERE NOT EXISTS (SELECT 1 FROM complaints WHERE title = 'Lampadaire éteint depuis une semaine');

INSERT INTO complaints (title, description, location, status, citizen_id, assigned_agent_id, category_id)
SELECT 'Dépôt sauvage d''ordures',
       'Des sacs poubelle s''accumulent depuis plusieurs jours près du marché.',
       'Marché central, Casablanca', 'RESOLVED',
       (SELECT id FROM users WHERE email = 'citizen@platform.com'),
       (SELECT id FROM users WHERE email = 'agent@platform.com'),
       (SELECT id FROM categories WHERE name = 'Propreté')
WHERE NOT EXISTS (SELECT 1 FROM complaints WHERE title = 'Dépôt sauvage d''ordures');

INSERT INTO complaints (title, description, location, status, citizen_id, assigned_agent_id, category_id)
SELECT 'Arbre menaçant de tomber',
       'Un arbre du parc penche dangereusement après les dernières pluies.',
       'Parc de la Ligue Arabe, Casablanca', 'WAITING',
       (SELECT id FROM users WHERE email = 'citizen@platform.com'),
       (SELECT id FROM users WHERE email = 'agent@platform.com'),
       (SELECT id FROM categories WHERE name = 'Espaces verts')
WHERE NOT EXISTS (SELECT 1 FROM complaints WHERE title = 'Arbre menaçant de tomber');

INSERT INTO comments (content, complaint_id, author_id)
SELECT 'Équipe technique envoyée sur place, intervention prévue sous 48h.',
       (SELECT id FROM complaints WHERE title = 'Nid-de-poule dangereux Avenue Hassan II'),
       (SELECT id FROM users WHERE email = 'agent@platform.com')
WHERE NOT EXISTS (SELECT 1 FROM comments WHERE content = 'Équipe technique envoyée sur place, intervention prévue sous 48h.');
