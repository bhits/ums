package gov.samhsa.c2s.ums.infrastructure;

import gov.samhsa.c2s.ums.service.dto.UserDto;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient("fis")
public interface FisClient {
    @RequestMapping(value = "/patients", method = RequestMethod.POST)
    String publishFhirPatient( @RequestBody UserDto userDto);

    @RequestMapping(value = "/patients", method = RequestMethod.PUT)
    String updateFhirPatient( @RequestBody UserDto userDto);
}
