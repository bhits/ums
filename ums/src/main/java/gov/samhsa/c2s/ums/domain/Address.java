/*******************************************************************************
 * Open Behavioral Health Information Technology Architecture (OBHITA.org)
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the <organization> nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
package gov.samhsa.c2s.ums.domain;


import gov.samhsa.c2s.ums.domain.reference.CountryCode;
import gov.samhsa.c2s.ums.domain.reference.StateCode;
import lombok.Data;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.envers.RelationTargetAuditMode;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * The Class Address.
 */
@Entity
@Audited
@Data
public class Address {
    /**
     * The id.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * The street address line.
     */
    @NotNull
    @Size(min = 1, max = 50)
    private String streetAddressLine;

    /**
     * The city.
     */
    @NotNull
    @Size(max = 30)
    private String city;

    /**
     * The state code.
     */
    @NotNull
    @ManyToOne(cascade = CascadeType.ALL)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private StateCode stateCode;

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
    @ManyToOne(cascade = CascadeType.ALL)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private CountryCode countryCode;

    @OneToMany
    @NotAudited
    List<User> Users;


}
