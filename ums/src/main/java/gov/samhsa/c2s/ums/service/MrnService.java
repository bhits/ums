package gov.samhsa.c2s.ums.service;

import org.springframework.stereotype.Service;

/**
 * The Interface MrnService.
 */
@Service
public interface MrnService {

    /**
     * Generate mrn.
     *
     * @return the string
     */
    String generateMrn();

    String getCodeSystem();
}