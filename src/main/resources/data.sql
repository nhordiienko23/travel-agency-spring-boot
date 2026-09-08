-- Очищаем таблицы перед вставкой, чтобы не было конфликтов при перезапуске
DELETE FROM vouchers;
DELETE FROM users;

-- ===================== USERS =====================
-- 1. Админ
INSERT INTO users (id, username, email, password, last_name, phone_number, balance, active, role)
VALUES ('a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d', 'admin', 'admin@travel.com', '$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6', 'Administrator', '+38011222333', 10000.0, TRUE, 'ADMIN');

-- 2. Пользователь 1
INSERT INTO users (id, username, email, password, last_name, phone_number, balance, active, role)
VALUES ('b2c3d4e5-f6a7-8b9c-0d1e-2f3a4b5c6d7e', 'user', 'user@travel.com', '$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6', 'Client', '+380999888777', 5000.0, TRUE, 'USER');

-- 3. Менеджер
INSERT INTO users (id, username, email, password, last_name, phone_number, balance, active, role)
VALUES ('f1e2d3c4-b5a6-7988-9900-112233445566', 'manager', 'manager@travel.com', '$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6', 'Manager', '+38077888999', 3000.0, TRUE, 'MANAGER');

-- 4.  юзеры для демонстрации пагинации (Страница 1-3)

INSERT INTO users (id, username, email, password, last_name, phone_number, balance, active, role)
VALUES ('c1d2e3f4-a5b6-c7d8-e9f0-1a2b3c4d5e6f', 'charlie', 'charlie@travel.com', '$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6', 'Brown', '+38011223344', 150.0, TRUE, 'USER');

INSERT INTO users (id, username, email, password, last_name, phone_number, balance, active, role)
VALUES ('d2e3f4a5-b6c7-d8e9-f01a-2b3c4d5e6f7a', 'david', 'david@travel.com', '$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6', 'Williams', '+38055667788', 900.0, TRUE, 'USER');

INSERT INTO users (id, username, email, password, last_name, phone_number, balance, active, role)
VALUES ('e3f4a5b6-c7d8-e9f0-1a2b-3c4d5e6f7a8b', 'emma', 'emma@travel.com', '$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6', 'Jones', '+38099887766', 3200.0, TRUE, 'USER');

INSERT INTO users (id, username, email, password, last_name, phone_number, balance, active, role)
VALUES ('f4a5b6c7-d8e9-f01a-2b3c-4d5e6f7a8b9c', 'frank', 'frank@travel.com', '$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6', 'Garcia', '+38011224455', 0.0, FALSE, 'USER');

INSERT INTO users (id, username, email, password, last_name, phone_number, balance, active, role)
VALUES ('a5b6c7d8-e9f0-1a2b-3c4d-5e6f7a8b9c0d', 'grace', 'grace@travel.com', '$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6', 'Miller', '+38033445566', 1250.0, TRUE, 'USER');

INSERT INTO users (id, username, email, password, last_name, phone_number, balance, active, role)
VALUES ('b6c7d8e9-f01a-2b3c-4d5e-6f7a8b9c0d1e', 'henry', 'henry@travel.com', '$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6', 'Davis', '+38077889900', 800.0, TRUE, 'USER');

INSERT INTO users (id, username, email, password, last_name, phone_number, balance, active, role)
VALUES ('c7d8e9f0-1a2b-3c4d-5e6f-7a8b9c0d1e2f', 'isabella', 'isabella@travel.com', '$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6', 'Rodriguez', '+38099001122', 450.0, TRUE, 'USER');

INSERT INTO users (id, username, email, password, last_name, phone_number, balance, active, role)
VALUES ('d8e9f01a-2b3c-4d5e-6f7a-8b9c0d1e2f3a', 'jack', 'jack@travel.com', '$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6', 'Martinez', '+38012312312', 0.0, FALSE, 'USER');

INSERT INTO users (id, username, email, password, last_name, phone_number, balance, active, role)
VALUES ('e9f01a2b-3c4d-5e6f-7a8b-9c0d1e2f3a4b', 'karen', 'karen@travel.com', '$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6', 'Hernandez', '+38032132132', 7600.0, TRUE, 'USER');
-- ===================== VOUCHERS =====================

