USE belsign;
GO

CREATE TABLE users_roles (
    id INT PRIMARY KEY IDENTITY(1,1),
    role VARCHAR(50)
);
GO

CREATE TABLE users (
    id INT PRIMARY KEY IDENTITY(1,1),
    workerId VARCHAR(50) NOT NULL,
    firstName VARCHAR(100),
    lastName VARCHAR(100),
    password VARCHAR(100) NOT NULL,
    role INT NOT NULL,
    FOREIGN KEY (role) REFERENCES users_roles(id)
);
GO

CREATE TABLE reports (
    id INT PRIMARY KEY identity(1,1),
    order_number VARCHAR(50) NOT NULL,
    status VARCHAR(50) DEFAULT 'Pending' CHECK (status IN ('Pending', 'Accepted', 'Rejected')),
    created_date DATETIME NOT NULL,
    status_update_date DATETIME,
    operator_id INT NOT NULL,
    inspected_by INT,
    FOREIGN KEY (operator_id) REFERENCES users(id),
    FOREIGN KEY (inspected_by) REFERENCES users(id)
);
GO

CREATE TABLE reports_images (
    id INT PRIMARY KEY IDENTITY(1,1),
    report_id INT NOT NULL,
    picture VARBINARY(MAX),
    comment VARCHAR(255),
    angle VARCHAR(50),
    FOREIGN KEY (report_id) REFERENCES reports(id)
);
GO
