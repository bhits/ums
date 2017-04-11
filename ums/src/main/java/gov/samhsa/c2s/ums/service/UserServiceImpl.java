package gov.samhsa.c2s.ums.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.samhsa.c2s.ums.domain.User;
import gov.samhsa.c2s.ums.domain.User;
import gov.samhsa.c2s.ums.domain.UserRepository;
import gov.samhsa.c2s.ums.service.dto.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import gov.samhsa.c2s.ums.service.exception.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    UserRepository  userRepository;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void saveUser(UserDto userDto) {
        User user = modelMapper.map(userDto,User.class);
        try {
            log.debug(objectMapper.writeValueAsString(user));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

    }

    //TODO: Implement Delete User after studying the business requirements
    @Override
    public void deleteUser(Long userId){

    }

    @Override
    public void updateUser(Long userId, UserDto userDto){}


    @Override
    public Object getUser(Long userId) {
        final User user = userRepository.findOneByIdAndIsDeleted(userId, false).orElseThrow(UserNotFoundException::new);
        return toUserDto(user);
    }

    private UserDto toUserDto(User user){
        return UserDto.builder()
                .id(user.getId())
                .lastName(user.getLastName())
                .firstName(user.getFirstName())
                .email(user.getEmail())
                .birthDate(user.getBirthDay())
                .genderCode(user.getAdministrativeGenderCode().getDisplayName())
                .socialSecurityNumber(user.getSocialSecurityNumber())
                .telephone(user.getTelecom().getTelephone())
                .address(user.getAddress().getStreetAddressLine())
                .city(user.getAddress().getCity())
                .stateCode(user.getAddress().getStateCode().getDisplayName())
                .zip(user.getAddress().getPostalCode())
                .build();
    }

}
