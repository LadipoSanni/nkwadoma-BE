package africa.nkwadoma.nkwadoma.domain.service.bankDetail;

import africa.nkwadoma.nkwadoma.application.ports.input.walletManagement.BankDetailUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.bankdetail.BankDetailOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.bankdetail.FinancierBankDetailOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.financier.FinancierOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.notification.meedlNotification.AsynchronousNotificationOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.constants.investmentVehicle.FinancierMessages;
import africa.nkwadoma.nkwadoma.domain.enums.identity.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.identity.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.enums.constants.BankDetailMessages;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.bankdetail.BankDetail;
import africa.nkwadoma.nkwadoma.domain.model.bankdetail.FinancierBankDetail;
import africa.nkwadoma.nkwadoma.domain.model.education.ServiceOffering;
import africa.nkwadoma.nkwadoma.domain.model.financier.Financier;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationEmployeeIdentity;
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
    private final AsynchronousNotificationOutputPort asynchronousNotificationOutputPort;
    private final FinancierBankDetailOutputPort financierBankDetailOutputPort;


    @Override
    public BankDetail addBankDetails(BankDetail bankDetail) throws MeedlException {
        MeedlValidator.validateObjectInstance(bankDetail, BankDetailMessages.INVALID_BANK_DETAIL.getMessage());
        bankDetail.validate();
        UserIdentity userIdentity = userIdentityOutputPort.findById(bankDetail.getUserId());
        bankDetail.setUserIdentity(userIdentity);
        bankDetail = addBankDetails(bankDetail, userIdentity);

        return bankDetail;
    }

    private BankDetail addBankDetails(BankDetail bankDetail, UserIdentity userIdentity) throws MeedlException {
        log.info("About to add bank detail by {} with user id {}", userIdentity.getRole().getRoleName(), userIdentity.getId());

        if (IdentityRole.FINANCIER.equals(userIdentity.getRole())){
            Financier financier = financierOutputPort.findFinancierByUserId(userIdentity.getId());
            log.info("Financier adding bank detail with financier id as {}", financier.getId());
            bankDetail.setActivationStatus(ActivationStatus.APPROVED);
            saveBankDetails(bankDetail, financier);
            saveFinancierBankDetail(financier, bankDetail);
            financierOutputPort.save(financier);
            bankDetail.setResponse("Financier bank details saved successfully");
            return bankDetail;
        }
        if (IdentityRole.isCooperateFinancier(userIdentity.getRole())){
            Financier financier = financierOutputPort.findFinancierByCooperateStaffUserId(userIdentity.getId());
            financier.setUserIdentity(userIdentity);
            if (ObjectUtils.isEmpty(financier)){
                log.error("Unable to determine your details as a cooperate financier. User id {}", userIdentity.getId());
                throw new MeedlException("Unable to determine your details as a cooperate financier");
            }
            log.info("Add bank detail by {} with user id {}", userIdentity.getRole(), userIdentity.getId());
            if (IdentityRole.COOPERATE_FINANCIER_SUPER_ADMIN.equals(userIdentity.getRole())){
                return addCooperateFinancierBankDetail(bankDetail, financier, ActivationStatus.APPROVED);
            }else {
                bankDetail = addCooperateFinancierBankDetail(bankDetail, financier, ActivationStatus.PENDING_APPROVAL);
                asynchronousNotificationOutputPort.notifyCooperateSuperAdminToApproveBankDetail(bankDetail, financier);
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

    private void saveFinancierBankDetail(Financier financier, BankDetail bankDetail) throws MeedlException {
        FinancierBankDetail financierBankDetail = FinancierBankDetail.builder()
                .financier(financier)
                .bankDetail(bankDetail)
                .build();
        log.info("saving financier bank detail. Financier id {}, bank detail {}", financier.getId(), bankDetail.getId());
        financierBankDetailOutputPort.save(financierBankDetail);
    }

    private BankDetail addCooperateFinancierBankDetail(BankDetail bankDetail, Financier financier, ActivationStatus activationStatus) throws MeedlException {
        bankDetail.setActivationStatus(activationStatus);
        saveBankDetails(bankDetail, financier);
        financierOutputPort.save(financier);
        saveFinancierBankDetail(financier, bankDetail);
        bankDetail.setResponse("Cooperate financier bank detail is "+activationStatus.getStatusName());
        log.info("Bank detail id {} for cooperate financier with id {} status {}", bankDetail.getId(), financier.getId(), activationStatus);
        return bankDetail;
    }

    private void saveBankDetails(BankDetail bankDetailToSave, Financier financier) throws MeedlException {
        List<BankDetail> existingBankDetails = financierBankDetailOutputPort.findAllBankDetailOfFinancier(financier);
        BankDetail savedBankDetail = bankDetailOutputPort.save(bankDetailToSave);
        bankDetailToSave.setId(savedBankDetail.getId());
        if (MeedlValidator.isEmptyCollection(existingBankDetails)){
            existingBankDetails = List.of(bankDetailToSave);
        }else {
            if (ActivationStatus.APPROVED.equals(bankDetailToSave.getActivationStatus())){
                existingBankDetails.forEach(existingBankDetail -> existingBankDetail.setActivationStatus(ActivationStatus.DEACTIVATED));
                bankDetailOutputPort.save(existingBankDetails);
            }
            existingBankDetails.add(bankDetailToSave);
        }
        financier.setBankDetails(existingBankDetails);
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
            organizationIdentity.setOrganizationEmployees(List.of(OrganizationEmployeeIdentity.builder().meedlUser(userIdentity).build()));
            organizationIdentityOutputPort.save(organizationIdentity);
            log.info("{} successfully added bank details", userIdentity.getRole().getRoleName());
        }else {
            log.error("Unable to find {} organization. user id {}",userIdentity.getRole().getRoleName(), userIdentity.getId());
        }
        return bankDetail;
    }
    @Override
    public BankDetail viewBankDetail(BankDetail bankDetail) throws MeedlException {
        MeedlValidator.validateObjectInstance(bankDetail, "Bank detail request cannot be empty.");
        MeedlValidator.validateUUID(bankDetail.getUserId(), "Please identify user viewing bank details");

        UserIdentity userIdentity;
        try {
            userIdentity = userIdentityOutputPort.findById(bankDetail.getUserId());

        } catch (MeedlException e) {
            log.error("Unable to identify user view bank details. Contact admin. {}", e.getMessage(), e);
            throw new MeedlException("Unable to identify user view bank details. Contact admin.");
        }
        if (IdentityRole.isFinancier(userIdentity.getRole())){
            Financier financier = null;
            if (IdentityRole.FINANCIER.equals(userIdentity.getRole())) {
                 financier = financierOutputPort.findFinancierByUserId(userIdentity.getId());
            }else {
                Optional<OrganizationIdentity> optionalOrganizationIdentity = organizationIdentityOutputPort.findByUserId(userIdentity.getId());
                if (optionalOrganizationIdentity.isEmpty()){
                    /// Notify meedl admin of attempt
                    log.error("User with id {} is not a cooperate financier as organization is not found ", userIdentity.getId());
                    throw new MeedlException(FinancierMessages.NOT_A_FINANCIER.getMessage());
                }
                 financier = financierOutputPort.findFinancierByOrganizationId(optionalOrganizationIdentity.get().getId());
            }
            log.info("Finding bank detail by financier id {} in view bank detail", financier.getId());
            FinancierBankDetail financierBankDetail = financierBankDetailOutputPort.findApprovedBankDetailByFinancierId(financier);
            if (ObjectUtils.isNotEmpty(financierBankDetail)) {
                log.info("The approved  bank detail of the financier is {}", financierBankDetail.getBankDetail());
                return financierBankDetail.getBankDetail();
            }else {
                return null;
            }
        }
        return bankDetailOutputPort.findByBankDetailId(bankDetail.getId());
    }
    public BankDetail viewAllBankDetail(BankDetail bankDetail) throws MeedlException {
        MeedlValidator.validateObjectInstance(bankDetail, BankDetailMessages.INVALID_BANK_DETAIL.getMessage());
        MeedlValidator.validateUUID(bankDetail.getUserId(), BankDetailMessages.INVALID_BANK_DETAIL.getMessage());

        UserIdentity foundUser = userIdentityOutputPort.findById(bankDetail.getUserId());
        if (IdentityRole.isMeedlStaff(foundUser.getRole()) || IdentityRole.isOrganizationStaff(foundUser.getRole())
        || IdentityRole.isCooperateFinancier(foundUser.getRole())){
//            OrganizationIdentity organizationIdentity =
        }
        return null;
    }
}
