package com.fk.platform.keycloak.authentication.authenticators.apikey;

import org.keycloak.models.KeycloakSession;
import org.keycloak.services.resource.RealmResourceProvider;

/**
 * Provider for the API key management REST endpoints.
 */
public class ApiKeyResourceProvider implements RealmResourceProvider {

    private final KeycloakSession session;

    public ApiKeyResourceProvider(KeycloakSession session) {
        this.session = session;
    }

    @Override
    public Object getResource() {
        return new ApiKeyResource(session);
    }

    @Override
    public void close() {
        // Nothing to close
    }
}