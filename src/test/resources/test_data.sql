INSERT INTO users (workerId, firstName, lastName, password, role, active) VALUES
      ('OP001', 'Alice', 'Andersen', 'password123', 1, 1),   -- Operator
      ('OP002', 'Bob', 'Bentsen', 'pass456', 1, 1),          -- Operator
      ('IN001', 'Clara', 'Christensen', 'inspect789', 2, 1), -- Inspector
      ('AD001', 'David', 'Damgaard', 'admin000', 3, 1),      -- Administrator
      ('OP003', 'Eva', 'Eriksen', 'pass999', 1, 0);          -- Inaktiv operator

INSERT INTO reports (order_number, status, created_date, update_date, operator_id, inspected_by, inspector_comment) VALUES
    ('ORD1001', 'Pending', GETDATE(), NULL, 1, NULL, NULL),
    ('ORD1002', 'Accepted', GETDATE(), DATEADD(DAY, 1, GETDATE()), 2, 3, 'Looks good.'),
    ('ORD1003', 'Rejected', GETDATE(), DATEADD(DAY, 2, GETDATE()), 1, 3, 'Missing info.'),
    ('ORD1004', 'Accepted', GETDATE(), GETDATE(), 2, 3, 'OK after revision.'),
    ('ORD1005', 'Pending', GETDATE(), NULL, 1, NULL, NULL);

INSERT INTO reports_images (report_id, picture, comment, angle) VALUES
    (1, CAST('0xFFD8FFE000104A464946' AS VARBINARY(MAX)), 'Front view', 'Front'),
    (1, CAST('0xFFD8FFE000104A464946' AS VARBINARY(MAX)), 'Side view', 'Right'),
    (2, CAST('0xFFD8FFE000104A464946' AS VARBINARY(MAX)), 'Top view', 'Left'),
    (3, CAST('0xFFD8FFE000104A464946' AS VARBINARY(MAX)), 'Damaged part', 'Left'),
    (4, CAST('0xFFD8FFE000104A464946' AS VARBINARY(MAX)), 'After repair', 'Back');


