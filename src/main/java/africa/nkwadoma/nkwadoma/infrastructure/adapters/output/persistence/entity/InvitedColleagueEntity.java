package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity;

import africa.nkwadoma.nkwadoma.domain.enums.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.enums.InviteStatus;
import africa.nkwadoma.nkwadoma.domain.model.UserIdentity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class InvitedColleagueEntity {
    @Id
    private String colleagueId;
//    @OneToOne
//    @JoinColumn(name = "user_id", referencedColumnName = "colleague_id")
//    private UserEntity userEntity;
    @Enumerated(value = EnumType.STRING)
    private IdentityRole role;
    private String inviteeId;
    private LocalDateTime invitedAt;
    private LocalDateTime respondedAt;
    @Enumerated(value = EnumType.STRING)
    private InviteStatus inviteStatus;
}
