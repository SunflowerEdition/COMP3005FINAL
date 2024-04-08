/*
  gender:
    'M' => 'Male'
    'F' => 'Female'
    'O' => 'Other'
*/
CREATE TABLE Member (
  id SERIAL PRIMARY KEY,
  email VARCHAR(50) UNIQUE NOT NULL,
  first_name VARCHAR(35) NOT NULL,
  last_name VARCHAR(35) NOT NULL,
  phone_number VARCHAR(15) NOT NULL,
  address VARCHAR(100) NOT NULL,
  gender CHAR(1) NOT NULL CHECK (gender IN('M', 'F', 'O')),
  birthday DATE NOT NULL,
  password VARCHAR(20) NOT NULL
);

/*
  gender:
    'M' => 'Male'
    'F' => 'Female'
    'O' => 'Other'
    
  status:
    'A' => 'Active'
    'I' => 'Inactive'
    'V' => 'Vacation'
*/
CREATE TABLE Trainer (
  id SERIAL PRIMARY KEY,
  email VARCHAR(50) UNIQUE NOT NULL,
  first_name VARCHAR(35) NOT NULL,
  last_name VARCHAR(35) NOT NULL,
  phone_number VARCHAR(15) NOT NULL,
  address VARCHAR(100) NOT NULL,
  gender CHAR(1) NOT NULL CHECK (gender IN('M', 'F', 'O')),
  start_date DATE NOT NULL,
  end_date DATE,
  birthday DATE NOT NULL,
  status CHAR(1) NOT NULL CHECK (status IN('A', 'I', 'V')),
  password VARCHAR(20) NOT NULL
);

/*
  gender:
    'M' => 'Male'
    'F' => 'Female'
    'O' => 'Other'
    
  status:
    'A' => 'Active'
    'I' => 'Inactive'
    'V' => 'Vacation'
*/
CREATE TABLE Admin (
  id SERIAL PRIMARY KEY,
  email VARCHAR(50) UNIQUE NOT NULL,
  first_name VARCHAR(35) NOT NULL,
  last_name VARCHAR(35) NOT NULL,
  phone_number VARCHAR(15) NOT NULL,
  address VARCHAR(100) NOT NULL,
  gender CHAR(1) NOT NULL CHECK (gender IN('M', 'F', 'O')),
  start_date DATE NOT NULL,
  end_date DATE,
  birthday DATE NOT NULL,
  status CHAR(1) NOT NULL CHECK (status IN('A', 'I', 'V')),
  password VARCHAR(20) NOT NULL
);

CREATE TABLE Room (
  room_id SERIAL PRIMARY KEY,
  number CHAR(4) NOT NULL,
  description VARCHAR(100),
  capacity INT,
  last_clean CHAR(16) NOT NULL
);

CREATE TABLE Equipment (
  equipment_id SERIAL PRIMARY KEY,
  description VARCHAR(100),
  last_maintain CHAR(16),
  room_id INT REFERENCES Room(room_id)
);

CREATE TABLE HealthMetric (
  health_metric_id SERIAL PRIMARY KEY,
  measured_on DATE NOT NULL,
  type VARCHAR(30) NOT NULL,
  value FLOAT NOT NULL,
  member_id INT REFERENCES Member(id)
);

/*
  status:
    'A' => 'Active'
    'I' => 'Inactive'
    'F' => 'Frozen'
*/
CREATE TABLE Membership (
  membership_id SERIAL PRIMARY KEY,
  status CHAR(1) NOT NULL CHECK (status IN('A', 'I', 'F')),
  start_date DATE NOT NULL,
  end_date DATE,
  balance NUMERIC(5, 2),
  member_id INT REFERENCES Member(id)
);

CREATE TABLE Routine (
  routine_id SERIAL PRIMARY KEY,
  name VARCHAR(40) NOT NULL,
  description VARCHAR(100),
  frequency VARCHAR(15),
  member_id INT REFERENCES Member(id)
);

CREATE TABLE Exercise (
  exercise_id SERIAL PRIMARY KEY,
  name VARCHAR(40) NOT NULL,
  description VARCHAR(100),
  sets INT,
  repetitions INT,
  routine_id INT REFERENCES Routine(routine_id) ON DELETE CASCADE
);

/*
  status:
    'A' => 'Active'
    'I' => 'Inactive'
    'C' => 'Completed'
*/
CREATE TABLE FitnessGoal (
  fitness_goal_id SERIAL PRIMARY KEY,
  name VARCHAR(40) NOT NULL,
  current VARCHAR(20),
  target VARCHAR(20) NOT NULL,
  deadline DATE,
  status CHAR(1) CHECK (status IN ('A', 'I', 'C')),
  member_id INT REFERENCES Member(id)
);

CREATE TABLE PersonalSession (
  p_session_id SERIAL PRIMARY KEY,
  name VARCHAR(40) NOT NULL,
  description VARCHAR(100),
  price NUMERIC(4, 2),
  moment CHAR(16) NOT NULL,
  member_id INT REFERENCES Member(id),
  trainer_id INT REFERENCES Trainer(id) NOT NULL,
  room_id INT REFERENCES Room(room_id) NOT NULL
);

CREATE TABLE GroupSession (
  g_session_id SERIAL PRIMARY KEY,
  name VARCHAR(40) NOT NULL,
  description VARCHAR(100),
  price NUMERIC(4, 2) NOT NULL,
  moment CHAR(16) NOT NULL,
  capacity INT NOT NULL,
  trainer_id INT REFERENCES Trainer(id) NOT NULL,
  room_id INT REFERENCES Room(room_id) NOT NULL
);

CREATE TABLE Participants (
  g_session_id INT REFERENCES GroupSession(g_session_id) ON DELETE CASCADE,
  member_id INT REFERENCES Member(id) ON DELETE CASCADE
);
