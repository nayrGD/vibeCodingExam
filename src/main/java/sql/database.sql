CREATE TYPE expense_frequency AS ENUM ('NONE', 'MONTHLY', 'WEEKLY', 'YEARLY');

CREATE TABLE users (
    id          VARCHAR(36) PRIMARY KEY,
    ref         VARCHAR(255),
    first_name  VARCHAR(255),
    last_name   VARCHAR(255),
    email       VARCHAR(255) NOT NULL UNIQUE,
    phone       VARCHAR(50)
);

-- ============================================================
-- Table de base pour les flux de trésorerie (correspond à CashFlow.java)
-- Stratégie d'héritage : "table par classe" (joined inheritance)
-- ============================================================
CREATE TABLE cash_flow (
    id              VARCHAR(36) PRIMARY KEY,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    amount          NUMERIC(19, 4) NOT NULL,
    user_id         VARCHAR(36) NOT NULL,
    cash_flow_type  VARCHAR(20) NOT NULL,

    CONSTRAINT fk_cash_flow_user
        FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT chk_cash_flow_type
        CHECK (cash_flow_type IN ('DONATION', 'EXPENSE'))
);

CREATE INDEX idx_cash_flow_user_id ON cash_flow(user_id);
CREATE INDEX idx_cash_flow_created_at ON cash_flow(created_at);

-- ============================================================
-- Table des dons (correspond à Donation.java)
-- ============================================================
CREATE TABLE donation (
    id      VARCHAR(36) PRIMARY KEY,
    comment TEXT,

    CONSTRAINT fk_donation_cash_flow
        FOREIGN KEY (id) REFERENCES cash_flow(id)
        ON DELETE CASCADE
);

-- ============================================================
-- Table des dépenses (correspond à Expense.java / ExpenseFrequency.java)
-- ============================================================
CREATE TABLE expense (
    id        VARCHAR(36) PRIMARY KEY,
    reason    VARCHAR(500),
    frequency expense_frequency NOT NULL DEFAULT 'NONE',

    CONSTRAINT fk_expense_cash_flow
        FOREIGN KEY (id) REFERENCES cash_flow(id)
        ON DELETE CASCADE
);

CREATE VIEW cash_flow_full AS
SELECT
    cf.id,
    cf.created_at,
    cf.amount,
    cf.user_id,
    cf.cash_flow_type,
    d.comment       AS donation_comment,
    e.reason        AS expense_reason,
    e.frequency     AS expense_frequency
FROM cash_flow cf
LEFT JOIN donation d ON d.id = cf.id
LEFT JOIN expense  e ON e.id = cf.id;
