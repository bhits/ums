package gov.samhsa.c2s.ums.service;

import gov.samhsa.c2s.ums.service.dto.AccessDecisionDto;
import gov.samhsa.c2s.ums.service.dto.UserDto;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.StringTokenizer;

public interface UserService {
    @Transactional
    void registerUser(UserDto consentDto);

    @Transactional
    void disableUser(Long userId);

    @Transactional
    void enableUser(Long userId);

    @Transactional
    void updateUser(Long userId, UserDto userDto);

    @Transactional(readOnly = true)
    UserDto getUser(Long userId);

    @Transactional(readOnly = true)
    UserDto getUserByUserAuthId(String userAuthId);

    @Transactional(readOnly = true)
    Page<UserDto> getAllUsers(Optional<Integer> page, Optional<Integer> size);

    @Transactional(readOnly = true)
    List<UserDto> searchUsersByFirstNameAndORLastName(StringTokenizer token);

    @Transactional(readOnly = true)
    List<UserDto> searchUsersByDemographic(String firstName, String lastName, LocalDate birthDate, String genderCode);

    @Transactional(readOnly = true)
    List<UserDto> searchUsersByIdentifier(String value, String system);

    @Transactional
    void updateUserLocale(Long userId, String localeCode);

    @Transactional
    void updateUserLocaleByUserAuthId(String userAuthId, String localeCode);

    @Transactional(readOnly = true)
    AccessDecisionDto accessDecision(String userAuthId, String patientMRN);

    //Todo: Get all Users by role type

}
