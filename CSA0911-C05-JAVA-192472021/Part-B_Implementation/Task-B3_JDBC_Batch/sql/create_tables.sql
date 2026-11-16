-- ============================================================
-- Programming in Java - Assignment 5 (CO5)
-- Table Creation Script for Transport Office System
-- Student: K VENKATA VAMSI GOUD | Reg No: 192472021 | Course: CSA09
-- ============================================================

USE transport_db;

DROP TABLE IF EXISTS bus_pass;

CREATE TABLE bus_pass (
    pass_id VARCHAR(30) PRIMARY KEY,
    reg_no VARCHAR(30) NOT NULL,
    name VARCHAR(100) NOT NULL,
    route_no INT NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    boarding_stop VARCHAR(100) DEFAULT 'Main Campus Gate',
    validity_date DATE DEFAULT '2026-12-31',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Indexing for performance optimization on route lookups
CREATE INDEX idx_bus_pass_route ON bus_pass(route_no);

SELECT 'Table bus_pass created with primary key and route index.' AS Status;
