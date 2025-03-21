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


-- 16 march
ALTER TABLE vehicles
    ADD COLUMN description TEXT;    

UPDATE vehicles SET description = 'Spacious sedan with excellent fuel efficiency.' WHERE id=1;
UPDATE vehicles SET description = 'Compact and reliable, perfect for city driving.' WHERE id=2;
UPDATE vehicles SET description = 'Powerful muscle car with a bold design.' WHERE id=3;
UPDATE vehicles SET description = 'Affordable hatchback with modern features.' WHERE id=4;

ALTER TABLE purchases
    ADD COLUMN payment_method ENUM('CREDIT_CARD', 'PAYPAL', 'DEBIT_CARD') NOT NULL,
    ADD COLUMN amount DECIMAL(10,2) NOT NULL,
    ADD COLUMN transaction_id VARCHAR(100),
    ADD COLUMN status ENUM('PENDING', 'COMPLETED', 'FAILED') DEFAULT 'PENDING';

-- 19 March
ALTER TABLE rentals
	ADD COLUMN status ENUM('RETURNED','RENTED');

UPDATE rentals SET status = 'RENTED' where id = 1;

-- 21 March
CREATE TABLE listings (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    vehicle_id INT, -- Will be populated after inserting into vehicles table
    name VARCHAR(100) NOT NULL, -- Car name (e.g., "Toyota Camry")
    year INT NOT NULL, -- Model year (e.g., 2020)
    price DECIMAL(10, 2), -- Sale price (NULL if for rent only)
    rent_rate DECIMAL(10, 2), -- Daily rental rate (NULL if for sale only)
    listing_type ENUM('SALE', 'RENT') NOT NULL, -- Type of listing
    status ENUM('ACTIVE', 'RENTED', 'SOLD', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE', -- Listing status
    description TEXT, -- Detailed description of the car
    picture VARCHAR(255), -- File path or URL to car image (e.g., "toyota_camry_2020.jpg")
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP, -- When the listing was created
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- Last updated
    location VARCHAR(100), -- City or area (e.g., "New York, NY")
    mileage INT, -- Mileage in kilometers or miles
    fuel_type ENUM('PETROL', 'DIESEL', 'ELECTRIC', 'HYBRID'), -- Fuel type
    transmission ENUM('MANUAL', 'AUTOMATIC'), -- Transmission type
    available_from DATETIME, -- Availability start date (for rentals)
    available_until DATETIME, -- Availability end date (for rentals)
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE, -- Link to user who posted
    FOREIGN KEY (vehicle_id) REFERENCES vehicles(id) ON DELETE SET NULL -- Link to vehicles table
);



select * from purchases;
use motorverse;
select * from rentals

