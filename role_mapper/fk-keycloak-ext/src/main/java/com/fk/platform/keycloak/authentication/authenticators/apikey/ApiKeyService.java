package com.fk.platform.keycloak.authentication.authenticators.apikey;

import org.jboss.logging.Logger;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.UserModel;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Optional;

/**
 * Service for generating and managing API keys.
 */
public class ApiKeyService {
    private static final Logger logger = Logger.getLogger(ApiKeyService.class);

    // User attribute name for storing API key
    private static final String API_KEY_ATTRIBUTE = "api_key";
    // User attribute for storing API key expiration date
    private static final String API_KEY_EXPIRY_ATTRIBUTE = "api_key_expiry";

    private final KeycloakSession session;

    public ApiKeyService(KeycloakSession session) {
        this.session = session;
    }

    /**
     * Generate a new API key for a user.
     *
     * @param user The user to generate an API key for
     * @param expiryDays Number of days until the API key expires (0 for no expiry)
     * @return The generated API key
     */
    public String generateApiKey(UserModel user, int expiryDays) {
        logger.debugf("Generating new API key for user %s", user.getUsername());

        // Generate a secure random API key
        String apiKey = generateSecureRandomString();

        // Store it as a user attribute
        user.setSingleAttribute(API_KEY_ATTRIBUTE, apiKey);

        // Set expiry if requested
        if (expiryDays > 0) {
            LocalDateTime expiryDate = LocalDateTime.now().plusDays(expiryDays);
            String expiryDateStr = expiryDate.format(DateTimeFormatter.ISO_DATE_TIME);
            user.setSingleAttribute(API_KEY_EXPIRY_ATTRIBUTE, expiryDateStr);
            logger.debugf("API key will expire on %s", expiryDateStr);
        } else {
            // Remove any existing expiry
            user.removeAttribute(API_KEY_EXPIRY_ATTRIBUTE);
        }

        return apiKey;
    }

    /**
     * Revoke a user's API key.
     *
     * @param user The user whose API key should be revoked
     */
    public void revokeApiKey(UserModel user) {
        logger.debugf("Revoking API key for user %s", user.getUsername());
        user.removeAttribute(API_KEY_ATTRIBUTE);
        user.removeAttribute(API_KEY_EXPIRY_ATTRIBUTE);
    }

    /**
     * Check if a user's API key has expired.
     *
     * @param user The user to check
     * @return true if the API key has expired, false otherwise
     */
    public boolean isApiKeyExpired(UserModel user) {
        Optional<String> expiryDateStr = user.getAttributeStream(API_KEY_EXPIRY_ATTRIBUTE).findFirst();

        if (expiryDateStr.isPresent()) {
            try {
                LocalDateTime expiryDate = LocalDateTime.parse(expiryDateStr.get(), DateTimeFormatter.ISO_DATE_TIME);
                return LocalDateTime.now().isAfter(expiryDate);
            } catch (Exception e) {
                logger.warnf("Invalid expiry date format for user %s: %s", user.getUsername(), e.getMessage());
                return false;
            }
        }

        // No expiry date means it doesn't expire
        return false;
    }

    /**
     * Generate a cryptographically secure random string for use as an API key.
     *
     * @return A secure random string
     */
    private String generateSecureRandomString() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32]; // 256 bits
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}