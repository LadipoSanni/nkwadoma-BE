package africa.nkwadoma.nkwadoma.domain.enums.constants.loan.disbursement;

import lombok.Getter;

@Getter
public enum DisbursementRuleStatus {
    ACTIVE("Active"),
    INACTIVE("Inactive"),
    APPLIED("Applied"),
    EXECUTED("Executed"),
    PENDING_APPROVAL("Pending approval"),
    APPROVED("Approved"),
    DECLINED("Declined");

    private final String statusName;

    DisbursementRuleStatus(String statusName) {
        this.statusName = statusName;
    }


}
