package gov.samhsa.c2s.ums.domain;

import lombok.Data;
import org.hibernate.envers.Audited;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Date;

@Entity
@Data
@Table(indexes = @Index(columnList = "emailToken", name = "email_token_idx", unique = true))
@Audited
public class UserActivation {
    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    @OneToOne
    private User user;

    @NotEmpty
    private String emailToken;

    @NotEmpty
    private String verificationCode;

    @NotNull
    @Future
    private Date emailTokenExpiration;

    @Transient
    private Instant emailTokenExpirationAsInstant;

    private boolean isVerified;

}
