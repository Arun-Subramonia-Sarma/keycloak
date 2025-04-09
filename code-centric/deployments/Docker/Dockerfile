FROM quay.io/keycloak/keycloak:26.1.4

# Copy JAR files and immediately verify they exist
COPY code-centric/deployments/Docker/keycloak-custom*.jar /opt/keycloak/providers/
COPY code-centric/deployments/Docker/hello-role-mapper*.jar /opt/keycloak/providers/
RUN ls -la /opt/keycloak/providers/

# Build with more verbose output
RUN /opt/keycloak/bin/kc.sh build --spi-user-cache-default-enabled=true --http-relative-path=/expkeycloak --health-enabled=true --metrics-enabled=true --db=postgres --features=admin-api,account-api,admin-fine-grained-authz --verbose

# Verify providers after build
RUN ls -la /opt/keycloak/providers/
RUN find /opt/keycloak -name "*.jar" | grep -i mapper

# Command to run Keycloak in production mode
CMD ["start", "--optimized"]