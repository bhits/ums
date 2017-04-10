package gov.samhsa.c2s.ums.service;

import gov.samhsa.c2s.ums.service.dto.UserDto;
import org.springframework.transaction.annotation.Transactional;

public interface UserService {
    @Transactional
    void saveUser(UserDto consentDto);


}
