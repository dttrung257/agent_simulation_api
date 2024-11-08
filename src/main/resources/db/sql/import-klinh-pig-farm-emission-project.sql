INSERT INTO projects
(id, user_id, name, location, created_at, updated_at, created_by, updated_by)
VALUES
    (12, 1, 'pig farm emission', '/pig-farm-emission', '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn');

INSERT INTO models
(id, name, project_id, user_id, created_at, updated_at, created_by, updated_by)
VALUES
    (221, 'simulator-01-fixed-diet', 12, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (222, 'disease-pig', 12, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (223, 'factor', 12, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (224, 'farm', 12, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (225, 'food-disease-config', 12, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (226, 'food-disease-factor', 12, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (227, 'food-disease-pig', 12, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (228, 'food-water-disease-pig', 12, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (229, 'multi-disease-pig', 12, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (230, 'pig', 12, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (231, 'transmit-disease-config', 12, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (232, 'transmit-disease-factor', 12, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (233, 'transmit-disease-pig', 12, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (234, 'simulator-01', 12, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (235, 'simulator-02', 12, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (236, 'simulator-03', 12, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (237, 'simulator-04', 12, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (238, 'simulator-05', 12, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (239, 'simulator-06', 12, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (240, 'simulator-07', 12, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (241, 'trough', 12, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (242, 'water-disease-config', 12, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (243, 'water-disease-factor', 12, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (244, 'config', 12, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (245, 'pig-fixed-diet', 12, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (246, 'gas-concentration', 12, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn');

INSERT INTO experiments
(id, name, model_id, project_id, user_id, created_at, updated_at, created_by, updated_by)
VALUES
    (122, 'NormalFixedDiet', 221, 12, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (123, 'Normal', 234, 12, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (124, 'CC', 235, 12, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (125, 'DC', 236, 12, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (126, 'CD', 237, 12, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (127, 'DD', 238, 12, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (128, 'Transmit', 239, 12, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (129, 'Multi', 240, 12, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn');
