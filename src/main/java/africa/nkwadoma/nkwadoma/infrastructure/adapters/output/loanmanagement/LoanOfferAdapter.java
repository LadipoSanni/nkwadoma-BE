package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.loanmanagement;

import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.LoanOfferOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.constants.CohortMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.OrganizationMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.ProgramMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.loan.LoanMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.loan.LoanOfferMessages;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanOffer;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity.LoanOfferEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.LoanOfferMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.LoanOfferEntityRepository;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.LoanOfferProjection;
import africa.nkwadoma.nkwadoma.domain.exceptions.loan.LoanException;
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
        MeedlValidator.validateObjectInstance(loanOffer, LoanOfferMessages.LOAN_OFFER_CANNOT_BE_EMPTY.getMessage());
        loanOffer.validate();
        LoanOfferEntity loanOfferEntity = loanOfferMapper.toLoanOfferEntity(loanOffer);
        loanOfferEntity = loanOfferEntityRepository.save(loanOfferEntity);
        return loanOfferMapper.toLoanOffer(loanOfferEntity);
    }

    @Override
    public LoanOffer findLoanOfferById(String loanOfferId) throws MeedlException {
        log.info("Find loan offer by id: {}", loanOfferId);
        MeedlValidator.validateUUID(loanOfferId, LoanOfferMessages.INVALID_LOAN_OFFER_ID.getMessage());
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
    public Page<LoanOffer> findLoanOfferInOrganization(String organizationId,int pageSize , int pageNumber) throws MeedlException {
        MeedlValidator.validateUUID(organizationId, OrganizationMessages.INVALID_ORGANIZATION_ID.getMessage());
        Pageable pageRequest = PageRequest.of(pageNumber,pageSize);
        Page<LoanOfferProjection> loanOfferProjections =
                loanOfferEntityRepository.findAllLoanOfferInOrganization(organizationId,pageRequest);
        return loanOfferProjections.map(loanOfferMapper::mapProjectionToLoanOffer);
    }

    @Override
    public Page<LoanOffer> findAllLoanOffers(int pageSize, int pageNumber) throws MeedlException {
        MeedlValidator.validatePageSize(pageSize);
        MeedlValidator.validatePageNumber(pageNumber);
        Pageable pageRequest = PageRequest.of(pageNumber,pageSize);
        Page<LoanOfferProjection> loanOfferProjections =
                loanOfferEntityRepository.findAllLoanOffer(pageRequest);
        log.info("Loan offers found: {}", loanOfferProjections);
        Page<LoanOffer> mappedloanOffers = loanOfferProjections.map(loanOfferMapper::mapProjectionToLoanOffer);
        log.info("Mapped loans offers: {}", mappedloanOffers);
        return mappedloanOffers;
    }

    @Override
    public Page<LoanOffer> searchLoanOffer(LoanOffer loanOffer) throws MeedlException {
        MeedlValidator.validateUUID(loanOffer.getOrganizationId(), OrganizationMessages.INVALID_ORGANIZATION_ID.getMessage());
        MeedlValidator.validateUUID(loanOffer.getProgramId(), ProgramMessages.INVALID_PROGRAM_ID.getMessage());
        MeedlValidator.validatePageSize(loanOffer.getPageSize());
        MeedlValidator.validatePageNumber(loanOffer.getPageNumber());
        Pageable pageRequest = PageRequest.of(loanOffer.getPageNumber(), loanOffer.getPageSize());
        Page<LoanOfferProjection> loanOfferProjections =
                loanOfferEntityRepository.
                        findAllLoanOfferByLoaneeNameInOrganizationAndProgram(loanOffer.getProgramId(),
                                loanOffer.getOrganizationId(), loanOffer.getName(), pageRequest);

        return loanOfferProjections.map(loanOfferMapper::mapProjectionToLoanOffer);
    }

    @Override
    public Page<LoanOffer> filterLoanOfferByProgram(LoanOffer loanOffer) throws MeedlException {
        MeedlValidator.validateUUID(loanOffer.getOrganizationId(), OrganizationMessages.INVALID_ORGANIZATION_ID.getMessage());
        MeedlValidator.validateUUID(loanOffer.getProgramId(), ProgramMessages.INVALID_PROGRAM_ID.getMessage());
        MeedlValidator.validatePageSize(loanOffer.getPageSize());
        MeedlValidator.validatePageNumber(loanOffer.getPageNumber());
        Pageable pageRequest = PageRequest.of(loanOffer.getPageNumber(), loanOffer.getPageSize());
        Page<LoanOfferProjection> loanOfferProjections =
                loanOfferEntityRepository.filterLoanOfferByProgramIdAndOrganization(loanOffer.getProgramId(),
                        loanOffer.getOrganizationId(),pageRequest);
        return loanOfferProjections.map(loanOfferMapper::mapProjectionToLoanOffer);
    }

    @Override
    public LoanOffer findLoanOfferByLoaneeId(String loaneeId) throws MeedlException {
        MeedlValidator.validateUUID(loaneeId, LoanMessages.INVALID_LOANEE_ID.getMessage());
        LoanOfferEntity loanOfferEntity = loanOfferEntityRepository.findLoanOfferByLoaneeId(loaneeId);
        return loanOfferMapper.toLoanOffer(loanOfferEntity);
    }

    @Override
    public int countNumberOfPendingLoanOfferForCohort(String id) throws MeedlException {
        MeedlValidator.validateUUID(id, CohortMessages.INVALID_COHORT_ID.getMessage());
        return loanOfferEntityRepository.countPendingOfferByCohortId(id);
    }
}
