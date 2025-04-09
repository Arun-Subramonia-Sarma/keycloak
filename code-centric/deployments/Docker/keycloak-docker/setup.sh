#!/bin/bash

# Create directory for initialization scripts
mkdir -p init-postgresql

# Copy the init script to the directory
cat > init-postgresql/01-init-schema.sql << 'EOF'
-- Create the schema for Keycloak
CREATE SCHEMA IF NOT EXISTS keycloak_26_1_4;
GRANT ALL PRIVILEGES ON SCHEMA keycloak_26_1_4 TO keycloak;
EOF

# Create .env file if it doesn't exist
if [ ! -f .env ]; then
    cat > .env << 'EOF'
# Keycloak and PostgreSQL Configuration
POSTGRES_PASSWORD=secure_password_change_me
EOF
    echo ".env file created. Please edit it to set your passwords."
fi

echo "Setup complete! You can now run 'docker-compose up -d'"
echo "The first build will take some time as Keycloak needs to be optimized."
