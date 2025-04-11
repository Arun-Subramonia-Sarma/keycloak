package com.fk.platform.keycloak.authentication.authenticators.apikey;

import org.keycloak.Config;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Factory for the API Key Authenticator.
 */
public class ApiKeyAuthenticatorFactory implements AuthenticatorFactory {

    public static final String PROVIDER_ID = "api-key-authenticator";
    private static final ApiKeyAuthenticator SINGLETON = new ApiKeyAuthenticator();

    @Override
    public String getDisplayType() {
        return "API Key Authentication";
    }

    @Override
    public String getReferenceCategory() {
        return "api-key";
    }

    @Override
    public boolean isConfigurable() {
        return true;
    }

    @Override
    public AuthenticationExecutionModel.Requirement[] getRequirementChoices() {
        return new AuthenticationExecutionModel.Requirement[] {
                AuthenticationExecutionModel.Requirement.REQUIRED,
                AuthenticationExecutionModel.Requirement.ALTERNATIVE,
                AuthenticationExecutionModel.Requirement.DISABLED
        };
    }

    @Override
    public boolean isUserSetupAllowed() {
        return true;
    }

    @Override
    public String getHelpText() {
        return "Validates the API key sent in a request header or query parameter against the API key stored in user attributes.";
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        // You can add configuration properties here if needed
        return new ArrayList<>();
    }

    @Override
    public Authenticator create(KeycloakSession session) {
        return SINGLETON;
    }

    @Override
    public void init(Config.Scope config) {
        // NOOP
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        // NOOP
    }

    @Override
    public void close() {
        // NOOP
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }
}