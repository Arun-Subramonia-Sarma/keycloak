package com.fourkites.platform.mapper;

import org.keycloak.models.ClientSessionContext;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.ProtocolMapperModel;
import org.keycloak.models.UserSessionModel;
import org.keycloak.protocol.oidc.mappers.*;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.IDToken;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class HelloMapper extends AbstractOIDCProtocolMapper implements OIDCAccessTokenMapper, OIDCIDTokenMapper, UserInfoTokenMapper {
    private static final Logger logger = Logger.getLogger(HelloMapper.class.getName());

    // The internal name of this mapper in Keycloak
    public static final String PROVIDER_ID = "oidc-contant-text-mapper";

    // The human-readable name of this mapper in the Keycloak admin console
    public static final String DISPLAY_TYPE = "Add a default text to the token";

    // The description shown in the Keycloak admin console
    public static final String DISPLAY_CATEGORY = "Token mapper";
    public static final String HELP_TEXT = "Add a default text to the token";

    // Configuration properties shown in Keycloak admin console
    protected static final List<ProviderConfigProperty> configProperties = new ArrayList<>();

    // Define custom configuration parameters
    protected static final String CLAIM_NAME = "claim.name";
    private static final String COMPANY_ATTRIBUTE = "company.attribute";
    private static final String INCLUDE_IN_ID_TOKEN = "include.in.idtoken";
    private static final String INCLUDE_IN_ACCESS_TOKEN = "include.in.accesstoken";
    private static final String INCLUDE_IN_USERINFO = "include.in.userinfo";

    static {
        // Add configuration for claim name
        ProviderConfigProperty claimNameProperty = new ProviderConfigProperty();
        claimNameProperty.setName(CLAIM_NAME);
        claimNameProperty.setLabel("Claim Name");
        claimNameProperty.setType(ProviderConfigProperty.STRING_TYPE);
        claimNameProperty.setDefaultValue("companyRoles");
        claimNameProperty.setHelpText("Name of the claim to add to the token that will contain the company roles map");
        configProperties.add(claimNameProperty);


        // Add configuration for including claim in ID token
        ProviderConfigProperty includeInIdTokenProperty = new ProviderConfigProperty();
        includeInIdTokenProperty.setName(INCLUDE_IN_ID_TOKEN);
        includeInIdTokenProperty.setLabel("Add to ID token");
        includeInIdTokenProperty.setType(ProviderConfigProperty.BOOLEAN_TYPE);
        includeInIdTokenProperty.setDefaultValue("true");
        includeInIdTokenProperty.setHelpText("Include this claim in the ID token");
        configProperties.add(includeInIdTokenProperty);

        // Add configuration for including claim in access token
        ProviderConfigProperty includeInAccessTokenProperty = new ProviderConfigProperty();
        includeInAccessTokenProperty.setName(INCLUDE_IN_ACCESS_TOKEN);
        includeInAccessTokenProperty.setLabel("Add to access token");
        includeInAccessTokenProperty.setType(ProviderConfigProperty.BOOLEAN_TYPE);
        includeInAccessTokenProperty.setDefaultValue("true");
        includeInAccessTokenProperty.setHelpText("Include this claim in the access token");
        configProperties.add(includeInAccessTokenProperty);

        // Add configuration for including claim in UserInfo
        ProviderConfigProperty includeInUserInfoProperty = new ProviderConfigProperty();
        includeInUserInfoProperty.setName(INCLUDE_IN_USERINFO);
        includeInUserInfoProperty.setLabel("Add to UserInfo");
        includeInUserInfoProperty.setType(ProviderConfigProperty.BOOLEAN_TYPE);
        includeInUserInfoProperty.setDefaultValue("true");
        includeInUserInfoProperty.setHelpText("Include this claim in the UserInfo response");
        configProperties.add(includeInUserInfoProperty);
    }

    @Override
    public String getDisplayCategory() {
        return DISPLAY_CATEGORY;
    }

    @Override
    public String getDisplayType() {
        return DISPLAY_TYPE;
    }

    @Override
    public String getHelpText() {
        return HELP_TEXT;
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return configProperties;
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    @Override
    protected void setClaim(IDToken token, ProtocolMapperModel mappingModel, UserSessionModel userSession,
                            KeycloakSession keycloakSession, ClientSessionContext clientSessionCtx) {
        logger.info("Entering the class "+this.getClass().getName()+" method setClaim");

        OIDCAttributeMapperHelper.mapClaim(token, mappingModel,"Hello, world" );
    }

    @Override
    protected void setClaim(AccessTokenResponse accessTokenResponse, ProtocolMapperModel mappingModel, UserSessionModel userSession, KeycloakSession keycloakSession, ClientSessionContext clientSessionCtx) {
        logger.info("Entering the class "+this.getClass().getName()+" method setClaim");

        OIDCAttributeMapperHelper.mapClaim(accessTokenResponse, mappingModel,"Hello, world" );
    }
}
