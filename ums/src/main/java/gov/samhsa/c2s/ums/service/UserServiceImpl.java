package gov.samhsa.c2s.ums.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.samhsa.c2s.ums.config.UmsProperties;
import gov.samhsa.c2s.ums.domain.User;
import gov.samhsa.c2s.ums.domain.UserRepository;
import gov.samhsa.c2s.ums.service.dto.UserDto;
import gov.samhsa.c2s.ums.service.exception.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.StringTokenizer;

import static java.util.stream.Collectors.toList;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository  userRepository;

    private final ModelMapper modelMapper;

    private final ObjectMapper objectMapper;

    @Autowired
    private UmsProperties umsProperties;

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
    public void disableUser(Long userId){
        //TODO:
    }

    @Override
    public void updateUser(Long userId, UserDto userDto){}


    @Override
    public Object getUser(Long userId) {
        final User user = userRepository.findOneByIdAndIsDisabled(userId, false)
                .orElseThrow(UserNotFoundException::new);
        return modelMapper.map(user,UserDto.class);
    }

    @Override
    public Page<UserDto> getAllUsers(Optional<Integer> page, Optional<Integer> size){
        final PageRequest pageRequest = new PageRequest(page.filter(p -> p >= 0).orElse(0),
                size.filter(s -> s > 0 && s <= umsProperties.getUser().getPagination().getMaxSize())
                                                            .orElse(umsProperties.getUser().getPagination().getDefaultSize()));
        final Page<User> usersPage = userRepository.findAllAndIsDisabled(false, pageRequest);
        final List<User> userList = usersPage.getContent();
        final List<UserDto> userDtoList = userListToUserDtoList(userList);
        Page<UserDto> newPage = new PageImpl<>(userDtoList, pageRequest, usersPage.getTotalElements());
        return newPage;
    }

    @Override
    public List<UserDto> searchUsersByFirstNameAndORLastName(StringTokenizer token){

        List<User> userList;
        Integer pageNumber = 0;
        Pageable pageRequest = new PageRequest(pageNumber, umsProperties.getUser().getPagination().getDefaultSize());

        if (token.countTokens() == 1) {
            String firstName = token.nextToken(); // First Token could be first name or the last name
            userList = userRepository.findAllByFirstNameLikesOrLastNameLikesAndIsDisabled("%" + firstName + "%", false, pageRequest);
        } else if (token.countTokens() >= 2) {
            String firstName = token.nextToken(); // First Token is the first name
            String lastName = token.nextToken();  // Last Token is the last name
            userList = userRepository.findAllByFirstNameLikesAndLastNameLikesAndIsDisabled("%" + firstName + "%", "%" + lastName + "%", false, pageRequest);
        } else {
            userList = new ArrayList<>();
        }
        return userListToUserDtoList(userList);
    }

    @Override
    public List<UserDto> searchUsersByDemographic(String firstName,
                                                  String lastName,
                                                  Date birthDate,
                                                  String genderCode,
                                                  Optional<Integer> page,
                                                  Optional<Integer> size){
        return null;
    }

    private List<UserDto> userListToUserDtoList(List<User> userList){
        List<UserDto> userDtoList = new ArrayList<>();

        if(userList!= null && userList.size() > 0){
            for (User tempUser:userList){
                userDtoList.add(modelMapper.map(tempUser,UserDto.class));
            }
        }
        return userDtoList;
    }

}
