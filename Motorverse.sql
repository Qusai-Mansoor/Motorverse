CREATE DATABASE motorverse;
USE motorverse;

CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    date_of_birth DATE NOT NULL,
    phone_number VARCHAR(15),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE vehicles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    year INT NOT NULL,
    price DECIMAL(10,2),
    rent_rate DECIMAL(10,2),
    status ENUM('AVAILABLE', 'SOLD', 'RENTED') DEFAULT 'AVAILABLE'
);

CREATE TABLE purchases (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    vehicle_id INT,
    purchase_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (vehicle_id) REFERENCES vehicles(id)
);

CREATE TABLE rentals (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    vehicle_id INT,
    start_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    end_date DATETIME,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (vehicle_id) REFERENCES vehicles(id)
);

-- Sample Data
INSERT INTO users (email, password, first_name, last_name, date_of_birth, phone_number) VALUES
('user1@example.com', 'pass123', 'Ahmed', 'Ali', '1995-03-15', '+923001234567'),
('user2@example.com', 'pass456', 'Qusai', 'Mansoor', '1996-07-22', '+923009876543'),
('ahmad@example.com', 'pass789', 'Ahmad', 'Khan', '1994-11-10', '+923007654321'),
('sara@example.com', 'pass101', 'Sara', 'Malik', '1998-02-28', '+923001112233'),
('hassan@example.com', 'pass202', 'Hassan', 'Raza', '1993-09-05', '+923004445566');

INSERT INTO vehicles (name, year, price, rent_rate, status) VALUES
('Toyota Camry', 2020, 25000.00, 50.00, 'AVAILABLE'),
('Honda Civic', 2019, 20000.00, 40.00, 'AVAILABLE'),
('Ford Mustang', 2021, 35000.00, 70.00, 'AVAILABLE'),
('Suzuki Swift', 2020, 18000.00, 35.00, 'AVAILABLE');

ALTER TABLE vehicles
    ADD COLUMN picture VARCHAR(255) default 'Camry_Pic.jpg';

ALTER TABLE vehicles
    ADD COLUMN description TEXT;    

UPDATE vehicles SET description = 'Spacious sedan with excellent fuel efficiency.' WHERE id=1;
UPDATE vehicles SET description = 'Compact and reliable, perfect for city driving.' WHERE id=2;
UPDATE vehicles SET description = 'Powerful muscle car with a bold design.' WHERE id=3;
UPDATE vehicles SET description = 'Affordable hatchback with modern features.' WHERE id=4;

select * from users;
select * from vehicles

