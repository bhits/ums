package gov.samhsa.c2s.ums.domain.reference;

import org.springframework.data.jpa.repository.JpaRepository;


public interface CountryCodeRepository extends JpaRepository<CountryCode, Long> {
    CountryCode findByCode(String code);
}
