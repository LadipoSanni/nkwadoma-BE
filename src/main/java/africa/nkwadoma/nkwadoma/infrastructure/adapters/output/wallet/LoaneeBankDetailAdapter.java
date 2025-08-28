package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.wallet;

import africa.nkwadoma.nkwadoma.application.ports.output.bankdetail.LoaneeBankDetailOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.constants.BankDetailMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.OrganizationMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.investmentVehicle.FinancierMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.loan.LoaneeMessages;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.bankdetail.BankDetail;
import africa.nkwadoma.nkwadoma.domain.model.bankdetail.LoaneeBankDetail;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.wallet.BankDetailMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.wallet.LoaneeBankDetailMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.wallet.FinancierBankDetailEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.wallet.LoaneeBankDetailEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.wallet.OrganizationBankDetailEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.wallet.LoaneeBankDetailRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
public class LoaneeBankDetailAdapter implements LoaneeBankDetailOutputPort {
    private final LoaneeBankDetailMapper loaneeBankDetailMapper;
    private final LoaneeBankDetailRepository loaneeBankDetailRepository;
    private final BankDetailMapper bankDetailMapper;
    @Override
    public LoaneeBankDetail save(LoaneeBankDetail loaneeBankDetail) throws MeedlException {
        MeedlValidator.validateObjectInstance(loaneeBankDetail, "Provide a valid loanee's bank detail to save");
        MeedlValidator.validateObjectInstance(loaneeBankDetail.getBankDetail(), BankDetailMessages.INVALID_BANK_DETAIL.getMessage());
        MeedlValidator.validateObjectInstance(loaneeBankDetail.getLoanee(), FinancierMessages.EMPTY_FINANCIER_PROVIDED.getMessage());
        MeedlValidator.validateUUID(loaneeBankDetail.getBankDetail().getId(), BankDetailMessages.INVALID_BANK_DETAIL_ID.getMessage());
        MeedlValidator.validateUUID(loaneeBankDetail.getLoanee().getId(), LoaneeMessages.INVALID_LOANEE_ID.getMessage());

        log.info("Done validating for loanee's bank detail");
        LoaneeBankDetailEntity loaneeBankDetailEntity = loaneeBankDetailMapper.map(loaneeBankDetail);
        loaneeBankDetailEntity = loaneeBankDetailRepository.save(loaneeBankDetailEntity);
        return loaneeBankDetailMapper.map(loaneeBankDetailEntity);
    }

    @Override
    public List<BankDetail> findAllBankDetailOfLoanee(Loanee loanee) throws MeedlException {
        MeedlValidator.validateObjectInstance(loanee, LoaneeMessages.LOANEE_CANNOT_BE_EMPTY.getMessage());
        MeedlValidator.validateUUID(loanee.getId(), LoaneeMessages.INVALID_LOANEE_ID.getMessage());

        log.info("Viewing all loanee's bank detail of a single loanee with id {}", loanee.getId());
        List<LoaneeBankDetailEntity> loaneeBankDetailEntities = loaneeBankDetailRepository.findAllByLoaneeEntity_id(loanee.getId());

        return loaneeBankDetailEntities.stream()
                .map(LoaneeBankDetailEntity::getBankDetailEntity)
                .map(bankDetailMapper::toBankDetail)
                .toList();
    }

    @Override
    public void deleteById(String loaneeBankDetailId) throws MeedlException {
        MeedlValidator.validateUUID(loaneeBankDetailId, "Loanee bank detail id is required to delete");
        loaneeBankDetailRepository.deleteById(loaneeBankDetailId);
    }

    @Override
    public LoaneeBankDetail findApprovedBankDetailByLoaneeId(Loanee loanee) throws MeedlException {
        MeedlValidator.validateObjectInstance(loanee, LoaneeMessages.LOANEE_CANNOT_BE_EMPTY.getMessage());
        MeedlValidator.validateUUID(loanee.getId(), LoaneeMessages.INVALID_LOANEE_ID.getMessage());
        log.info("Viewing loanee bank detail for approved detail by loanee id {} adapter level", loanee.getId());
        LoaneeBankDetailEntity loaneeBankDetailEntity = loaneeBankDetailRepository.findApprovedBankDetailByLoaneeId(loanee.getId());
        return loaneeBankDetailMapper.map(loaneeBankDetailEntity);
    }
}
