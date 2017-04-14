package gov.samhsa.c2s.ums.domain.reference;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * The Interface AdministrativeGenderCodeRepository.
 */
@Repository
public interface AdministrativeGenderCodeRepository extends JpaSpecificationExecutor<AdministrativeGenderCode>, JpaRepository<AdministrativeGenderCode, Long> {

    /**
     * Find by code.
     *
     * @param code the code
     * @return the administrative gender code
     */
    AdministrativeGenderCode findByCode(String code);
}
