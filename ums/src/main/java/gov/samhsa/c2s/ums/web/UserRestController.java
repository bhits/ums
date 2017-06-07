package gov.samhsa.c2s.ums.web;

import gov.samhsa.c2s.ums.service.UserService;
import gov.samhsa.c2s.ums.service.dto.AccessDecisionDto;
import gov.samhsa.c2s.ums.service.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.StringTokenizer;

@RestController
@RequestMapping("/users")
public class UserRestController {
    private final UserService userService;

    @Autowired
    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    /**
     * @param userDto User Dto Object
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void registerUser(@Valid @RequestBody UserDto userDto) {
        userService.registerUser(userDto);
    }


    /**
     * Enable User
     *
     * @param userId PK of User
     */
    @PutMapping("/{userId}/enabled")
    @ResponseStatus(HttpStatus.OK)
    public void enableUser(@PathVariable Long userId) {
        userService.enableUser(userId);
    }


    /**
     * Disable User
     *
     * @param userId PK of User
     */
    @PutMapping("/{userId}/disabled")
    @ResponseStatus(HttpStatus.OK)
    public void disableUser(@PathVariable Long userId) {
        userService.disableUser(userId);
    }

    /**
     * Update User locale by userAuthId
     *
     * @param userAuthId
     */

    @PutMapping("/locale")
    @ResponseStatus(HttpStatus.OK)
    public void updateUserLocaleByUserAuthId(@RequestParam String userAuthId,@RequestHeader("Accept-Language") Locale locale)
    {
        userService.updateUserLocaleByUserAuthId(userAuthId,locale.getLanguage());
    }

    @GetMapping("/accessDecision")
    @ResponseStatus(HttpStatus.OK)
    public AccessDecisionDto accessDecision(@RequestParam String userAuthId, @RequestParam String patientMRN)
    {
        return userService.accessDecision(userAuthId,patientMRN);
    }

    /**
     * Update User
     *
     * @param userId  PK of User
     * @param userDto User Dto Object
     */
    @PutMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void updateUser(@PathVariable Long userId, @Valid @RequestBody UserDto userDto) {
        userService.updateUser(userId, userDto);
    }

    /**
     * Get User
     *
     * @param userId Pk of User
     * @return User
     */
    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable Long userId) {
        return userService.getUser(userId);
    }


    /**
     * Find All Users
     *
     * @return User
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<UserDto> getAllUsers(@RequestParam("page") Optional<Integer> page,
                                     @RequestParam("size") Optional<Integer> size) {
        return userService.getAllUsers(page, size);
    }

    @GetMapping(value = "/authId/{userAuthId}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto getUserById(@PathVariable("userAuthId") String userAuthId) {
        //Get User based on User AUTH Id
        if (userAuthId != null)
            return userService.getUserByUserAuthId(userAuthId);
        else return null;
    }

    /**
     * Find All Users that match the First Name and/or the Last Name.
     *
     * @param term term
     * @return List of Users
     */
    @GetMapping(value = "/search")
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> searchUsersByFirstNameAndORLastName(@RequestParam("term") String term) {
        StringTokenizer tokenizer = new StringTokenizer(term, " ");
        return userService.searchUsersByFirstNameAndORLastName(tokenizer);
    }

    /**
     * @param firstName  First Name
     * @param lastName   Last Name
     * @param birthDate  Date Of Birth
     * @param genderCode Gender Code
     * @return List of Users
     */
    @GetMapping(value = "/search/patientDemographic")
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> searchUsersByDemographic(@RequestParam("firstName") String firstName,
                                                  @RequestParam("lastName") String lastName,
                                                  @RequestParam("birthDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate birthDate,
                                                  @RequestParam("genderCode") String genderCode) {
        return userService.searchUsersByDemographic(firstName, lastName, birthDate, genderCode);
    }

}
