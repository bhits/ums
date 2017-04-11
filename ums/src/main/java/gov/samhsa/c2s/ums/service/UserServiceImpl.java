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
        //ums.user.isdeted = true
        //delete from user.user_activation table
        //delete user_uaa_id from uaa.user
    }

    @Override
    public void updateUser(Long userId, UserDto userDto){}


    @Override
    public Object getUser(Long userId) {
        final User user = userRepository.findOnebyId(userId).orElseThrow(UserNotFoundException::new);
        return toUserDto(user);
    }

    private UserDto toUserDto(User user){
        return UserDto.builder()
                .id(user.getId())
                .lastName(user.getLastName())
                .firstName(user.getFirstName())
                .email(user.getEmail())
                .birthDate(user.getBirthDay())
                //.genderCode(user.getAdministrativeGenderCode())
                .socialSecurityNumber(user.getSocialSecurityNumber())
                //.telephone(user.getTelecom())
                //.address(user.getAddress())
                //.city()
                //.stateCode()
                //.zip(user.)
                .build();
    }

}
