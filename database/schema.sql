CREATE TABLE `users`
(
    `id`         bigint PRIMARY KEY AUTO_INCREMENT,
    `username`   varchar(50) UNIQUE NOT NULL,
    `password`   varchar(255)       NOT NULL COMMENT 'BCrypt encoded',
    `full_name`  nvarchar(100) NOT NULL,
    `phone`      varchar(15),
    `role`       varchar(20)        NOT NULL COMMENT 'OWNER, STAFF',
    `is_active`  boolean   DEFAULT true,
    `created_at` timestamp DEFAULT (now()),
    `updated_at` timestamp,
    `created_by` bigint
);

CREATE TABLE `products`
(
    `id`            bigint PRIMARY KEY AUTO_INCREMENT,
    `name`          nvarchar(100) NOT NULL COMMENT 'Ex: Cơm cháy, Bánh bao',
    `selling_price` decimal(10, 2) NOT NULL COMMENT 'Giá bán - Ex: 35000',
    `cost_price`    decimal(10, 2) NOT NULL COMMENT 'Giá vốn - for profit calculation',
    `image_url`     varchar(500) COMMENT 'AWS S3 URL',
    `is_active`     boolean   DEFAULT true,
    `created_at`    timestamp DEFAULT (now()),
    `updated_at`    timestamp
);

CREATE TABLE `product_price_history`
(
    `id`             bigint PRIMARY KEY AUTO_INCREMENT,
    `product_id`     bigint         NOT NULL,
    `selling_price`  decimal(10, 2) NOT NULL,
    `cost_price`     decimal(10, 2) NOT NULL,
    `effective_from` timestamp      NOT NULL DEFAULT (now()),
    `effective_to`   timestamp COMMENT 'NULL means current price',
    `changed_by`     bigint         NOT NULL COMMENT 'User who changed the price'
);

CREATE TABLE `storage_inventory`
(
    `id`           bigint PRIMARY KEY AUTO_INCREMENT,
    `product_id`   bigint UNIQUE NOT NULL,
    `quantity`     integer       NOT NULL DEFAULT 0,
    `last_updated` timestamp              DEFAULT (now())
);

CREATE TABLE `storage_transactions`
(
    `id`               bigint PRIMARY KEY AUTO_INCREMENT,
    `product_id`       bigint      NOT NULL,
    `transaction_type` varchar(20) NOT NULL COMMENT 'IN (from baker/purchase), OUT (to shift)',
    `quantity`         integer     NOT NULL COMMENT 'Always positive',
    `reference_id`     bigint COMMENT 'Links to shift_stock_additions.id if type=OUT',
    `note`             nvarchar(500),
    `created_by`       bigint      NOT NULL,
    `created_at`       timestamp DEFAULT (now())
);

CREATE TABLE `shift_templates`
(
    `id`         bigint PRIMARY KEY AUTO_INCREMENT,
    `name`       nvarchar(50) NOT NULL COMMENT 'Morning, Midday, Afternoon, Night',
    `start_time` time NOT NULL COMMENT 'Ex: 04:00:00',
    `end_time`   time NOT NULL COMMENT 'Ex: 11:00:00',
    `is_active`  boolean DEFAULT true
);

CREATE TABLE `shifts`
(
    `id`                   bigint PRIMARY KEY AUTO_INCREMENT,
    `shift_template_id`    bigint      NOT NULL,
    `shift_date`           date        NOT NULL,
    `manager_id`           bigint      NOT NULL COMMENT 'Staff assigned as Shift Manager',
    `status`               varchar(20) NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING, OPEN, CLOSED',
    `opened_at`            timestamp COMMENT 'When manager actually opened',
    `closed_at`            timestamp COMMENT 'When manager actually closed',
    `initial_cash`         decimal(12, 2)       DEFAULT 0 COMMENT 'Tiền lẻ đầu ca',
    `total_cash_sales`     decimal(12, 2)       DEFAULT 0,
    `total_transfer_sales` decimal(12, 2)       DEFAULT 0,
    `total_revenue`        decimal(12, 2)       DEFAULT 0 COMMENT 'cash + transfer',
    `total_cost`           decimal(12, 2)       DEFAULT 0 COMMENT 'For profit calculation',
    `expected_cash`        decimal(12, 2) COMMENT 'initial_cash + total_cash_sales',
    `actual_cash_handed`   decimal(12, 2) COMMENT 'Physical count by manager',
    `cash_discrepancy`     decimal(12, 2) COMMENT 'actual - expected',
    `cash_note`            nvarchar(500) COMMENT 'Explanation if cash discrepancy',
    `created_at`           timestamp            DEFAULT (now()),
    `updated_at`           timestamp
);

