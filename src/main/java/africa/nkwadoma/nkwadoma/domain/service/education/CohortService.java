package africa.nkwadoma.nkwadoma.domain.service.education;

import africa.nkwadoma.nkwadoma.application.ports.input.education.CohortUseCase;
import africa.nkwadoma.nkwadoma.application.ports.input.loanmanagement.LoaneeUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.education.*;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.LoanBreakdownOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.LoanOfferOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.notification.email.AsynchronousMailingOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.CohortStatus;
import africa.nkwadoma.nkwadoma.domain.enums.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.enums.constants.CohortMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.OrganizationMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.ProgramMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.UserMessages;
import africa.nkwadoma.nkwadoma.domain.enums.loanenums.LoanOfferStatus;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.exceptions.ResourceNotFoundException;
import africa.nkwadoma.nkwadoma.domain.exceptions.education.EducationException;
import africa.nkwadoma.nkwadoma.domain.model.education.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanOffer;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanReferral;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.CohortLoanDetailMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.CohortMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.*;
import org.springframework.data.domain.*;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static africa.nkwadoma.nkwadoma.domain.enums.constants.ProgramMessages.PROGRAM_NOT_FOUND;
import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.cohort.SuccessMessages.COHORT_INVITED;
import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.loan.SuccessMessages.LOANEE_HAS_BEEN_REFERED;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@EnableAsync
@RequiredArgsConstructor
public class CohortService implements CohortUseCase {
    private final CohortOutputPort cohortOutputPort;
    private final ProgramOutputPort programOutputPort;
    private final LoaneeOutputPort loaneeOutputPort;
    private final ProgramCohortOutputPort programCohortOutputPort;
    private final LoanDetailsOutputPort loanDetailsOutputPort;
    private final LoanBreakdownOutputPort loanBreakdownOutputPort;
    private final CohortMapper cohortMapper;
    private final UserIdentityOutputPort userIdentityOutputPort;
    private final LoaneeUseCase loaneeUseCase;
    private final OrganizationIdentityOutputPort organizationIdentityOutputPort;
    private final AsynchronousMailingOutputPort asynchronousMailingOutputPort;
    private final CohortLoanDetailOutputPort cohortLoanDetailOutputPort;
    private final CohortLoanDetailMapper cohortLoanDetailMapper;
    private final CohortLoaneeOutputPort cohortLoaneeOutputPort;
    private final LoanOfferOutputPort loanOfferOutputPort;

    @Override
    public Cohort createCohort(Cohort cohort) throws MeedlException {
        MeedlValidator.validateObjectInstance(cohort, CohortMessages.INPUT_CANNOT_BE_NULL.getMessage());
        cohort.validate();
        String cohortName = cohort.getName();
        UserIdentity userIdentity = userIdentityOutputPort.findById(cohort.getCreatedBy());
        if (userIdentity.getRole().equals(IdentityRole.ORGANIZATION_ADMIN)) {
            cohort.validateLoanBreakDowns();
        }
        log.info("Creating cohort with name {} at service level", cohortName);
        Program program = checkifCohortNameExistInProgram(cohort, cohortName);
        cohort.setCreatedAt(LocalDateTime.now());
        cohort.setNumberOfLoanees(0);
        cohort.setExpectedEndDate(cohort.getStartDate().plusMonths(program.getDuration()));
        activateStatus(cohort);
        Cohort savedCohort = cohortOutputPort.save(cohort);
        List<LoanBreakdown> savedLoanBreakdowns = saveLoanBreakdown(cohort.getLoanBreakdowns(), savedCohort);
        programCohortOutputPort.linkCohortToProgram(program, savedCohort);
        BigDecimal totalFee = calculateTotalLoanBreakdownAmount(savedLoanBreakdowns, cohort.getTuitionAmount());
        savedCohort.setTotalCohortFee(totalFee);
        savedCohort.setOrganizationId(program.getOrganizationId());
        savedCohort = cohortOutputPort.save(savedCohort);
        savedCohort.setLoanBreakdowns(savedLoanBreakdowns);
        savedCohort.setProgramName(program.getName());
        updateNumberOfCohortInOrganization(program.getOrganizationId());

        CohortLoanDetail cohortLoanDetail = buildCohortLoanDetail(savedCohort);
        cohortLoanDetailOutputPort.save(cohortLoanDetail);
        return savedCohort;
    }

    private static CohortLoanDetail buildCohortLoanDetail(Cohort savedCohort) {
        return CohortLoanDetail.builder()
                .cohort(savedCohort)
                .totalAmountRequested(BigDecimal.valueOf(0))
                .totalAmountReceived(BigDecimal.valueOf(0))
                .totalOutstandingAmount(BigDecimal.valueOf(0))
                .totalAmountRepaid(BigDecimal.valueOf(0))
                .build();
    }

