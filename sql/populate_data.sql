-- Health Track Personal Wellness Platform
-- Sample Data Population Script
-- Group 27

USE healthtrack;

-- Clear existing data (in reverse dependency order)
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE MONTHLY_SUMMARY;
TRUNCATE TABLE GROUP_MEMBER;
TRUNCATE TABLE FAMILY_GROUP;
TRUNCATE TABLE INVITATION;
TRUNCATE TABLE CHALLENGE_PARTICIPANT;
TRUNCATE TABLE CHALLENGE;
TRUNCATE TABLE APPOINTMENT;
TRUNCATE TABLE ACTION;
TRUNCATE TABLE USER_PROVIDER;
TRUNCATE TABLE PHONE;
TRUNCATE TABLE EMAIL;
TRUNCATE TABLE PROVIDER;
TRUNCATE TABLE USER;
SET FOREIGN_KEY_CHECKS = 1;

-- 1. Insert USERS
INSERT INTO USER (user_id, health_id, full_name, account_status, created_at) VALUES
(1, 'HT001', 'Zhang Wei', 'Active', '2024-01-15 10:00:00'),
(2, 'HT002', 'Li Mei', 'Active', '2024-01-20 14:30:00'),
(3, 'HT003', 'Wang Gang', 'Active', '2024-02-01 09:15:00'),
(4, 'HT004', 'Liu Fang', 'Active', '2024-02-10 16:45:00'),
(5, 'HT005', 'Chen Ming', 'Active', '2024-02-15 11:20:00'),
(6, 'HT006', 'Zhao Li', 'Active', '2024-03-01 13:00:00'),
(7, 'HT007', 'Sun Lei', 'Active', '2024-03-05 10:30:00'),
(8, 'HT008', 'Wu Jing', 'Inactive', '2024-03-10 15:00:00'),
(9, 'HT009', 'Xu Hui', 'Active', '2024-03-15 09:00:00'),
(10, 'HT010', 'Ma Lin', 'Active', '2024-03-20 14:00:00');

-- 2. Insert EMAILS (multiple emails per user)
INSERT INTO EMAIL (email, user_id, is_verified, verified_at, created_at) VALUES
('zhangwei@email.com', 1, TRUE, '2024-01-15 10:05:00', '2024-01-15 10:00:00'),
('zhang.wei.work@email.com', 1, TRUE, '2024-01-16 09:00:00', '2024-01-16 08:55:00'),
('limei@email.com', 2, TRUE, '2024-01-20 14:35:00', '2024-01-20 14:30:00'),
('li.mei.personal@email.com', 2, FALSE, NULL, '2024-01-25 10:00:00'),
('wanggang@email.com', 3, TRUE, '2024-02-01 09:20:00', '2024-02-01 09:15:00'),
('liufang@email.com', 4, TRUE, '2024-02-10 16:50:00', '2024-02-10 16:45:00'),
('chenming@email.com', 5, TRUE, '2024-02-15 11:25:00', '2024-02-15 11:20:00'),
('zhaoli@email.com', 6, TRUE, '2024-03-01 13:05:00', '2024-03-01 13:00:00'),
('sunlei@email.com', 7, TRUE, '2024-03-05 10:35:00', '2024-03-05 10:30:00'),
('wujing@email.com', 8, FALSE, NULL, '2024-03-10 15:00:00'),
('xuhui@email.com', 9, TRUE, '2024-03-15 09:05:00', '2024-03-15 09:00:00'),
('malin@email.com', 10, TRUE, '2024-03-20 14:05:00', '2024-03-20 14:00:00');

-- 3. Insert PHONES (0 or 1 per user)
INSERT INTO PHONE (phone_number, user_id, is_verified, verified_at, created_at) VALUES
('13800138001', 1, TRUE, '2024-01-15 10:10:00', '2024-01-15 10:05:00'),
('13800138002', 2, TRUE, '2024-01-20 14:40:00', '2024-01-20 14:35:00'),
('13800138003', 3, TRUE, '2024-02-01 09:25:00', '2024-02-01 09:20:00'),
('13800138004', 4, TRUE, '2024-02-10 16:55:00', '2024-02-10 16:50:00'),
('13800138005', 5, TRUE, '2024-02-15 11:30:00', '2024-02-15 11:25:00'),
('13800138006', 6, TRUE, '2024-03-01 13:10:00', '2024-03-01 13:05:00'),
('13800138007', 7, TRUE, '2024-03-05 10:40:00', '2024-03-05 10:35:00'),
('13800138008', 8, FALSE, NULL, '2024-03-10 15:05:00'),
('13800138009', 9, TRUE, '2024-03-15 09:10:00', '2024-03-15 09:05:00');
-- User 10 has no phone number

