package com.fk.platform.keycloak.authentication.authenticators.apikey;

import org.jboss.logging.Logger;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.services.resource.RealmResourceProvider;
import org.keycloak.services.resource.RealmResourceProviderFactory;

/**
 * Factory for creating API key management resource providers.
 */
public class ApiKeyResourceProviderFactory implements RealmResourceProviderFactory {
    private static final Logger logger = Logger.getLogger(ApiKeyResourceProviderFactory.class);
    public static final String ID = "api-keys";

    @Override
    public RealmResourceProvider create(KeycloakSession session) {
        logger.debugf("Calling the API Key Resource provider with the session %s", session);
        return new ApiKeyResourceProvider(session);
    }

    @Override
    public void init(Config.Scope config) {
        // Nothing to initialize
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        // Nothing to post-initialize
    }

    @Override
    public void close() {
        // Nothing to close
    }

    @Override
    public String getId() {
        return ID;
    }
}