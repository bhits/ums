package gov.samhsa.c2s.ums.domain;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by sadhana.chandra on 4/14/2017.
 */
public interface RolePermissionRepository extends JpaRepository<RolePermission, Long> {
}
