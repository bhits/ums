package gov.samhsa.c2s.ums.infrastructure;

import gov.samhsa.c2s.ums.domain.Scope;
import gov.samhsa.c2s.ums.domain.User;
import gov.samhsa.c2s.ums.domain.UserActivation;
import gov.samhsa.c2s.ums.domain.UserScopeAssignment;
import gov.samhsa.c2s.ums.domain.UserScopeAssignmentRepository;
import gov.samhsa.c2s.ums.infrastructure.dto.IdentifierDto;
import gov.samhsa.c2s.ums.infrastructure.dto.SearchResultsWrapperWithId;
import gov.samhsa.c2s.ums.infrastructure.exception.IdCannotBeFoundException;
import org.cloudfoundry.identity.uaa.scim.ScimGroupMember;
import org.cloudfoundry.identity.uaa.scim.ScimUser;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.client.RestOperations;

import java.util.Arrays;

import static gov.samhsa.c2s.common.unit.matcher.ArgumentMatchers.matching;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ScimServiceImplTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private String scimBaseUrl="scimBaseUrl";

    private String usersEndpoint=scimBaseUrl+"/Users";;

    private String groupsEndpoint=scimBaseUrl+"/Groups";

    @Mock
    private UserScopeAssignmentRepository userScopeAssignmentRepository;

    @Mock
    private RestOperations restTemplate;

    @InjectMocks
    ScimServiceImpl scimServiceImpl=new ScimServiceImpl(scimBaseUrl);

    @Test
    public void testSave(){
        ScimUser scimUser=new ScimUser();
        ScimUser scimUser1=new ScimUser();
        scimUser1.setId("id");

        when(restTemplate.postForObject(usersEndpoint,scimUser,ScimUser.class)).thenReturn(scimUser1);

        //Act
        ScimUser save=scimServiceImpl.save(scimUser);

        //assert
        assertEquals(scimUser1,save);
    }

    @Test
    public void testFindGroupIdByDisplayName(){
        //Arrange
        final String id="id";
        final String groupDisplayName="groupDisplayName";
        SearchResultsWrapperWithId searchResultsMock=mock(SearchResultsWrapperWithId.class);
        when(restTemplate.getForObject(groupsEndpoint + "?filter=displayName eq \"" + groupDisplayName + "\"&attributes=id", SearchResultsWrapperWithId.class)).thenReturn(searchResultsMock);
        IdentifierDto identifierDto=mock(IdentifierDto.class);
        when(searchResultsMock.getResources()).thenReturn(Arrays.asList(identifierDto));
        when(identifierDto.getId()).thenReturn(id);

        //Act
       String groupId= scimServiceImpl.findGroupIdByDisplayName(groupDisplayName);

        //Assert
        assertEquals(id,groupId);

    }

    @Test
    public void testFindGroupIdByDisplayName_WhenIdCannotBeFound_ThrowsException() throws Exception{
        thrown.expect(IdCannotBeFoundException.class);

        //arrange
        final String groupDisplayName="groupDisplayName";
        SearchResultsWrapperWithId searchResultsMock=mock(SearchResultsWrapperWithId.class);
        when(restTemplate.getForObject(groupsEndpoint + "?filter=displayName eq \"" + groupDisplayName + "\"&attributes=id", SearchResultsWrapperWithId.class)).thenReturn(searchResultsMock);
        IdentifierDto identifierDto=mock(IdentifierDto.class);
        when(searchResultsMock.getResources()).thenReturn(Arrays.asList(identifierDto));
        when(identifierDto.getId()).thenReturn(null);

        //Act
        String groupId= scimServiceImpl.findGroupIdByDisplayName(groupDisplayName);

        //Assert
        assertNull(groupId);

    }

    @Test
    public void testFindUserIdByUserName(){
        final String username="username";
        final String id="id";
        final SearchResultsWrapperWithId searchResultsMock=mock(SearchResultsWrapperWithId.class);
        when(restTemplate.getForObject(usersEndpoint + "?filter=userName eq \"" + username + "\"&attributes=id", SearchResultsWrapperWithId.class)).thenReturn(searchResultsMock);
        IdentifierDto identifierDto=mock(IdentifierDto.class);
        when(searchResultsMock.getResources()).thenReturn(Arrays.asList(identifierDto));
        when(identifierDto.getId()).thenReturn(id);

        //Act
        String userId=scimServiceImpl.findUserIdByUserName(username);

        //Assert
        assertEquals(id,userId);
    }

    @Test
    public void testAddUserToGroup(){
        //Arrange
        String groupId="groupId";
        UserScopeAssignment userScopeAssignment=mock(UserScopeAssignment.class);
        Scope scope=mock(Scope.class);
        UserActivation userActivation=mock(UserActivation.class);
        ScimGroupMember scimGroupMemberResponse=mock(ScimGroupMember.class);
        User user=mock(User.class);
        final String id="id";
        when(userActivation.getUser()).thenReturn(user);
        when(user.getUserAuthId()).thenReturn(id);

        when(restTemplate.postForObject(eq(groupsEndpoint + "/{groupId}/members"), argThat(matching((ScimGroupMember member)->member.getMemberId().equals(id))), eq(ScimGroupMember.class), eq(groupId))).thenReturn(scimGroupMemberResponse);

        //Act
      final ScimGroupMember response= scimServiceImpl.addUserToGroup(userActivation,scope,groupId);

      //Assert
      assertEquals(scimGroupMemberResponse,response);

      verify(userActivation).getUser();
      verify(user).getUserAuthId();
    }
}
