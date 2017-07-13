package gov.samhsa.c2s.ums.web;

import gov.samhsa.c2s.ums.service.UserService;
import gov.samhsa.c2s.ums.service.dto.AccessDecisionDto;
import gov.samhsa.c2s.ums.service.dto.UpdateUserLimitedFieldsDto;
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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.StringTokenizer;

@RestController
@RequestMapping("/users")
public class UserRestController {

    @Autowired
    private UserService userService;

    /**
     * @param userDto User Dto Object
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto registerUser(@Valid @RequestBody UserDto userDto) {
        return userService.registerUser(userDto);
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
    public void updateUserLocaleByUserAuthId(@RequestParam String userAuthId, @RequestHeader("Accept-Language") Locale locale) {
        userService.updateUserLocaleByUserAuthId(userAuthId, locale.getLanguage());
    }

    @GetMapping("/accessDecision")
    @ResponseStatus(HttpStatus.OK)
    public AccessDecisionDto accessDecision(@RequestParam String userAuthId, @RequestParam String patientMRN) {
        return userService.accessDecision(userAuthId, patientMRN);
    }

    /**
     * Update User
     *
     * @param userId  PK of User
     * @param userDto User Dto Object
     */
    @PutMapping("/{userId}")
    public UserDto updateUser(@PathVariable Long userId, @Valid @RequestBody UserDto userDto) {
        return userService.updateUser(userId, userDto);
    }

    /**
     * Update the following fields for a user:
     * <ul>
     *     <li>UserDto.addresses (only the home address)</li>
     *     <li>UserDto.telecoms (only the home phone number & home e-mail address</li>
     *     <li>UserDto.locale</li>
     * </ul>
     *
     * @param userId  PK of User
     * @param updateUserLimitedFieldsDto - the UpdateUserLimitedFieldsDto object containing the new values for the user fields to be updated
     * @see UpdateUserLimitedFieldsDto
     */
    @PutMapping("/{userId}/limitedFields")
    public UserDto updateUserLimitedFields(@PathVariable Long userId, @Valid @RequestBody UpdateUserLimitedFieldsDto updateUserLimitedFieldsDto) {
        return userService.updateUserLimitedFields(userId, updateUserLimitedFieldsDto);
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
                                     @RequestParam("size") Optional<Integer> size,
                                     @RequestParam("role") Optional<String> roleCode) {
        return userService.getAllUsers(page, size, roleCode);
    }

    @GetMapping(value = "/authId/{userAuthId}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto getUserById(@PathVariable("userAuthId") String userAuthId) {
        //Get User based on User AUTH Id
        if (userAuthId != null)
            return userService.getUserByUserAuthId(userAuthId);
        else return null;
    }

    //TODO:Based on Tao's comment:Code need be refractor to put StringTokenizer in the service class
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
    public Page<UserDto> searchUsersByDemographic(@RequestParam(value = "firstName", required = false) String firstName,
                                                  @RequestParam(value = "lastName", required = false) String lastName,
                                                  @RequestParam(value = "birthDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate birthDate,
                                                  @RequestParam(value = "genderCode", required = false) String genderCode,
                                                  @RequestParam(value = "mrn", required = false) String mrn,
                                                  @RequestParam(value = "roleCode", required = false) String roleCode,
                                                  @RequestParam("page") Optional<Integer> page,
                                                  @RequestParam("size") Optional<Integer> size) {
        return userService.searchUsersByDemographic(firstName, lastName, birthDate, genderCode,mrn, roleCode,page, size);
    }

    @GetMapping("/search/identifier")
    public List<UserDto> searchUsersByIdentifier(@RequestParam String value, @RequestParam String system) {
        return userService.searchUsersByIdentifier(value, system);
    }

}