CREATE TABLE `shift_staff`
(
    `id`       bigint PRIMARY KEY AUTO_INCREMENT,
    `shift_id` bigint NOT NULL,
    `user_id`  bigint NOT NULL
);

CREATE TABLE `shift_inventory`
(
    `id`                     bigint PRIMARY KEY AUTO_INCREMENT,
    `shift_id`               bigint         NOT NULL,
    `product_id`             bigint         NOT NULL,
    `opening_qty`            integer        NOT NULL DEFAULT 0 COMMENT 'Received from previous shift',
    `opening_confirmed`      boolean                 DEFAULT false,
    `opening_confirmed_at`   timestamp,
    `added_qty`              integer                 DEFAULT 0 COMMENT 'Sum of additions during shift',
    `sold_qty`               integer                 DEFAULT 0,
    `system_closing_qty`     integer COMMENT 'opening + added - sold',
    `actual_closing_qty`     integer COMMENT 'Physical count',
    `discrepancy`            integer COMMENT 'actual - system. Negative = LOSS',
    `discrepancy_note`       nvarchar(500) COMMENT 'Explanation if discrepancy',
    `selling_price_snapshot` decimal(10, 2) NOT NULL,
    `cost_price_snapshot`    decimal(10, 2) NOT NULL
);

CREATE TABLE `shift_stock_additions`
(
    `id`         bigint PRIMARY KEY AUTO_INCREMENT,
    `shift_id`   bigint  NOT NULL,
    `product_id` bigint  NOT NULL,
    `quantity`   integer NOT NULL,
    `added_by`   bigint  NOT NULL COMMENT 'Shift manager who received',
    `note`       nvarchar(500),
    `created_at` timestamp DEFAULT (now())
);

CREATE TABLE `shift_handovers`
(
    `id`            bigint PRIMARY KEY AUTO_INCREMENT,
    `from_shift_id` bigint      NOT NULL COMMENT 'Closing shift',
    `to_shift_id`   bigint      NOT NULL COMMENT 'Opening shift',
    `handed_by`     bigint      NOT NULL COMMENT 'Previous shift manager',
    `handed_at`     timestamp   NOT NULL,
    `received_by`   bigint      NOT NULL COMMENT 'Current shift manager',
    `received_at`   timestamp,
    `status`        varchar(20) NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING, CONFIRMED, DISPUTED',
    `dispute_note`  nvarchar(500) COMMENT 'If receiver disputes quantities'
);

CREATE TABLE `shift_handover_details`
(
    `id`               bigint PRIMARY KEY AUTO_INCREMENT,
    `handover_id`      bigint  NOT NULL,
    `product_id`       bigint  NOT NULL,
    `handed_qty`       integer NOT NULL COMMENT 'Qty reported by closing shift',
    `received_qty`     integer COMMENT 'Qty confirmed by opening shift',
    `has_discrepancy`  boolean DEFAULT false,
    `discrepancy_note` nvarchar(500)
);

CREATE TABLE `orders`
(
    `id`             bigint PRIMARY KEY AUTO_INCREMENT,
    `shift_id`       bigint             NOT NULL,
    `order_number`   varchar(20) UNIQUE NOT NULL COMMENT 'Ex: ORD-20251229-0001',
    `subtotal`       decimal(12, 2)     NOT NULL,
    `discount`       decimal(12, 2) DEFAULT 0,
    `total_amount`   decimal(12, 2)     NOT NULL,
    `payment_method` varchar(20)        NOT NULL COMMENT 'CASH, TRANSFER',
    `created_by`     bigint             NOT NULL,
    `created_at`     timestamp      DEFAULT (now())
);

