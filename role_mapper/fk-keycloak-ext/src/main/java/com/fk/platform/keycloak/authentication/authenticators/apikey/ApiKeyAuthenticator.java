package com.fk.platform.keycloak.authentication.authenticators.apikey;

import org.jboss.logging.Logger;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.Authenticator;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.UserProvider;

import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import java.util.Base64;
import java.util.Optional;

/**
 * Custom Keycloak authenticator that validates API key from HTTP header or query parameter.
 */
public class ApiKeyAuthenticator implements Authenticator {
    private static final Logger logger = Logger.getLogger(ApiKeyAuthenticator.class);

    // Header name for API key
    private static final String API_KEY_HEADER = "X-API-Key";
    // Query param name for API key
    private static final String API_KEY_QUERY_PARAM = "api_key";
    // User attribute name for storing API key
    private static final String API_KEY_ATTRIBUTE = "api_key";

    @Override
    public void authenticate(AuthenticationFlowContext context) {
        String apiKey = extractApiKey(context);

        if (apiKey == null || apiKey.isEmpty()) {
            logger.debug("No API key provided in request");
            challengeResponse(context);
            return;
        }

        logger.debugf("Validating API key: %s", maskApiKey(apiKey));

        RealmModel realm = context.getRealm();
        UserProvider userProvider = context.getSession().users();

        // Find user with matching API key attribute
        Optional<UserModel> optionalUser = userProvider.searchForUserByUserAttributeStream(
                        realm, API_KEY_ATTRIBUTE, apiKey)
                .findFirst();

        if (optionalUser.isPresent()) {
            UserModel user = optionalUser.get();
            logger.debugf("API key matched user: %s", user.getUsername());

            // Check if user is enabled
            if (!user.isEnabled()) {
                logger.debug("User account is disabled");
                context.failure(AuthenticationFlowError.USER_DISABLED);
                return;
            }

            context.setUser(user);
            context.success();
        } else {
            logger.debug("Invalid API key - no matching user found");
            context.failure(AuthenticationFlowError.INVALID_CREDENTIALS);
        }
    }

    private String extractApiKey(AuthenticationFlowContext context) {
        // Try to get from header first
        String apiKey = context.getHttpRequest().getHttpHeaders().getHeaderString(API_KEY_HEADER);

        // If not in header, try query parameter
        if (apiKey == null || apiKey.isEmpty()) {
            MultivaluedMap<String, String> queryParameters = context.getHttpRequest().getUri().getQueryParameters();
            apiKey = queryParameters.getFirst(API_KEY_QUERY_PARAM);
        }

        return apiKey;
    }

    private void challengeResponse(AuthenticationFlowContext context) {
        Response challenge = Response.status(Response.Status.UNAUTHORIZED)
                .header(HttpHeaders.WWW_AUTHENTICATE, "API-Key")
                .entity("API key required")
                .build();

        context.failure(AuthenticationFlowError.INVALID_CREDENTIALS, challenge);
    }

    private String maskApiKey(String apiKey) {
        if (apiKey == null || apiKey.length() < 8) {
            return "***";
        }
        // Only show first 4 characters followed by "***"
        return apiKey.substring(0, 4) + "***";
    }

    @Override
    public void action(AuthenticationFlowContext context) {
        // Not needed for API key authentication - this is for form-based flows
    }

    @Override
    public boolean requiresUser() {
        // We don't need a user already set before this authenticator
        return false;
    }

    @Override
    public boolean configuredFor(KeycloakSession session, RealmModel realm, UserModel user) {
        // Check if this authenticator is configured for the user
        // In our case, check if the user has an API key attribute
        return user.getAttributeStream(API_KEY_ATTRIBUTE).findFirst().isPresent();
    }

    @Override
    public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) {
        // No required actions for API key authentication
    }

    @Override
    public void close() {
        // No resources to close
    }
}