CREATE TABLE tb_user (
  id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
  username VARCHAR(255) UNIQUE NOT NULL,
  email VARCHAR(255) UNIQUE NOT NULL,
  password VARCHAR(255) NOT NULL,
  account_non_expired BOOLEAN NOT NULL,
  account_non_locked BOOLEAN NOT NULL,
  credentials_non_expired BOOLEAN NOT NULL,
  enabled BOOLEAN NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);