CREATE TABLE `order_items`
(
    `id`         bigint PRIMARY KEY AUTO_INCREMENT,
    `order_id`   bigint         NOT NULL,
    `product_id` bigint         NOT NULL,
    `quantity`   integer        NOT NULL,
    `unit_price` decimal(10, 2) NOT NULL COMMENT 'Price at time of sale',
    `unit_cost`  decimal(10, 2) NOT NULL COMMENT 'Cost at time of sale',
    `line_total` decimal(12, 2) NOT NULL COMMENT 'quantity * unit_price'
);

CREATE TABLE `audit_logs`
(
    `id`          bigint PRIMARY KEY AUTO_INCREMENT,
    `user_id`     bigint,
    `action`      varchar(50) NOT NULL COMMENT 'LOGIN, LOGOUT, CREATE, UPDATE, DELETE',
    `entity_type` varchar(50) NOT NULL COMMENT 'SHIFT, ORDER, PRODUCT, etc.',
    `entity_id`   bigint,
    `old_value`   text COMMENT 'JSON of previous state',
    `new_value`   text COMMENT 'JSON of new state',
    `ip_address`  varchar(45),
    `user_agent`  varchar(500),
    `created_at`  timestamp DEFAULT (now())
);

CREATE UNIQUE INDEX `users_index_0` ON `users` (`username`);

CREATE INDEX `users_index_1` ON `users` (`role`);

CREATE INDEX `users_index_2` ON `users` (`is_active`);

CREATE INDEX `products_index_3` ON `products` (`name`);

CREATE INDEX `products_index_4` ON `products` (`is_active`);

CREATE INDEX `product_price_history_index_5` ON `product_price_history` (`product_id`);

CREATE INDEX `product_price_history_index_6` ON `product_price_history` (`effective_from`);

CREATE UNIQUE INDEX `storage_inventory_index_7` ON `storage_inventory` (`product_id`);

CREATE INDEX `storage_transactions_index_8` ON `storage_transactions` (`product_id`);

CREATE INDEX `storage_transactions_index_9` ON `storage_transactions` (`transaction_type`);

CREATE INDEX `storage_transactions_index_10` ON `storage_transactions` (`created_at`);

CREATE INDEX `shifts_index_11` ON `shifts` (`shift_date`);

CREATE INDEX `shifts_index_12` ON `shifts` (`status`);

CREATE INDEX `shifts_index_13` ON `shifts` (`manager_id`);

CREATE UNIQUE INDEX `shifts_index_14` ON `shifts` (`shift_date`, `shift_template_id`);

CREATE UNIQUE INDEX `shift_staff_index_15` ON `shift_staff` (`shift_id`, `user_id`);

CREATE UNIQUE INDEX `shift_inventory_index_16` ON `shift_inventory` (`shift_id`, `product_id`);

CREATE INDEX `shift_inventory_index_17` ON `shift_inventory` (`shift_id`);

CREATE INDEX `shift_inventory_index_18` ON `shift_inventory` (`product_id`);

CREATE INDEX `shift_stock_additions_index_19` ON `shift_stock_additions` (`shift_id`);

CREATE INDEX `shift_stock_additions_index_20` ON `shift_stock_additions` (`product_id`);

CREATE INDEX `shift_handovers_index_21` ON `shift_handovers` (`from_shift_id`);

CREATE INDEX `shift_handovers_index_22` ON `shift_handovers` (`to_shift_id`);

CREATE INDEX `shift_handovers_index_23` ON `shift_handovers` (`status`);

CREATE INDEX `shift_handover_details_index_24` ON `shift_handover_details` (`handover_id`);

CREATE INDEX `shift_handover_details_index_25` ON `shift_handover_details` (`product_id`);

CREATE INDEX `orders_index_26` ON `orders` (`shift_id`);

