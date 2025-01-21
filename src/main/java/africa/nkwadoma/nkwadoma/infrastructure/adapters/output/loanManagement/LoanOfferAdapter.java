package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.loanManagement;

import africa.nkwadoma.nkwadoma.application.ports.output.loan.LoanOfferOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.constants.loan.LoanMessages;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanOffer;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity.LoanOfferEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.LoanOfferMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.LoanOfferEntityRepository;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.LoanOfferProjection;
import africa.nkwadoma.nkwadoma.infrastructure.exceptions.LoanException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.*;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class LoanOfferAdapter implements LoanOfferOutputPort {

    private final LoanOfferMapper loanOfferMapper;
    private final LoanOfferEntityRepository loanOfferEntityRepository;

    @Override
    public LoanOffer  save(LoanOffer loanOffer) throws MeedlException {
        MeedlValidator.validateObjectInstance(loanOffer);
        loanOffer.validate();
        LoanOfferEntity loanOfferEntity = loanOfferMapper.toLoanOfferEntity(loanOffer);
        loanOfferEntity = loanOfferEntityRepository.save(loanOfferEntity);
        return loanOfferMapper.toLoanOffer(loanOfferEntity);
    }

    @Override
    public LoanOffer findLoanOfferById(String loanOfferId) throws MeedlException {
        MeedlValidator.validateUUID(loanOfferId);
        LoanOfferProjection loanOfferProjection = loanOfferEntityRepository.findLoanOfferById(loanOfferId);
        if (ObjectUtils.isEmpty(loanOfferProjection)) {
            throw new LoanException(LoanMessages.LOAN_OFFER_NOT_FOUND.getMessage());
        }
        return loanOfferMapper.mapProjectionToLoanOffer(loanOfferProjection);
    }

    @Override
    public void deleteLoanOfferById(String loanOfferId) {
        loanOfferEntityRepository.deleteById(loanOfferId);  
    }

    @Override
    public Page<LoanOffer> findLoanOfferInOrganization(String organization,int pageSize , int pageNumber) throws MeedlException {
        MeedlValidator.validateUUID(organization);
        Pageable pageRequest = PageRequest.of(pageNumber,pageSize);
        Page<LoanOfferProjection> loanOfferProjections =
                loanOfferEntityRepository.findAllLoanOfferInOrganization(organization,pageRequest);
        return loanOfferMapper.toLoanOffers(loanOfferProjections);
    }

    @Override
    public Page<LoanOffer> findAllLoanOffers(int pageSize, int pageNumber) throws MeedlException {
        MeedlValidator.validatePageSize(pageSize);
        MeedlValidator.validatePageNumber(pageNumber);
        Pageable pageRequest = PageRequest.of(pageNumber,pageSize);
        Page<LoanOfferProjection> loanOfferProjections =
                loanOfferEntityRepository.findAllLoanOffer(pageRequest);
        log.info("Loan offers found: {}", loanOfferProjections);
        Page<LoanOffer> mappedloanOffers = loanOfferMapper.toLoanOffers(loanOfferProjections);
        log.info("Mapped loans offers: {}", mappedloanOffers);
        return mappedloanOffers;
    }
}
