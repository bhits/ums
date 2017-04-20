package gov.samhsa.c2s.ums.infrastructure;

import gov.samhsa.c2s.ums.config.ApplicationContextConfig;
import gov.samhsa.c2s.ums.domain.Scope;
import gov.samhsa.c2s.ums.domain.UserActivation;
import gov.samhsa.c2s.ums.domain.UserScopeAssignment;
import gov.samhsa.c2s.ums.domain.UserScopeAssignmentRepository;
import gov.samhsa.c2s.ums.infrastructure.dto.IdentifierDto;
import gov.samhsa.c2s.ums.infrastructure.dto.SearchResultsWrapperWithId;
import gov.samhsa.c2s.ums.infrastructure.exception.IdCannotBeFoundException;
import org.cloudfoundry.identity.uaa.scim.ScimGroupMember;
import org.cloudfoundry.identity.uaa.scim.ScimUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestOperations;

import java.util.Objects;

@Service
public class ScimServiceImpl implements ScimService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final String scimBaseUrl;
    private final String usersEndpoint;
    private final String groupsEndpoint;

    @Autowired
    private UserScopeAssignmentRepository userScopeAssignmentRepository;

    @Autowired
    @Qualifier(ApplicationContextConfig.OAUTH2_REST_TEMPLATE_CLIENT_CREDENTIALS)
    private RestOperations restTemplate;

    @Autowired
    public ScimServiceImpl(@Value("${c2s.ums.scim.url}") String uaaBaseUrl) {
        Assert.hasText(uaaBaseUrl, "Missing SCIM endpoint");
        this.scimBaseUrl = uaaBaseUrl;
        this.usersEndpoint = this.scimBaseUrl + "/Users";
        this.groupsEndpoint = this.scimBaseUrl + "/Groups";
    }

    @Override
    public ScimUser save(ScimUser scimUser) {
        final ScimUser scimUserResp = restTemplate.postForObject(usersEndpoint, scimUser, ScimUser.class);
        return scimUserResp;
    }

    @Override
    public String findGroupIdByDisplayName(String groupDisplayName) {
        final SearchResultsWrapperWithId id = restTemplate.getForObject(groupsEndpoint + "?filter=displayName eq \"" + groupDisplayName + "\"&attributes=id", SearchResultsWrapperWithId.class);
        return extractId(id);
    }

    @Override
    public String findUserIdByUserName(String username) {
        final SearchResultsWrapperWithId id = restTemplate.getForObject(usersEndpoint + "?filter=userName eq \"" + username + "\"&attributes=id", SearchResultsWrapperWithId.class);
        return extractId(id);
    }

    @Override
    @Transactional
    public ScimGroupMember addUserToGroup(UserActivation userActivation, Scope scope, String groupId) {
        UserScopeAssignment userScopeAssignment = new UserScopeAssignment();
        ScimGroupMember scimGroupMemberResponse = null;
        try {
            userScopeAssignment.setUserActivation(userActivation);
            userScopeAssignment.setScope(scope);
            ScimGroupMember scimGroupMember = new ScimGroupMember(userActivation.getUser().getOauth2UserId());
            userScopeAssignment.setAssigned(true);
            userScopeAssignmentRepository.save(userScopeAssignment);
            scimGroupMemberResponse = restTemplate.postForObject(groupsEndpoint + "/{groupId}/members", scimGroupMember, ScimGroupMember.class, groupId);
        } catch (Exception e) {
            logger.error("Error in assigning scope to user in UAA.");
            userScopeAssignment.setAssigned(false);
            userScopeAssignmentRepository.save(userScopeAssignment);
        }

        return scimGroupMemberResponse;
    }

    @Override
    public void addUserToGroups(UserActivation userActivation) {
        userActivation.getUser().getRoles().stream().flatMap(role -> role.getScopes().stream()).distinct().forEach(scope -> addUserToGroup(userActivation, scope, findGroupIdByDisplayName(scope.getScopeName())));
    }

    private static final String extractId(SearchResultsWrapperWithId searchResultsWrapperWithId) {
        return searchResultsWrapperWithId.getResources().stream()
                .filter(Objects::nonNull)
                .map(IdentifierDto::getId)
                .filter(StringUtils::hasText)
                .findAny().orElseThrow(IdCannotBeFoundException::new);
    }

    @Override
    public void inactiveUser(String userId) {
        //get scim user by userId
        ScimUser scimUser = restTemplate.getForObject(usersEndpoint+"/{userId}",ScimUser.class,userId);

        //set scimUser to inactive
        scimUser.setActive(false);

        HttpHeaders headers = new HttpHeaders();
        headers.set("If-Match", String.valueOf(scimUser.getVersion()));
        HttpEntity<ScimUser> entity = new HttpEntity<ScimUser>(scimUser, headers);

        restTemplate.put(usersEndpoint+"/"+ userId,entity);

    }

    @Override
    public void activeUser(String userId) {
        //get scim user by userId
        final ScimUser scimUser = restTemplate.getForObject(usersEndpoint+"/{userId}", ScimUser.class,userId);

        //set scimUser to inactive
        scimUser.setActive(true);
        HttpHeaders headers = new HttpHeaders();
        headers.set("If-Match", String.valueOf(scimUser.getVersion()));
        HttpEntity<ScimUser> entity = new HttpEntity<ScimUser>(scimUser, headers);

        restTemplate.put(usersEndpoint+"/"+ userId,entity);
    }

    @Override
    public void updateUserWithNewGroup(UserActivation userActivation, Scope scope) {
        ScimGroupMember scimGroupMember = new ScimGroupMember(userActivation.getUser().getOauth2UserId());
        String groupId = findGroupIdByDisplayName(scope.getScopeName());
        final ScimGroupMember scimGroupMemberResponse = restTemplate.postForObject(groupsEndpoint + "/{groupId}/members", scimGroupMember, ScimGroupMember.class, groupId);
    }
}