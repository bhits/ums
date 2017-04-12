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
    Optional<User> findOneByIdAndIsDisabled(Long userId, boolean isDisabled);

    Page<User> findAllAndIsDisabled(boolean isDisabled, Pageable pageable);

    @Query("select u from User u where (u.firstName like ?1 or u.lastName like ?1) and u.isDisabled = ?2")
    List<User> findAllByFirstNameLikesOrLastNameLikesAndIsDisabled(String token1, boolean isDisabled, Pageable pageRequest);

    @Query("select u from User u where (u.firstName like ?1 or u.firstName like ?2) and (u.lastName like ?1 or u.lastName like ?2) and (u.isDisabled = ?3)")
    List<User> findAllByFirstNameLikesAndLastNameLikesAndIsDisabled(String token1, String token2, boolean isDisabled, Pageable pageRequest);

    List<User> findAllByFirstNameAndLastNameAndBirthDayAndAdministrativeGenderCodeAndIsDisabled(String firstName, String lastName, Date birthDate,
                                                                                      AdministrativeGenderCode administrativeGenderCode, boolean isDisabled);
}
