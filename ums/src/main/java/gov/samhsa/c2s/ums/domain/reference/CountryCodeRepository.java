package gov.samhsa.c2s.ums.domain.reference;

import gov.samhsa.c2s.ums.domain.Address;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by sadhana.chandra on 4/14/2017.
 */
public interface CountryCodeRepository extends JpaRepository<CountryCode, Long> {
    CountryCode findByCode(String code);
}
