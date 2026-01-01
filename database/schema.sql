-- ==========================================
-- BAKERY MANAGEMENT SYSTEM
-- Complete MySQL Schema
-- Version 2.0 - Added Shift Closing Photos
-- ==========================================
--
-- Business Rules:
-- - 4 shifts: Morning (4-11), Midday (11-14), Afternoon (14-21), Night (21-4)
-- - 2-4 staff per shift, 1 Shift Manager (rotational)
-- - Double verification on shift handover
-- - Stock flow: Storage → Shift (sales floor)
-- - Payment: Cash + Bank Transfer
-- - Owner manages storage, Staff manages shifts
-- - Photos taken at shift close for evidence (optional, 30-day retention)
-- ==========================================

-- Drop tables if exist (for clean reinstall)
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `audit_logs`;
DROP TABLE IF EXISTS `order_items`;
DROP TABLE IF EXISTS `orders`;
DROP TABLE IF EXISTS `shift_handover_details`;
DROP TABLE IF EXISTS `shift_handovers`;
DROP TABLE IF EXISTS `shift_closing_photos`;
DROP TABLE IF EXISTS `shift_stock_additions`;
DROP TABLE IF EXISTS `shift_inventory`;
DROP TABLE IF EXISTS `shift_staff`;
DROP TABLE IF EXISTS `shifts`;
DROP TABLE IF EXISTS `shift_templates`;
DROP TABLE IF EXISTS `storage_transactions`;
DROP TABLE IF EXISTS `storage_inventory`;
DROP TABLE IF EXISTS `product_price_history`;
DROP TABLE IF EXISTS `products`;
DROP TABLE IF EXISTS `users`;

SET FOREIGN_KEY_CHECKS = 1;

-- ==========================================
-- USER MANAGEMENT
-- ==========================================