    public void updateNumberOfCohortInOrganization(String organizationId) throws MeedlException {
        OrganizationIdentity organizationIdentity = organizationIdentityOutputPort.findById(organizationId);
        organizationIdentity.setNumberOfCohort(organizationIdentity.getNumberOfCohort() + 1);
        organizationIdentityOutputPort.save(organizationIdentity);
    }

    private Program checkifCohortNameExistInProgram(Cohort cohort, String cohortName) throws MeedlException {
        Program program = programOutputPort.findProgramById(cohort.getProgramId());
        log.info("Checking if cohort name exists in program found : {}", program);
        if (ObjectUtils.isEmpty(program)) {
            log.info("Program selected for cohort creation was not found. Cohort name {}, {}",cohortName, ProgramMessages.PROGRAM_NOT_FOUND.getMessage());
            throw new ResourceNotFoundException(ProgramMessages.PROGRAM_NOT_FOUND.getMessage());
        }
        List<ProgramCohort> programCohortList = programCohortOutputPort.findAllByProgramId(cohort.getProgramId());
        Optional<ProgramCohort> existingProgramCohort = programCohortList.stream()
                .filter(eachProgramCohort -> eachProgramCohort.getCohort().getName().equalsIgnoreCase(cohortName))
                .findFirst();
        if (existingProgramCohort.isPresent()) {
            log.info("Cohort with name {} already exists in program. Cohort id {}", cohortName, existingProgramCohort.get().getId());
            throw new EducationException(CohortMessages.COHORT_WITH_NAME_EXIST.getMessage());
        }
        return program;
    }

    @Override
    public Cohort editCohort(Cohort cohort) throws MeedlException {
        cohort.updateValidation();
        Cohort foundCohort = cohortOutputPort.findCohort(cohort.getId());
        Program program = programOutputPort.findProgramById(foundCohort.getProgramId());
        checkIfCohortNameExist(cohort, foundCohort);
        if (!ObjectUtils.isEmpty(foundCohort.getLoanDetail())) {
            throw new EducationException(CohortMessages.COHORT_WITH_LOAN_DETAILS_CANNOT_BE_EDITED.getMessage());
        }
        cohortMapper.updateCohort(foundCohort, cohort);
        foundCohort.setUpdatedAt(LocalDateTime.now());
        foundCohort.setExpectedEndDate(foundCohort.getStartDate().plusMonths(program.getDuration()));
        activateStatus(foundCohort);
        List<LoanBreakdown> foundLoanBreakDown = loanBreakdownOutputPort.findAllByCohortId(cohort.getId());
        cohortOutputPort.save(foundCohort);
        foundCohort.setLoanBreakdowns(foundLoanBreakDown);
        foundCohort.setProgramName(program.getName());
        return foundCohort;
    }

    private void checkIfCohortNameExist(Cohort cohort, Cohort foundCohort) throws MeedlException {
        Cohort foundCohortByName = null;
        if (StringUtils.isNotEmpty(cohort.getName())) {
            foundCohortByName = cohortOutputPort.checkIfCohortExistWithName(cohort.getName());
        }
        if (ObjectUtils.isNotEmpty(foundCohortByName)) {
            if (!StringUtils.equals(foundCohort.getId(), foundCohortByName.getId())) {
                throw new EducationException(CohortMessages.COHORT_WITH_NAME_EXIST.getMessage());
            }
        }
    }

    private List<LoanBreakdown> saveLoanBreakdown(List<LoanBreakdown> breakdowns, Cohort savedCohort) {
        List<LoanBreakdown> loanBreakdowns = breakdowns.stream()
                .peek(loanBreakdownObject -> loanBreakdownObject.setCohort(savedCohort))
                .toList();
        return loanBreakdownOutputPort.saveAllLoanBreakDown(loanBreakdowns);
    }

    private static void activateStatus(Cohort cohort) {
        LocalDate now = LocalDate.now();
        if (cohort.getStartDate().isAfter(now)) {
            cohort.setActivationStatus(ActivationStatus.INACTIVE);
            cohort.setCohortStatus(CohortStatus.INCOMING);
        } else if (cohort.getStartDate().isBefore(now) && cohort.getExpectedEndDate().isAfter(now)
                || cohort.getStartDate().equals(now) && cohort.getExpectedEndDate().isAfter(now)) {
            cohort.setActivationStatus(ActivationStatus.ACTIVE);
            cohort.setCohortStatus(CohortStatus.CURRENT);
        } else if (cohort.getExpectedEndDate().isBefore(now) || cohort.getExpectedEndDate().isEqual(now)) {
            cohort.setActivationStatus(ActivationStatus.INACTIVE);
            cohort.setCohortStatus(CohortStatus.GRADUATED);
        }
    }

