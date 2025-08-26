package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.bankdetail;

import africa.nkwadoma.nkwadoma.application.ports.output.bankdetail.FinancierBankDetailOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.constants.BankDetailMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.investmentVehicle.FinancierMessages;
import africa.nkwadoma.nkwadoma.domain.enums.identity.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.bankdetail.BankDetail;
import africa.nkwadoma.nkwadoma.domain.model.bankdetail.FinancierBankDetail;
import africa.nkwadoma.nkwadoma.domain.model.financier.Financier;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.bankdetail.BankDetailMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.bankdetail.FinancierBankDetailMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.bankdetail.BankDetailEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.bankdetail.FinancierBankDetailEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.bankdetail.FinancierBankDetailRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
public class FinancierBankDetailAdapter implements FinancierBankDetailOutputPort {
    private final FinancierBankDetailMapper financierBankDetailMapper;
    private final FinancierBankDetailRepository financierBankDetailRepository;
    private final BankDetailMapper bankDetailMapper;

    @Override
    public FinancierBankDetail save(FinancierBankDetail financierBankDetail) throws MeedlException {
        MeedlValidator.validateObjectInstance(financierBankDetail, "Provide a valid financier bank detail to save");
        MeedlValidator.validateObjectInstance(financierBankDetail.getBankDetail(), BankDetailMessages.INVALID_BANK_DETAIL.getMessage());
        MeedlValidator.validateObjectInstance(financierBankDetail.getFinancier(), FinancierMessages.EMPTY_FINANCIER_PROVIDED.getMessage());
        FinancierBankDetailEntity financierBankDetailEntity = financierBankDetailMapper.map(financierBankDetail);
        financierBankDetailEntity = financierBankDetailRepository.save(financierBankDetailEntity);
        return financierBankDetailMapper.map(financierBankDetailEntity);
    }

    @Override
    public FinancierBankDetail findByFinancierIdAndStatus(Financier financier, ActivationStatus activationStatus) throws MeedlException {
        MeedlValidator.validateObjectInstance(financier, FinancierMessages.EMPTY_FINANCIER_PROVIDED.getMessage());
        return null;
    }
    @Override
    public FinancierBankDetail findApprovedBankDetailByFinancierId(Financier financier) throws MeedlException {
        MeedlValidator.validateObjectInstance(financier, FinancierMessages.EMPTY_FINANCIER_PROVIDED.getMessage());
        MeedlValidator.validateUUID(financier.getId(), FinancierMessages.INVALID_FINANCIER_ID.getMessage());
        log.info("Viewing financier bank detail for approved detail by financier id {} adapter level", financier.getId());
        FinancierBankDetailEntity financierBankDetailEntity = financierBankDetailRepository.findApprovedBankDetailByFinancierId(financier.getId());
        return financierBankDetailMapper.map(financierBankDetailEntity);
    }

    @Override
    public List<BankDetail> findAllBankDetailOfFinancier(Financier financier) throws MeedlException {
        MeedlValidator.validateObjectInstance(financier, FinancierMessages.EMPTY_FINANCIER_PROVIDED.getMessage());
        MeedlValidator.validateUUID(financier.getId(), FinancierMessages.INVALID_FINANCIER_ID.getMessage());

        log.info("Viewing all financier bank detail of a single financier with id {}", financier.getId());
        List<FinancierBankDetailEntity> financierBankDetailEntities = financierBankDetailRepository.findAllByFinancierEntity_Id(financier.getId());

        return financierBankDetailEntities.stream()
                .map(FinancierBankDetailEntity::getBankDetailEntity)
                .map(bankDetailMapper::toBankDetail)
                .toList();

    }
}
