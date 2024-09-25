package africa.nkwadoma.nkwadoma.domain.model;

import africa.nkwadoma.nkwadoma.domain.enums.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.enums.InviteStatus;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InvitedColleagueIdentity {
    private String colleagueId;
    private UserIdentity userIdentity;
    private IdentityRole role;
    private String inviteeId;
    private LocalDateTime invitedAt;
    private LocalDateTime respondedAt;
    private InviteStatus inviteStatus;
}
