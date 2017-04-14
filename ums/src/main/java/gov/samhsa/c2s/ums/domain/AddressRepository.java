package gov.samhsa.c2s.ums.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface AddressRepository  extends JpaRepository<Address, Long> {
}
