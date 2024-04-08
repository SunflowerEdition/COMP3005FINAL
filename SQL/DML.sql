INSERT INTO Admin (email, first_name, last_name, phone_number, address, gender, 
start_date, birthday, status, password) VALUES 
('tomasteixeira@gmail.com', 'Tomas', 'Teixeira', '819-555-0001', 
  '01 Fake Road, QC, Canada', 'M', '2023-04-12', '2001-04-03', 'A', 'tomasteixeira1');

INSERT INTO Trainer (email, first_name, last_name, phone_number, address, gender, 
start_date, birthday, status, password) VALUES 
('tomasteixeira@gmail.com', 'Tomas', 'Teixeira', '819-555-0001', 
  '01 Fake Road, QC, Canada', 'M', '2023-04-12', '2001-04-03', 'A', 'tomasteixeira1'),
('johnsmith@gmail.com', 'John', 'Smith', '819-555-0002',
'02 Fake Boulevard, ON, Canada', 'M', '2023-08-12', '1996-10-12', 'A', 'johnsmith1'),
('sarahwilson@gmail.com', 'Sarah', 'Wilson', '819-555-0003',
'03 Fake Road, QC, Canada', 'F', '2023-08-13', '2000-09-20', 'A', 'sarahwilson1');

INSERT INTO Member (email, first_name, last_name, phone_number, address, gender, 
 birthday, password) VALUES
('michaeljohnson@gmail.com', 'Michael', 'Johnson', '789-012-3456', 
  '789 Oak St', 'M', '1985-12-10', 'michaeljohnson1'),
('sarahbrown@gmail.com', 'Sarah', 'Brown', '234-567-8901', 
  '234 Pine St', 'F', '1988-07-05', 'sarahbrown1'),
('davidwilson@gmail.com', 'David', 'Wilson', '567-890-1234', 
  '567 Maple St', 'M', '1993-03-25', 'davidwilson1'),
('jessicamiller@gmail.com', 'Jessica', 'Miller', '890-123-4567', 
  '890 Cedar St', 'F', '1991-09-30', 'jessicamiller1'),
('christophertaylor@gmail.com', 'Christopher', 'Taylor', '345-678-9012', 
  '345 Birch St', 'M', '1987-11-18', 'christophertaylor1'),
('amandadavis@gmail.com', 'Amanda', 'Davis', '012-345-6789', 
  '012 Walnut St', 'F', '1994-06-12', 'amandadavis1'),
('mattmartinez@gmail.com', 'Matt', 'Martinez', '901-234-5678', 
  '901 Cherry St', 'M', '1989-04-08', 'mattmartinez1'),
('laurengarcia@gmail.com', 'Lauren', 'Garcia', '678-901-2345', 
  '678 Vine St', 'F', '1990-10-03', 'laurengarcia1'),
('sophiemartinez@gmail.com', 'Sophie', 'Martinez', '9081-235-5678',
  '901 Cherry St', 'F', '1989-12-10', 'sophiemartinez1');
  
/* Memberships for all members */
INSERT INTO Membership (status, start_date, end_date, balance, member_id) VALUES
('A', '2024-01-01', '2025-01-01', 0.0, 1),
('A', '2024-01-01', '2025-01-01', 60.0, 2),
('A', '2024-01-01', '2025-01-01', 0.0, 3),
('A', '2024-01-01', '2025-01-01', 37.50, 4),
('A', '2024-01-01', '2025-01-01', 0.0, 5),
('A', '2024-01-01', '2025-01-01', 0.0, 6),
('I', '2023-01-01', '2024-01-01', 0, 7),
('A', '2024-01-01', '2025-01-01', 100.0, 7),
('A', '2024-01-01', '2025-01-01', 0.0, 8),
('F', '2024-01-01', NULL, 0.0, 9);

INSERT INTO Room (number, description, capacity, last_clean) VALUES
('A001', 'Main Weight Room', 100, '2024-03-23 14:00'),
('B001', 'Spin Room One', 15, '2024-03-23 14:00'),
('B002', 'Spin Room Two', 15, '2024-03-23 14:00'),
('B003', 'Spin Room Three', 15, '2024-03-23 14:00'),
('B004', 'Sauna One', 10, '2024-03-23 14:00'),
('B005', 'Sauna Two', 10, '2024-03-23 14:00'),
('C001', 'Cross Fit One', 10, '2024-03-23 14:00'),
('C002', 'Cross Fit Two', 20, '2024-03-23 14:00'),
('C003', 'Cross Fit Three', 40, '2024-03-23 14:00');

