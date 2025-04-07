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

public class CompanyRoleMapper extends AbstractOIDCProtocolMapper implements OIDCAccessTokenMapper, OIDCIDTokenMapper, UserInfoTokenMapper {
    private static final Logger logger = Logger.getLogger(CompanyRoleMapper.class.getName());

    // The internal name of this mapper in Keycloak
    public static final String PROVIDER_ID = "oidc-company-roles-group-mapper";

    // The human-readable name of this mapper in the Keycloak admin console
    public static final String DISPLAY_TYPE = "Company Roles from Groups";

    // The description shown in the Keycloak admin console
    public static final String DISPLAY_CATEGORY = "Token mapper";
    public static final String HELP_TEXT = "Maps user's group roles to companies based on group attributes";

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

        // Add configuration for company attribute name
        ProviderConfigProperty companyAttributeProperty = new ProviderConfigProperty();
        companyAttributeProperty.setName(COMPANY_ATTRIBUTE);
        companyAttributeProperty.setLabel("Company Attribute");
        companyAttributeProperty.setType(ProviderConfigProperty.STRING_TYPE);
        companyAttributeProperty.setDefaultValue("companyname");
        companyAttributeProperty.setHelpText("Name of the attribute in the group that defines the company name");
        configProperties.add(companyAttributeProperty);

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
        UserModel user = userSession.getUser();
        if (user == null) {
            logger.warning("User not found in session");
            return;
        }

        // Get the claim name from the configuration
        String claimName = mappingModel.getConfig().get(CLAIM_NAME);
        if (claimName == null) {
            claimName = "companyRoles"; // Default claim name if not configured
        }

        // Get the company attribute name from the configuration
        var ref = new Object() {
            String companyAttribute = mappingModel.getConfig().get(COMPANY_ATTRIBUTE);
        };
        if (ref.companyAttribute == null) {
            ref.companyAttribute = "companyname"; // Default attribute name if not configured
        }

        
        //final String companyAttribute = "companyname";
                // Get all user groups
        Stream<GroupModel> userGroups = user.getGroupsStream();

        // Create a map to store roles by company
        Map<String, Set<String>> companyRolesMap = new HashMap<>();

        // Process each group
        userGroups.forEach(group -> {
            // Get company name from the group attribute
            String companyName = getSingleAttribute(group, ref.companyAttribute);
            if (companyName == null || companyName.isEmpty())
                companyName ="defaultCompany";

            // Get all roles assigned to this group
            Set<String> groupRoles = group.getRoleMappingsStream()
                    .map(RoleModel::getName)
                    .collect(Collectors.toSet());

            if (!groupRoles.isEmpty()) {
                // If company already exists, add these roles to the existing set
                // Otherwise create a new set with these roles
                companyRolesMap.computeIfAbsent(companyName, k -> new HashSet<>())
                        .addAll(groupRoles);
            }

        });

        // Convert Set<String> to List<String> for each company
        Map<String, List<String>> finalCompanyRolesMap = new HashMap<>();
        companyRolesMap.forEach((company, roles) -> {
            finalCompanyRolesMap.put(company, new ArrayList<>(roles));
        });
        logger.info("Adding to the claim name "+claimName+" with the values "+finalCompanyRolesMap);
        // Set the claim in the token
        //token.setOtherClaims(claimName, finalCompanyRolesMap);
        //logger.info("The token set is "+token.getOtherClaims());
        OIDCAttributeMapperHelper.mapClaim(token, mappingModel,finalCompanyRolesMap );
    }

    /**
     * Helper method to get a single attribute value from a group
     */
    private String getSingleAttribute(GroupModel group, String attributeName) {
        List<String> attributeValues = group.getAttributes().getOrDefault(attributeName, Collections.emptyList());
        return attributeValues.isEmpty() ? null : attributeValues.get(0);
    }

//    @Override
//    public boolean includeInIDToken() {
//        return true;
//    }
//
//    @Override
//    public boolean includeInAccessToken() {
//        return true;
//    }
//
//    @Override
//    public boolean includeInUserInfo() {
//        return true;
//    }
}
