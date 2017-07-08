package gov.samhsa.c2s.ums.service;

import gov.samhsa.c2s.ums.service.dto.UserAvatarDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public interface UserAvatarService {
    @Transactional(readOnly = true)
    UserAvatarDto getUserAvatarByUserId(Long userId);
}