CREATE UNIQUE INDEX `orders_index_27` ON `orders` (`order_number`);

CREATE INDEX `orders_index_28` ON `orders` (`created_at`);

CREATE INDEX `orders_index_29` ON `orders` (`payment_method`);

CREATE INDEX `order_items_index_30` ON `order_items` (`order_id`);

CREATE INDEX `order_items_index_31` ON `order_items` (`product_id`);

CREATE INDEX `audit_logs_index_32` ON `audit_logs` (`user_id`);

CREATE INDEX `audit_logs_index_33` ON `audit_logs` (`action`);

CREATE INDEX `audit_logs_index_34` ON `audit_logs` (`entity_type`);

CREATE INDEX `audit_logs_index_35` ON `audit_logs` (`created_at`);

ALTER TABLE `users`
    ADD FOREIGN KEY (`created_by`) REFERENCES `users` (`id`);

ALTER TABLE `product_price_history`
    ADD FOREIGN KEY (`product_id`) REFERENCES `products` (`id`);

ALTER TABLE `product_price_history`
    ADD FOREIGN KEY (`changed_by`) REFERENCES `users` (`id`);

ALTER TABLE `products`
    ADD FOREIGN KEY (`id`) REFERENCES `storage_inventory` (`product_id`);

ALTER TABLE `storage_transactions`
    ADD FOREIGN KEY (`product_id`) REFERENCES `products` (`id`);

ALTER TABLE `storage_transactions`
    ADD FOREIGN KEY (`created_by`) REFERENCES `users` (`id`);

ALTER TABLE `shifts`
    ADD FOREIGN KEY (`shift_template_id`) REFERENCES `shift_templates` (`id`);

ALTER TABLE `shifts`
    ADD FOREIGN KEY (`manager_id`) REFERENCES `users` (`id`);

ALTER TABLE `shift_staff`
    ADD FOREIGN KEY (`shift_id`) REFERENCES `shifts` (`id`);

ALTER TABLE `shift_staff`
    ADD FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

ALTER TABLE `shift_inventory`
    ADD FOREIGN KEY (`shift_id`) REFERENCES `shifts` (`id`);

ALTER TABLE `shift_inventory`
    ADD FOREIGN KEY (`product_id`) REFERENCES `products` (`id`);

ALTER TABLE `shift_stock_additions`
    ADD FOREIGN KEY (`shift_id`) REFERENCES `shifts` (`id`);

ALTER TABLE `shift_stock_additions`
    ADD FOREIGN KEY (`product_id`) REFERENCES `products` (`id`);

ALTER TABLE `shift_stock_additions`
    ADD FOREIGN KEY (`added_by`) REFERENCES `users` (`id`);

ALTER TABLE `shift_handovers`
    ADD FOREIGN KEY (`from_shift_id`) REFERENCES `shifts` (`id`);

ALTER TABLE `shift_handovers`
    ADD FOREIGN KEY (`to_shift_id`) REFERENCES `shifts` (`id`);

ALTER TABLE `shift_handovers`
    ADD FOREIGN KEY (`handed_by`) REFERENCES `users` (`id`);

ALTER TABLE `shift_handovers`
    ADD FOREIGN KEY (`received_by`) REFERENCES `users` (`id`);

ALTER TABLE `shift_handover_details`
    ADD FOREIGN KEY (`handover_id`) REFERENCES `shift_handovers` (`id`);

ALTER TABLE `shift_handover_details`
    ADD FOREIGN KEY (`product_id`) REFERENCES `products` (`id`);

ALTER TABLE `orders`
    ADD FOREIGN KEY (`shift_id`) REFERENCES `shifts` (`id`);

ALTER TABLE `orders`
    ADD FOREIGN KEY (`created_by`) REFERENCES `users` (`id`);

ALTER TABLE `order_items`
    ADD FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`);

ALTER TABLE `order_items`
    ADD FOREIGN KEY (`product_id`) REFERENCES `products` (`id`);

ALTER TABLE `audit_logs`
    ADD FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);
