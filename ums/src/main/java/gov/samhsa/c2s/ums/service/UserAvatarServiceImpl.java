package gov.samhsa.c2s.ums.service;

import gov.samhsa.c2s.ums.config.UmsProperties;
import gov.samhsa.c2s.ums.domain.User;
import gov.samhsa.c2s.ums.domain.UserAvatar;
import gov.samhsa.c2s.ums.domain.UserAvatarRepository;
import gov.samhsa.c2s.ums.domain.UserRepository;
import gov.samhsa.c2s.ums.service.dto.AvatarBytesAndMetaDto;
import gov.samhsa.c2s.ums.service.dto.UserAvatarDto;
import gov.samhsa.c2s.ums.service.exception.InvalidAvatarInputException;
import gov.samhsa.c2s.ums.service.exception.UserAvatarDeleteException;
import gov.samhsa.c2s.ums.service.exception.UserAvatarNotFoundException;
import gov.samhsa.c2s.ums.service.exception.UserAvatarSaveException;
import gov.samhsa.c2s.ums.service.exception.UserNotFoundException;
import gov.samhsa.c2s.ums.service.exception.checkedexceptions.NoImageReaderForFileTypeException;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.Dimension;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class UserAvatarServiceImpl implements UserAvatarService {
    private static final Long REQUIRED_WIDTH_IN_PIXELS = 48L;  // TODO: Replace this hardcoded constant with externalized configurable value
    private static final Long REQUIRED_HEIGHT_IN_PIXELS = 48L;  // TODO: Replace this hardcoded constant with externalized configurable value

    private final UmsProperties umsProperties;
    private final ModelMapper modelMapper;
    private final ImageProcessingService imageProcessingService;
    private final UserAvatarRepository userAvatarRepository;
    private final UserRepository userRepository;

    @Autowired
    public UserAvatarServiceImpl(UmsProperties umsProperties,
                                 ModelMapper modelMapper,
                                 ImageProcessingService imageProcessingService,
                                 UserAvatarRepository userAvatarRepository,
                                 UserRepository userRepository) {
        this.umsProperties = umsProperties;
        this.modelMapper = modelMapper;
        this.imageProcessingService = imageProcessingService;
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
    public UserAvatarDto saveUserAvatar(Long userId, AvatarBytesAndMetaDto avatarFile) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User Not Found!"));

        UserAvatar savedUserAvatar;
        Optional<UserAvatar> currentUserAvatar = userAvatarRepository.findByUserId(userId);

        UserAvatar newUserAvatar = currentUserAvatar
                .map(userAvatar -> buildUserAvatar(userAvatar, avatarFile, user))
                .orElseGet(() -> buildUserAvatar(new UserAvatar(), avatarFile, user));

        try {
            savedUserAvatar = userAvatarRepository.save(newUserAvatar);
        } catch (RuntimeException e) {
            log.error("A RuntimeException occurred while attempting to save a user avatar", e);
            throw new UserAvatarSaveException("Unable to save user avatar");
        }

        return modelMapper.map(savedUserAvatar, UserAvatarDto.class);
    }

    @Override
    @Transactional
    public void deleteUserAvatar(Long userId) {
        try {
            userAvatarRepository.deleteByUserId(userId);
        } catch (RuntimeException e) {
            log.error("A RuntimeException occurred while attempting to delete a user's avatar", e);
            throw new UserAvatarDeleteException("Unable to delete user's avatar");
        }
    }

    private UserAvatar buildUserAvatar(UserAvatar userAvatar, AvatarBytesAndMetaDto avatarFile, User user) {
        if (avatarFile.getFileContents() == null || avatarFile.getFileContents().length <= 0) {
            log.error("Unable to generate a new UserAvatar object because value of avatarFile.getFileContents is null or the length is less than or equal to zero");
            throw new InvalidAvatarInputException("The avatar file cannot be null");
        }

        assertImageFileTypeAllowed(avatarFile);

        Long imageFileSize = checkImageFileSize(avatarFile);
        // Ensure avatar image's height and width are valid
        Dimension imageDimension = checkImageDimensions(avatarFile);

        userAvatar.setFileContents(avatarFile.getFileContents());
        userAvatar.setFileExtension(avatarFile.getFileExtension());
        userAvatar.setFileName(avatarFile.getFileName());
        userAvatar.setFileSizeBytes(imageFileSize);
        userAvatar.setFileHeightPixels((long) imageDimension.height);
        userAvatar.setFileWidthPixels((long) imageDimension.width);
        userAvatar.setUser(user);

        return userAvatar;
    }

    private Dimension checkImageDimensions(AvatarBytesAndMetaDto avatarFile) {
        Dimension imageDimension;

        try {
            imageDimension = imageProcessingService.getImageDimension(avatarFile.getFileContents(), avatarFile.getFileExtension());
        } catch (NoImageReaderForFileTypeException e) {
            log.error("An exception occurred while attempting to determine the dimensions of the uploaded avatar image file", e);
            throw new UserAvatarSaveException("Unable to process avatar image file");
        }

        if (imageDimension.width != REQUIRED_WIDTH_IN_PIXELS) {
            log.warn("Unable to generate a new UserAvatar object because the uploaded image's width is not equal to required value (" + REQUIRED_WIDTH_IN_PIXELS + "): " + imageDimension.width);
            throw new InvalidAvatarInputException("The avatar file image's width is not valid");
        }

        if (imageDimension.height != REQUIRED_HEIGHT_IN_PIXELS) {
            log.warn("Unable to generate a new UserAvatar object because the uploaded image's height is not equal to required value (" + REQUIRED_HEIGHT_IN_PIXELS + "): " + imageDimension.height);
            throw new InvalidAvatarInputException("The avatar file image's height is not valid");
        }

        return imageDimension;
    }

    private Long checkImageFileSize(AvatarBytesAndMetaDto avatarFile) {
        Long maxImageFileSize = umsProperties.getAvatars().getMaxFileSize();
        Long imageFileSize = imageProcessingService.getImageFileSizeBytes(avatarFile.getFileContents());

        if (imageFileSize > maxImageFileSize) {
            log.warn("Unable to generate a new UserAvatar object because the uploaded image file's size is greater than the max allowed file size (Max Size: " + maxImageFileSize + "): " + imageFileSize);
            throw new InvalidAvatarInputException("The avatar file's size is greater than the allowed maximum");
        }

        return imageFileSize;
    }

    private void assertImageFileTypeAllowed(AvatarBytesAndMetaDto avatarFile) {
        String imageFileType;

        try {
            imageFileType = imageProcessingService.getImageFileType(avatarFile.getFileContents(), avatarFile.getFileExtension());
        } catch (NoImageReaderForFileTypeException e) {
            log.error("An exception occurred while attempting to determine the file type of the uploaded avatar image file", e);
            throw new InvalidAvatarInputException("The avatar file's type is not allowed or not recognized");
        }

        List<String> allowedFileTypesList = umsProperties.getAvatars().getAllowedFileTypesList();

        if (allowedFileTypesList.parallelStream().noneMatch(fileType -> fileType.equalsIgnoreCase(imageFileType))) {
            log.warn("Unable to generate a new UserAvatar object because the uploaded image file's type not allowed: " + imageFileType);
            log.debug("Allowed Image File Types: " + allowedFileTypesList.toString());
            throw new InvalidAvatarInputException("The avatar file's type is not allowed or not recognized");
        }
    }
}
