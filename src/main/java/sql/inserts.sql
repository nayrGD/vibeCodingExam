INSERT INTO users (id, ref, first_name, last_name, email, phone) VALUES
    ('11111111-1111-1111-1111-111111111111', 'REF-001', 'Ryan', 'Tanjona', 'ryan@example.com', '+261340000001'),
    ('22222222-2222-2222-2222-222222222222', 'REF-002', 'Marie', 'Rakoto', 'marie@example.com', '+261340000002');

INSERT INTO cash_flow (id, created_at, amount, user_id, cash_flow_type) VALUES
    ('a1111111-aaaa-1111-aaaa-111111111111', now() - interval '10 days', 150.00, '11111111-1111-1111-1111-111111111111', 'DONATION'),
    ('a2222222-aaaa-2222-aaaa-222222222222', now() - interval '5 days',  75.50,  '22222222-2222-2222-2222-222222222222', 'DONATION'),
    ('b1111111-bbbb-1111-bbbb-111111111111', now() - interval '8 days',  40.00,  '11111111-1111-1111-1111-111111111111', 'EXPENSE'),
    ('b2222222-bbbb-2222-bbbb-222222222222', now() - interval '2 days',  120.00, '22222222-2222-2222-2222-222222222222', 'EXPENSE');

INSERT INTO donation (id, comment) VALUES
    ('a1111111-aaaa-1111-aaaa-111111111111', 'Don ponctuel de bienvenue'),
    ('a2222222-aaaa-2222-aaaa-222222222222', 'Soutien mensuel');

INSERT INTO expense (id, reason, frequency) VALUES
    ('b1111111-bbbb-1111-bbbb-111111111111', 'Hébergement serveur', 'MONTHLY'::expense_frequency),
    ('b2222222-bbbb-2222-bbbb-222222222222', 'Achat matériel',      'NONE'::expense_frequency);
