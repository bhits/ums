package gov.samhsa.c2s.ums.service;

import gov.samhsa.c2s.ums.domain.User;
import gov.samhsa.c2s.ums.domain.UserRepository;
import gov.samhsa.c2s.ums.service.dto.UserDto;
import gov.samhsa.c2s.ums.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserRepository  userRepository;

    @Override
    public void saveUser(UserDto consentDto) {

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
