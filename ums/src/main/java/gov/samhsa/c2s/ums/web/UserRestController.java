package gov.samhsa.c2s.ums.web;

import gov.samhsa.c2s.ums.service.UserService;
import gov.samhsa.c2s.ums.service.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/users")
public class UserRestController {
    @Autowired
    UserService userService;

    /**
     * Create User
     * @param userDto
     */
    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveUser(@Valid @RequestBody UserDto userDto) {
        userService.saveUser(userDto);
    }


    /**
     * Delete User
     * @param userId
     */
    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
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
}
