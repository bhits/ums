package gov.samhsa.c2s.ums.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.validation.constraints.Pattern;

@Entity
@Data
public class Patient {
    /**
     * The id.
     */
    @Id
    @GeneratedValue
    private Long id;

    @OneToOne
    private Demographics demographics;

    @Pattern(regexp = "^[\\w-]+(\\.[\\w-]+)*@([a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*?\\.[a-zA-Z]{2,6}|(\\d{1,3}\\.){3}\\d{1,3})(:\\d{4})?$")
    private String registrationPurposeEmail;
}
