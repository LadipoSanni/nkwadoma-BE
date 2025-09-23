package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.loanmanagement;

import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.*;
import africa.nkwadoma.nkwadoma.domain.enums.constants.*;
import africa.nkwadoma.nkwadoma.domain.enums.constants.identity.UserMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.loan.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import africa.nkwadoma.nkwadoma.domain.validation.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.loan.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.*;

import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class LoanRequestAdapter implements LoanRequestOutputPort {
    private final LoanRequestRepository loanRequestRepository;
    private final LoanRequestMapper loanRequestMapper;

    @Override
    public LoanRequest save(LoanRequest loanRequest) throws MeedlException {
        MeedlValidator.validateObjectInstance(loanRequest, LoanMessages.LOAN_REQUEST_MUST_NOT_BE_EMPTY.getMessage());
        MeedlValidator.validateObjectInstance(loanRequest.getStatus(), LoaneeMessages.LOAN_REQUEST_STATUS_CANNOT_BE_EMPTY.getMessage());
        loanRequest.validate();
        LoanRequestEntity loanRequestEntity = loanRequestMapper.toLoanRequestEntity(loanRequest);
        log.info("Mapped loanRequest to be saved: {}", loanRequestEntity);
        LoanRequestEntity savedLoanRequestEntity = loanRequestRepository.save(loanRequestEntity);
        return loanRequestMapper.toLoanRequest(savedLoanRequestEntity);
    }

    @Override
    public LoanRequest findById(String loanRequestId) throws MeedlException {
        MeedlValidator.validateUUID(loanRequestId, LoanMessages.LOAN_REQUEST_MUST_NOT_BE_EMPTY.getMessage());
        LoanRequestProjection loanRequestProjection =
                loanRequestRepository.findLoanRequestById(loanRequestId)
                        .orElseThrow(()->new MeedlException(LoanMessages.LOAN_REQUEST_NOT_FOUND.getMessage()));

        LoanRequest loanRequest = loanRequestMapper.mapProjectionToLoanRequest(loanRequestProjection);
        log.info("Mapped Loan request: {}", loanRequest);
        return loanRequest;
    }

    @Override
    public Optional<LoanRequest> findLoanRequestById(String loanRequestId) throws MeedlException {
        LoanRequestEntity loanRequestEntity = loanRequestRepository.findById(loanRequestId).
                orElseThrow(()-> new MeedlException(LoanMessages.LOAN_REQUEST_NOT_FOUND.getMessage()));
        log.info("Found Loan request: {}", loanRequestEntity);
        Optional<LoanRequest> loanRequest = Optional.of(loanRequestMapper.toLoanRequest(loanRequestEntity));

        return loanRequest;
    }

    @Override
    public void deleteLoanRequestById(String id) throws MeedlException {
        MeedlValidator.validateUUID(id, LoanMessages.LOAN_REQUEST_ID_CANNOT_BE_EMPTY.getMessage());
        loanRequestRepository.deleteById(id);
    }

    @Override
    public Page<LoanRequest> viewAll(int pageNumber, int pageSize) throws MeedlException {
        MeedlValidator.validatePageNumber(pageNumber);
        MeedlValidator.validatePageSize(pageSize);
        Page<LoanRequestProjection> loanRequests = loanRequestRepository.findAllLoanRequests(
                PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Order.desc("createdDate"))));
        log.info("Loan requests retrieved from DB: {}", loanRequests.getContent());
        return loanRequests.map(loanRequestMapper::mapProjectionToLoanRequest);
    }

    @Override
    public Page<LoanRequest> viewAllLoanRequestForLoanee(String userId, int pageNumber, int pageSize) throws MeedlException {
        MeedlValidator.validatePageNumber(pageNumber);
        MeedlValidator.validatePageSize(pageSize);
        MeedlValidator.validateUUID(userId, UserMessages.INVALID_USER_ID.getMessage());
        Page<LoanRequestProjection> loanRequests =
                loanRequestRepository.findAllLoanRequestsForLoanee(userId, PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Order.desc("createdDate"))));
        log.info("Loan requests retrieved from DB for loanee: {}", loanRequests.getContent());
        return loanRequests.map(loanRequestMapper::mapProjectionToLoanRequest);
    }

    @Override
    public Page<LoanRequest> viewAll(String organizationId, int pageNumber, int pageSize) throws MeedlException {
        MeedlValidator.validateUUID(organizationId, OrganizationMessages.INVALID_ORGANIZATION_ID.getMessage());
        MeedlValidator.validatePageNumber(pageNumber);
        MeedlValidator.validatePageSize(pageSize);
        Page<LoanRequestProjection> loanRequests = loanRequestRepository.findAllLoanRequestsByOrganizationId
                (PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Order.desc("createdDate"))), organizationId);
        log.info("Loan requests retrieved: {}", loanRequests.getContent());
        return loanRequests.map(loanRequestMapper::mapProjectionToLoanRequest);
    }

    @Override
    public Page<LoanRequest> searchLoanRequest(LoanRequest loanRequest) throws MeedlException {
        MeedlValidator.validatePageSize(loanRequest.getPageSize());
        MeedlValidator.validatePageNumber(loanRequest.getPageNumber());
        Pageable pageRequest = PageRequest.of(loanRequest.getPageNumber(), loanRequest.getPageSize());
        log.info("request that got into adapter {}", loanRequest.getName());
        Page<LoanRequestProjection> loanRequestProjections =
                loanRequestRepository.findAllLoanRequestByLoaneeNameInOrganizationAndProgram(loanRequest.getProgramId(),
                        loanRequest.getOrganizationId(),loanRequest.getName(),pageRequest);
        log.info("Loan requests retrieved from DB: {}", loanRequestProjections.getContent());
        return loanRequestProjections.map(loanRequestMapper::mapProjectionToLoanRequest);
    }

    @Override
    public Page<LoanRequest> filterLoanRequestByProgram(String programId, String organizationId,int pageSize, int pageNumber) throws MeedlException {
        MeedlValidator.validateUUID(organizationId, OrganizationMessages.INVALID_ORGANIZATION_ID.getMessage());
        MeedlValidator.validateUUID(programId, ProgramMessages.INVALID_PROGRAM_ID.getMessage());
        MeedlValidator.validatePageSize(pageSize);
        MeedlValidator.validatePageNumber(pageNumber);
        Pageable pageRequest = PageRequest.of(pageNumber,pageSize);
        Page<LoanRequestProjection> loanRequestProjections =
                loanRequestRepository.filterLoanRequestByProgramIdAndOrganization(programId,
                        organizationId,pageRequest);
        return loanRequestProjections.map(loanRequestMapper::mapProjectionToLoanRequest);
    }

    @Override
    public LoanRequest findLoanRequestByLoaneeId(String loaneeId) throws MeedlException {
        MeedlValidator.validateUUID(loaneeId,LoaneeMessages.INVALID_LOANEE_ID.getMessage());

        Optional<LoanRequestProjection> loanRequestProjection =
                loanRequestRepository.findLoanRequestByLoaneeEntityId(loaneeId);
        LoanRequest loanRequest = loanRequestMapper.mapProjectionToLoanRequest(loanRequestProjection.get());
        log.info("Mapped Loan request: {}", loanRequest);
        return loanRequest;
    }

    @Override
    public int getCountOfAllVerifiedLoanRequestInOrganization(String organizationId) {
        return loanRequestRepository.getCountOfVerifiedLoanRequestInOrganization(organizationId);
    }


}
