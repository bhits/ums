-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
-- -----------------------------------------------------
-- Schema ums
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema ums
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `ums` DEFAULT CHARACTER SET utf8 ;
USE `ums` ;

-- -----------------------------------------------------
-- Table `state_code`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `state_code` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `code` VARCHAR(255) NOT NULL,
  `code_system` VARCHAR(255) NULL DEFAULT NULL,
  `code_system_name` VARCHAR(255) NOT NULL,
  `code_systemoid` VARCHAR(255) NULL DEFAULT NULL,
  `description` VARCHAR(255) NULL DEFAULT NULL,
  `display_name` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `country_code`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `country_code` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `code` VARCHAR(255) NOT NULL,
  `code_system` VARCHAR(255) NULL DEFAULT NULL,
  `code_system_name` VARCHAR(255) NOT NULL,
  `code_systemoid` VARCHAR(255) NULL DEFAULT NULL,
  `description` VARCHAR(255) NULL DEFAULT NULL,
  `display_name` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `address`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `address` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `city` VARCHAR(30) NOT NULL,
  `postal_code` VARCHAR(255) NOT NULL,
  `street_address_line` VARCHAR(50) NOT NULL,
  `country_code_id` BIGINT(20) NOT NULL,
  `state_code_id` BIGINT(20) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `FK2apbqk15r1fk1ce5c3dfs1ndn` (`country_code_id` ASC),
  INDEX `FKbqj2j4pyht7nxbxqdwcr9ei87` (`state_code_id` ASC),
  CONSTRAINT `FKbqj2j4pyht7nxbxqdwcr9ei87`
    FOREIGN KEY (`state_code_id`)
    REFERENCES `state_code` (`id`),
  CONSTRAINT `FK2apbqk15r1fk1ce5c3dfs1ndn`
    FOREIGN KEY (`country_code_id`)
    REFERENCES `country_code` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `revinfo`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `revinfo` (
  `rev` INT(11) NOT NULL AUTO_INCREMENT,
  `revtstmp` BIGINT(20) NULL DEFAULT NULL,
  PRIMARY KEY (`rev`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `address_aud`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `address_aud` (
  `id` BIGINT(20) NOT NULL,
  `rev` INT(11) NOT NULL,
  `revtype` TINYINT(4) NULL DEFAULT NULL,
  `city` VARCHAR(255) NULL DEFAULT NULL,
  `postal_code` VARCHAR(255) NULL DEFAULT NULL,
  `street_address_line` VARCHAR(255) NULL DEFAULT NULL,
  `country_code_id` BIGINT(20) NULL DEFAULT NULL,
  `state_code_id` BIGINT(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`, `rev`),
  INDEX `FKcc7vlgg86eqe1dmvivbkv046v` (`rev` ASC),
  CONSTRAINT `FKcc7vlgg86eqe1dmvivbkv046v`
    FOREIGN KEY (`rev`)
    REFERENCES `revinfo` (`rev`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `locale`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `locale` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `code` VARCHAR(255) NOT NULL,
  `code_system` VARCHAR(255) NULL DEFAULT NULL,
  `code_system_name` VARCHAR(255) NOT NULL,
  `code_systemoid` VARCHAR(255) NULL DEFAULT NULL,
  `description` VARCHAR(255) NULL DEFAULT NULL,
  `display_name` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `administrative_gender_code`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `administrative_gender_code` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `code` VARCHAR(255) NOT NULL,
  `code_system` VARCHAR(255) NULL DEFAULT NULL,
  `code_system_name` VARCHAR(255) NOT NULL,
  `code_systemoid` VARCHAR(255) NULL DEFAULT NULL,
  `description` VARCHAR(255) NULL DEFAULT NULL,
  `display_name` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `user`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `user` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `birth_day` DATETIME NULL DEFAULT NULL,
  `first_name` VARCHAR(30) NOT NULL,
  `is_disabled` BIT(1) NOT NULL,
  `last_name` VARCHAR(30) NOT NULL,
  `oauth2user_id` VARCHAR(255) NULL DEFAULT NULL,
  `social_security_number` VARCHAR(255) NULL DEFAULT NULL,
  `address_id` BIGINT(20) NULL DEFAULT NULL,
  `administrative_gender_code_id` BIGINT(20) NULL DEFAULT NULL,
  `locale_id` BIGINT(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `FKddefmvbrws3hvl5t0hnnsv8ox` (`address_id` ASC),
  INDEX `FK7d89j3j1pmvbfbxafigy2mlkb` (`administrative_gender_code_id` ASC),
  INDEX `FKfbdc5480okukpuvd43xidfahh` (`locale_id` ASC),
  CONSTRAINT `FKfbdc5480okukpuvd43xidfahh`
    FOREIGN KEY (`locale_id`)
    REFERENCES `locale` (`id`),
  CONSTRAINT `FK7d89j3j1pmvbfbxafigy2mlkb`
    FOREIGN KEY (`administrative_gender_code_id`)
    REFERENCES `administrative_gender_code` (`id`),
  CONSTRAINT `FKddefmvbrws3hvl5t0hnnsv8ox`
    FOREIGN KEY (`address_id`)
    REFERENCES `address` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `address_users`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `address_users` (
  `address_id` BIGINT(20) NOT NULL,
  `users_id` BIGINT(20) NOT NULL,
  UNIQUE INDEX `UK_moh08f2ymndlthxp7rxvokm16` (`users_id` ASC),
  INDEX `FK4a8j1hau5xcc6oi2561e4lw63` (`address_id` ASC),
  CONSTRAINT `FK4a8j1hau5xcc6oi2561e4lw63`
    FOREIGN KEY (`address_id`)
    REFERENCES `address` (`id`),
  CONSTRAINT `FKqgtytujw3se5lgom2i9ppp8rd`
    FOREIGN KEY (`users_id`)
    REFERENCES `user` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `patient`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `patient` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `mrn` VARCHAR(255) NULL DEFAULT NULL,
  `user_id` BIGINT(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `FKp6ttmfrxo2ejiunew4ov805uc` (`user_id` ASC),
  CONSTRAINT `FKp6ttmfrxo2ejiunew4ov805uc`
    FOREIGN KEY (`user_id`)
    REFERENCES `user` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `relationship`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `relationship` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `code` VARCHAR(255) NOT NULL,
  `name` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `role`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `role` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `code` VARCHAR(255) NOT NULL,
  `name` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `role_aud`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `role_aud` (
  `id` BIGINT(20) NOT NULL,
  `rev` INT(11) NOT NULL,
  `revtype` TINYINT(4) NULL DEFAULT NULL,
  `code` VARCHAR(255) NULL DEFAULT NULL,
  `name` VARCHAR(255) NULL DEFAULT NULL,
  PRIMARY KEY (`id`, `rev`),
  INDEX `FKrks7qtsmup3w81fdp0d6omfk7` (`rev` ASC),
  CONSTRAINT `FKrks7qtsmup3w81fdp0d6omfk7`
    FOREIGN KEY (`rev`)
    REFERENCES `revinfo` (`rev`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `scope`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `scope` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `scope_description` VARCHAR(255) NOT NULL,
  `scope_name` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `role_scopes`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `role_scopes` (
  `roles_id` BIGINT(20) NOT NULL,
  `scopes_id` BIGINT(20) NOT NULL,
  PRIMARY KEY (`roles_id`, `scopes_id`),
  INDEX `FK7qfwaml7g6mtnl6w4xm9be0hp` (`scopes_id` ASC),
  CONSTRAINT `FKj4benp61mf5m7wnast2llih1`
    FOREIGN KEY (`roles_id`)
    REFERENCES `role` (`id`),
  CONSTRAINT `FK7qfwaml7g6mtnl6w4xm9be0hp`
    FOREIGN KEY (`scopes_id`)
    REFERENCES `scope` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `role_scopes_aud`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `role_scopes_aud` (
  `rev` INT(11) NOT NULL,
  `roles_id` BIGINT(20) NOT NULL,
  `scopes_id` BIGINT(20) NOT NULL,
  `revtype` TINYINT(4) NULL DEFAULT NULL,
  PRIMARY KEY (`rev`, `roles_id`, `scopes_id`),
  CONSTRAINT `FKhi78gervneyp2v5aw2ky5l0p6`
    FOREIGN KEY (`rev`)
    REFERENCES `revinfo` (`rev`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `scope_aud`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `scope_aud` (
  `id` BIGINT(20) NOT NULL,
  `rev` INT(11) NOT NULL,
  `revtype` TINYINT(4) NULL DEFAULT NULL,
  `scope_description` VARCHAR(255) NULL DEFAULT NULL,
  `scope_name` VARCHAR(255) NULL DEFAULT NULL,
  PRIMARY KEY (`id`, `rev`),
  INDEX `FKqvi4bxfyyv068d1x8gff1gsid` (`rev` ASC),
  CONSTRAINT `FKqvi4bxfyyv068d1x8gff1gsid`
    FOREIGN KEY (`rev`)
    REFERENCES `revinfo` (`rev`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `telecom`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `telecom` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `system` VARCHAR(30) NOT NULL,
  `value` VARCHAR(30) NOT NULL,
  `user_id` BIGINT(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `FKtjrtnyppa9bm7x0sy3bp77w5n` (`user_id` ASC),
  CONSTRAINT `FKtjrtnyppa9bm7x0sy3bp77w5n`
    FOREIGN KEY (`user_id`)
    REFERENCES `user` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `telecom_aud`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `telecom_aud` (
  `id` BIGINT(20) NOT NULL,
  `rev` INT(11) NOT NULL,
  `revtype` TINYINT(4) NULL DEFAULT NULL,
  `system` VARCHAR(255) NULL DEFAULT NULL,
  `value` VARCHAR(255) NULL DEFAULT NULL,
  `user_id` BIGINT(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`, `rev`),
  INDEX `FKix3c360mmqvddy76o1m46ihar` (`rev` ASC),
  CONSTRAINT `FKix3c360mmqvddy76o1m46ihar`
    FOREIGN KEY (`rev`)
    REFERENCES `revinfo` (`rev`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `user_activation`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `user_activation` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `email_token` VARCHAR(255) NOT NULL,
  `email_token_expiration` DATETIME NOT NULL,
  `is_verified` BIT(1) NOT NULL,
  `verification_code` VARCHAR(255) NOT NULL,
  `user_id` BIGINT(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `email_token_idx` (`email_token` ASC),
  INDEX `FKidgebcgp5cij94pf1tup1f62p` (`user_id` ASC),
  CONSTRAINT `FKidgebcgp5cij94pf1tup1f62p`
    FOREIGN KEY (`user_id`)
    REFERENCES `user` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `user_activation_aud`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `user_activation_aud` (
  `id` BIGINT(20) NOT NULL,
  `rev` INT(11) NOT NULL,
  `revtype` TINYINT(4) NULL DEFAULT NULL,
  `email_token` VARCHAR(255) NULL DEFAULT NULL,
  `email_token_expiration` DATETIME NULL DEFAULT NULL,
  `is_verified` BIT(1) NULL DEFAULT NULL,
  `verification_code` VARCHAR(255) NULL DEFAULT NULL,
  `user_id` BIGINT(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`, `rev`),
  INDEX `FK9re0c59t3i31p2ujugy4tujpk` (`rev` ASC),
  CONSTRAINT `FK9re0c59t3i31p2ujugy4tujpk`
    FOREIGN KEY (`rev`)
    REFERENCES `revinfo` (`rev`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `user_aud`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `user_aud` (
  `id` BIGINT(20) NOT NULL,
  `rev` INT(11) NOT NULL,
  `revtype` TINYINT(4) NULL DEFAULT NULL,
  `address_id` BIGINT(20) NULL DEFAULT NULL,
  `administrative_gender_code_id` BIGINT(20) NULL DEFAULT NULL,
  `locale_id` BIGINT(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`, `rev`),
  INDEX `FK89ntto9kobwahrwxbne2nqcnr` (`rev` ASC),
  CONSTRAINT `FK89ntto9kobwahrwxbne2nqcnr`
    FOREIGN KEY (`rev`)
    REFERENCES `revinfo` (`rev`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `user_patient_relationship`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `user_patient_relationship` (
  `user_id` BIGINT(20) NOT NULL,
  `relationship_id` BIGINT(20) NOT NULL,
  `patient_id` BIGINT(20) NOT NULL,
  PRIMARY KEY (`patient_id`, `relationship_id`, `user_id`),
  INDEX `FKcueft195i7ut2u6mddio89uvc` (`user_id` ASC),
  INDEX `FKk1yggc721hn2263rr543bm4k1` (`relationship_id` ASC),
  CONSTRAINT `FKg70k723dfb4benj3lv2rjhp76`
    FOREIGN KEY (`patient_id`)
    REFERENCES `patient` (`id`),
  CONSTRAINT `FKcueft195i7ut2u6mddio89uvc`
    FOREIGN KEY (`user_id`)
    REFERENCES `user` (`id`),
  CONSTRAINT `FKk1yggc721hn2263rr543bm4k1`
    FOREIGN KEY (`relationship_id`)
    REFERENCES `relationship` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `user_roles`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `user_roles` (
  `users_id` BIGINT(20) NOT NULL,
  `roles_id` BIGINT(20) NOT NULL,
  PRIMARY KEY (`users_id`, `roles_id`),
  INDEX `FKj9553ass9uctjrmh0gkqsmv0d` (`roles_id` ASC),
  CONSTRAINT `FK7ecyobaa59vxkxckg6t355l86`
    FOREIGN KEY (`users_id`)
    REFERENCES `user` (`id`),
  CONSTRAINT `FKj9553ass9uctjrmh0gkqsmv0d`
    FOREIGN KEY (`roles_id`)
    REFERENCES `role` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `user_scope_assignment`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `user_scope_assignment` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `assigned` BIT(1) NOT NULL,
  `scope_id` BIGINT(20) NULL DEFAULT NULL,
  `user_activation_id` BIGINT(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `UK89m6kbqn388uowpe9ph77ruwt` (`user_activation_id` ASC, `scope_id` ASC),
  INDEX `FKmvcfov9puvk2higv7h54wbyii` (`scope_id` ASC),
  CONSTRAINT `FKtldupmh35x5ragy1g4x2wcj84`
    FOREIGN KEY (`user_activation_id`)
    REFERENCES `user_activation` (`id`),
  CONSTRAINT `FKmvcfov9puvk2higv7h54wbyii`
    FOREIGN KEY (`scope_id`)
    REFERENCES `scope` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `user_scope_assignment_aud`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `user_scope_assignment_aud` (
  `id` BIGINT(20) NOT NULL,
  `rev` INT(11) NOT NULL,
  `revtype` TINYINT(4) NULL DEFAULT NULL,
  `assigned` BIT(1) NULL DEFAULT NULL,
  `scope_id` BIGINT(20) NULL DEFAULT NULL,
  `user_activation_id` BIGINT(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`, `rev`),
  INDEX `FKrgqa5ttc70311wm8a2g5x9p1i` (`rev` ASC),
  CONSTRAINT `FKrgqa5ttc70311wm8a2g5x9p1i`
    FOREIGN KEY (`rev`)
    REFERENCES `revinfo` (`rev`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
