package com.fk.platform.mapper;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.keycloak.models.*;
import org.keycloak.protocol.oidc.mappers.OIDCAttributeMapperHelper;
import org.keycloak.representations.IDToken;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.*;
import java.util.stream.Stream;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CompanyRoleMapperTest extends TestCase {
    private CompanyRoleMapper mapper;
    private ProtocolMapperModel mappingModel;

    @Mock
    private KeycloakSession session;

    @Mock
    private UserSessionModel userSession;

    @Mock
    private UserModel user;

    @Mock
    private ClientSessionContext clientSessionContext;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        mapper = new CompanyRoleMapper();

        // Set up the mapping model with our configuration
        mappingModel = new ProtocolMapperModel();
        Map<String, String> config = new HashMap<>();
        config.put(CompanyRoleMapper.CLAIM_NAME, "companyRoles");
        config.put("company.attribute", "companyname");
        config.put(OIDCAttributeMapperHelper.INCLUDE_IN_ID_TOKEN, "true");
        config.put(OIDCAttributeMapperHelper.INCLUDE_IN_ACCESS_TOKEN, "true");
        mappingModel.setConfig(config);

        // Set up mock user with roles
        when(userSession.getUser()).thenReturn(user);
    }

    @Test
    public void testGroupsWithCompanyAttribute() {
        // Create mock groups
        GroupModel group1 = mockGroup("companyAAdmin", "CompanyA",
                Arrays.asList("admin", "user"));

        GroupModel group2 = mockGroup("companyAEditor", "CompanyA",
                Arrays.asList("editor"));

        GroupModel group3 = mockGroup("companyBViewer", "CompanyB",
                Arrays.asList("viewer"));

        GroupModel group4 = mockGroup("noCompanyGroup", null,
                Arrays.asList("role1", "role2"));

        // Set up the user's groups
        when(user.getGroupsStream()).thenReturn(
                Stream.of(group1, group2, group3, group4)
        );

        // Create a token to populate
        IDToken token = new IDToken();

        // Call the setClaim method
        mapper.setClaim(token, mappingModel, userSession, session, clientSessionContext);

        // Verify the results
        @SuppressWarnings("unchecked")
        Map<String, List<String>> companyRoles = (Map<String, List<String>>) token.getOtherClaims().get("companyRoles");

        assertNotNull("Company roles claim should exist", companyRoles);
        assertEquals("Should have 3 companies", 3, companyRoles.size());

        // Verify CompanyA roles
        assertTrue("Should contain CompanyA", companyRoles.containsKey("CompanyA"));
        List<String> companyARoles = companyRoles.get("CompanyA");
        assertEquals("CompanyA should have 3 unique roles", 3, companyARoles.size());
        assertTrue("CompanyA should have admin role", companyARoles.contains("admin"));
        assertTrue("CompanyA should have user role", companyARoles.contains("user"));
        assertTrue("CompanyA should have editor role", companyARoles.contains("editor"));

        // Verify CompanyB roles
        assertTrue("Should contain CompanyB", companyRoles.containsKey("CompanyB"));
        List<String> companyBRoles = companyRoles.get("CompanyB");
        assertEquals("CompanyB should have 1 role", 1, companyBRoles.size());
        assertTrue("CompanyB should have viewer role", companyBRoles.contains("viewer"));

        // Verify defaultCompany roles
        assertTrue("Should contain defaultCompany", companyRoles.containsKey("defaultCompany"));
        List<String> defaultCompanyRoles = companyRoles.get("defaultCompany");
        assertEquals("defaultCompanyRoles should have 2 role", 2, defaultCompanyRoles.size());
        assertTrue("defaultCompanyRoles should have role1 role", defaultCompanyRoles.contains("role1"));
        assertTrue("defaultCompanyRoles should have role2 role", defaultCompanyRoles.contains("role2"));


    }

    @Test
    public void testCustomCompanyAttribute() {
        // Change the company attribute name
        mappingModel.getConfig().put("company.attribute", "organization");

        // Create mock groups with the new attribute name
        GroupModel group1 = mockGroupWithCustomAttribute("group1", "organization", "Org1",
                Arrays.asList("admin"));

        GroupModel group2 = mockGroupWithCustomAttribute("group2", "organization", "Org2",
                Arrays.asList("editor"));

        // Set up the user's groups
        when(user.getGroupsStream()).thenReturn(Stream.of(group1, group2));

        // Create a token to populate
        IDToken token = new IDToken();

        // Call the setClaim method
        mapper.setClaim(token, mappingModel, userSession, session, clientSessionContext);

        // Verify the results
        @SuppressWarnings("unchecked")
        Map<String, List<String>> companyRoles = (Map<String, List<String>>) token.getOtherClaims().get("companyRoles");

        assertNotNull("Company roles claim should exist", companyRoles);
        assertEquals("Should have 2 organizations", 2, companyRoles.size());

        // Verify Org1 roles
        assertTrue("Should contain Org1", companyRoles.containsKey("Org1"));
        List<String> org1Roles = companyRoles.get("Org1");
        assertEquals("Org1 should have 1 role", 1, org1Roles.size());
        assertTrue("Org1 should have admin role", org1Roles.contains("admin"));

        // Verify Org2 roles
        assertTrue("Should contain Org2", companyRoles.containsKey("Org2"));
        List<String> org2Roles = companyRoles.get("Org2");
        assertEquals("Org2 should have 1 role", 1, org2Roles.size());
        assertTrue("Org2 should have editor role", org2Roles.contains("editor"));
    }

    // Helper method to create a mock group with roles and company attribute
    private GroupModel mockGroup(String name, String companyName, List<String> roleNames) {
        GroupModel group = mock(GroupModel.class);
        when(group.getName()).thenReturn(name);

        // Set up company attribute
        Map<String, List<String>> attributes = new HashMap<>();
        if (companyName != null) {
            attributes.put("companyname", Collections.singletonList(companyName));
        }
        when(group.getAttributes()).thenReturn(attributes);

        // Set up roles
        Set<RoleModel> roles = new HashSet<>();
        for (String roleName : roleNames) {
            RoleModel role = mock(RoleModel.class);
            when(role.getName()).thenReturn(roleName);
            roles.add(role);
        }
        when(group.getRoleMappingsStream()).thenReturn(roles.stream());

        return group;
    }

    // Helper method to create a mock group with custom attribute
    private GroupModel mockGroupWithCustomAttribute(String name, String attributeName,
                                                    String attributeValue, List<String> roleNames) {
        GroupModel group = mock(GroupModel.class);
        when(group.getName()).thenReturn(name);

        // Set up custom attribute
        Map<String, List<String>> attributes = new HashMap<>();
        attributes.put(attributeName, Collections.singletonList(attributeValue));
        when(group.getAttributes()).thenReturn(attributes);

        // Set up roles
        Set<RoleModel> roles = new HashSet<>();
        for (String roleName : roleNames) {
            RoleModel role = mock(RoleModel.class);
            when(role.getName()).thenReturn(roleName);
            roles.add(role);
        }
        when(group.getRoleMappingsStream()).thenReturn(roles.stream());

        return group;
    }
}