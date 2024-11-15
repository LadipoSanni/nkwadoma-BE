package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.education;

import africa.nkwadoma.nkwadoma.application.ports.output.education.CohortOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.education.LoanDetailsOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.education.ProgramCohortOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.education.ProgramOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationEmployeeIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loan.LoanBreakdownOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.CohortStatus;
import africa.nkwadoma.nkwadoma.domain.enums.constants.CohortMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.MeedlMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.ProgramMessages;
import africa.nkwadoma.nkwadoma.domain.exceptions.IdentityException;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.exceptions.education.CohortException;
import africa.nkwadoma.nkwadoma.domain.exceptions.education.EducationException;
import africa.nkwadoma.nkwadoma.domain.model.education.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationEmployeeIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education.CohortEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity.LoanBreakdownEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.CohortMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.education.CohortRepository;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.LoanBreakdownRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.*;
import java.util.List;
import java.util.stream.*;

import static africa.nkwadoma.nkwadoma.domain.enums.constants.CohortMessages.*;
import static africa.nkwadoma.nkwadoma.domain.enums.constants.IdentityMessages.USER_NOT_FOUND;
import static africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator.validateDataElement;


@Slf4j
@RequiredArgsConstructor
public class CohortPersistenceAdapter implements CohortOutputPort {

    private final ProgramOutputPort programOutputPort;
    private final CohortRepository cohortRepository;
    private final CohortMapper cohortMapper;
    private final UserIdentityOutputPort userIdentityOutputPort;
    private final ProgramCohortOutputPort programCohortOutputPort;
    private final LoanBreakdownRepository loanBreakdownRepository;
    private final LoanBreakdownOutputPort loanBreakdownOutputPort;
    private final LoanDetailsOutputPort loanDetailsOutputPort;
    private final OrganizationEmployeeIdentityOutputPort employeeIdentityOutputPort;
    private final OrganizationIdentityOutputPort organizationIdentityOutputPort;




    @Override
    public Cohort saveCohort(Cohort cohort) throws MeedlException {
        MeedlValidator.validateObjectInstance(cohort);
        cohort.validate();
        Program program = programOutputPort.findProgramById(cohort.getProgramId());
        if (program == null) {
            throw new CohortException(ProgramMessages.PROGRAM_NOT_FOUND.getMessage());
        }
//        OrganizationIdentity organizationIdentity = findCreatorOrganization(cohort.getCreatedBy());
//        if (!Objects.equals(organizationIdentity.getId(), program.getOrganizationId())){
//            throw new CohortException(CohortMessages.CREATEDBY_NOT_EXIST_IN_ORGANIZATION.getMessage());
//        }
        List<ProgramCohort> programCohortList = programCohortOutputPort.findAllByProgramId(cohort.getProgramId());
        log.info("Found program cohort: {}", programCohortList);
        Optional<ProgramCohort> existingProgramCohort = programCohortList.stream()
                .filter(eachProgramCohort -> eachProgramCohort.getCohort().getName().equals(cohort.getName()))
                .findFirst();
        if (existingProgramCohort.isEmpty() && cohort.getId() != null) {
            existingProgramCohort = programCohortList.stream()
                    .filter(eachProgramCohort -> eachProgramCohort.getCohort().getId().equals(cohort.getId()))
                    .findFirst();
        }
        Cohort retrievedCohort  =  updateOrAddCohortToProgram(cohort, existingProgramCohort, program);
        programOutputPort.saveProgram(program);
        return retrievedCohort;
    }

//    private OrganizationIdentity findCreatorOrganization(String createdBy) throws MeedlException {
//        MeedlValidator.validateUUID(createdBy);
//        log.info("Validating the created by: {}",createdBy);
//        OrganizationEmployeeIdentity employeeIdentity = employeeIdentityOutputPort.findByCreatedBy(createdBy);
//        if (ObjectUtils.isEmpty(employeeIdentity)) {
//            throw new EducationException(MeedlMessages.INVALID_CREATED_BY.getMessage());
//        }
//        return organizationIdentityOutputPort.findById(employeeIdentity.getOrganization());
//    }

