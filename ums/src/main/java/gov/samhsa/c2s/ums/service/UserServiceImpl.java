package gov.samhsa.c2s.ums.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.samhsa.c2s.ums.domain.User;
import gov.samhsa.c2s.ums.domain.UserRepository;
import gov.samhsa.c2s.ums.service.dto.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserServiceImpl implements UserService {


    private final UserRepository  userRepository;

    private final ModelMapper modelMapper;

    private final ObjectMapper objectMapper;

    @Autowired
    public UserServiceImpl(ModelMapper modelMapper, UserRepository userRepository, ObjectMapper objectMapper) {
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public void saveUser(UserDto userDto) {
        User user = modelMapper.map(userDto,User.class);
        try {
            log.debug(objectMapper.writeValueAsString(user));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

    }
}
