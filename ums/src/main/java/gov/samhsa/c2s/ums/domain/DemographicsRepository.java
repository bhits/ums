package gov.samhsa.c2s.ums.domain;

import gov.samhsa.c2s.ums.domain.reference.AdministrativeGenderCode;
import org.springframework.data.domain.Page;
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

    Optional<Demographics> findOneByIdentifiersValueAndIdentifiersIdentifierSystemSystem(String value, String system);


    @Query("SELECT DISTINCT p FROM Demographics p WHERE "
            + " ((?1 = null) OR (p.firstName LIKE ?1))"
            + " AND ((?2 = null) OR (p.lastName LIKE ?2))"
            + " AND ((?3 = null) OR (p.administrativeGenderCode LIKE ?3))"
            + " AND ((?4 = null) OR (p.birthDay LIKE ?4 ))")
    Page<Demographics> query(
            String firstName,
            String lastName,
            AdministrativeGenderCode genderCode,
            LocalDate birthDay,
            Pageable pageable);
}
