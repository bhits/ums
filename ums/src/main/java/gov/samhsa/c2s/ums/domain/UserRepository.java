package gov.samhsa.c2s.ums.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findAll();

    Optional<User> findOneByIdAndDisabled(Long userId, boolean disabled);

    Optional<User> findOneByUserAuthIdAndDisabled(String userAuthId, boolean disabled);


    Page<User> findAllByDisabled(boolean isDisabled, Pageable pageable);

}
