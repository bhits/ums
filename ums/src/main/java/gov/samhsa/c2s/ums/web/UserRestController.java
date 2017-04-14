package gov.samhsa.c2s.ums.web;

import gov.samhsa.c2s.ums.service.UserService;
import gov.samhsa.c2s.ums.service.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.StringTokenizer;

@RestController
@RequestMapping("/users")
public class UserRestController {
    @Autowired
    UserService userService;

    @PostMapping("/")
    // @ResponseStatus(HttpStatus.CREATED)
    public void saveUser(@Valid @RequestBody UserDto userDto) {
        userService.saveUser(userDto);
    }


    /**
     * Disable User
     * @param userId
     */
    @PutMapping("/disable/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void disableUser(@PathVariable Long userId) {
        userService.disableUser(userId);
    }

    /**
     * Update User
     * @param userId
     * @param userDto
     */
    @PutMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void updateUser(@PathVariable Long userId, @Valid @RequestBody UserDto userDto) {
        userService.updateUser(userId, userDto);
    }

    /**
     * Get User
     * @param userId
     * @return
     */
    @GetMapping("/{userId}")
    public Object getUser(@PathVariable Long userId){
        return userService.getUser(userId);
    }

    /**
     * Get User based on OAuth2 User Id
     * @param oAuth2UserId
     * @return
     */
    @GetMapping("/OAuth2/{oAuth2UserId}")
    public Object getUserByOAuth2Id(@PathVariable String oAuth2UserId){
        return userService.getUserByOAuth2Id(oAuth2UserId);
    }

    /**
     * Find All Users
     * @return
     */
    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public Page<UserDto> getAllUsers(@RequestParam Optional<Integer> page,
                                     @RequestParam Optional<Integer> size) {
        return userService.getAllUsers(page, size);
    }

    /**
     * Find All Users that match the First Name and/or the Last Name.
     * @param token
     * @return
     */
    @GetMapping(value = "/search/{token}")
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> searchUsersByFirstNameAndORLastName(@PathVariable String token) {
        StringTokenizer tokenizer = new StringTokenizer(token, " ");
        return userService.searchUsersByFirstNameAndORLastName(tokenizer);
    }

    /**
     *
     * @param firstName
     * @param lastName
     * @param birthDate
     * @param genderCode
     * @return
     */
    @GetMapping(value = "/search/patientDemographic")
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> searchUsersByDemographic(@RequestParam("firstName") String firstName,
                                                  @RequestParam("lastName") String lastName,
                                                  @RequestParam("birthDate") @DateTimeFormat(pattern = "MM/dd/yyyy") Date birthDate,
                                                  @RequestParam("genderCode") String genderCode) {
        return userService.searchUsersByDemographic(firstName, lastName, birthDate, genderCode);
    }

}
