package gov.samhsa.c2s.ums.domain;

import org.springframework.data.jpa.repository.JpaRepository;


public interface LocaleRepository extends JpaRepository<Locale, Long> {
    Locale findByCode(String code);
}