-- 1. HEALTH (Здоровье)
INSERT INTO vouchers (id, title, description, price, tour_type, transfer_type, hotel_type, status, arrival_date, eviction_date, user_id, is_hot)
VALUES ('11111111-1111-1111-1111-111111111111', 'Spa Resort Relax', 'Medical spa and mineral springs', 1100.0, 'HEALTH', 'BUS', 'THREE_STARS', 'REGISTERED', '2026-10-01', '2026-10-08', NULL, FALSE);
INSERT INTO vouchers (id, title, description, price, tour_type, transfer_type, hotel_type, status, arrival_date, eviction_date, user_id, is_hot)
VALUES ('11111111-1111-1111-1111-111111111112', 'Alpine Sanatorium', 'Clean mountain air and recovery', 1900.0, 'HEALTH', 'TRAIN', 'FOUR_STARS', 'REGISTERED', '2026-10-10', '2026-10-20', NULL, TRUE);
INSERT INTO vouchers (id, title, description, price, tour_type, transfer_type, hotel_type, status, arrival_date, eviction_date, user_id, is_hot)
VALUES ('11111111-1111-1111-1111-111111111113', 'Thermal Springs Deluxe', 'Luxury health improvement package', 3200.0, 'HEALTH', 'PLANE', 'FIVE_STARS', 'REGISTERED', '2026-11-01', '2026-11-10', NULL, FALSE);

-- 2. SPORTS (Спорт)
INSERT INTO vouchers (id, title, description, price, tour_type, transfer_type, hotel_type, status, arrival_date, eviction_date, user_id, is_hot)
VALUES ('22222222-2222-2222-2222-222222222221', 'Alps Skiing', 'Winter sports adventure', 1200.0, 'SPORTS', 'BUS', 'FOUR_STARS', 'REGISTERED', '2027-01-10', '2027-01-20', NULL, FALSE);
INSERT INTO vouchers (id, title, description, price, tour_type, transfer_type, hotel_type, status, arrival_date, eviction_date, user_id, is_hot)
VALUES ('22222222-2222-2222-2222-222222222222', 'Cycling Camp', 'Professional bicycle training', 900.0, 'SPORTS', 'MINIBUS', 'TWO_STARS', 'REGISTERED', '2027-05-01', '2027-05-10', NULL, TRUE);
INSERT INTO vouchers (id, title, description, price, tour_type, transfer_type, hotel_type, status, arrival_date, eviction_date, user_id, is_hot)
VALUES ('22222222-2222-2222-2222-222222222223', 'Surfing Camp Portugal', 'Ocean waves and professional trainers', 1500.0, 'SPORTS', 'PRIVATE_CAR', 'THREE_STARS', 'REGISTERED', '2027-06-01', '2027-06-10', NULL, FALSE);

-- 3. LEISURE (Отдых)
INSERT INTO vouchers (id, title, description, price, tour_type, transfer_type, hotel_type, status, arrival_date, eviction_date, user_id, is_hot)
VALUES ('33333333-3333-3333-3333-333333333331', 'Maldives Paradise', '7 days in a luxury resort', 2500.0, 'LEISURE', 'PLANE', 'FIVE_STARS', 'REGISTERED', '2026-12-01', '2026-12-08', NULL, TRUE);
INSERT INTO vouchers (id, title, description, price, tour_type, transfer_type, hotel_type, status, arrival_date, eviction_date, user_id, is_hot)
VALUES ('33333333-3333-3333-3333-333333333332', 'Paris Tour', 'Fashion and sightseeing', 1500.0, 'LEISURE', 'TRAIN', 'FOUR_STARS', 'REGISTERED', '2026-09-10', '2026-09-15', NULL, TRUE);
INSERT INTO vouchers (id, title, description, price, tour_type, transfer_type, hotel_type, status, arrival_date, eviction_date, user_id, is_hot)
VALUES ('33333333-3333-3333-3333-333333333333', 'London Weekend', 'Quick getaway', 600.0, 'LEISURE', 'SHIP', 'THREE_STARS', 'REGISTERED', '2026-12-15', '2026-12-18', NULL, FALSE);

