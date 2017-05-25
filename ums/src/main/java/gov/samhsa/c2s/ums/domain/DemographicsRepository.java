package gov.samhsa.c2s.ums.domain;

import gov.samhsa.c2s.ums.domain.reference.AdministrativeGenderCode;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DemographicsRepository extends JpaRepository<Demographics, Long> {

    @Query("select u from Demographics u where (u.firstName like ?1 or u.lastName like ?1)")
    List<Demographics> findAllByFirstNameLikesOrLastNameLikes(String token1, Pageable pageRequest);

    @Query("select u from Demographics u where (u.firstName like ?1 or u.firstName like ?2) and (u.lastName like ?1 or u.lastName like ?2) ")
    List<Demographics> findAllByFirstNameLikesAndLastNameLikes(String token1, String token2, Pageable pageRequest);

    List<Demographics> findAllByFirstNameAndLastNameAndBirthDayAndAdministrativeGenderCode(String firstName, String lastName, LocalDate birthDate,
                                                                                           AdministrativeGenderCode administrativeGenderCode);

    Optional<Demographics> findOneByIdentifiersValueAndIdentifiersSystemSystem(String value, String system);
}
