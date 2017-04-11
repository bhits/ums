package gov.samhsa.c2s.ums.web;

import gov.samhsa.c2s.ums.service.UserService;
import gov.samhsa.c2s.ums.service.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

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

}
