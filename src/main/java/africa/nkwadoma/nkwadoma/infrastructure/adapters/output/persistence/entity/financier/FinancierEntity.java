package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.financier;

import africa.nkwadoma.nkwadoma.domain.enums.AccreditationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.identity.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.investmentvehicle.FinancierType;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.identity.UserEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.investmentvehicle.CooperationEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import java.util.List;

@Getter
@Setter
@Entity
public class FinancierEntity {

    @Id
    @UuidGenerator
    private String id;
    @Enumerated(EnumType.STRING)
    private ActivationStatus activationStatus;
    @Enumerated(EnumType.STRING)
    private AccreditationStatus accreditationStatus;
    @Enumerated(EnumType.STRING)
    private FinancierType financierType;
    @OneToOne
    private UserEntity userIdentity;

    //source of fund
    @ElementCollection(fetch = FetchType.EAGER)
    List<String> sourceOfFunds;

    //Declaration
    private boolean declarationAndAgreement;
    private boolean politicallyExposed;

    @OneToOne
    private CooperationEntity cooperation;
    private BigDecimal totalAmountInvested;
    private LocalDateTime createdAt;
    private boolean privacyPolicyAccepted;
}