    private BigDecimal calculateTotalLoanBreakdownAmount(List<LoanBreakdown> loanBreakdowns, BigDecimal tutionFee) {
        return loanBreakdowns.stream()
                .map(LoanBreakdown::getItemAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add).add(tutionFee);
    }

    @Override
    public Cohort viewCohortDetails(String userId,  String cohortId) throws MeedlException {
        MeedlValidator.validateUUID(userId, UserMessages.INVALID_USER_ID.getMessage());
        MeedlValidator.validateUUID(cohortId, CohortMessages.INVALID_COHORT_ID.getMessage());
        Cohort cohort = cohortOutputPort.viewCohortDetails(userId, cohortId);
        Program program = programOutputPort.findProgramById(cohort.getProgramId());
        cohort.setProgramName(program.getName());
        CohortLoanDetail cohortLoanDetail = cohortLoanDetailOutputPort.findByCohortId(cohort.getId());
        log.info("cohort loan details == {}", cohortLoanDetail);
        log.info("cohort before mapping == {}", cohort);
        cohortMapper.mapCohortLoanDetailToCohort(cohort,cohortLoanDetail);
        log.info("mapped cohort == {}", cohort);
        int pendingLoanOffers = loanOfferOutputPort.countNumberOfPendingLoanOfferForCohort(cohort.getId());
        log.info("pendingLoanOffers == {}", pendingLoanOffers);
        cohort.setNumberOfPendingLoanOffers(pendingLoanOffers);
        cohort.setExpectedEndDate(cohort.getStartDate().plusMonths(program.getDuration()));
        return cohort;
    }


    @Override
    public Page<Cohort> viewAllCohortInAProgram(Cohort cohort) throws MeedlException {
        MeedlValidator.validateObjectInstance(cohort,CohortMessages.COHORT_CANNOT_BE_EMPTY.name());
        MeedlValidator.validateUUID(cohort.getProgramId(), ProgramMessages.INVALID_PROGRAM_ID.getMessage());
        MeedlValidator.validatePageNumber(cohort.getPageNumber());
        MeedlValidator.validatePageSize(cohort.getPageSize());
        Program foundProgram = programOutputPort.findProgramById(cohort.getProgramId());
        if (ObjectUtils.isEmpty(foundProgram)) {
            log.error("While trying to view all cohort in a program, the program {} was not found.", cohort.getProgramId());
            throw new ResourceNotFoundException(PROGRAM_NOT_FOUND.getMessage());
        }
        Page<Cohort> cohorts = cohortOutputPort.findAllCohortInAProgram(cohort);
        cohorts.getContent().stream().map(eachCohort-> {
            try {
                CohortLoanDetail cohortLoanDetail = cohortLoanDetailOutputPort.findByCohortId(eachCohort.getId());
                if (cohortLoanDetail != null) {
                    eachCohort.setTotalAmountRequested(cohortLoanDetail.getTotalAmountRequested());
                    eachCohort.setTotalOutstandingAmount(cohortLoanDetail.getTotalOutstandingAmount());
                    eachCohort.setTotalAmountReceived(cohortLoanDetail.getTotalAmountReceived());
                    eachCohort.setTotalAmountRepaid(cohortLoanDetail.getTotalAmountRepaid());
                }
            } catch (MeedlException e) {
                log.error("Error fetching loan details for cohort {}", eachCohort.getId(), e);
                throw new RuntimeException(e);
            }
            log.info("--------> Each cohort ----> {}", eachCohort);
            return eachCohort;
        });
        return cohorts;
    }

    @Override
    public void deleteCohort(String id) throws MeedlException {
        MeedlValidator.validateUUID(id, CohortMessages.INVALID_COHORT_ID.getMessage());
        List<Loanee> loanees = loaneeOutputPort.findAllLoaneesByCohortId(id);
        if (ObjectUtils.isNotEmpty(loanees)) {
            throw new EducationException(CohortMessages.COHORT_WITH_LOANEE_CANNOT_BE_DELETED.getMessage());
        }
        Cohort cohort = cohortOutputPort.findCohort(id);
        cohortOutputPort.deleteCohort(cohort.getId());
        Program program = decreaseNumberOfCohortInProgram(cohort);
        decreaseNumberOfCohortInOrganization(program);
    }



    private void decreaseNumberOfCohortInOrganization(Program program) throws MeedlException {
        OrganizationIdentity organizationIdentity = organizationIdentityOutputPort.findById(program.getOrganizationId());
        organizationIdentity.setNumberOfCohort(organizationIdentity.getNumberOfCohort() - 1);
        organizationIdentityOutputPort.save(organizationIdentity);
    }

    private Program decreaseNumberOfCohortInProgram(Cohort cohort) throws MeedlException {
        Program program = programOutputPort.findProgramById(cohort.getProgramId());
        program.setNumberOfCohort(program.getNumberOfCohort() - 1);
        programOutputPort.saveProgram(program);
        return program;
    }


