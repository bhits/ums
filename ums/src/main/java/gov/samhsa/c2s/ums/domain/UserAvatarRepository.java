package gov.samhsa.c2s.ums.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserAvatarRepository extends JpaRepository<UserAvatar, Long> {
    Optional<UserAvatar> findByUserId(Long userId);
}
