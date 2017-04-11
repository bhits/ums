package gov.samhsa.c2s.ums.service;

import gov.samhsa.c2s.ums.service.dto.UserDto;
import org.springframework.transaction.annotation.Transactional;

public interface UserService {
    @Transactional
    void saveUser(UserDto consentDto);

    @Transactional
    void deleteUser(Long userId);

    @Transactional
    void updateUser(Long userId, UserDto userDto);

    @Transactional(readOnly = true)
    Object getUser(Long userId);

}
