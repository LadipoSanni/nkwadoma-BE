package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.loanmanagement;

import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.LoaneeLoanAggregateOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.constants.OrganizationMessages;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanDetailSummary;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoaneeLoanAggregate;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity.LoaneeLoanAggregateEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.loanee.LoaneeLoanAggregateMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.LoanSummaryProjection;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.LoaneeLoanAggregateProjection;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.LoaneeLoanAggregateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoaneeLoanAggregateAdapter implements LoaneeLoanAggregateOutputPort {

    private final LoaneeLoanAggregateMapper loaneeLoanAggregateMapper;
    private final LoaneeLoanAggregateRepository loaneeLoanAggregateRepository;

    @Override
    public LoaneeLoanAggregate save(LoaneeLoanAggregate loaneeLoanAggregate) throws MeedlException {
        MeedlValidator.validateObjectInstance(loaneeLoanAggregate,"Loanee loan aggregate cannot be empty");
        loaneeLoanAggregate.validate();
        log.info("Saving loanee loan aggregate after validation in adapter");
        LoaneeLoanAggregateEntity loaneeLoanAggregateEntity = loaneeLoanAggregateMapper.toLoaneeLoanAggregateEntity(loaneeLoanAggregate);
        loaneeLoanAggregateEntity = loaneeLoanAggregateRepository.save(loaneeLoanAggregateEntity);
        return loaneeLoanAggregateMapper.toLoaneeLoanAggregate(loaneeLoanAggregateEntity);
    }

    @Override
    public void delete(String loaneeLoanAggregateId) throws MeedlException {
        MeedlValidator.validateUUID(loaneeLoanAggregateId,"Loanee loan aggregate id cannot be empty");
        loaneeLoanAggregateRepository.deleteById(loaneeLoanAggregateId);
    }

    @Override
    public LoaneeLoanAggregate findByLoaneeId(String id) throws MeedlException {
        MeedlValidator.validateUUID(id,"Loanee id cannot be empty");
        LoaneeLoanAggregateEntity loaneeLoanAggregateEntity =
                loaneeLoanAggregateRepository.findByLoaneeId(id);
        return loaneeLoanAggregateMapper.toLoaneeLoanAggregate(loaneeLoanAggregateEntity);
    }

    @Override
    public Page<LoaneeLoanAggregate> findAllLoanAggregate(int pageSize, int pageNumber) {
        Pageable pageRequest = PageRequest.of(pageNumber, pageSize);
        Page<LoaneeLoanAggregateProjection> loaneeLoanAggregateProjections =
                loaneeLoanAggregateRepository.findAllByPagination(pageRequest);
        return loaneeLoanAggregateProjections.map(loaneeLoanAggregateMapper::mapProjectionToLoaneeLoanAggregate);
    }

    @Override
    public Page<LoaneeLoanAggregate> searchLoanAggregate(String name, int pageSize, int pageNumber) {
        Pageable pageRequest = PageRequest.of(pageNumber, pageSize);
        Page<LoaneeLoanAggregateProjection> loaneeLoanAggregateProjections =
                loaneeLoanAggregateRepository.searchLoaneeLoanAggregate(name,pageRequest);
        return loaneeLoanAggregateProjections.map(loaneeLoanAggregateMapper::mapProjectionToLoaneeLoanAggregate);
    }

    @Override
    public LoanDetailSummary getLoanAggregationSummary() {
        LoanSummaryProjection loanSummaryProjection = loaneeLoanAggregateRepository.getLoanSummary();
        return loaneeLoanAggregateMapper.mapLoanSummaryProjectionToLoanDetailSummary(loanSummaryProjection);
    }

    @Override
    public LoaneeLoanAggregate findByLoaneeLoanAggregateByLoaneeLoanDetailId(String id) throws MeedlException {
        MeedlValidator.validateUUID(id,"Loanee loan detail cannot be empty");
        log.info("Finding loanee loan aggregate by id {}", id);
        LoaneeLoanAggregateEntity loaneeLoanAggregateEntity = loaneeLoanAggregateRepository.findByLoaneeLoandetailId(id);
        log.info("Loanee loan aggregate entity found in adapter with id {}", loaneeLoanAggregateEntity.getId());
        return loaneeLoanAggregateMapper.toLoaneeLoanAggregate(loaneeLoanAggregateEntity);
    }

    @Override
    public Page<LoaneeLoanAggregate> findAllLoanAggregateByOrganizationId(String organizationId, int pageSize, int pageNumber) throws MeedlException {
        MeedlValidator.validateUUID(organizationId, OrganizationMessages.INVALID_ORGANIZATION_ID.getMessage());
        Pageable pageRequest = PageRequest.of(pageNumber, pageSize);

        Page<LoaneeLoanAggregateProjection> loaneeLoanAggregateProjections =
                loaneeLoanAggregateRepository.findAllByOrganizationId(organizationId,pageRequest);
        return loaneeLoanAggregateProjections.map(loaneeLoanAggregateMapper::mapProjectionToLoaneeLoanAggregate);
    }

    @Override
    public Page<LoaneeLoanAggregate> searchLoanAggregateByOrganizationId(Loanee loanee, int pageSize, int pageNumber) throws MeedlException {
        MeedlValidator.validateUUID(loanee.getOrganizationId(), OrganizationMessages.INVALID_ORGANIZATION_ID.getMessage());
        Pageable pageRequest = PageRequest.of(pageNumber, pageSize);
        Page<LoaneeLoanAggregateProjection> loaneeLoanAggregateProjections =
                loaneeLoanAggregateRepository.searchLoaneeLoanAggregateByOrganizationId(loanee.getLoaneeName(),
                        loanee.getOrganizationId(),pageRequest);
        return loaneeLoanAggregateProjections.map(loaneeLoanAggregateMapper::mapProjectionToLoaneeLoanAggregate);
    }

}
