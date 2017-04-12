package gov.samhsa.c2s.ums.service;

import gov.samhsa.c2s.ums.service.dto.UserDto;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.StringTokenizer;

public interface UserService {
    @Transactional
    void saveUser(UserDto consentDto);

    @Transactional
    void disableUser(Long userId);

    @Transactional
    void updateUser(Long userId, UserDto userDto);

    @Transactional(readOnly = true)
    Object getUser(Long userId);

    @Transactional(readOnly = true)
    Page<UserDto> getAllUsers(Optional<Integer> page, Optional<Integer> size);

    @Transactional(readOnly = true)
    List<UserDto> searchUsersByFirstNameAndORLastName(StringTokenizer token, Optional<Integer> page, Optional<Integer> size);

    @Transactional(readOnly = true)
    List<UserDto> searchUsersByDemographic(String firstName, String lastName, Date birthDate, String genderCode, Optional<Integer> page, Optional<Integer> size);

    //Todo: Get all Users by role type

    //Todo: Get User based on uaa_users_id

}
