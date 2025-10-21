package africa.nkwadoma.nkwadoma.domain.enums.constants.loan.disbursement;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;

import java.util.Set;

@Slf4j
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

    public static boolean isApplicableDisbursementRule(DisbursementRuleStatus disbursementRuleStatus){
        if (ObjectUtils.isEmpty(disbursementRuleStatus)){
            log.error("Disbursement rule status to check if its applicable disbursement rule");
        }
        return applicableDisbursementRules().contains(disbursementRuleStatus);
    }
    public static Set<DisbursementRuleStatus> applicableDisbursementRules(){
        return Set.of(ACTIVE, APPROVED, APPLIED, EXECUTED);
    }

    DisbursementRuleStatus(String statusName) {
        this.statusName = statusName;
    }


}
