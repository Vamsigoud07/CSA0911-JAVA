-- ============================================================
-- Programming in Java - Assignment 5 (CO5)
-- Sample Seed Data for Bus Pass Ingestion Verification
-- Student: K VENKATA VAMSI GOUD | Reg No: 192472021 | Course: CSA09
-- ============================================================

USE transport_db;

INSERT INTO bus_pass (pass_id, reg_no, name, route_no, amount, boarding_stop, validity_date) VALUES
('P1001', '192472001', 'Aarav Kumar', 5, 4500.00, 'Tambaram Bus Stand', '2026-12-31'),
('P1002', '192472002', 'Ananya Sharma', 12, 5200.00, 'Guindy Metro Station', '2026-12-31'),
('P1003', '192472003', 'Karthik Raja', 7, 3800.00, 'Poonamallee Bypass', '2026-12-31'),
('P1004', '192472004', 'Deepa Venkat', 14, 4800.00, 'Porur Junction', '2026-12-31'),
('P1005', '192472005', 'Suresh Babu', 3, 3200.00, 'Koyambedu CMBT', '2026-12-31'),
('P1006', '192472006', 'Pooja Iyer', 9, 4100.00, 'Avadi Bus Terminus', '2026-12-31'),
('P1007', '192472007', 'Vamsi Goud', 15, 4500.00, 'Thiruvanmiyur Depot', '2026-12-31'),
('P1008', '192472008', 'Manoj Reddy', 7, 3800.00, 'Maduravoyal Flyover', '2026-12-31'),
('P1009', '192472009', 'Sneha Patel', 18, 5500.00, 'Central Railway Station', '2026-12-31'),
('P1010', '192472010', 'Rahul Verma', 5, 4500.00, 'Chromepet Signal', '2026-12-31');

SELECT COUNT(*) AS total_sample_records FROM bus_pass;