INSERT INTO Equipment (description, last_maintain, room_id) VALUES 
('Barbell', '2024-03-01 16:30', 1),
('Dumbbells', '2024-03-01 16:30', 1),
('Bench Press', '2024-03-01 16:30', 1),
('Spin Bike #1', '2024-03-01 16:30', 2),
('Spin Bike #2', '2024-03-01 16:30', 2),
('Spin Bike #3', '2024-03-01 16:30', 2),
('Spin Bike #4', '2024-03-01 16:30', 3),
('Spin Bike #5', '2024-03-01 16:30', 3),
('Spin Bike #6', '2024-03-01 16:30', 3),
('Spin Bike #7', '2024-03-01 16:30', 4),
('Spin Bike #8', '2024-03-01 16:30', 4),
('Spin Bike #9', '2024-03-01 16:30', 4),
('Kettlebells', '2024-03-01 16:30', 7),
('Medicine Balls', '2024-03-01 16:30', 7);

/* Creation of Metrics/Routines/Fitness Goals only for 1 user */
INSERT INTO HealthMetric (measured_on, type, value, member_id) VALUES
('2024-03-01', 'Body Fat Percentage', 22.5, 7),
('2024-03-05', 'Body Fat Percentage', 22.3, 7),
('2024-03-10', 'Body Fat Percentage', 22.1, 7),
('2024-03-15', 'Body Fat Percentage', 21.8, 7),
('2024-03-20', 'Body Fat Percentage', 21.6, 7),
('2024-03-25', 'Body Fat Percentage', 21.3, 7),
('2024-03-30', 'Body Fat Percentage', 21.1, 7),
('2024-03-01', 'Weight', 175.5, 7),
('2024-03-05', 'Weight', 174.8, 7),
('2024-03-10', 'Weight', 174.3, 7),
('2024-03-15', 'Weight', 173.9, 7),
('2024-03-20', 'Weight', 173.2, 7),
('2024-03-25', 'Weight', 172.7, 7),
('2024-03-30', 'Weight', 172.1, 7);

INSERT INTO Routine (name, description, frequency, member_id) VALUES
('Monday Weightlifting', 'Full-Body with Focus on Chest', '1/week', 7),
('Tuesday Cardio', 'Casual cardio focus on technique', '1/week', 7),
('Wednesday Weightlifting', 'Full-Body with Focus on Back', '1/week', 7);

INSERT INTO Exercise (name, description, sets, repetitions, routine_id) VALUES
('Squats', 'Focus on range', 3, 6, 1),
('Pull-ups', 'Technique over reps', 3, 6, 1),
('Bench Press', 'Activate chest', 6, 6, 1),
('Chest Flys', 'Super-Set into pushups', 6, 6, 1),
('Push-ups', 'To failure', 6, 0, 1),
('Running', '45 minutes to an hour', 1, 1, 2),
('Squats', 'Focus on range', 3, 6, 3),
('Bench Press', 'Activate chest', 6, 6, 3),
('Pull-ups', 'Technique over reps', 3, 6, 1),
('Bent-Over Rows', 'Go heavy', 3, 6, 1),
('Military Press', 'Rep it out', 3, 12, 1);

INSERT INTO FitnessGoal (name, current, target, deadline, status, member_id) VALUES
('Body Fat Percentage', '22.5', '20.5', '2024-05-01', 'A', 7),
('Weight', '175.5', '160', '2024-05-01', 'A', 7);

/* Group and Personal Session creation */
INSERT INTO PersonalSession (name, description, price, moment, member_id, trainer_id, room_id) VALUES
('Weightlifting 1-on-1 training', 'Intermediate training, 1 hour long', 30.00, '2024-05-01 10:00', NULL, 1, 1),
('Weightlifting 1-on-1 training', 'Intermediate training, 1 hour long', 30.00, '2024-05-01 11:00', NULL, 1, 1),
('Weightlifting 1-on-1 training', 'Intermediate training, 1 hour long', 30.00, '2024-05-01 12:00', NULL, 1, 1);

INSERT INTO GroupSession (name, description, price, moment, capacity, trainer_id, room_id) VALUES
('Spining Lesson', 'All-Levels, 1 hour long', 25.00, '2024-05-01 10:00', 5, 2, 2),
('Spining Lesson', 'All-Levels, 1 hour long', 25.00, '2024-05-01 11:00', 5, 2, 2);

INSERT INTO Participants (g_session_id, member_id) VALUES
(1, 1), (1, 2), (1, 3), (2, 4), (2, 5), (2, 6);



INSERT INTO PersonalSession (name, description, price, moment, member_id, trainer_id, room_id) VALUES
('Training', 'N/A', 30.00, '2024-04-07 20:00', NULL, 1, 1);


































