INSERT INTO Users (username, email, password, User_Role, enabled)
VALUES ('admin', 'admin@mail.de', '$2a$10$YliZcWB.fWtqtR0D9rqy4eevkDu8lOoECvq..r9eVEaA0ARZKfXDa', 'ADMIN', true);
INSERT INTO Poi (latitude, longitude, title, body, url, user_generated)
VALUES (48.18487979280497, 16.421012727226096, 'CodersBay', 'Die Coders.Bay Vienna ist wunderschön',
        'https://www.codersbay.wien', true);
INSERT INTO Poi (latitude, longitude, title, body, url, user_generated)
VALUES (48.0, 16.0, 'Ort der ganzen Zahlen', 'Kurze Koordinaten sind wunderschön.',
        'https://www.stadt.wien', true);
--
-- INSERT INTO Users (username, email, password, user_Role, enabled, status)
-- VALUES
--     ('max_muster', 'max.muster@example.com', 'hashedpassword123', 'USER', true, 'ACTIVE'),
--     ('admin_julia', 'julia.admin@example.com', 'securepass456', 'ADMIN', true, 'ACTIVE'),
--     ('dev_peter', 'peter.dev@example.com', 'devpass789', 'USER', true, 'ACTIVE');

