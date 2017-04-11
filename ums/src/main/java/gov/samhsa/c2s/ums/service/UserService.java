package gov.samhsa.c2s.ums.service;

import gov.samhsa.c2s.ums.service.dto.UserDto;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

public interface UserService {
    @Transactional
    void saveUser(UserDto consentDto);

    @Transactional
    void deleteUser(Long userId);

    @Transactional
    void updateUser(Long userId, UserDto userDto);

    @Transactional(readOnly = true)
    Object getUser(Long userId);

    @Transactional(readOnly = true)
    List<UserDto> getAllUsers();

    @Transactional(readOnly = true)
    List<UserDto> searchUsersByFirstNameAndLastName(StringTokenizer token);

    @Transactional(readOnly = true)
    List<UserDto> searchUsersByDemographic(String firstName, String lastName, Date birthDate, String genderCode);



}
