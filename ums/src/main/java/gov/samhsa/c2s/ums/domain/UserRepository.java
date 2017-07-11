package gov.samhsa.c2s.ums.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findById(Long id);

    Optional<User> findByIdAndDisabled(Long userId, boolean disabled);

    Optional<User> findByUserAuthIdAndDisabled(String userAuthId, boolean disabled);

    List<User> findAllByDemographicsIdentifiersValueAndDemographicsIdentifiersIdentifierSystemSystem(String value, String system);

    Page<User> findAllByDisabled(boolean isDisabled, Pageable pageable);

    Page<User> findAllByRolesCode(String roleCode, Pageable pageable);
}
