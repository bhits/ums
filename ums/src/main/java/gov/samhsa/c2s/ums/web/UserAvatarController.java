package gov.samhsa.c2s.ums.web;

import gov.samhsa.c2s.ums.service.UserAvatarService;
import gov.samhsa.c2s.ums.service.dto.AvatarBytesAndMetaDto;
import gov.samhsa.c2s.ums.service.dto.UserAvatarDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @PostMapping("/user/{userId}/avatar")
    public UserAvatarDto saveNewUserAvatar(
            @PathVariable Long userId,
            @RequestBody AvatarBytesAndMetaDto avatarFile,
            @RequestParam(value = "fileWidthPixels") Long fileWidthPixels,
            @RequestParam(value = "fileHeightPixels") Long fileHeightPixels
    ) {
        // TODO: Replace request params for width & height with values calculated directly from the uploaded file within the UserAvatarService

        return userAvatarService.saveUserAvatar(userId, avatarFile, fileWidthPixels, fileHeightPixels);
    }

    @PutMapping("/user/{userId}/avatar")
    public UserAvatarDto updateUserAvatar(
            @PathVariable Long userId,
            @RequestBody AvatarBytesAndMetaDto avatarFile,
            @RequestParam(value = "fileWidthPixels") Long fileWidthPixels,
            @RequestParam(value = "fileHeightPixels") Long fileHeightPixels
    ) {
        // TODO: Replace request params for width & height with values calculated directly from the uploaded file within the UserAvatarService

        return userAvatarService.updateUserAvatar(userId, avatarFile, fileWidthPixels, fileHeightPixels);
    }

    @DeleteMapping("/user/{userId}/avatar")
    public void deleteUserAvatar(@PathVariable Long userId) {
        userAvatarService.deleteUserAvatar(userId);
    }

}
