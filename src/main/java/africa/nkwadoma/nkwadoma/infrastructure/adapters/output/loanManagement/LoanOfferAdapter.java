package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.loanManagement;

import africa.nkwadoma.nkwadoma.application.ports.output.loan.LoanOfferOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanOffer;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity.LoanOfferEntitiy;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.LoanOfferMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.LoanOfferEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

@Component
@RequiredArgsConstructor
public class LoanOfferAdapter implements LoanOfferOutputPort {

    private final LoanOfferMapper loanOfferMapper;
    private final LoanOfferEntityRepository loanOfferEntityRepository;

    @Override
    public LoanOffer save(LoanOffer loanOffer) throws MeedlException {
        MeedlValidator.validateObjectInstance(loanOffer);
        loanOffer.validate();
        LoanOfferEntitiy loanOfferEntitiy = loanOfferMapper.toLoanOfferEntity(loanOffer);
        loanOfferEntitiy = loanOfferEntityRepository.save(loanOfferEntitiy);
        return loanOfferMapper.toLoanOffer(loanOfferEntitiy);
    }

    @Override
    public LoanOffer findLoanOfferById(String loanOfferId){
        return null;
    }

    @Override
    public void deleteLoanOfferById(String loanOfferId) {
        loanOfferEntityRepository.deleteById(loanOfferId);  
    }

    @Override
    public Page<LoanOffer> findLoanOfferInOrganization(String organization,int pageSize , int pageNumber) throws MeedlException {
        MeedlValidator.validateUUID(organization);
        Pageable pageRequest = PageRequest.of(pageNumber,pageSize);
        Page<LoanOfferEntitiy> loanOfferEntities =
                loanOfferEntityRepository.findAllLoanOfferInOrganization(organization,pageRequest);
        return loanOfferEntities.map(loanOfferMapper::toLoanOffer);
    }

    @Override
    public Page<LoanOffer> findAllLoanOffers(int pageSize, int pageNumber) throws MeedlException {
        MeedlValidator.validatePageSize(pageSize);
        MeedlValidator.validatePageNumber(pageNumber);
        Pageable pageRequest = PageRequest.of(pageNumber,pageSize);
        Page<LoanOfferEntitiy> loanOfferEntities =
                loanOfferEntityRepository.findAll(pageRequest);
        return loanOfferEntities.map(loanOfferMapper::toLoanOffer);
    }
}
