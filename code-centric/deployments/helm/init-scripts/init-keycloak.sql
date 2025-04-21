
-- Create keycloak user
CREATE USER keycloak WITH PASSWORD 'keycloak';

-- Create keycloak schema owned by keycloak user
CREATE SCHEMA keycloak AUTHORIZATION keycloak;

-- Grant privileges to keycloak user
GRANT ALL PRIVILEGES ON SCHEMA keycloak TO keycloak;

-- Set search path for keycloak user
ALTER ROLE keycloak SET search_path TO keycloak;

-- Connect as keycloak user to validate setup
\connect postgres keycloak
SELECT current_user, current_schema;