package gov.samhsa.c2s.ums.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.samhsa.c2s.ums.domain.User;
import gov.samhsa.c2s.ums.domain.UserRepository;
import gov.samhsa.c2s.ums.service.dto.UserDto;
import gov.samhsa.c2s.ums.service.exception.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

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

    @Override
    public void deleteUser(Long userId){
        //TODO: Implement Delete User after studying the business requirements
    }

    @Override
    public void updateUser(Long userId, UserDto userDto){}


    @Override
    public Object getUser(Long userId) {
        final User user = userRepository.findOneByIdAndIsDeleted(userId, false).orElseThrow(UserNotFoundException::new);
        return modelMapper.map(user,UserDto.class);
    }

    @Override
    public List<UserDto> getAllUsers(){
        return null;
    }

    @Override
    public List<UserDto> searchUsersByFirstNameAndLastName(StringTokenizer token){
        return null;
    }

    @Override
    public List<UserDto> searchUsersByDemographic(String firstName, String lastName, Date birthDate, String genderCode){
        return null;
    }


}
