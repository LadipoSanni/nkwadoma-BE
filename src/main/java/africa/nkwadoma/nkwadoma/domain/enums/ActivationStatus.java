package africa.nkwadoma.nkwadoma.domain.enums;

import java.util.EnumSet;
import java.util.Set;

public enum ActivationStatus {
    ACTIVE,
    INACTIVE,
    INVITED,
    DEACTIVATED,
    PENDING_APPROVAL,
    PENDING_INVITE,
    APPROVED,
    DECLINED;

    public static Set<ActivationStatus> getActiveLikeStatuses() {
        return EnumSet.of(
                ActivationStatus.ACTIVE,
                ActivationStatus.INACTIVE,
                ActivationStatus.INVITED,
                ActivationStatus.DEACTIVATED,
                ActivationStatus.PENDING_INVITE,
                ActivationStatus.APPROVED
        );
    }

    public static Set<ActivationStatus> getPendingOrDeclinedStatuses() {
        return EnumSet.of(
                ActivationStatus.DECLINED,
                ActivationStatus.PENDING_APPROVAL
        );
    }
}