-- 4. Insert PROVIDERS
INSERT INTO PROVIDER (provider_id, license_no, provider_name, is_verified, verified_at, created_at) VALUES
(1, 'MD001', 'Dr. Zhang San', TRUE, '2024-01-01 10:00:00', '2024-01-01 09:00:00'),
(2, 'MD002', 'Dr. Li Si', TRUE, '2024-01-02 11:00:00', '2024-01-02 10:00:00'),
(3, 'MD003', 'Dr. Wang Wu', TRUE, '2024-01-03 12:00:00', '2024-01-03 11:00:00'),
(4, 'MD004', 'Dr. Zhao Liu', TRUE, '2024-01-04 13:00:00', '2024-01-04 12:00:00'),
(5, 'MD005', 'Dr. Sun Qi', FALSE, NULL, '2024-01-05 14:00:00'),
(6, 'MD006', 'Dr. Zhou Ba', TRUE, '2024-01-06 15:00:00', '2024-01-06 14:00:00'),
(7, 'MD007', 'Dr. Wu Jiu', TRUE, '2024-01-07 16:00:00', '2024-01-07 15:00:00'),
(8, 'MD008', 'Dr. Zheng Shi', TRUE, '2024-01-08 17:00:00', '2024-01-08 16:00:00');

-- 5. Insert USER_PROVIDER relationships
INSERT INTO USER_PROVIDER (user_provider_id, user_id, provider_id, is_primary, link_status, linked_at, unlinked_at) VALUES
(1, 1, 1, TRUE, 'Active', '2024-01-20 10:00:00', NULL),
(2, 1, 2, FALSE, 'Active', '2024-01-25 10:00:00', NULL),
(3, 2, 1, TRUE, 'Active', '2024-02-01 10:00:00', NULL),
(4, 3, 3, TRUE, 'Active', '2024-02-05 10:00:00', NULL),
(5, 4, 4, TRUE, 'Active', '2024-02-12 10:00:00', NULL),
(6, 5, 5, TRUE, 'Active', '2024-02-18 10:00:00', NULL),
(7, 6, 6, TRUE, 'Active', '2024-03-05 10:00:00', NULL),
(8, 7, 7, TRUE, 'Active', '2024-03-08 10:00:00', NULL),
(9, 8, 8, TRUE, 'Active', '2024-03-12 10:00:00', NULL),
(10, 9, 1, TRUE, 'Active', '2024-03-18 10:00:00', NULL),
(11, 10, 2, TRUE, 'Active', '2024-03-22 10:00:00', NULL),
(12, 2, 3, FALSE, 'Inactive', '2024-02-10 10:00:00', '2024-02-15 10:00:00');

-- 6. Insert ACTIONS (parent table)
INSERT INTO ACTION (action_id, action_type, created_by, created_at) VALUES
(1, 'Appointment', 1, '2024-03-01 10:00:00'),
(2, 'Appointment', 2, '2024-03-05 14:00:00'),
(3, 'Appointment', 3, '2024-03-10 09:00:00'),
(4, 'Appointment', 1, '2024-03-15 11:00:00'),
(5, 'Appointment', 4, '2024-03-20 15:00:00'),
(6, 'Appointment', 5, '2024-04-01 10:00:00'),
(7, 'Appointment', 6, '2024-04-05 14:00:00'),
(8, 'Appointment', 7, '2024-04-10 09:00:00'),
(9, 'Appointment', 1, '2024-04-15 11:00:00'),
(10, 'Appointment', 2, '2024-04-20 15:00:00'),
(11, 'Challenge', 1, '2024-03-01 16:00:00'),
(12, 'Challenge', 2, '2024-03-10 10:00:00'),
(13, 'Challenge', 3, '2024-03-15 14:00:00'),
(14, 'Challenge', 4, '2024-03-20 11:00:00'),
(15, 'Challenge', 5, '2024-04-01 16:00:00'),
(16, 'Challenge', 6, '2024-04-10 10:00:00');

