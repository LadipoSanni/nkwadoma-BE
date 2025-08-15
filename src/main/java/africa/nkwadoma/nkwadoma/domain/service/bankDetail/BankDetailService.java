package africa.nkwadoma.nkwadoma.domain.service.bankDetail;

import africa.nkwadoma.nkwadoma.application.ports.input.walletManagement.BankDetailUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.bankdetail.BankDetailOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.enums.constants.BankDetailMessages;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.bankdetail.BankDetail;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class BankDetailService implements BankDetailUseCase {
    private final BankDetailOutputPort bankDetailOutputPort;
    private final UserIdentityOutputPort userIdentityOutputPort;

    @Override
    public BankDetail addBankDetails(BankDetail bankDetail) throws MeedlException {
        MeedlValidator.validateObjectInstance(bankDetail, BankDetailMessages.INVALID_BANK_DETAIL.getMessage());
        bankDetail.validate();
        UserIdentity userIdentity = userIdentityOutputPort.findById(bankDetail.getUserId());
        if ((!IdentityRole.MEEDL_SUPER_ADMIN.equals(userIdentity.getRole())) &&
                (!IdentityRole.ORGANIZATION_SUPER_ADMIN.equals(userIdentity.getRole()))){
            bankDetail.setActivationStatus(ActivationStatus.PENDING_APPROVAL);
            if (IdentityRole.isMeedlStaff(userIdentity.getRole())){
//                notifyMeedlSuperAdminOfBankDetailAddition();
            }
//            if (IdentityRole.isOrganizationStaff())
        }else {
            bankDetail.setActivationStatus(ActivationStatus.APPROVED);
        }
        bankDetail = bankDetailOutputPort.save(bankDetail);
        bankDetail.setResponse("Added bank details successfully");
        return bankDetail;
    }
}
