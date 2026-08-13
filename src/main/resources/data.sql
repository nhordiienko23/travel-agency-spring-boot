-- Очищаем таблицы перед вставкой, чтобы не было конфликтов при перезапуске (если ddl-auto=none)
DELETE FROM vouchers;
DELETE FROM users;

-- ===================== USERS =====================
-- 1. Админ
INSERT INTO users (id, username, email, password, last_name, phone_number, balance, active, role)
VALUES ('a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d', 'admin', 'admin@travel.com', '$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6', 'Administrator', '+48111222333', 10000.0, TRUE, 'ADMIN');

-- 2. Пользователь 1
INSERT INTO users (id, username, email, password, last_name, phone_number, balance, active, role)
VALUES ('b2c3d4e5-f6a7-8b9c-0d1e-2f3a4b5c6d7e', 'user', 'user@travel.com', '$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6', 'Client', '+48999888777', 5000.0, TRUE, 'USER');

-- 3. Менеджер
INSERT INTO users (id, username, email, password, last_name, phone_number, balance, active, role)
VALUES ('f1e2d3c4-b5a6-7988-9900-112233445566', 'manager', 'manager@travel.com', '$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6', 'Manager', '+48777888999', 3000.0, TRUE, 'MANAGER');

-- 4. Дополнительные пользователи (чтобы сработала пагинация > 5)
INSERT INTO users (id, username, email, password, last_name, phone_number, balance, active, role)
VALUES ('7a8b9c0d-1e2f-3a4b-5c6d-7e8f9a0b1c2d', 'alice', 'alice@travel.com', '$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6', 'Smith', '+48123456789', 6000.0, TRUE, 'USER');

INSERT INTO users (id, username, email, password, last_name, phone_number, balance, active, role)
VALUES ('8b9c0d1e-2f3a-4b5c-6d7e-8f9a0b1c2d3e', 'bob', 'bob@travel.com', '$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6', 'Johnson', '+48987654321', 4500.0, FALSE, 'USER');

INSERT INTO users (id, username, email, password, last_name, phone_number, balance, active, role)
VALUES ('9c0d1e2f-3a4b-5c6d-7e8f-9a0b1c2d3e4f', 'charlie', 'charlie@travel.com', '$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6', 'Brown', '+48555444333', 7000.0, TRUE, 'USER');

INSERT INTO users (id, username, email, password, last_name, phone_number, balance, active, role)
VALUES ('0d1e2f3a-4b5c-6d7e-8f9a-0b1c2d3e4f5a', 'david', 'david@travel.com', '$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6', 'Williams', '+48111999222', 8000.0, TRUE, 'USER');


-- ===================== VOUCHERS =====================
-- Существующие 4 доступных тура
INSERT INTO vouchers (id, title, description, price, tour_type, transfer_type, hotel_type, status, arrival_date, eviction_date, user_id, is_hot)
VALUES ('c3d4e5f6-a7b8-9c0d-1e2f-3a4b5c6d7e8f', 'Maldives Paradise', '7 days in a luxury resort', 2500.0, 'LEISURE', 'PLANE', 'FIVE_STARS', 'REGISTERED', '2026-12-01', '2026-12-08', NULL, TRUE);

INSERT INTO vouchers (id, title, description, price, tour_type, transfer_type, hotel_type, status, arrival_date, eviction_date, user_id, is_hot)
VALUES ('d4e5f6a7-b8c9-0d1e-2f3a-4b5c6d7e8f9a', 'Alps Skiing', 'Winter sports adventure', 1200.0, 'SPORTS', 'BUS', 'FOUR_STARS', 'REGISTERED', '2027-01-10', '2027-01-20', NULL, FALSE);

INSERT INTO vouchers (id, title, description, price, tour_type, transfer_type, hotel_type, status, arrival_date, eviction_date, user_id, is_hot)
VALUES ('e5f6a7b8-c90d-1e2f-3a4b-5c6d7e8f9a0b', 'Rome Getaway', 'Explore ancient history', 800.0, 'CULTURAL', 'PLANE', 'THREE_STARS', 'REGISTERED', '2026-05-15', '2026-05-20', NULL, FALSE);

