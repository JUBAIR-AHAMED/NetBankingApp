CREATE TABLE activity_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    actor_id BIGINT NOT NULL,
    subject_id BIGINT NOT NULL,
    key_value BIGINT NOT NULL,
    tablename VARCHAR(25) NOT NULL,
    action VARCHAR(255) NOT NULL,
    details TEXT NOT NULL,
    action_time BIGINT NOT NULL
);

CREATE TABLE IF NOT EXISTS `user` (
    `user_id` BIGINT NOT NULL AUTO_INCREMENT UNIQUE,
    `password` VARCHAR(255) NOT NULL,
    `role` VARCHAR(10) NOT NULL,
    `name` VARCHAR(100) NOT NULL,
    `email` VARCHAR(100) NOT NULL UNIQUE,
    `mobile` VARCHAR(15) NOT NULL UNIQUE,
    `date_of_birth` DATE NOT NULL,
    `status` VARCHAR(10) NOT NULL,
    `creation_time` BIGINT NOT NULL,
    `modified_time` BIGINT,
    `modified_by` BIGINT NOT NULL,
    PRIMARY KEY (`user_id`)
);

CREATE TABLE IF NOT EXISTS `branch` (
	`branch_id` bigint NOT NULL AUTO_INCREMENT UNIQUE,
	`ifsc` bigint  NOT NULL UNIQUE,
	`name` varchar(255) NOT NULL,
	`employee_id` bigint NOT NULL UNIQUE,
	`address` varchar(255) NOT NULL,
	`creation_time` bigint NOT NULL,
	`modified_time` bigint,
	`modified_by` bigint NOT NULL,
	PRIMARY KEY (`branch_id`),
	FOREIGN KEY (`employee_id`) REFERENCES `user`(`user_id`)
);

CREATE TABLE IF NOT EXISTS `employee` (
    `employee_id` bigint NOT NULL UNIQUE,
    `branch_id` bigint,
    PRIMARY KEY (`employee_id`),
    FOREIGN KEY (`employee_id`) REFERENCES `user`(`user_id`) ON DELETE CASCADE,
    FOREIGN KEY (`branch_id`) REFERENCES `branch`(`branch_id`) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS `customer` (
    `customer_id` bigint NOT NULL UNIQUE,
    `aadhar_number` bigint NOT NULL UNIQUE,
    `pan_number` varchar(255) NOT NULL UNIQUE,
    PRIMARY KEY (`customer_id`),
    FOREIGN KEY (`customer_id`) REFERENCES `user`(`user_id`) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS `account` (
    `account_number` BIGINT NOT NULL AUTO_INCREMENT UNIQUE,
    `user_id` BIGINT NOT NULL,
    `branch_id` BIGINT NOT NULL,
    `account_type` VARCHAR(50) NOT NULL,
    `date_of_opening` BIGINT NOT NULL,
    `balance` FLOAT NOT NULL,
    `status` VARCHAR(20) NOT NULL,
    `creation_time` BIGINT NOT NULL,
    `modified_time` BIGINT, 
    `modified_by` BIGINT NOT NULL,
    PRIMARY KEY (`account_number`),
    FOREIGN KEY (`branch_id`) REFERENCES `branch`(`branch_id`) ON DELETE CASCADE,
    FOREIGN KEY (`user_id`) REFERENCES `user`(`user_id`) ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS `transaction` (
	`reference_number` bigint NOT NULL AUTO_INCREMENT,
	`transaction_amount` FLOAT NOT NULL,
	`timestamp` bigint NOT NULL,
	`balance` FLOAT NOT NULL,
	`account_number` bigint NOT NULL,
	`user_id` bigint NOT NULL,
	`transaction_account` bigint,
	`creation_time` BIGINT NOT NULL,
	`modified_by` bigint NOT NULL,
	`type` varchar(10) NOT NULL,
	PRIMARY KEY (`reference_number`, `account_number`),
	FOREIGN KEY (`account_number`) REFERENCES `account`(`account_number`) ON DELETE CASCADE,
	FOREIGN KEY (`user_id`) REFERENCES `user`(`user_id`) ON DELETE CASCADE
);

DELIMITER $$

CREATE TRIGGER after_customer_delete
AFTER DELETE ON customer
FOR EACH ROW
BEGIN
    -- Delete from user table where user_id matches the one being deleted
    DELETE FROM user WHERE user_id = OLD.customer_id;
END $$

CREATE TRIGGER after_employee_delete
AFTER DELETE ON employee
FOR EACH ROW
BEGIN
    -- Delete from user table where user_id matches the one being deleted
    DELETE FROM user WHERE user_id = OLD.employee_id;
END $$

DELIMITER ;

DELIMITER $$

CREATE TRIGGER update_modified_time_user
AFTER UPDATE ON user
FOR EACH ROW
BEGIN
    UPDATE user
    SET modified_time = UNIX_TIMESTAMP() * 1000
    WHERE user_id = NEW.user_id;
END $$

DELIMITER ;

DELIMITER $$

CREATE TRIGGER update_modified_time_customer
AFTER UPDATE ON customer
FOR EACH ROW
BEGIN
    UPDATE user
    SET modified_time = UNIX_TIMESTAMP() * 1000
    WHERE user_id = NEW.customer_id;
END $$

CREATE TRIGGER update_modified_time_employee
AFTER UPDATE ON employee
FOR EACH ROW
BEGIN
    UPDATE user
    SET modified_time = UNIX_TIMESTAMP() * 1000
    WHERE user_id = NEW.employee_id;
END $$

CREATE TRIGGER update_modified_time_branch
AFTER UPDATE ON branch
FOR EACH ROW
BEGIN
    UPDATE user
    SET modified_time = UNIX_TIMESTAMP() * 1000
    WHERE branch_id = NEW.branch_id;
END $$

CREATE TRIGGER update_modified_time_account
AFTER UPDATE ON account
FOR EACH ROW
BEGIN
    UPDATE user
    SET modified_time = UNIX_TIMESTAMP() * 1000
    WHERE account_number = NEW.account_number;
END $$

DELIMITER ;

DELIMITER $$

CREATE TRIGGER set_creation_time_user
BEFORE INSERT ON user
FOR EACH ROW
BEGIN
    IF NEW.creation_time IS NULL THEN
        SET NEW.creation_time = UNIX_TIMESTAMP() * 1000;
    END IF;
END $$

DELIMITER ;

DELIMITER $$

CREATE TRIGGER set_creation_time_branch
BEFORE INSERT ON branch
FOR EACH ROW
BEGIN
    IF NEW.creation_time IS NULL THEN
        SET NEW.creation_time = UNIX_TIMESTAMP() * 1000;
    END IF;
END $$

CREATE TRIGGER set_creation_time_account
BEFORE INSERT ON account
FOR EACH ROW
BEGIN
    IF NEW.creation_time IS NULL THEN
        SET NEW.creation_time = UNIX_TIMESTAMP() * 1000;
    END IF;
END $$

CREATE TRIGGER set_creation_time_transaction
BEFORE INSERT ON transaction
FOR EACH ROW
BEGIN
    IF NEW.creation_time IS NULL THEN
        SET NEW.creation_time = UNIX_TIMESTAMP() * 1000;
    END IF;
END $$

DELIMITER ;
