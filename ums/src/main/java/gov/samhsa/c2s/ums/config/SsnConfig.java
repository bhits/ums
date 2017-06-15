package gov.samhsa.c2s.ums.config;

import gov.samhsa.c2s.ums.domain.IdentifierSystem;
import gov.samhsa.c2s.ums.domain.IdentifierSystemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
@ConditionalOnProperty(name = "c2s.ums.ssn.update-in-database-on-startup", havingValue = "true")
public class SsnConfig {

    private final Logger logger = LoggerFactory.getLogger(SsnConfig.class);

    @Autowired
    private UmsProperties umsProperties;

    @Autowired
    private IdentifierSystemRepository identifierSystemRepository;

    @PostConstruct
    public void initSsnIdentifierSystem() {
        logger.info("Updating SSN configuration in database");
        final UmsProperties.Ssn ssn = umsProperties.getSsn();
        final String codeSystem = ssn.getCodeSystem();
        final IdentifierSystem identifierSystem = identifierSystemRepository.findBySystem(codeSystem).orElseGet(IdentifierSystem::new);
        identifierSystem.setSystem(codeSystem);
        identifierSystem.setDisplay(ssn.getDisplayName());
        identifierSystem.setOid(ssn.getCodeSystemOID());
        identifierSystem.setReassignable(ssn.isReassignable());
        identifierSystemRepository.save(identifierSystem);
        logger.info("SSN configuration in database is updated");
    }
}
