package gov.samhsa.c2s.ums.infrastructure;

import gov.samhsa.c2s.ums.config.ApplicationContextConfig;
import gov.samhsa.c2s.ums.domain.Scope;
import gov.samhsa.c2s.ums.domain.UserActivation;
import gov.samhsa.c2s.ums.domain.UserScopeAssignment;
import gov.samhsa.c2s.ums.domain.UserScopeAssignmentRepository;
import gov.samhsa.c2s.ums.infrastructure.dto.IdentifierDto;
import gov.samhsa.c2s.ums.infrastructure.dto.SearchResultsWrapperWithId;
import gov.samhsa.c2s.ums.infrastructure.exception.IdCannotBeFoundException;
import gov.samhsa.c2s.ums.service.UserService;
import gov.samhsa.c2s.ums.service.dto.TelecomDto;
import gov.samhsa.c2s.ums.service.dto.UpdateUserLimitedFieldsDto;
import gov.samhsa.c2s.ums.service.dto.UserDto;
import gov.samhsa.c2s.ums.service.dto.UsernameUsedDto;
import org.cloudfoundry.identity.uaa.resources.SearchResults;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
    private UserService userService;

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
            ScimGroupMember scimGroupMember = new ScimGroupMember(userActivation.getUser().getUserAuthId());
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
    public void inactivateUser(String userId) {
        //get scim user by userId
        ScimUser scimUser = restTemplate.getForObject(usersEndpoint + "/{userId}", ScimUser.class, userId);

        //set scimUser as inactive
        scimUser.setActive(false);

        HttpHeaders headers = new HttpHeaders();
        headers.set("If-Match", String.valueOf(scimUser.getVersion()));
        HttpEntity<ScimUser> entity = new HttpEntity<ScimUser>(scimUser, headers);

        restTemplate.put(usersEndpoint + "/" + userId, entity);

    }

    @Override
    public void activateUser(String userId) {
        //get scim user by userId
        final ScimUser scimUser = restTemplate.getForObject(usersEndpoint + "/{userId}", ScimUser.class, userId);

        //set scimUser as active
        scimUser.setActive(true);
        HttpHeaders headers = new HttpHeaders();
        headers.set("If-Match", String.valueOf(scimUser.getVersion()));
        HttpEntity<ScimUser> entity = new HttpEntity<ScimUser>(scimUser, headers);

        restTemplate.put(usersEndpoint + "/" + userId, entity);
    }

    @Override
    public void updateUserBasicInfo(String userId, UserDto userDto) {
        //Assert arguments
        Assert.hasText(userId, "User ID must exist");
        Assert.notNull(userDto, "UserDto cannot be null");

        //Get scim user by userId
        ScimUser scimUser = restTemplate.getForObject(usersEndpoint + "/{userId}", ScimUser.class, userId);

        //Set the value of first name and last name in uaa
        scimUser.getName().setGivenName(userDto.getFirstName());
        scimUser.getName().setFamilyName(userDto.getLastName());

        //Get the value of email in telecomDto
        Optional<TelecomDto> telecomDto = userDto.getTelecoms().stream().filter(telecomDto1 -> telecomDto1.getSystem().toString().equals("EMAIL")).findFirst();

        if (telecomDto.isPresent()) {
            List<ScimUser.Email> emails = new ArrayList();
            String email = telecomDto.get().getValue();
            ScimUser.Email emailToAdd = new ScimUser.Email();
            emailToAdd.setValue(email);
            emails.add(emailToAdd);
            //Set the updated email
            scimUser.setEmails(emails);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("If-Match", String.valueOf(scimUser.getVersion()));
        HttpEntity<ScimUser> entity = new HttpEntity<ScimUser>(scimUser, headers);

        restTemplate.put(usersEndpoint + "/" + userId, entity);
    }

    @Override
    public void updateUserLimitedInfo(String userId, UpdateUserLimitedFieldsDto updateUserLimitedFieldsDto) {
        //Assert arguments
        Assert.notNull(updateUserLimitedFieldsDto, "UserDto cannot be null");

        //Get scim user by userId
        ScimUser scimUser = restTemplate.getForObject(usersEndpoint + "/{userId}", ScimUser.class, userId);

        //Get the value of email
        List<ScimUser.Email> emails = new ArrayList();
        String email = updateUserLimitedFieldsDto.getHomeEmail();
        ScimUser.Email emailToAdd = new ScimUser.Email();
        emailToAdd.setValue(email);
        emails.add(emailToAdd);
        //Set the updated email
        scimUser.setEmails(emails);

        //Get the value of phoneNumber
        List<ScimUser.PhoneNumber> phoneNumbers = new ArrayList<>();
        String phoneNumber = updateUserLimitedFieldsDto.getHomePhone();
        ScimUser.PhoneNumber phoneNumberToAdd = new ScimUser.PhoneNumber();
        phoneNumberToAdd.setValue(phoneNumber);
        //Set the updated phoneNumber
        phoneNumbers.add(phoneNumberToAdd);

        HttpHeaders headers = new HttpHeaders();
        headers.set("If-Match", String.valueOf(scimUser.getVersion()));
        HttpEntity<ScimUser> entity = new HttpEntity<ScimUser>(scimUser, headers);

        restTemplate.put(usersEndpoint + "/" + userId, entity);
    }

    @Override
    public void updateUserWithNewGroup(UserActivation userActivation, Scope scope) {
        ScimGroupMember scimGroupMember = new ScimGroupMember(userActivation.getUser().getUserAuthId());
        String groupId = findGroupIdByDisplayName(scope.getScopeName());
        final ScimGroupMember scimGroupMemberResponse = restTemplate.postForObject(groupsEndpoint + "/{groupId}/members", scimGroupMember, ScimGroupMember.class, groupId);
    }

    @Override
    public UsernameUsedDto checkUsername(String username) {
        String filter = "?filter=userName eq \"" + username + "\"";
        SearchResults<ScimUser> searchResults = restTemplate.getForObject(usersEndpoint + filter, SearchResults.class);
        if (searchResults.getTotalResults() == 0)
            return new UsernameUsedDto(false);
        else
            return new UsernameUsedDto(true);
    }
}