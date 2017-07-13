package gov.samhsa.c2s.ums.service;

import gov.samhsa.c2s.ums.domain.User;
import gov.samhsa.c2s.ums.domain.UserAvatar;
import gov.samhsa.c2s.ums.domain.UserAvatarRepository;
import gov.samhsa.c2s.ums.domain.UserRepository;
import gov.samhsa.c2s.ums.service.dto.UserAvatarDto;
import gov.samhsa.c2s.ums.service.exception.InvalidAvatarInputException;
import gov.samhsa.c2s.ums.service.exception.UserAvatarNotFoundException;
import gov.samhsa.c2s.ums.service.exception.UserAvatarSaveException;
import gov.samhsa.c2s.ums.service.exception.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@Slf4j
public class UserAvatarServiceImpl implements UserAvatarService {
    private static final Long REQUIRED_WIDTH_IN_PIXELS = 460L;  // TODO: Replace this hardcoded constant with externalized configurable value
    private static final Long REQUIRED_HEIGHT_IN_PIXELS = 460L;  // TODO: Replace this hardcoded constant with externalized configurable value

    private final ModelMapper modelMapper;
    private final UserAvatarRepository userAvatarRepository;
    private final UserRepository userRepository;

    @Autowired
    public UserAvatarServiceImpl(ModelMapper modelMapper, UserAvatarRepository userAvatarRepository, UserRepository userRepository) {
        this.modelMapper = modelMapper;
        this.userAvatarRepository = userAvatarRepository;
        this.userRepository = userRepository;
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

    @Override
    @Transactional
    public UserAvatarDto saveUserAvatar(Long userId, MultipartFile avatarFile, Long fileWidthPixels, Long fileHeightPixels) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User Not Found!"));

        UserAvatar savedUserAvatar = userAvatarRepository.save(buildNewUserAvatar(avatarFile, fileWidthPixels, fileHeightPixels, user));

        return modelMapper.map(savedUserAvatar, UserAvatarDto.class);
    }

    private UserAvatar buildNewUserAvatar(MultipartFile avatarFile, Long fileWidthPixels, Long fileHeightPixels, User user) {
        if (avatarFile == null) {
            log.error("Unable to generate a new UserAvatar object in buildNewUserAvatar method because value of avatarFile parameter is null");
            throw new InvalidAvatarInputException("The avatar file cannot be null");
        }

        // TODO: Add check to ensure file extension is one of the permitted types

        // TODO: Add check to ensure file size is equal or less than configured max file size

        // TODO: Add check for viruses in file via call to ClamAV antivirus scanner service

        if (fileWidthPixels == null || !fileWidthPixels.equals(REQUIRED_WIDTH_IN_PIXELS)) {
            log.error("Unable to generate a new UserAvatar object in buildNewUserAvatar method because value of fileWidthPixels parameter is null or not equal to required value (" + REQUIRED_WIDTH_IN_PIXELS + "):", fileWidthPixels);
            throw new InvalidAvatarInputException("The avatar file image width is not valid");
        }

        if (fileHeightPixels == null || !fileHeightPixels.equals(REQUIRED_HEIGHT_IN_PIXELS)) {
            log.error("Unable to generate a new UserAvatar object in buildNewUserAvatar method because value of fileHeightPixels parameter is null or not equal to required value (" + REQUIRED_HEIGHT_IN_PIXELS + "):", fileHeightPixels);
            throw new InvalidAvatarInputException("The avatar file image height is not valid");
        }

        byte[] uploadedFileBytes;

        try {
            // extract file content as byte array
            uploadedFileBytes = avatarFile.getBytes();
        }catch(IOException e){
            log.error("An IOException occurred while invoking avatarFile.getBytes from inside the buildNewUserAvatar method", e);
            throw new UserAvatarSaveException("An error occurred while attempting to save a new user avatar");
        }

        UserAvatar newUserAvatar = new UserAvatar();
        newUserAvatar.setFileContents(uploadedFileBytes);
        newUserAvatar.setFileExtension(extractExtensionFromFileName(avatarFile.getOriginalFilename()));
        newUserAvatar.setFileName(avatarFile.getOriginalFilename());
        newUserAvatar.setFileSizeBytes(avatarFile.getSize());
        newUserAvatar.setFileHeightPixels(fileHeightPixels);
        newUserAvatar.setFileWidthPixels(fileWidthPixels);
        newUserAvatar.setUser(user);

        return newUserAvatar;
    }

    private String extractExtensionFromFileName(String fileName) {
        int indexOfLastDot = fileName.lastIndexOf(".");

        if (indexOfLastDot < 0) {
            log.error("Unable to extract file extension from file name in object in extractExtensionFromFileName method because the index of the '.' character in the file name string could not be located", fileName);
            throw new InvalidAvatarInputException("Unable to determine the file extension");
        }

        return fileName.substring(indexOfLastDot + 1);
    }
}
