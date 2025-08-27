package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.wallet;

import africa.nkwadoma.nkwadoma.application.ports.output.bankdetail.OrganizationBankDetailOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.constants.BankDetailMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.OrganizationMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.investmentVehicle.FinancierMessages;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.bankdetail.BankDetail;
import africa.nkwadoma.nkwadoma.domain.model.bankdetail.FinancierBankDetail;
import africa.nkwadoma.nkwadoma.domain.model.bankdetail.OrganizationBankDetail;
import africa.nkwadoma.nkwadoma.domain.model.financier.Financier;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.wallet.BankDetailMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.wallet.OrganizationBankDetailMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.wallet.FinancierBankDetailEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.wallet.OrganizationBankDetailEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.wallet.OrganizationBankDetailRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
@Slf4j
@Component
@AllArgsConstructor
public class OrganizationBankDetailAdapter implements OrganizationBankDetailOutputPort {
    private final OrganizationBankDetailRepository organizationBankDetailRepository;
    private final OrganizationBankDetailMapper organizationBankDetailMapper;
    private final BankDetailMapper bankDetailMapper;

    @Override
    public OrganizationBankDetail save(OrganizationBankDetail organizationBankDetail) throws MeedlException {
        MeedlValidator.validateObjectInstance(organizationBankDetail, "Organization bank detail cannot be empty");
        MeedlValidator.validateObjectInstance(organizationBankDetail.getBankDetail(), BankDetailMessages.INVALID_BANK_DETAIL.getMessage());
        MeedlValidator.validateObjectInstance(organizationBankDetail.getOrganizationIdentity(), OrganizationMessages.ORGANIZATION_MUST_NOT_BE_EMPTY.getMessage());
        MeedlValidator.validateUUID(organizationBankDetail.getBankDetail().getId(), BankDetailMessages.INVALID_BANK_DETAIL_ID.getMessage());
        MeedlValidator.validateUUID(organizationBankDetail.getOrganizationIdentity().getId(), OrganizationMessages.INVALID_ORGANIZATION_ID.getMessage());
        log.info("Done validating for organization bank detail");
        OrganizationBankDetailEntity organizationBankDetailEntity = organizationBankDetailMapper.map(organizationBankDetail);
        organizationBankDetailEntity = organizationBankDetailRepository.save(organizationBankDetailEntity);
        return organizationBankDetailMapper.map(organizationBankDetailEntity);
    }

    @Override
    public OrganizationBankDetail findApprovedBankDetailByOrganizationId(OrganizationIdentity organizationIdentity) throws MeedlException {
        MeedlValidator.validateObjectInstance(organizationIdentity, OrganizationMessages.ORGANIZATION_MUST_NOT_BE_EMPTY.getMessage());
        MeedlValidator.validateUUID(organizationIdentity.getId(), OrganizationMessages.INVALID_ORGANIZATION_ID.getMessage());
        log.info("Viewing organization bank detail for approved detail by financier id {} adapter level", organizationIdentity.getId());
        OrganizationBankDetailEntity organizationBankDetailEntity = organizationBankDetailRepository.findApprovedBankDetailByOrganizationId((organizationIdentity.getId()));
        return organizationBankDetailMapper.map(organizationBankDetailEntity);
    }

    @Override
    public List<BankDetail> findAllBankDetailOfOrganization(OrganizationIdentity organizationIdentity) throws MeedlException {
        MeedlValidator.validateObjectInstance(organizationIdentity, OrganizationMessages.ORGANIZATION_MUST_NOT_BE_EMPTY.getMessage());
        MeedlValidator.validateUUID(organizationIdentity.getId(), OrganizationMessages.INVALID_ORGANIZATION_ID.getMessage());

        log.info("Viewing all organization bank detail of a single organization with id {}", organizationIdentity.getId());
        List<OrganizationBankDetailEntity> organizationBankDetailEntities = organizationBankDetailRepository.findAllByOrganizationEntity_Id(organizationIdentity.getId());

        return organizationBankDetailEntities.stream()
                .map(OrganizationBankDetailEntity::getBankDetailEntity)
                .map(bankDetailMapper::toBankDetail)
                .toList();
    }

}
