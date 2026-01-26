-- Seed initial users
-- All passwords are BCrypt hashed with cost factor 10
-- For development, all users have password = "admin"

-- admin / admin (id=1)
INSERT INTO users (id, username, password, name) VALUES (
    1, 'admin',
    '$2a$10$k2hobHKh5EmRBgUeEwlSYu4K58P9zpw2Ysahedi0eoHaCi8Lpy4bC',
    'Administrator'
);

-- sales / admin (id=2)
INSERT INTO users (id, username, password, name) VALUES (
    2, 'sales',
    '$2a$10$k2hobHKh5EmRBgUeEwlSYu4K58P9zpw2Ysahedi0eoHaCi8Lpy4bC',
    'Sales User'
);

-- stock / admin (id=3)
INSERT INTO users (id, username, password, name) VALUES (
    3, 'stock',
    '$2a$10$k2hobHKh5EmRBgUeEwlSYu4K58P9zpw2Ysahedi0eoHaCi8Lpy4bC',
    'Inventory User'
);

-- viewer / admin (id=4)
INSERT INTO users (id, username, password, name) VALUES (
    4, 'viewer',
    '$2a$10$k2hobHKh5EmRBgUeEwlSYu4K58P9zpw2Ysahedi0eoHaCi8Lpy4bC',
    'Viewer'
);

-- Reset auto-increment to continue after seed data
ALTER TABLE users ALTER COLUMN id RESTART WITH 5;