    private Cohort updateOrAddCohortToProgram(Cohort cohort, Optional<ProgramCohort> existingProgramCohort, Program program) throws MeedlException {

        if (existingProgramCohort.isPresent() && existingProgramCohort.get().getCohort() != null) {
            Cohort cohortToUpdate = existingProgramCohort.get().getCohort();
            List<LoanBreakdown> loanBreakdowns = loanBreakdownOutputPort.findAllByCohortId(cohortToUpdate.getId());
            if (cohortToUpdate.getTuitionAmount() != null) {
                BigDecimal totalCohortFee = calculateTotalLoanBreakdownAmount(loanBreakdowns, cohortToUpdate.getTuitionAmount());
                cohortToUpdate.setTotalCohortFee(totalCohortFee);
            }
            cohort.setLoanBreakdowns(loanBreakdowns);
            cohort = updateCohort(cohort, cohortToUpdate);
        } else {
            if (cohort.getTuitionAmount() != null) {
                BigDecimal totalCohortFee = calculateTotalLoanBreakdownAmount(cohort.getLoanBreakdowns(), cohort.getTuitionAmount());
                cohort.setTotalCohortFee(totalCohortFee);
            }
            cohort = newCohort(cohort, program);
        }
        return cohort;
    }

    private Cohort updateCohort(Cohort cohort, Cohort cohortToUpdate) throws MeedlException {
        CohortEntity cohortEntity;
        List<LoanBreakdown> savedLoanBreakdowns;
        if (cohort.getId() != null && cohort.getId().equals(cohortToUpdate.getId())) {
            cohortEntity = cohortMapper.toCohortEntity(cohortToUpdate);
            if (cohortEntity.getLoanDetail() != null) {
                throw new CohortException(CohortMessages.COHORT_WITH_LOAN_DETAILS_CANNOT_BE_EDITED.getMessage());
            }if (cohort.getLoanDetail() != null){
               LoanDetail loanDetail =  loanDetailsOutputPort.saveLoanDetails(cohort.getLoanDetail());
               cohort.setLoanDetail(loanDetail);
            }
            cohortToUpdate = cohortMapper.cohortToUpdateCohort(cohort);
            cohortToUpdate.setUpdatedAt(LocalDateTime.now());
            cohortToUpdate.setUpdatedBy(cohortToUpdate.getCreatedBy());
            activateStatus(cohortToUpdate);
            cohortEntity = cohortMapper.toCohortEntity(cohortToUpdate);
            cohortEntity = cohortRepository.save(cohortEntity);
            savedLoanBreakdowns = saveLoanBreakdown(cohortToUpdate.getLoanBreakdowns(), cohortEntity);
            } else {
                throw new CohortException(COHORT_EXIST.getMessage());
            }
        cohort = cohortMapper.toCohort(cohortEntity);
        cohort.setLoanBreakdowns(savedLoanBreakdowns);
        return cohort;
    }

    private Cohort newCohort(Cohort cohort, Program program) throws MeedlException {
        cohort.setCreatedAt(LocalDateTime.now());
        cohort.setNumberOfLoanees(0);
        activateStatus(cohort);
        ProgramCohort newProgramCohort = new ProgramCohort();
        if (cohort.getLoanDetail() != null) {
            LoanDetail loanDetail = loanDetailsOutputPort.saveLoanDetails(cohort.getLoanDetail());
            cohort.setLoanDetail(loanDetail);
        }
        CohortEntity cohortEntity = cohortMapper.toCohortEntity(cohort);
        cohortEntity = cohortRepository.save(cohortEntity);
        List<LoanBreakdown> savedLoanBreakdowns = saveLoanBreakdown(cohort.getLoanBreakdowns(), cohortEntity);
        cohort = cohortMapper.toCohort(cohortEntity);
        program.setNumberOfCohort(program.getNumberOfCohort() + 1);
        newProgramCohort.setCohort(cohort);
        newProgramCohort.setProgramId(program.getId());
        log.info("The program id is {}", newProgramCohort.getProgramId());
        programCohortOutputPort.save(newProgramCohort);
        cohort = cohortMapper.toCohort(cohortEntity);
        cohort.setLoanBreakdowns(savedLoanBreakdowns);
        return cohort;
    }

