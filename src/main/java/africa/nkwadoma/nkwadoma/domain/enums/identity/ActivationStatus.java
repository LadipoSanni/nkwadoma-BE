package africa.nkwadoma.nkwadoma.domain.enums.identity;

import lombok.Getter;

import java.util.EnumSet;
import java.util.Set;

@Getter
public enum ActivationStatus {
    ACTIVE("Active"),
    INACTIVE("Inactive"),
    INVITED("Invited"),
    DEACTIVATED("Deactivated"),
    PENDING_APPROVAL("Pending approval"),
    PENDING_INVITE("Pending invite"),
    APPROVED("Approved"),
    DECLINED("Declined");

    private final String statusName;

    ActivationStatus(String statusName) {
        this.statusName = statusName;
    }


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
