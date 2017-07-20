ALTER TABLE user ADD last_updated_by VARCHAR(255);
ALTER TABLE user ADD last_updated_date DATETIME;
ALTER TABLE user ADD created_by VARCHAR(255);
ALTER TABLE user ADD created_date DATETIME;

ALTER TABLE user_aud ADD last_updated_by VARCHAR(255);
ALTER TABLE user_aud ADD last_updated_date DATETIME;
ALTER TABLE user_aud ADD created_by VARCHAR(255);
ALTER TABLE user_aud ADD created_date DATETIME;
