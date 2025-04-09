FROM quay.io/keycloak/keycloak:26.1.4 
COPY code-centric/deployments/Docker/*.jar /opt/keycloak/providers/
RUN /opt/keycloak/bin/kc.sh build --spi-user-cache-default-enabled=true --http-relative-path=/expkeycloak --health-enabled=true --metrics-enabled=true --db=postgres --features=admin-api,account-api,admin-fine-grained-authz
# Command to run Keycloak in production mode
CMD ["start", "--optimized"]
