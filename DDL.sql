DROP TABLE user_login;
DROP TABLE role;
DROP TABLE user;
DROP TABLE state;

CREATE TABLE state (
  code char(2) NOT NULL,
  name varchar(14) NOT NULL UNIQUE,
  PRIMARY KEY (code)
);

INSERT INTO state VALUES ('AL','Alabama');
INSERT INTO state VALUES ('AK','Alaska');
INSERT INTO state VALUES ('AZ','Arizona');
INSERT INTO state VALUES ('AR','Arkansas');
INSERT INTO state VALUES ('CA','California');
INSERT INTO state VALUES ('CO','Colorado');
INSERT INTO state VALUES ('CT','Connecticut');
INSERT INTO state VALUES ('DE','Delaware');
INSERT INTO state VALUES ('FL','Florida');
INSERT INTO state VALUES ('GA','Georgia');
INSERT INTO state VALUES ('HI','Hawaii');
INSERT INTO state VALUES ('ID','Idaho');
INSERT INTO state VALUES ('IL','Illinois');
INSERT INTO state VALUES ('IN','Indiana');
INSERT INTO state VALUES ('IA','Iowa');
INSERT INTO state VALUES ('KS','Kansas');
INSERT INTO state VALUES ('KY','Kentucky');
INSERT INTO state VALUES ('LA','Louisiana');
INSERT INTO state VALUES ('ME','Maine');
INSERT INTO state VALUES ('MD','Maryland');
INSERT INTO state VALUES ('MA','Massachusetts');
INSERT INTO state VALUES ('MI','Michigan');
INSERT INTO state VALUES ('MN','Minnesota');
INSERT INTO state VALUES ('MS','Mississippi');
INSERT INTO state VALUES ('MO','Missouri');
INSERT INTO state VALUES ('MT','Montana');
INSERT INTO state VALUES ('NE','Nebraska');
INSERT INTO state VALUES ('NV','Nevada');
INSERT INTO state VALUES ('NH','New Hampshire');
INSERT INTO state VALUES ('NJ','New Jersey');
INSERT INTO state VALUES ('NM','New Mexico');
INSERT INTO state VALUES ('NY','New York');
INSERT INTO state VALUES ('NC','North Carolina');
INSERT INTO state VALUES ('ND','North Dakota');
INSERT INTO state VALUES ('OH','Ohio');
INSERT INTO state VALUES ('OK','Oklahoma');
INSERT INTO state VALUES ('OR','Oregon');
INSERT INTO state VALUES ('PA','Pennsylvania');
INSERT INTO state VALUES ('RI','Rhode Island');
INSERT INTO state VALUES ('SC','South Carolina');
INSERT INTO state VALUES ('SD','South Dakota');
INSERT INTO state VALUES ('TN','Tennessee');
INSERT INTO state VALUES ('TX','Texas');
INSERT INTO state VALUES ('UT','Utah');
INSERT INTO state VALUES ('VT','Vermont');
INSERT INTO state VALUES ('VA','Virginia');
INSERT INTO state VALUES ('WA','Washington');
INSERT INTO state VALUES ('WV','West Virginia');
INSERT INTO state VALUES ('WI','Wisconsin');
INSERT INTO state VALUES ('WY','Wyoming');

CREATE TABLE user (
	user_id INT AUTO_INCREMENT PRIMARY KEY,
	first_name varchar(20) NOT NULL,
	middle_name varchar(20), -- optional
	last_name varchar(20) NOT NULL,
	address varchar(20) NOT NULL,
	apt_suite_other varchar(20), -- optional
	city varchar(20) NOT NULL,
	state_code char(2) NOT NULL,
	zip_code varchar(10) NOT NULL,
	phone_number char(10) NOT NULL,
	email_address varchar(50), NOT NULL,
	birth_date DATE NOT NULL,
	FOREIGN KEY (state_code) REFERENCES state(code)
);

create table role (
	role_id INT PRIMARY KEY,
	description varchar(30)
);

INSERT INTO role VALUES (1, 'ADMIN');
INSERT INTO role VALUES (2, 'USER');

CREATE TABLE user_login (
	user_id INT PRIMARY KEY,
	username varchar(20) NOT NULL UNIQUE,
	password varchar(60) NOT NULL,
	role_id INT,
	FOREIGN KEY (role_id) REFERENCES role(role_id),
	FOREIGN KEY (user_id) REFERENCES user(user_id)
);

INSERT INTO user (first_name, last_name, address, city, state_code, zip_code, phone_number, email_address, birth_date) 
	VALUES ('root', 'root', '', '', 'AL', '00000', '0000000000', '', CURDATE());

INSERT INTO user_login VALUES (1, 'root', '$2a$10$at0n0lfN9he7dKT8DHOL2ecaVTZXGmO4oBuBd1d.ZGGUE/JdP11gu', 1);
	
INSERT INTO user (first_name, last_name, address, city, state_code, zip_code, phone_number, email_address, birth_date) 
	VALUES ('John', 'Smith', '1 Main st.', 'a city', 'AL', '11111', '1111111111', 'an email', CURDATE());
	
INSERT INTO user_login VALUES (2, 'JSmith', '$2a$10$Am1WcWPQd1N5PBDfQDrPv.51emh/SiBE5lUmQheqHiEZmTXiwIo0S', 2);
	
INSERT INTO user (first_name, last_name, address, city, state_code, zip_code, phone_number, email_address, birth_date) 
	VALUES ('Bruce', 'Wayne', '1 Main st.', 'Gotham', 'NY', '00000', '0000000000', 'bat@batman.com', CURDATE());
	
INSERT INTO user_login VALUES (3, 'Batman', '$2a$10$Am1WcWPQd1N5PBDfQDrPv.51emh/SiBE5lUmQheqHiEZmTXiwIo0S', 2);

INSERT INTO user (first_name, last_name, address, city, state_code, zip_code, phone_number, email_address, birth_date) 
	VALUES ('Clark', 'Kent', '1 Main St.', 'Smallville', 'KS', '00000', '0000000000', 'super@superman.com', CURDATE());
	
INSERT INTO user_login VALUES (4, 'Superman', '$2a$10$Am1WcWPQd1N5PBDfQDrPv.51emh/SiBE5lUmQheqHiEZmTXiwIo0S', 2);
