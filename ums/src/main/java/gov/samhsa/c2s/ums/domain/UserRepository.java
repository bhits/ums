package gov.samhsa.c2s.ums.domain;

import gov.samhsa.c2s.ums.domain.reference.AdministrativeGenderCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findOneByIdAndIsDeleted(Long userId, boolean isDeleted);

    Page<User> findAllAndNotDeleted(boolean isDeleted, Pageable pageable);

    @Query("select u from User u where u.firstName like ?1 or u.lastName like ?1")
    Page<User> findAllByFirstNameLikesOrLastNameLikes(String token1, Pageable pageRequest);

    @Query("select u from User u where (u.firstName like ?1 or u.firstName like ?2) and (u.lastName like ?1 or u.lastName like ?2)")
    Page<User> findAllByFirstNameLikesAndLastNameLikes(String token1, String token2, Pageable pageRequest);

    Page<User> findAllByFirstNameAndLastNameAndBirthDayAndAdministrativeGenderCode(String firstName, String lastName, Date birthDate,
                                                                                      AdministrativeGenderCode administrativeGenderCode);
}