CREATE TABLE `users`
(
    `id`         BIGINT PRIMARY KEY AUTO_INCREMENT,
    `username`   VARCHAR(50) NOT NULL,
    `password`   VARCHAR(255) NOT NULL COMMENT 'BCrypt encoded',
    `full_name`  NVARCHAR(100) NOT NULL,
    `phone`      VARCHAR(15),
    `role`       VARCHAR(20) NOT NULL COMMENT 'OWNER, STAFF',
    `is_active`  BOOLEAN DEFAULT TRUE,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP,
    `created_by` BIGINT,

    CONSTRAINT `uk_users_username` UNIQUE (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX `idx_users_role` ON `users` (`role`);
CREATE INDEX `idx_users_is_active` ON `users` (`is_active`);

-- ==========================================
-- PRODUCT MANAGEMENT
-- ==========================================

CREATE TABLE `products`
(
    `id`            BIGINT PRIMARY KEY AUTO_INCREMENT,
    `name`          NVARCHAR(100) NOT NULL COMMENT 'Ex: Cơm cháy, Bánh bao',
    `selling_price` DECIMAL(10, 2) NOT NULL COMMENT 'Giá bán - Ex: 35000',
    `cost_price`    DECIMAL(10, 2) NOT NULL COMMENT 'Giá vốn - for profit calculation',
    `image_url`     VARCHAR(500) COMMENT 'AWS S3 URL',
    `is_active`     BOOLEAN DEFAULT TRUE,
    `created_at`    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at`    TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX `idx_products_name` ON `products` (`name`);
CREATE INDEX `idx_products_is_active` ON `products` (`is_active`);

CREATE TABLE `product_price_history`
(
    `id`             BIGINT PRIMARY KEY AUTO_INCREMENT,
    `product_id`     BIGINT NOT NULL,
    `selling_price`  DECIMAL(10, 2) NOT NULL,
    `cost_price`     DECIMAL(10, 2) NOT NULL,
    `effective_from` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `effective_to`   TIMESTAMP NULL COMMENT 'NULL means current price',
    `changed_by`     BIGINT NOT NULL COMMENT 'User who changed the price'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX `idx_product_price_history_product_id` ON `product_price_history` (`product_id`);
CREATE INDEX `idx_product_price_history_effective_from` ON `product_price_history` (`effective_from`);

-- ==========================================
-- STORAGE MANAGEMENT
-- ==========================================

CREATE TABLE `storage_inventory`
(
    `id`           BIGINT PRIMARY KEY AUTO_INCREMENT,
    `product_id`   BIGINT NOT NULL,
    `quantity`     INT NOT NULL DEFAULT 0,
    `last_updated` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT `uk_storage_inventory_product_id` UNIQUE (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `storage_transactions`
(
    `id`               BIGINT PRIMARY KEY AUTO_INCREMENT,
    `product_id`       BIGINT NOT NULL,
    `transaction_type` VARCHAR(20) NOT NULL COMMENT 'IN (from baker/purchase), OUT (to shift)',
    `quantity`         INT NOT NULL COMMENT 'Always positive',
    `reference_id`     BIGINT COMMENT 'Links to shift_stock_additions.id if type=OUT',
    `note`             NVARCHAR(500),
    `created_by`       BIGINT NOT NULL,
    `created_at`       TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX `idx_storage_transactions_product_id` ON `storage_transactions` (`product_id`);
CREATE INDEX `idx_storage_transactions_type` ON `storage_transactions` (`transaction_type`);
CREATE INDEX `idx_storage_transactions_created_at` ON `storage_transactions` (`created_at`);

-- ==========================================
-- SHIFT MANAGEMENT
-- ==========================================

CREATE TABLE `shift_templates`
(
    `id`         BIGINT PRIMARY KEY AUTO_INCREMENT,
    `name`       NVARCHAR(50) NOT NULL COMMENT 'Morning, Midday, Afternoon, Night',
    `start_time` TIME NOT NULL COMMENT 'Ex: 04:00:00',
    `end_time`   TIME NOT NULL COMMENT 'Ex: 11:00:00',
    `is_active`  BOOLEAN DEFAULT TRUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `shifts`
(
    `id`                   BIGINT PRIMARY KEY AUTO_INCREMENT,
    `shift_template_id`    BIGINT NOT NULL,
    `shift_date`           DATE NOT NULL,
    `manager_id`           BIGINT NOT NULL COMMENT 'Staff assigned as Shift Manager',
    `status`               VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING, OPEN, CLOSED',
    `opened_at`            TIMESTAMP NULL COMMENT 'When manager actually opened',
    `closed_at`            TIMESTAMP NULL COMMENT 'When manager actually closed',
    `initial_cash`         DECIMAL(12, 2) DEFAULT 0 COMMENT 'Tiền lẻ đầu ca',
    `total_cash_sales`     DECIMAL(12, 2) DEFAULT 0,
    `total_transfer_sales` DECIMAL(12, 2) DEFAULT 0,
    `total_revenue`        DECIMAL(12, 2) DEFAULT 0 COMMENT 'cash + transfer',
    `total_cost`           DECIMAL(12, 2) DEFAULT 0 COMMENT 'For profit calculation',
    `expected_cash`        DECIMAL(12, 2) COMMENT 'initial_cash + total_cash_sales',
    `actual_cash_handed`   DECIMAL(12, 2) COMMENT 'Physical count by manager',
    `cash_discrepancy`     DECIMAL(12, 2) COMMENT 'actual - expected',
    `cash_note`            NVARCHAR(500) COMMENT 'Explanation if cash discrepancy',
    `created_at`           TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at`           TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT `uk_shifts_date_template` UNIQUE (`shift_date`, `shift_template_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX `idx_shifts_shift_date` ON `shifts` (`shift_date`);
CREATE INDEX `idx_shifts_status` ON `shifts` (`status`);
CREATE INDEX `idx_shifts_manager_id` ON `shifts` (`manager_id`);

CREATE TABLE `shift_staff`
(
    `id`       BIGINT PRIMARY KEY AUTO_INCREMENT,
    `shift_id` BIGINT NOT NULL,
    `user_id`  BIGINT NOT NULL,

    CONSTRAINT `uk_shift_staff` UNIQUE (`shift_id`, `user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==========================================
-- SHIFT INVENTORY TRACKING (CORE AUDIT)
-- ==========================================

CREATE TABLE `shift_inventory`
(
    `id`                     BIGINT PRIMARY KEY AUTO_INCREMENT,
    `shift_id`               BIGINT NOT NULL,
    `product_id`             BIGINT NOT NULL,
    `opening_qty`            INT NOT NULL DEFAULT 0 COMMENT 'Received from previous shift',
    `opening_confirmed`      BOOLEAN DEFAULT FALSE,
    `opening_confirmed_at`   TIMESTAMP NULL,
    `added_qty`              INT DEFAULT 0 COMMENT 'Sum of additions during shift',
    `sold_qty`               INT DEFAULT 0,
    `system_closing_qty`     INT COMMENT 'opening + added - sold',
    `actual_closing_qty`     INT COMMENT 'Physical count',
    `discrepancy`            INT COMMENT 'actual - system. Negative = LOSS',
    `discrepancy_note`       NVARCHAR(500) COMMENT 'Explanation if discrepancy',
    `selling_price_snapshot` DECIMAL(10, 2) NOT NULL,
    `cost_price_snapshot`    DECIMAL(10, 2) NOT NULL,

    CONSTRAINT `uk_shift_inventory` UNIQUE (`shift_id`, `product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX `idx_shift_inventory_shift_id` ON `shift_inventory` (`shift_id`);
CREATE INDEX `idx_shift_inventory_product_id` ON `shift_inventory` (`product_id`);

CREATE TABLE `shift_stock_additions`
(
    `id`         BIGINT PRIMARY KEY AUTO_INCREMENT,
    `shift_id`   BIGINT NOT NULL,
    `product_id` BIGINT NOT NULL,
    `quantity`   INT NOT NULL,
    `added_by`   BIGINT NOT NULL COMMENT 'Shift manager who received',
    `note`       NVARCHAR(500),
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX `idx_shift_stock_additions_shift_id` ON `shift_stock_additions` (`shift_id`);
CREATE INDEX `idx_shift_stock_additions_product_id` ON `shift_stock_additions` (`product_id`);

-- ==========================================
-- SHIFT CLOSING PHOTOS (Evidence for handover)
-- ==========================================
-- - Photos taken when closing shift (optional but recommended)
-- - One photo per product
-- - Stored on Cloudinary
-- - Auto-delete after 30 days
-- - Only Owner can view photos

CREATE TABLE `shift_closing_photos`
(
    `id`                   BIGINT PRIMARY KEY AUTO_INCREMENT,
    `shift_inventory_id`   BIGINT NOT NULL COMMENT 'FK to shift_inventory - which product in which shift',
    `cloudinary_public_id` VARCHAR(255) NOT NULL COMMENT 'For Cloudinary API deletion',
    `photo_url`            VARCHAR(500) NOT NULL COMMENT 'Cloudinary secure URL',
    `thumbnail_url`        VARCHAR(500) COMMENT 'Smaller version for quick loading',
    `taken_by`             BIGINT NOT NULL COMMENT 'Shift manager who took photo',
    `taken_at`             TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `expires_at`           TIMESTAMP NOT NULL COMMENT 'Auto-delete after 30 days',
    `note`                 NVARCHAR(255) COMMENT 'Optional note about the photo'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX `idx_shift_closing_photos_shift_inventory_id` ON `shift_closing_photos` (`shift_inventory_id`);
CREATE INDEX `idx_shift_closing_photos_expires_at` ON `shift_closing_photos` (`expires_at`);
CREATE INDEX `idx_shift_closing_photos_taken_by` ON `shift_closing_photos` (`taken_by`);

-- ==========================================
-- SHIFT HANDOVER (DOUBLE VERIFICATION)
-- ==========================================

CREATE TABLE `shift_handovers`
(
    `id`            BIGINT PRIMARY KEY AUTO_INCREMENT,
    `from_shift_id` BIGINT NOT NULL COMMENT 'Closing shift',
    `to_shift_id`   BIGINT NOT NULL COMMENT 'Opening shift',
    `handed_by`     BIGINT NOT NULL COMMENT 'Previous shift manager',
    `handed_at`     TIMESTAMP NOT NULL,
    `received_by`   BIGINT NOT NULL COMMENT 'Current shift manager',
    `received_at`   TIMESTAMP NULL,
    `status`        VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING, CONFIRMED, DISPUTED',
    `dispute_note`  NVARCHAR(500) COMMENT 'If receiver disputes quantities'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX `idx_shift_handovers_from_shift_id` ON `shift_handovers` (`from_shift_id`);
CREATE INDEX `idx_shift_handovers_to_shift_id` ON `shift_handovers` (`to_shift_id`);
CREATE INDEX `idx_shift_handovers_status` ON `shift_handovers` (`status`);

CREATE TABLE `shift_handover_details`
(
    `id`               BIGINT PRIMARY KEY AUTO_INCREMENT,
    `handover_id`      BIGINT NOT NULL,
    `product_id`       BIGINT NOT NULL,
    `handed_qty`       INT NOT NULL COMMENT 'Qty reported by closing shift',
    `received_qty`     INT COMMENT 'Qty confirmed by opening shift',
    `has_discrepancy`  BOOLEAN DEFAULT FALSE,
    `discrepancy_note` NVARCHAR(500)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX `idx_shift_handover_details_handover_id` ON `shift_handover_details` (`handover_id`);
CREATE INDEX `idx_shift_handover_details_product_id` ON `shift_handover_details` (`product_id`);

-- ==========================================
-- SALES TRANSACTIONS
-- ==========================================

CREATE TABLE `orders`
(
    `id`             BIGINT PRIMARY KEY AUTO_INCREMENT,
    `shift_id`       BIGINT NOT NULL,
    `order_number`   VARCHAR(20) NOT NULL COMMENT 'Ex: ORD-20251229-0001',
    `subtotal`       DECIMAL(12, 2) NOT NULL,
    `discount`       DECIMAL(12, 2) DEFAULT 0,
    `total_amount`   DECIMAL(12, 2) NOT NULL,
    `payment_method` VARCHAR(20) NOT NULL COMMENT 'CASH, TRANSFER',
    `created_by`     BIGINT NOT NULL,
    `created_at`     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT `uk_orders_order_number` UNIQUE (`order_number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX `idx_orders_shift_id` ON `orders` (`shift_id`);
CREATE INDEX `idx_orders_created_at` ON `orders` (`created_at`);
CREATE INDEX `idx_orders_payment_method` ON `orders` (`payment_method`);

CREATE TABLE `order_items`
(
    `id`         BIGINT PRIMARY KEY AUTO_INCREMENT,
    `order_id`   BIGINT NOT NULL,
    `product_id` BIGINT NOT NULL,
    `quantity`   INT NOT NULL,
    `unit_price` DECIMAL(10, 2) NOT NULL COMMENT 'Price at time of sale',
    `unit_cost`  DECIMAL(10, 2) NOT NULL COMMENT 'Cost at time of sale',
    `line_total` DECIMAL(12, 2) NOT NULL COMMENT 'quantity * unit_price'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX `idx_order_items_order_id` ON `order_items` (`order_id`);
CREATE INDEX `idx_order_items_product_id` ON `order_items` (`product_id`);

-- ==========================================
-- AUDIT LOG (For security - important for NAB)
-- ==========================================

CREATE TABLE `audit_logs`
(
    `id`          BIGINT PRIMARY KEY AUTO_INCREMENT,
    `user_id`     BIGINT,
    `action`      VARCHAR(50) NOT NULL COMMENT 'LOGIN, LOGOUT, CREATE, UPDATE, DELETE',
    `entity_type` VARCHAR(50) NOT NULL COMMENT 'SHIFT, ORDER, PRODUCT, etc.',
    `entity_id`   BIGINT,
    `old_value`   TEXT COMMENT 'JSON of previous state',
    `new_value`   TEXT COMMENT 'JSON of new state',
    `ip_address`  VARCHAR(45),
    `user_agent`  VARCHAR(500),
    `created_at`  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX `idx_audit_logs_user_id` ON `audit_logs` (`user_id`);
CREATE INDEX `idx_audit_logs_action` ON `audit_logs` (`action`);
CREATE INDEX `idx_audit_logs_entity_type` ON `audit_logs` (`entity_type`);
CREATE INDEX `idx_audit_logs_created_at` ON `audit_logs` (`created_at`);

-- ==========================================
-- FOREIGN KEYS
-- ==========================================

-- Users
ALTER TABLE `users`
    ADD CONSTRAINT `fk_users_created_by`
        FOREIGN KEY (`created_by`) REFERENCES `users` (`id`) ON DELETE SET NULL;

-- Product Price History
ALTER TABLE `product_price_history`
    ADD CONSTRAINT `fk_product_price_history_product`
        FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE CASCADE;

ALTER TABLE `product_price_history`
    ADD CONSTRAINT `fk_product_price_history_user`
        FOREIGN KEY (`changed_by`) REFERENCES `users` (`id`) ON DELETE RESTRICT;

-- Storage
ALTER TABLE `storage_inventory`
    ADD CONSTRAINT `fk_storage_inventory_product`
        FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE CASCADE;

ALTER TABLE `storage_transactions`
    ADD CONSTRAINT `fk_storage_transactions_product`
        FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE RESTRICT;

ALTER TABLE `storage_transactions`
    ADD CONSTRAINT `fk_storage_transactions_user`
        FOREIGN KEY (`created_by`) REFERENCES `users` (`id`) ON DELETE RESTRICT;

-- Shifts
ALTER TABLE `shifts`
    ADD CONSTRAINT `fk_shifts_template`
        FOREIGN KEY (`shift_template_id`) REFERENCES `shift_templates` (`id`) ON DELETE RESTRICT;

ALTER TABLE `shifts`
    ADD CONSTRAINT `fk_shifts_manager`
        FOREIGN KEY (`manager_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT;

ALTER TABLE `shift_staff`
    ADD CONSTRAINT `fk_shift_staff_shift`
        FOREIGN KEY (`shift_id`) REFERENCES `shifts` (`id`) ON DELETE CASCADE;

ALTER TABLE `shift_staff`
    ADD CONSTRAINT `fk_shift_staff_user`
        FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

-- Shift Inventory
ALTER TABLE `shift_inventory`
    ADD CONSTRAINT `fk_shift_inventory_shift`
        FOREIGN KEY (`shift_id`) REFERENCES `shifts` (`id`) ON DELETE CASCADE;

ALTER TABLE `shift_inventory`
    ADD CONSTRAINT `fk_shift_inventory_product`
        FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE RESTRICT;

ALTER TABLE `shift_stock_additions`
    ADD CONSTRAINT `fk_shift_stock_additions_shift`
        FOREIGN KEY (`shift_id`) REFERENCES `shifts` (`id`) ON DELETE CASCADE;

ALTER TABLE `shift_stock_additions`
    ADD CONSTRAINT `fk_shift_stock_additions_product`
        FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE RESTRICT;

ALTER TABLE `shift_stock_additions`
    ADD CONSTRAINT `fk_shift_stock_additions_user`
        FOREIGN KEY (`added_by`) REFERENCES `users` (`id`) ON DELETE RESTRICT;

-- Shift Closing Photos
ALTER TABLE `shift_closing_photos`
    ADD CONSTRAINT `fk_shift_closing_photos_inventory`
        FOREIGN KEY (`shift_inventory_id`) REFERENCES `shift_inventory` (`id`) ON DELETE CASCADE;

ALTER TABLE `shift_closing_photos`
    ADD CONSTRAINT `fk_shift_closing_photos_user`
        FOREIGN KEY (`taken_by`) REFERENCES `users` (`id`) ON DELETE RESTRICT;

-- Shift Handovers
ALTER TABLE `shift_handovers`
    ADD CONSTRAINT `fk_shift_handovers_from_shift`
        FOREIGN KEY (`from_shift_id`) REFERENCES `shifts` (`id`) ON DELETE CASCADE;

ALTER TABLE `shift_handovers`
    ADD CONSTRAINT `fk_shift_handovers_to_shift`
        FOREIGN KEY (`to_shift_id`) REFERENCES `shifts` (`id`) ON DELETE CASCADE;

ALTER TABLE `shift_handovers`
    ADD CONSTRAINT `fk_shift_handovers_handed_by`
        FOREIGN KEY (`handed_by`) REFERENCES `users` (`id`) ON DELETE RESTRICT;

ALTER TABLE `shift_handovers`
    ADD CONSTRAINT `fk_shift_handovers_received_by`
        FOREIGN KEY (`received_by`) REFERENCES `users` (`id`) ON DELETE RESTRICT;

ALTER TABLE `shift_handover_details`
    ADD CONSTRAINT `fk_shift_handover_details_handover`
        FOREIGN KEY (`handover_id`) REFERENCES `shift_handovers` (`id`) ON DELETE CASCADE;

ALTER TABLE `shift_handover_details`
    ADD CONSTRAINT `fk_shift_handover_details_product`
        FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE RESTRICT;

-- Orders
ALTER TABLE `orders`
    ADD CONSTRAINT `fk_orders_shift`
        FOREIGN KEY (`shift_id`) REFERENCES `shifts` (`id`) ON DELETE RESTRICT;

ALTER TABLE `orders`
    ADD CONSTRAINT `fk_orders_user`
        FOREIGN KEY (`created_by`) REFERENCES `users` (`id`) ON DELETE RESTRICT;

ALTER TABLE `order_items`
    ADD CONSTRAINT `fk_order_items_order`
        FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE CASCADE;

ALTER TABLE `order_items`
    ADD CONSTRAINT `fk_order_items_product`
        FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE RESTRICT;

-- Audit Logs
ALTER TABLE `audit_logs`
    ADD CONSTRAINT `fk_audit_logs_user`
        FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE SET NULL;

-- ==========================================
-- SEED DATA FOR SHIFT TEMPLATES
-- ==========================================

INSERT INTO `shift_templates` (`name`, `start_time`, `end_time`, `is_active`) VALUES
                                                                                  ('Ca Sáng', '04:00:00', '11:00:00', TRUE),
                                                                                  ('Ca Trưa', '11:00:00', '14:00:00', TRUE),
                                                                                  ('Ca Chiều', '14:00:00', '21:00:00', TRUE),
                                                                                  ('Ca Tối', '21:00:00', '04:00:00', TRUE);

-- ==========================================
-- END OF SCHEMA
-- ==========================================