package gov.samhsa.c2s.ums.domain;


import lombok.Data;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import java.util.Date;
import java.util.Set;

@Entity
@Data
@Audited
@EntityListeners(AuditingEntityListener.class)
public class User {
    /**
     * The id.
     */
    @Id
    @GeneratedValue
    private Long id;

    @CreatedDate
    private Date createdDate;

    private String createdBy;

    @LastModifiedDate
    private Date lastUpdatedDate;

    private String lastUpdatedBy;

    private String userAuthId;


    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Role> roles;

    /**
     * The Locale.
     */
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @NotAudited
    private Locale locale;

    private boolean disabled = false;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Demographics demographics;

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    private UserAvatar userAvatar;
}