    private static void activateStatus(Cohort cohort) {
        LocalDateTime now = LocalDateTime.now();
        if (cohort.getStartDate().isAfter(now)) {
            cohort.setActivationStatus(ActivationStatus.INACTIVE);
            cohort.setCohortStatus(CohortStatus.INCOMING);
        } else if (cohort.getStartDate().isBefore(now) && cohort.getExpectedEndDate().isAfter(now)) {
            cohort.setActivationStatus(ActivationStatus.ACTIVE);
            cohort.setCohortStatus(CohortStatus.CURRENT);
        } else if (cohort.getExpectedEndDate().isBefore(now) || cohort.getExpectedEndDate().isEqual(now)) {
            cohort.setActivationStatus(ActivationStatus.INACTIVE);
            cohort.setCohortStatus(CohortStatus.GRADUATED);
        }
    }

    @Override
    public Cohort viewCohortDetails(String userId, String programId, String cohortId) throws MeedlException {
        validateDataElement(userId);
        validateDataElement(programId);
        validateDataElement(cohortId);
        UserIdentity userIdentity = userIdentityOutputPort.findById(userId);
        if (userIdentity == null){
            throw new IdentityException(USER_NOT_FOUND.getMessage());
        }
        List<ProgramCohort> programCohorts = programCohortOutputPort.findAllByProgramId(programId);
        return getCohort(cohortId,programCohorts );
    }

    @Transactional
    @Override
    public void deleteCohort(String id) throws MeedlException {
        MeedlValidator.validateDataElement(id);
        CohortEntity cohortEntity = cohortRepository.findById(id).orElseThrow(() -> new CohortException(COHORT_DOES_NOT_EXIST.getMessage()));
        programCohortOutputPort.deleteAllByCohort(cohortEntity);
        loanBreakdownRepository.deleteAllByCohort(cohortEntity);
        cohortRepository.deleteById(id);
    }

    @Override
    public Cohort findCohort(String cohortId) throws CohortException {
        CohortEntity cohortEntity = cohortRepository.findById(cohortId).orElseThrow(() -> new CohortException(COHORT_DOES_NOT_EXIST.getMessage()));
        return cohortMapper.toCohort(cohortEntity);
    }

    @Override
    public Cohort save(Cohort cohort) {
        CohortEntity cohortEntity = cohortMapper.toCohortEntity(cohort);
        cohortEntity = cohortRepository.save(cohortEntity);
        return cohortMapper.toCohort(cohortEntity);
    }

    private static Cohort getCohort(String cohortId, List<ProgramCohort> programCohorts) throws CohortException {
        return programCohorts.stream()
                .filter(eachCohort -> eachCohort.getCohort().getId().equals(cohortId))
                .findFirst()
                .orElseThrow(() -> new CohortException(COHORT_DOES_NOT_EXIST.getMessage())).getCohort();
    }
    private List<LoanBreakdown> saveLoanBreakdown(List<LoanBreakdown> breakdowns, CohortEntity savedCohort) {
        List<LoanBreakdown> loanBreakdowns = breakdowns.stream()
                .peek(loanBreakdownObject -> loanBreakdownObject.setCohort(cohortMapper.toCohort(savedCohort)))
                        .toList();
        List<LoanBreakdownEntity> loanBreakdownEntities = loanBreakdowns.stream()
                .map(cohortMapper::mapToLoanBreakdownEntity)
                        .toList();
         loanBreakdownEntities = loanBreakdownRepository.saveAll(loanBreakdownEntities);
         return loanBreakdownEntities.stream().map(cohortMapper::mapToLoanBreakdown).toList();
    }
    private BigDecimal calculateTotalLoanBreakdownAmount(List<LoanBreakdown> loanBreakdowns,BigDecimal tutionFee) {
        return loanBreakdowns.stream()
                .map(LoanBreakdown::getItemAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add).add(tutionFee);
    }

    @Override
    public Cohort searchForCohortInAProgram(String name, String programId) throws MeedlException {
        List<ProgramCohort> programCohorts = programCohortOutputPort.findAllByProgramId(programId);
        return programCohorts.stream()
                .filter(eachProgramCohort -> eachProgramCohort.getCohort().getName().equalsIgnoreCase(name.trim()))
                .findFirst().orElseThrow(() -> new CohortException(COHORT_DOES_NOT_EXIST.getMessage())).getCohort();
    }

    @Override
    public List<Cohort> findAllCohortInAProgram(String programId) throws MeedlException {
        List<ProgramCohort> programCohorts = programCohortOutputPort.findAllByProgramId(programId);
        return programCohorts.stream()
                .map(ProgramCohort::getCohort)
                .toList();
    }
}