-- 4. SAFARI (Сафари)
INSERT INTO vouchers (id, title, description, price, tour_type, transfer_type, hotel_type, status, arrival_date, eviction_date, user_id, is_hot)
VALUES ('44444444-4444-4444-4444-444444444441', 'Safari Kenya', 'Wild animals spotting', 4000.0, 'SAFARI', 'PLANE', 'FOUR_STARS', 'REGISTERED', '2026-11-01', '2026-11-14', NULL, TRUE);
INSERT INTO vouchers (id, title, description, price, tour_type, transfer_type, hotel_type, status, arrival_date, eviction_date, user_id, is_hot)
VALUES ('44444444-4444-4444-4444-444444444442', 'Tanzania Jeep Expedition', 'Extreme wildlife tour', 3500.0, 'SAFARI', 'JEEPS', 'THREE_STARS', 'REGISTERED', '2027-02-10', '2027-02-20', NULL, FALSE);
INSERT INTO vouchers (id, title, description, price, tour_type, transfer_type, hotel_type, status, arrival_date, eviction_date, user_id, is_hot)
VALUES ('44444444-4444-4444-4444-444444444443', 'South Africa Park Tour', 'Lions and elephants safari', 4500.0, 'SAFARI', 'MINIBUS', 'FIVE_STARS', 'REGISTERED', '2027-03-01', '2027-03-12', NULL, FALSE);

-- 5. WINE (Виноделие)
INSERT INTO vouchers (id, title, description, price, tour_type, transfer_type, hotel_type, status, arrival_date, eviction_date, user_id, is_hot)
VALUES ('55555555-5555-5555-5555-555555555551', 'Wine Tasting Italy', 'Tuscany vineyards', 1800.0, 'WINE', 'BUS', 'THREE_STARS', 'REGISTERED', '2027-04-05', '2027-04-12', NULL, FALSE);
INSERT INTO vouchers (id, title, description, price, tour_type, transfer_type, hotel_type, status, arrival_date, eviction_date, user_id, is_hot)
VALUES ('55555555-5555-5555-5555-555555555552', 'French Cellars Tour', 'Bordeaux and Champagne tasting', 2300.0, 'WINE', 'TRAIN', 'FOUR_STARS', 'REGISTERED', '2027-05-15', '2027-05-22', NULL, TRUE);
INSERT INTO vouchers (id, title, description, price, tour_type, transfer_type, hotel_type, status, arrival_date, eviction_date, user_id, is_hot)
VALUES ('55555555-5555-5555-5555-555555555553', 'Georgian Wine Heritage', 'Kakheti valleys and qvevri wine', 1300.0, 'WINE', 'PRIVATE_CAR', 'TWO_STARS', 'REGISTERED', '2027-06-10', '2027-06-17', NULL, FALSE);

-- 6. ECO (Эко-туризм)
INSERT INTO vouchers (id, title, description, price, tour_type, transfer_type, hotel_type, status, arrival_date, eviction_date, user_id, is_hot)
VALUES ('66666666-6666-6666-6666-666666666661', 'Carpathian Eco Trail', 'Clean nature and wooden houses', 700.0, 'ECO', 'ELECTRICAL_CARS', 'TWO_STARS', 'REGISTERED', '2027-07-01', '2027-07-07', NULL, FALSE);
INSERT INTO vouchers (id, title, description, price, tour_type, transfer_type, hotel_type, status, arrival_date, eviction_date, user_id, is_hot)
VALUES ('66666666-6666-6666-6666-666666666662', 'Amazon Jungle Lodge', 'Deep rainforest immersion', 2800.0, 'ECO', 'SHIP', 'ONE_STAR', 'REGISTERED', '2027-08-01', '2027-08-10', NULL, TRUE);
INSERT INTO vouchers (id, title, description, price, tour_type, transfer_type, hotel_type, status, arrival_date, eviction_date, user_id, is_hot)
VALUES ('66666666-6666-6666-6666-666666666663', 'Norwegian Fjords Bio', 'Green energy and pristine fjords', 2200.0, 'ECO', 'ELECTRICAL_CARS', 'THREE_STARS', 'REGISTERED', '2027-06-20', '2027-06-28', NULL, FALSE);