-- 7. Insert APPOINTMENTS
INSERT INTO APPOINTMENT (action_id, provider_id, scheduled_at, consultation_type, memo, status, cancel_reason, cancel_time) VALUES
(1, 1, '2024-03-05 10:00:00', 'InPerson', 'Regular checkup', 'Scheduled', NULL, NULL),
(2, 1, '2024-03-08 14:00:00', 'Virtual', 'Follow-up consultation', 'Scheduled', NULL, NULL),
(3, 3, '2024-03-12 09:00:00', 'InPerson', 'Annual physical exam', 'Scheduled', NULL, NULL),
(4, 2, '2024-03-18 11:00:00', 'Virtual', 'Consultation about test results', 'Cancelled', 'Schedule conflict', '2024-03-17 10:00:00'),
(5, 4, '2024-03-25 15:00:00', 'InPerson', 'Specialist consultation', 'Scheduled', NULL, NULL),
(6, 5, '2024-04-05 10:00:00', 'Virtual', 'Telemedicine appointment', 'Scheduled', NULL, NULL),
(7, 6, '2024-04-08 14:00:00', 'InPerson', 'Regular checkup', 'Scheduled', NULL, NULL),
(8, 7, '2024-04-12 09:00:00', 'Virtual', 'Follow-up', 'Scheduled', NULL, NULL),
(9, 1, '2024-04-18 11:00:00', 'InPerson', 'Routine visit', 'Scheduled', NULL, NULL),
(10, 2, '2024-04-25 15:00:00', 'Virtual', 'Consultation', 'Scheduled', NULL, NULL);

-- 8. Insert CHALLENGES
INSERT INTO CHALLENGE (action_id, goal, start_date, end_date, status) VALUES
(11, 'Walk 10000 steps daily for 30 days', '2024-03-01', '2024-03-31', 'Completed'),
(12, 'Drink 8 glasses of water daily for 21 days', '2024-03-10', '2024-03-31', 'Active'),
(13, 'Exercise 30 minutes daily for 14 days', '2024-03-15', '2024-03-29', 'Completed'),
(14, 'Sleep 8 hours nightly for 30 days', '2024-03-20', '2024-04-19', 'Active'),
(15, 'Meditate 10 minutes daily for 21 days', '2024-04-01', '2024-04-22', 'Active'),
(16, 'No sugar challenge for 7 days', '2024-04-01', '2024-04-08', 'Expired');

-- 9. Insert CHALLENGE_PARTICIPANTS
INSERT INTO CHALLENGE_PARTICIPANT (challenge_participant_id, action_id, user_id, progress_value, progress_unit, updated_at, participant_status) VALUES
(1, 11, 1, 10000, 'steps', '2024-03-31 23:59:59', 'Joined'),
(2, 11, 2, 9500, 'steps', '2024-03-31 23:59:59', 'Joined'),
(3, 11, 3, 12000, 'steps', '2024-03-31 23:59:59', 'Joined'),
(4, 11, 4, 8500, 'steps', '2024-03-31 23:59:59', 'Joined'),
(5, 12, 2, 8, 'glasses', '2024-03-25 20:00:00', 'Joined'),
(6, 12, 5, 7, 'glasses', '2024-03-25 20:00:00', 'Joined'),
(7, 12, 6, 8, 'glasses', '2024-03-25 20:00:00', 'Joined'),
(8, 13, 3, 30, 'minutes', '2024-03-29 21:00:00', 'Joined'),
(9, 13, 7, 35, 'minutes', '2024-03-29 21:00:00', 'Joined'),
(10, 14, 4, 8, 'hours', '2024-03-30 07:00:00', 'Joined'),
(11, 14, 8, 7.5, 'hours', '2024-03-30 07:00:00', 'Joined'),
(12, 15, 5, 10, 'minutes', '2024-04-10 09:00:00', 'Joined'),
(13, 15, 9, 12, 'minutes', '2024-04-10 09:00:00', 'Joined'),
(14, 15, 10, 10, 'minutes', '2024-04-10 09:00:00', 'Joined'),
(15, 16, 6, 0, 'days', '2024-04-05 10:00:00', 'Declined');

