package gov.samhsa.c2s.ums.domain.reference;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

public interface StateCodeRepository extends JpaRepository<StateCode, Long> {
    StateCode findByCode(String code);
}