-- 7. ADVENTURE (Приключения)
INSERT INTO vouchers (id, title, description, price, tour_type, transfer_type, hotel_type, status, arrival_date, eviction_date, user_id, is_hot)
VALUES ('77777777-7777-7777-7777-777777777771', 'Everest Base Camp', 'Hiking adventure', 2200.0, 'ADVENTURE', 'PLANE', 'TWO_STARS', 'REGISTERED', '2027-05-10', '2027-05-25', NULL, TRUE);
INSERT INTO vouchers (id, title, description, price, tour_type, transfer_type, hotel_type, status, arrival_date, eviction_date, user_id, is_hot)
VALUES ('77777777-7777-7777-7777-777777777772', 'Iceland Volcano Trek', 'Geysers and lava fields', 2600.0, 'ADVENTURE', 'JEEPS', 'THREE_STARS', 'REGISTERED', '2027-07-10', '2027-07-18', NULL, FALSE);
INSERT INTO vouchers (id, title, description, price, tour_type, transfer_type, hotel_type, status, arrival_date, eviction_date, user_id, is_hot)
VALUES ('77777777-7777-7777-7777-777777777773', 'Grand Canyon Rafting', 'Extreme river expedition', 1900.0, 'ADVENTURE', 'MINIBUS', 'ONE_STAR', 'REGISTERED', '2027-08-15', '2027-08-22', NULL, FALSE);

-- 8. CULTURAL (Культурные)
INSERT INTO vouchers (id, title, description, price, tour_type, transfer_type, hotel_type, status, arrival_date, eviction_date, user_id, is_hot)
VALUES ('88888888-8888-8888-8888-888888888881', 'Rome Getaway', 'Explore ancient history', 800.0, 'CULTURAL', 'PLANE', 'THREE_STARS', 'REGISTERED', '2026-09-15', '2026-09-20', NULL, FALSE);
INSERT INTO vouchers (id, title, description, price, tour_type, transfer_type, hotel_type, status, arrival_date, eviction_date, user_id, is_hot)
VALUES ('88888888-8888-8888-8888-888888888882', 'Kyoto Temples Tour', 'Ancient Japanese traditions', 3100.0, 'CULTURAL', 'TRAIN', 'FOUR_STARS', 'REGISTERED', '2026-09-01', '2026-09-10', NULL, FALSE);
INSERT INTO vouchers (id, title, description, price, tour_type, transfer_type, hotel_type, status, arrival_date, eviction_date, user_id, is_hot)
VALUES ('88888888-8888-8888-8888-888888888883', 'Egyptian Pyramids Mystery', 'Pharaohs history and museums', 1400.0, 'CULTURAL', 'PLANE', 'FIVE_STARS', 'REGISTERED', '2027-02-01', '2027-02-08', NULL, TRUE);

-- Туры, уже КУПЛЕННЫЕ пользователем "user" (чтобы отображались в профиле и разделе "Мои туры")
INSERT INTO vouchers (id, title, description, price, tour_type, transfer_type, hotel_type, status, arrival_date, eviction_date, user_id, is_hot)
VALUES ('99999999-9999-9999-9999-999999999991', 'Kyoto Culinary Tour', 'Best food in Japan', 3500.0, 'CULTURAL', 'PLANE', 'FOUR_STARS', 'PAID', '2026-08-20', '2026-08-30', 'b2c3d4e5-f6a7-8b9c-0d1e-2f3a4b5c6d7e', FALSE);

INSERT INTO vouchers ( id, title, description, price, tour_type, transfer_type, hotel_type, status, arrival_date, eviction_date, user_id, is_hot )
VALUES ( '99999999-9999-9999-9999-999999999992', 'Swiss Alps Snow', 'Snowboarding weekend', 1500.0, 'SPORTS', 'BUS', 'THREE_STARS', 'PAID', '2026-12-25', '2027-01-05', 'b2c3d4e5-f6a7-8b9c-0d1e-2f3a4b5c6d7e', FALSE );