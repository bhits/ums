package gov.samhsa.c2s.ums.service;

import gov.samhsa.c2s.ums.domain.UserRepository;
import gov.samhsa.c2s.ums.service.dto.UserDto;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    UserRepository  userRepository;

    @Override
    public void saveUser(UserDto consentDto) {

    }
}
