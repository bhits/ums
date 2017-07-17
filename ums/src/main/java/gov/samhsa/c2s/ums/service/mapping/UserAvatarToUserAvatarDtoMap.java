package gov.samhsa.c2s.ums.service.mapping;

import gov.samhsa.c2s.ums.domain.UserAvatar;
import gov.samhsa.c2s.ums.service.dto.UserAvatarDto;
import org.modelmapper.PropertyMap;
import org.springframework.stereotype.Component;

@Component
public class UserAvatarToUserAvatarDtoMap extends PropertyMap<UserAvatar, UserAvatarDto> {

    @Override
    protected void configure() {
        map().setId(source.getId());
        map().setUserId(source.getUser().getId());
        map().setFileContents(source.getFileContents());
        map().setFileName(source.getFileName());
        map().setFileExtension(source.getFileExtension());
        map().setFileHeightPixels(source.getFileHeightPixels());
        map().setFileWidthPixels(source.getFileWidthPixels());
        map().setFileSizeBytes(source.getFileSizeBytes());
    }
}
