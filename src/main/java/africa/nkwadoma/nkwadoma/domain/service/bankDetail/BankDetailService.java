package africa.nkwadoma.nkwadoma.domain.service.bankDetail;

import africa.nkwadoma.nkwadoma.application.ports.input.walletManagement.BankDetailUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.bankdetail.BankDetailOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.financier.CooperateFinancierOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.financier.FinancierOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.investmentvehicle.CooperationOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.identity.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.identity.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.enums.constants.BankDetailMessages;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.bankdetail.BankDetail;
import africa.nkwadoma.nkwadoma.domain.model.education.ServiceOffering;
import africa.nkwadoma.nkwadoma.domain.model.financier.CooperateFinancier;
import africa.nkwadoma.nkwadoma.domain.model.financier.Financier;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class BankDetailService implements BankDetailUseCase {
    private final BankDetailOutputPort bankDetailOutputPort;
    private final UserIdentityOutputPort userIdentityOutputPort;
    private final FinancierOutputPort financierOutputPort;
    private final OrganizationIdentityOutputPort organizationIdentityOutputPort;
    private final CooperateFinancierOutputPort cooperateFinancierOutputPort;
    private final CooperationOutputPort cooperationOutputPort;

    @Override
    public BankDetail addBankDetails(BankDetail bankDetail) throws MeedlException {
        MeedlValidator.validateObjectInstance(bankDetail, BankDetailMessages.INVALID_BANK_DETAIL.getMessage());
        bankDetail.validate();
        UserIdentity userIdentity = userIdentityOutputPort.findById(bankDetail.getUserId());
        bankDetail.setUserIdentity(userIdentity);
        bankDetail = addBankDetails(bankDetail, userIdentity);

        bankDetail.setResponse("Added bank details successfully");
        return bankDetail;
    }

    private BankDetail addBankDetails(BankDetail bankDetail, UserIdentity userIdentity) throws MeedlException {
        log.info("About to add bank detail by {} with user id {}", userIdentity.getRole(), userIdentity.getId());

        if (IdentityRole.FINANCIER.equals(userIdentity.getRole())){
            Financier financier = financierOutputPort.findFinancierByUserId(userIdentity.getId());
            log.info("Financier adding bank detail with financier id as {}", financier.getId());
            bankDetail.setActivationStatus(ActivationStatus.APPROVED);
            bankDetail = bankDetailOutputPort.save(bankDetail);
            financier.setBankDetailId(bankDetail.getId());
            financierOutputPort.save(financier);
            return bankDetail;
        }
        if (IdentityRole.isCooperateFinancier(userIdentity.getRole())){
            CooperateFinancier cooperateFinancier =  cooperateFinancierOutputPort.findByUserId(userIdentity.getId());
            if (ObjectUtils.isEmpty(cooperateFinancier)){
                log.error("Unable to determine your details as a cooperate financier. User id {}", userIdentity.getId());
                throw new MeedlException("Unable to determine your details as a cooperate financier");
            }
            log.info("Add bank detail by {} with user id {}", userIdentity.getRole(), userIdentity.getId());
            if (IdentityRole.COOPERATE_FINANCIER_SUPER_ADMIN.equals(userIdentity.getRole())){
                return addCooperateFinancierBankDetail(bankDetail, cooperateFinancier, ActivationStatus.APPROVED);
            }else {
                bankDetail = addCooperateFinancierBankDetail(bankDetail, cooperateFinancier, ActivationStatus.PENDING_APPROVAL);
//                notifyCooperateSuperAdmin();
                return bankDetail;
            }
        }
        if (IdentityRole.MEEDL_SUPER_ADMIN.equals(userIdentity.getRole()) ||
                IdentityRole.ORGANIZATION_SUPER_ADMIN.equals(userIdentity.getRole())){
                return addOrganizationBankDetail(bankDetail, userIdentity, ActivationStatus.APPROVED);
        }else if (IdentityRole.isMeedlStaff(userIdentity.getRole()) ||
                IdentityRole.isOrganizationStaff(userIdentity.getRole())){
            bankDetail = addOrganizationBankDetail(bankDetail, userIdentity, ActivationStatus.PENDING_APPROVAL);
//            notifyActor();
            return bankDetail;
        }

        return bankDetail;
    }

    private BankDetail addCooperateFinancierBankDetail(BankDetail bankDetail, CooperateFinancier cooperateFinancier, ActivationStatus activationStatus) throws MeedlException {
        bankDetail.setActivationStatus(activationStatus);
        bankDetail = bankDetailOutputPort.save(bankDetail);
        cooperateFinancier.getCooperate().setBankDetailId(bankDetail.getId());
        cooperationOutputPort.save(cooperateFinancier.getCooperate());
        bankDetail.setResponse("Bank detail is "+activationStatus.getStatusName());
        log.info("Bank detail id {} for cooperate financier with id {}", bankDetail.getId(), cooperateFinancier.getId());
        return bankDetail;
    }

    private BankDetail addOrganizationBankDetail(BankDetail bankDetail, UserIdentity userIdentity, ActivationStatus activationStatus) throws MeedlException {
        Optional<OrganizationIdentity> optionalOrganizationIdentity =  organizationIdentityOutputPort.findByUserId(userIdentity.getId());
        if (optionalOrganizationIdentity.isPresent()) {
            OrganizationIdentity organizationIdentity = optionalOrganizationIdentity.get();
            bankDetail.setActivationStatus(activationStatus);
            bankDetail = bankDetailOutputPort.save(bankDetail);
            organizationIdentity.setBankDetailId(bankDetail.getId());
            List<ServiceOffering> serviceOfferings = organizationIdentityOutputPort.getServiceOfferings(organizationIdentity.getId());
            organizationIdentity.setServiceOfferings(serviceOfferings);
            organizationIdentityOutputPort.save(organizationIdentity);
            log.info("{} successfully added bank details", userIdentity.getRole().getRoleName());
        }else {
            log.error("Unable to find {} organization. user id {}",userIdentity.getRole().getRoleName(), userIdentity.getId());
        }
        return bankDetail;
    }
}
