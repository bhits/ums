package gov.samhsa.c2s.ums.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TelecomRepository  extends JpaRepository<Telecom, Long> {
}
