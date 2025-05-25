INSERT INTO users (id, email, password, username, role, created_at)
VALUES (
           gen_random_uuid(),
           'admin@gmail.com',
           '$2a$12$r8M6A9Z6s0NjPq/IY223heEQND5IXs8AOiEPNIMWgMLq9eWdkHNWi', -- 'admin'
           'Admin',
           'ADMIN',
           NOW()
       );

INSERT INTO manufacturers (id, name, description, logo_url)
VALUES (
           gen_random_uuid(),
           'Rolex',
           'Legendary Swiss watchmaker known for precision and luxury',
           'https://example.com/rolex-logo.png'
       );

INSERT INTO watches (id, name, description, manufacturer_id, price, stock_quantity, created_at)
VALUES
    (
        gen_random_uuid(),
        'Submariner',
        'Iconic luxury dive watch with exceptional craftsmanship',
        (SELECT id FROM manufacturers WHERE name = 'Rolex' LIMIT 1),
        9999.99,
        50,
        NOW()
    );
