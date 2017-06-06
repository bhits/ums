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
@ConditionalOnProperty(name = "c2s.ums.mrn.update-in-database-on-startup", havingValue = "true")
public class MrnConfig {

    private final Logger logger = LoggerFactory.getLogger(MrnConfig.class);

    @Autowired
    private UmsProperties umsProperties;

    @Autowired
    private IdentifierSystemRepository identifierSystemRepository;

    @PostConstruct
    public void initMrn() {
        logger.info("Updating MRN configuration in database");
        final UmsProperties.Mrn mrn = umsProperties.getMrn();
        final String codeSystem = mrn.getCodeSystem();
        final IdentifierSystem identifierSystem = identifierSystemRepository.findBySystem(codeSystem).orElseGet(IdentifierSystem::new);
        identifierSystem.setSystem(codeSystem);
        identifierSystem.setDisplay(mrn.getDisplayName());
        identifierSystem.setOid(mrn.getCodeSystemOID());
        identifierSystem.setSystemGenerated(true);
        identifierSystemRepository.save(identifierSystem);
        logger.info("MRN configuration in database is updated");
    }
}