INSERT INTO vouchers (id, title, description, price, tour_type, transfer_type, hotel_type, status, arrival_date, eviction_date, user_id, is_hot)
VALUES ('f6a7b8c9-0d1e-2f3a-4b5c-6d7e8f9a0b1c', 'Paris Tour', 'Fashion and sightseeing', 1500.0, 'LEISURE', 'TRAIN', 'FOUR_STARS', 'REGISTERED', '2026-09-10', '2026-09-15', NULL, TRUE);

-- Дополнительные 5 доступных туров (чтобы сработала пагинация > 5)
INSERT INTO vouchers (id, title, description, price, tour_type, transfer_type, hotel_type, status, arrival_date, eviction_date, user_id, is_hot)
VALUES ('1e2f3a4b-5c6d-7e8f-9a0b-1c2d3e4f5a6b', 'Dubai Relaxation', 'Desert and skyscrapers', 3000.0, 'LEISURE', 'PLANE', 'FIVE_STARS', 'REGISTERED', '2026-10-10', '2026-10-20', NULL, FALSE);

INSERT INTO vouchers (id, title, description, price, tour_type, transfer_type, hotel_type, status, arrival_date, eviction_date, user_id, is_hot)
VALUES ('2f3a4b5c-6d7e-8f9a-0b1c-2d3e4f5a6b7c', 'Safari Kenya', 'Wild animals spotting', 4000.0, 'SAFARI', 'PLANE', 'FOUR_STARS', 'REGISTERED', '2026-11-01', '2026-11-14', NULL, TRUE);

INSERT INTO vouchers (id, title, description, price, tour_type, transfer_type, hotel_type, status, arrival_date, eviction_date, user_id, is_hot)
VALUES ('3a4b5c6d-7e8f-9a0b-1c2d-3e4f5a6b7c8d', 'Wine Tasting Italy', 'Tuscany vineyards', 1800.0, 'WINE', 'BUS', 'THREE_STARS', 'REGISTERED', '2027-04-05', '2027-04-12', NULL, FALSE);

INSERT INTO vouchers (id, title, description, price, tour_type, transfer_type, hotel_type, status, arrival_date, eviction_date, user_id, is_hot)
VALUES ('4b5c6d7e-8f9a-0b1c-2d3e-4f5a6b7c8d9e', 'Everest Base Camp', 'Hiking adventure', 2200.0, 'ADVENTURE', 'PLANE', 'TWO_STARS', 'REGISTERED', '2027-05-10', '2027-05-25', NULL, TRUE);

INSERT INTO vouchers (id, title, description, price, tour_type, transfer_type, hotel_type, status, arrival_date, eviction_date, user_id, is_hot)
VALUES ('5c6d7e8f-9a0b-1c2d-3e4f-5a6b7c8d9e0f', 'London Weekend', 'Quick getaway', 600.0, 'LEISURE', 'PLANE', 'THREE_STARS', 'REGISTERED', '2026-12-15', '2026-12-18', NULL, FALSE);

-- Туры, уже КУПЛЕННЫЕ пользователем "user" (чтобы отображались у него в профиле и разделе "Мои туры")
INSERT INTO vouchers (id, title, description, price, tour_type, transfer_type, hotel_type, status, arrival_date, eviction_date, user_id, is_hot)
VALUES ('6d7e8f9a-0b1c-2d3e-4f5a-6b7c8d9e0f1a', 'Kyoto Culinary Tour', 'Best food in Japan', 3500.0, 'CULTURAL', 'PLANE', 'FOUR_STARS', 'PAID', '2026-08-20', '2026-08-30', 'b2c3d4e5-f6a7-8b9c-0d1e-2f3a4b5c6d7e', FALSE);

INSERT INTO vouchers (id, title, description, price, tour_type, transfer_type, hotel_type, status, arrival_date, eviction_date, user_id, is_hot)
VALUES ('7e8f9a0b-1c2d-3e4f-5a6b-7c8d9e0f1a2b', 'Swiss Alps', 'Snowboarding', 1500.0, 'SPORTS', 'BUS', 'THREE_STARS', 'REGISTERED', '2026-12-25', '2027-01-05', 'b2c3d4e5-f6a7-8b9c-0d1e-2f3a4b5c6d7e', FALSE);