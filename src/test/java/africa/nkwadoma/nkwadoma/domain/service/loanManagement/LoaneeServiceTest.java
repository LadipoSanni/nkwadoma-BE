package africa.nkwadoma.nkwadoma.domain.service.loanManagement;

import africa.nkwadoma.nkwadoma.application.ports.input.identity.CreateOrganizationUseCase;
import africa.nkwadoma.nkwadoma.application.ports.input.loan.LoaneeUsecase;
import africa.nkwadoma.nkwadoma.application.ports.output.education.CohortOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.education.LoaneeOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.education.ProgramCohortOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.education.ProgramOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityManagerOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationEmployeeIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.exceptions.education.ProgramCohortException;
import africa.nkwadoma.nkwadoma.domain.model.education.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationEmployeeIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoaneeLoanDetail;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class LoaneeServiceTest {

    @InjectMocks
    private LoaneeService loaneeService;
    @Mock
    private LoaneeOutputPort loaneeOutputPort;
    @Mock
    private ProgramOutputPort programOutputPort;
    @Mock
    private CohortOutputPort cohortOutputPort;
    @Mock
    private UserIdentityOutputPort userIdentityOutputPort;
    @Mock
    private IdentityManagerOutputPort identityManagerOutputPort;
    @Mock
    private OrganizationIdentityOutputPort organizationIdentityOutputPort;
    @Mock
    private OrganizationEmployeeIdentityOutputPort organizationEmployeeIdentityOutputPort;
    @Mock
    private ProgramCohortOutputPort programCohortOutputPort;

    private ProgramCohort programCohort;
    private Cohort elites;

    private Loanee firstLoanee;
    private String mockId = "5bc2ef97-1035-4e42-bc8b-22a90b809f7c";


    @BeforeEach
    void setUpLoanee(){
            UserIdentity loaneeUserIdentity = UserIdentity.builder()
                    .email("qudus55@gmail.com")
                    .firstName("qudus")
                    .lastName("lekan")
                    .role(IdentityRole.LOANEE)
                    .build();

            firstLoanee = new Loanee();
            firstLoanee.setLoanee(loaneeUserIdentity);
            firstLoanee.setCreatedBy(mockId);
            firstLoanee.setOrganizationId(mockId);
            firstLoanee.setProgramId(mockId);
            firstLoanee.setCohortId(mockId);

            LoaneeLoanDetail loaneeLoanDetail = new LoaneeLoanDetail();
            loaneeLoanDetail.setInitialDeposit(BigDecimal.valueOf(100));

            firstLoanee.setLoaneeLoanDetail(loaneeLoanDetail);



        LoanBreakdown loanBreakdown1 = new LoanBreakdown();
        loanBreakdown1.setCurrency("usd");
        loanBreakdown1.setItemAmount(BigDecimal.valueOf(30000));
        loanBreakdown1.setItemName("juno");


        LoaneeLoanDetail loaneeLoanDetails = new LoaneeLoanDetail();
        loaneeLoanDetails.setAmountRequested(BigDecimal.valueOf(3000));
        loaneeLoanDetails.setInitialDeposit(BigDecimal.valueOf(100));
        loaneeLoanDetail.setLoanBreakdown(List.of(loanBreakdown1));

        firstLoanee.setLoaneeLoanDetail(loaneeLoanDetail);

        elites = new Cohort();
        elites.setId(mockId);
        elites.setStartDate(LocalDateTime.of(2024,10,18,9,43));
        elites.setExpectedEndDate(LocalDateTime.of(2024,11,18,9,43));
        elites.setProgramId(mockId);
        elites.setName("Elite");
        elites.setCreatedBy(mockId);
        elites.setLoanBreakdowns(List.of(loanBreakdown1));
        elites.setTuitionAmount(BigDecimal.valueOf(4000000));

        programCohort = new ProgramCohort();
        programCohort.setCohort(elites);
        programCohort.setProgramId(mockId);
        programCohort.setId(mockId);


    }



    @Test
    void addLoaneeToCohort() throws MeedlException {
        OrganizationIdentity mockOrganizationIdentity = new OrganizationIdentity();
        mockOrganizationIdentity.setId(mockId);
        mockOrganizationIdentity.setName("org");
        mockOrganizationIdentity.setEmail("qudusa55@gmail.com");
        OrganizationEmployeeIdentity mockEmployeeIdentity = new OrganizationEmployeeIdentity();
        mockEmployeeIdentity.setId(mockId);
        mockEmployeeIdentity.setOrganization(mockOrganizationIdentity.getId());
        mockOrganizationIdentity.setOrganizationEmployees(List.of(mockEmployeeIdentity));
        lenient().when(organizationIdentityOutputPort.findById(firstLoanee.getOrganizationId()))
                .thenReturn(mockOrganizationIdentity);
        lenient().when(organizationEmployeeIdentityOutputPort.findByEmployeeId(firstLoanee.getCreatedBy()))
                .thenReturn(mockEmployeeIdentity);
        lenient().when(programCohortOutputPort.findAllByProgramId(firstLoanee.getProgramId()))
                .thenReturn(List.of(programCohort));
        when(loaneeOutputPort.save(firstLoanee)).thenReturn(firstLoanee);
        when(cohortOutputPort.saveCohort(any(Cohort.class))).thenReturn(elites);
        try {
            Loanee loanee = loaneeService.addLoaneeToCohort(firstLoanee);
            assertEquals(firstLoanee.getLoanee().getFirstName(), loanee.getLoanee().getFirstName());
            verify(loaneeOutputPort, times(1)).save(firstLoanee);
            verify(cohortOutputPort, times(1)).saveCohort(any(Cohort.class));
        } catch (MeedlException exception) {
            log.error("{} {}", exception.getClass().getName(), exception.getMessage());
        }
    }



}







