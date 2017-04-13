package gov.samhsa.c2s.ums.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddressDto {

    /**
     * The street address line.
     */
    @NotNull
    private String streetAddressLine;

    /**
     * The city.
     */
    @NotNull
    private String city;

    /**
     * The state code.
     */
    @NotNull
    private String stateCode;

    /**
     * The postal code.
     */
    @NotNull
    @Pattern(regexp = "\\d{5}(?:[-\\s]\\d{4})?")
    private String postalCode;

    /**
     * The country code.
     */
    @NotNull
    private String countryCode;
}
