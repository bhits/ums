package gov.samhsa.c2s.ums.web;

import gov.samhsa.c2s.ums.service.UserAvatarService;
import gov.samhsa.c2s.ums.service.dto.UserAvatarDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user-avatars")
public class UserAvatarController {
    private final UserAvatarService userAvatarService;

    @Autowired
    public UserAvatarController(UserAvatarService userAvatarService) {
        this.userAvatarService = userAvatarService;
    }

    @GetMapping("/user/{userId}/avatar")
    public UserAvatarDto getUserAvatar(@PathVariable Long userId) {
        return userAvatarService.getUserAvatarByUserId(userId);
    }
}