    @Override
    public Page<Cohort> searchForCohort(String userId, Cohort cohort) throws MeedlException {
        MeedlValidator.validateUUID(userId, UserMessages.INVALID_USER_ID.getMessage());
        UserIdentity userIdentity = userIdentityOutputPort.findById(userId);
        if (userIdentity.getRole().equals(IdentityRole.ORGANIZATION_ADMIN)){
            if (ObjectUtils.isEmpty(cohort.getProgramId())) {
                OrganizationIdentity organizationIdentity = programOutputPort.findCreatorOrganization(userId);
                return cohortOutputPort.searchCohortInOrganization(organizationIdentity.getId(),cohort.getName(),
                        cohort.getPageSize(),cohort.getPageNumber());
            }else {
                return cohortOutputPort.searchForCohortInAProgram(cohort.getName(),cohort.getProgramId(),
                        cohort.getPageSize(),cohort.getPageNumber());
            }
        }
        MeedlValidator.validateUUID(cohort.getOrganizationId(), OrganizationMessages.ORGANIZATION_ID_IS_REQUIRED.getMessage());
        return cohortOutputPort.findCohortByNameAndOrganizationId(cohort);
    }

    @Override
    public Page<Cohort> viewAllCohortInOrganization(String actorId, Cohort cohort) throws MeedlException {
        UserIdentity userIdentity = userIdentityOutputPort.findById(actorId);
        MeedlValidator.validateObjectInstance(cohort.getCohortStatus(), CohortMessages.COHORT_STATUS_CANNOT_BE_EMPTY.getMessage());
        if(userIdentity.getRole().equals(IdentityRole.PORTFOLIO_MANAGER)){
            MeedlValidator.validateUUID(cohort.getOrganizationId(), OrganizationMessages.INVALID_ORGANIZATION_ID.getMessage());
            OrganizationIdentity organizationIdentity = organizationIdentityOutputPort.findById(cohort.getOrganizationId());
            return cohortOutputPort.findAllCohortByOrganizationId(organizationIdentity.getId(),cohort);
        }
        OrganizationIdentity organizationIdentity = programOutputPort.findCreatorOrganization(actorId);
        return cohortOutputPort.findAllCohortByOrganizationId(organizationIdentity.getId(),cohort);
    }

    @Override
    public List<LoanBreakdown> getCohortLoanBreakDown(String cohortId) throws MeedlException {
        MeedlValidator.validateUUID(cohortId, CohortMessages.INVALID_COHORT_ID.getMessage());
         return loanBreakdownOutputPort.findAllByCohortId(cohortId);
    }

    @Override
    public String inviteCohort(String userId, String cohortId, List<String> loaneeIds) throws MeedlException {

        UserIdentity userIdentity = userIdentityOutputPort.findById(userId);

        Cohort foundCohort = cohortOutputPort.findCohort(cohortId);
        List<CohortLoanee> cohortLoanees = cohortLoaneeOutputPort.findSelectedLoaneesInCohort(foundCohort.getId(), loaneeIds);
        if (cohortLoanees == null || cohortLoanees.isEmpty()){
            log.info("Loanee(s) selected is/are not referable.");
            throw new EducationException("Loanee(s) selected is/are not referable.");
        }
        if (cohortLoanees.size() == 1){
           inviteTrainee(cohortLoanees.get(0));
            asynchronousMailingOutputPort.notifyLoanReferralActors(List.of(cohortLoanees.get(0).getLoanee()),
                    userIdentity);
//            loaneeUseCase.notifyLoanReferralActors(cohortLoanees);
            return LOANEE_HAS_BEEN_REFERED;
        }
        referCohort(cohortLoanees);
        log.info("Number of referable loanees :{} ", cohortLoanees.size());
        List<Loanee> loanees = cohortLoanees.stream().map(CohortLoanee::getLoanee).toList();
        asynchronousMailingOutputPort.notifyLoanReferralActors(loanees,userIdentity);
//        loaneeUseCase.notifyLoanReferralActors(cohortLoanees);
        return COHORT_INVITED;
    }

    public void referCohort(List<CohortLoanee> cohortLoanees) {
        Iterator<CohortLoanee> iterator = cohortLoanees.iterator();
        while (iterator.hasNext()) {
            CohortLoanee cohortLoanee = iterator.next();
            try {
                inviteTrainee(cohortLoanee);
            } catch (MeedlException e) {
                log.error("Failed to invite trainee with id: {}", cohortLoanee.getId(), e);
                iterator.remove();
            }
        }
    }

    private void inviteTrainee (CohortLoanee cohortLoanee) throws MeedlException {
        log.info("Single loanee is being referred...");
         loaneeUseCase.referLoanee(cohortLoanee);
    }

}