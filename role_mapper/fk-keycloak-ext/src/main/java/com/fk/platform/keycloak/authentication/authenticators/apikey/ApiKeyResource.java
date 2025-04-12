package com.fk.platform.keycloak.authentication.authenticators.apikey;

import org.jboss.logging.Logger;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.services.managers.AppAuthManager;
import org.keycloak.services.managers.AuthenticationManager;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

/**
 * REST endpoint for API key management.
 */
@Path("/api-keys")
public class ApiKeyResource {
    private static final Logger logger = Logger.getLogger(ApiKeyResource.class);

    private final KeycloakSession session;
    private final AuthenticationManager.AuthResult auth;

    public ApiKeyResource(KeycloakSession session) {
        this.session = session;
        this.auth = new AppAuthManager.BearerTokenAuthenticator(session).authenticate();
    }

    /**
     * Generate a new API key for the authenticated user.
     *
     * @param expiryDays Number of days until the API key expires (0 for no expiry)
     * @return Response containing the new API key
     */
    @POST
    @Path("/generate")
    @Produces(MediaType.APPLICATION_JSON)
    public Response generateApiKey(@QueryParam("expiryDays") @DefaultValue("30") int expiryDays) {
        logger.debugf("Logging using the auth %s",auth);
        if (auth == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        UserModel user = auth.getUser();
        logger.infof("Generating API key for user %s", user.getUsername());

        ApiKeyService service = new ApiKeyService(session);
        String apiKey = service.generateApiKey(user, expiryDays);

        Map<String, String> response = new HashMap<>();
        response.put("apiKey", apiKey);
        if (expiryDays > 0) {
            response.put("expiresIn", expiryDays + " days");
        } else {
            response.put("expiresIn", "never");
        }

        return Response.ok(response).build();
    }

    /**
     * Revoke the authenticated user's API key.
     *
     * @return Response indicating success
     */
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response revokeApiKey() {
        if (auth == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        UserModel user = auth.getUser();
        logger.infof("Revoking API key for user %s", user.getUsername());

        ApiKeyService service = new ApiKeyService(session);
        service.revokeApiKey(user);

        Map<String, String> response = new HashMap<>();
        response.put("status", "revoked");

        return Response.ok(response).build();
    }

    /**
     * Check if the authenticated user has a valid API key.
     *
     * @return Response indicating if the user has a valid API key
     */
    @GET
    @Path("/status")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getApiKeyStatus() {
        if (auth == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        UserModel user = auth.getUser();
        ApiKeyService service = new ApiKeyService(session);

        boolean hasApiKey = user.getAttributeStream("api_key").findFirst().isPresent();
        boolean isExpired = hasApiKey && service.isApiKeyExpired(user);

        Map<String, Object> response = new HashMap<>();
        response.put("hasApiKey", hasApiKey);
        response.put("isExpired", isExpired);
        response.put("isValid", hasApiKey && !isExpired);

        return Response.ok(response).build();
    }
}