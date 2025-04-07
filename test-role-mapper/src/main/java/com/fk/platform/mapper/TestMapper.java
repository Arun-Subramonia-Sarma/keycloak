package com.fk.platform.mapper;

import org.keycloak.models.*;
import org.keycloak.protocol.oidc.mappers.*;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.representations.IDToken;

import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TestMapper extends AbstractOIDCProtocolMapper implements OIDCAccessTokenMapper, OIDCIDTokenMapper, UserInfoTokenMapper {
    private static final Logger logger = Logger.getLogger(TestMapper.class.getName());

    // The internal name of this mapper in Keycloak
    public static final String PROVIDER_ID = "oidc-test-mapper";

    // The human-readable name of this mapper in the Keycloak admin console
    public static final String DISPLAY_TYPE = "Test Mappper just add a constant to the token";

    // The description shown in the Keycloak admin console
    public static final String DISPLAY_CATEGORY = "Token mapper";
    public static final String HELP_TEXT = "To add a constant text to the token";

    // Configuration properties shown in Keycloak admin console
    protected static final List<ProviderConfigProperty> configProperties = new ArrayList<>();

    // Define custom configuration parameters
    protected static final String CLAIM_NAME = "claim.name";
    private static final String COMPANY_ATTRIBUTE = "company.attribute";
    private static final String INCLUDE_IN_ID_TOKEN = "include.in.idtoken";
    private static final String INCLUDE_IN_ACCESS_TOKEN = "include.in.accesstoken";
    private static final String INCLUDE_IN_USERINFO = "include.in.userinfo";

    static {
  
        OIDCAttributeMapperHelper.addTokenClaimNameConfig(configProperties);
        OIDCAttributeMapperHelper.addIncludeInTokensConfig(configProperties, TestMapper.class);
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

}
