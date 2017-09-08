package gov.samhsa.c2s.ums.infrastructure;

import gov.samhsa.c2s.ums.domain.Scope;
import gov.samhsa.c2s.ums.domain.UserActivation;
import gov.samhsa.c2s.ums.service.dto.UserDto;
import gov.samhsa.c2s.ums.service.dto.UsernameUsedDto;
import org.cloudfoundry.identity.uaa.scim.ScimGroupMember;
import org.cloudfoundry.identity.uaa.scim.ScimUser;

public interface ScimService {
    ScimUser save(ScimUser scimUser);

    String findGroupIdByDisplayName(String groupDisplayName);

    String findUserIdByUserName(String username);

    ScimGroupMember addUserToGroup(UserActivation userActivation, Scope scope, String groupId);

    void addUserToGroups(UserActivation userActivation);

    void inactivateUser(String userId);

    void activateUser(String userId);

    void updateUserBasicInfo(String userId,UserDto userDto);

    void updateUserWithNewGroup(UserActivation userActivation, Scope scope);

    UsernameUsedDto checkUsername(String username);
}