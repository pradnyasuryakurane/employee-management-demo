-- Add soft delete columns to employees table
ALTER TABLE employees ADD COLUMN deleted_at TIMESTAMP;
ALTER TABLE employees ADD COLUMN deleted_by VARCHAR(100);

-- Create employee audit table
CREATE TABLE employee_audit (
    id BIGSERIAL PRIMARY KEY,
    employee_id BIGINT NOT NULL,
    audit_type VARCHAR(20) NOT NULL,
    performed_by VARCHAR(100),
    performed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    before_snapshot TEXT,
    after_snapshot TEXT,
    description VARCHAR(500)
);