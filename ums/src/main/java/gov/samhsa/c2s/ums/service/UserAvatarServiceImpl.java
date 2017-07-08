package gov.samhsa.c2s.ums.service;

import gov.samhsa.c2s.ums.domain.UserAvatarRepository;
import gov.samhsa.c2s.ums.service.dto.UserAvatarDto;
import gov.samhsa.c2s.ums.service.exception.UserAvatarNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class UserAvatarServiceImpl implements UserAvatarService {
    private final ModelMapper modelMapper;
    private final UserAvatarRepository userAvatarRepository;

    @Autowired
    public UserAvatarServiceImpl(ModelMapper modelMapper, UserAvatarRepository userAvatarRepository) {
        this.modelMapper = modelMapper;
        this.userAvatarRepository = userAvatarRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserAvatarDto getUserAvatarByUserId(Long userId) {
        return modelMapper.map(
                userAvatarRepository.findByUserId(userId)
                        .orElseThrow(UserAvatarNotFoundException::new),
                UserAvatarDto.class
        );
    }
}