-- 10. Insert INVITATIONS
INSERT INTO INVITATION (invitation_id, invitation_type, target_email, target_phone, initiated_at, expires_at, completed_at, status, to_new_user, initiated_by, action_id) VALUES
(1, 'Challenge', 'friend1@email.com', NULL, '2024-03-01 16:30:00', '2024-03-16 16:30:00', '2024-03-02 10:00:00', 'Accepted', TRUE, 1, 11),
(2, 'Challenge', NULL, '13900139001', '2024-03-01 16:35:00', '2024-03-16 16:35:00', NULL, 'Expired', TRUE, 1, 11),
(3, 'Challenge', 'limei@email.com', NULL, '2024-03-10 10:30:00', '2024-03-25 10:30:00', '2024-03-10 14:00:00', 'Accepted', FALSE, 2, 12),
(4, 'Challenge', NULL, '13800138003', '2024-03-10 10:35:00', '2024-03-25 10:35:00', '2024-03-11 09:00:00', 'Accepted', FALSE, 2, 12),
(5, 'Challenge', 'newuser@email.com', NULL, '2024-03-15 14:30:00', '2024-03-30 14:30:00', NULL, 'Pending', TRUE, 3, 13),
(6, 'Challenge', NULL, '13900139002', '2024-03-20 11:30:00', '2024-04-04 11:30:00', NULL, 'Pending', TRUE, 4, 14),
(7, 'DataShare', 'family@email.com', NULL, '2024-03-01 10:00:00', '2024-03-16 10:00:00', '2024-03-05 15:00:00', 'Accepted', FALSE, 1, NULL),
(8, 'DataShare', NULL, '13800138004', '2024-03-05 10:00:00', '2024-03-20 10:00:00', NULL, 'Expired', FALSE, 2, NULL);

-- 11. Insert FAMILY_GROUPS
INSERT INTO FAMILY_GROUP (group_id, group_name, created_by, created_at) VALUES
(1, 'Zhang Family', 1, '2024-02-01 10:00:00'),
(2, 'Li Family', 2, '2024-02-15 14:00:00'),
(3, 'Wang Family', 3, '2024-03-01 09:00:00');

-- 12. Insert GROUP_MEMBERS (at least 2 per group)
INSERT INTO GROUP_MEMBER (group_member_id, group_id, user_id, role, joined_at, left_at) VALUES
(1, 1, 1, 'Admin', '2024-02-01 10:00:00', NULL),
(2, 1, 4, 'Member', '2024-02-01 10:05:00', NULL),
(3, 1, 9, 'Caregiver', '2024-02-05 10:00:00', NULL),
(4, 2, 2, 'Admin', '2024-02-15 14:00:00', NULL),
(5, 2, 5, 'Member', '2024-02-15 14:05:00', NULL),
(6, 2, 6, 'Caregiver', '2024-02-20 10:00:00', NULL),
(7, 3, 3, 'Admin', '2024-03-01 09:00:00', NULL),
(8, 3, 7, 'Member', '2024-03-01 09:05:00', NULL);

-- 13. Insert MONTHLY_SUMMARIES
INSERT INTO MONTHLY_SUMMARY (monthly_summary_id, user_id, year, month, total_steps, total_appointments, last_updated, is_finalized) VALUES
(1, 1, 2024, 3, 310000, 2, '2024-04-01 00:00:00', TRUE),
(2, 1, 2024, 4, 120000, 1, '2024-04-15 10:00:00', FALSE),
(3, 2, 2024, 3, 285000, 1, '2024-04-01 00:00:00', TRUE),
(4, 2, 2024, 4, 95000, 1, '2024-04-20 15:00:00', FALSE),
(5, 3, 2024, 3, 360000, 1, '2024-04-01 00:00:00', TRUE),
(6, 4, 2024, 3, 255000, 1, '2024-04-01 00:00:00', TRUE),
(7, 4, 2024, 4, 80000, 0, '2024-04-10 10:00:00', FALSE),
(8, 5, 2024, 3, 240000, 0, '2024-04-01 00:00:00', TRUE),
(9, 5, 2024, 4, 75000, 1, '2024-04-05 10:00:00', FALSE),
(10, 6, 2024, 3, 270000, 0, '2024-04-01 00:00:00', TRUE),
(11, 6, 2024, 4, 100000, 1, '2024-04-08 14:00:00', FALSE),
(12, 7, 2024, 3, 330000, 0, '2024-04-01 00:00:00', TRUE),
(13, 7, 2024, 4, 110000, 1, '2024-04-12 09:00:00', FALSE),
(14, 9, 2024, 3, 290000, 0, '2024-04-01 00:00:00', TRUE),
(15, 10, 2024, 3, 280000, 0, '2024-04-01 00:00:00', TRUE);

-- Verify data integrity
SELECT 'Data population completed successfully!' AS Status;
SELECT COUNT(*) AS TotalUsers FROM USER;
SELECT COUNT(*) AS TotalProviders FROM PROVIDER;
SELECT COUNT(*) AS TotalAppointments FROM APPOINTMENT;
SELECT COUNT(*) AS TotalChallenges FROM CHALLENGE;

