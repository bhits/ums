package gov.samhsa.c2s.ums.service;

import gov.samhsa.c2s.ums.service.dto.GetUserResponseDto;
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
    void updateUser(Long userId, UserDto userDto);

    @Transactional(readOnly = true)
    Object getUser(Long userId);

    @Transactional(readOnly = true)
    Object getUserByOAuth2Id(String oAuth2UserId);

    @Transactional(readOnly = true)
    Page<GetUserResponseDto> getAllUsers(Optional<Integer> page, Optional<Integer> size);

    @Transactional(readOnly = true)
    List<GetUserResponseDto> searchUsersByFirstNameAndORLastName(StringTokenizer token);

    @Transactional(readOnly = true)
    List<GetUserResponseDto> searchUsersByDemographic(String firstName, String lastName, LocalDate birthDate, String genderCode);

    //Todo: Get all Users by role type

}
