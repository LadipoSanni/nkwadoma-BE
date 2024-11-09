//package africa.nkwadoma.nkwadoma.domain.service.loanManagement;
//
//import africa.nkwadoma.nkwadoma.application.ports.input.identity.CreateOrganizationUseCase;
//import africa.nkwadoma.nkwadoma.application.ports.input.loan.LoaneeUsecase;
//import africa.nkwadoma.nkwadoma.application.ports.output.education.CohortOutputPort;
//import africa.nkwadoma.nkwadoma.application.ports.output.education.LoaneeOutputPort;
//import africa.nkwadoma.nkwadoma.application.ports.output.education.ProgramCohortOutputPort;
//import africa.nkwadoma.nkwadoma.application.ports.output.education.ProgramOutputPort;
//import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityManagerOutputPort;
//import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationEmployeeIdentityOutputPort;
//import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationIdentityOutputPort;
//import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
//import africa.nkwadoma.nkwadoma.domain.enums.*;
//import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
//import africa.nkwadoma.nkwadoma.domain.model.education.Cohort;
//import africa.nkwadoma.nkwadoma.domain.model.education.LoanBreakdown;
//import africa.nkwadoma.nkwadoma.domain.model.education.Program;
//import africa.nkwadoma.nkwadoma.domain.model.education.ServiceOffering;
//import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationEmployeeIdentity;
//import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;
//import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
//import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;
//import africa.nkwadoma.nkwadoma.domain.model.loan.LoaneeLoanDetail;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.jupiter.api.*;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.math.BigDecimal;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.Mockito.when;
//
//@Slf4j
//@ExtendWith(MockitoExtension.class)
//public class LoaneeServiceTest {
//
//    @InjectMocks
//    private LoaneeService loaneeService;
//    @Mock
//    private LoaneeOutputPort loaneeOutputPort;
//    @Mock
//    private ProgramOutputPort programOutputPort;
//    @Mock
//    private CohortOutputPort cohortOutputPort;
//    @Mock
//    private UserIdentityOutputPort userIdentityOutputPort;
//    @Mock
//    private IdentityManagerOutputPort identityManagerOutputPort;
//    @Mock
//    private OrganizationIdentityOutputPort organizationIdentityOutputPort;
//    @Mock
//    private OrganizationEmployeeIdentityOutputPort organizationEmployeeIdentityOutputPort;
//    @Mock
//    private ProgramCohortOutputPort programCohortOutputPort;
//
//    private Loanee firstLoanee;
//    private String mockId = "5bc2ef97-1035-4e42-bc8b-22a90b809f7c";
//
//
//    @BeforeEach
//    void setUpLoanee(){
//        UserIdentity loaneeUserIdentity = UserIdentity.builder()
//                .email("qudus55@gmail.com")
//                .firstName("qudus")
//                .lastName("lekan")
//                .role(IdentityRole.LOANEE)
//                .build();
//
//        firstLoanee = new Loanee();
//        firstLoanee.setCreatedBy(mockId);
//        firstLoanee.setUser(loaneeUserIdentity);
//        firstLoanee.setOrganizationId(mockId);
//        firstLoanee.setProgramId(mockId);
//        firstLoanee.setCohortId(mockId);
//
//
//        LoanBreakdown loanBreakdown1 = new LoanBreakdown();
//        loanBreakdown1.setCurrency("usd");
//        loanBreakdown1.setItemAmount(BigDecimal.valueOf(30000));
//        loanBreakdown1.setItemName("juno");
//
//
//        LoaneeLoanDetail loaneeLoanDetail = new LoaneeLoanDetail();
//        loaneeLoanDetail.setAmountRequested(BigDecimal.valueOf(3000));
//        loaneeLoanDetail.setInitialDeposit(BigDecimal.valueOf(100));
//        loaneeLoanDetail.setLoanBreakdown(List.of(loanBreakdown1));
//
//        firstLoanee.setLoaneeLoanDetail(loaneeLoanDetail);
//
//    }
//
////    @Test
////    void addLoaneeToCohort() {
////        Loanee loanee = new Loanee();
////        try{
////            OrganizationIdentity mockOrganizationIdentity = new OrganizationIdentity();
////            mockOrganizationIdentity.setId(mockId);
////            when(organizationIdentityOutputPort.findById(firstLoanee.getOrganizationId())).thenReturn(mockOrganizationIdentity);
////            OrganizationEmployeeIdentity organizationEmployeeIdentity = new OrganizationEmployeeIdentity();
////            organizationEmployeeIdentity.setId(mockId);
////            when(organizationEmployeeIdentityOutputPort.findByEmployeeId(firstLoanee.getCreatedBy())).thenReturn(organizationEmployeeIdentity);
////            when(loaneeOutputPort.save(firstLoanee)).thenReturn(firstLoanee);
////            loanee = loaneeService.addLoaneeToCohort(firstLoanee);
////        } catch (MeedlException exception) {
////            log.error("{} {}", exception.getClass().getName(), exception.getMessage());
////        }
////        assertEquals(loanee.getUser().getFirstName(),firstLoanee.getUser().getFirstName());
////    }
//
//    @Test
//    void addLoaneeToCohort() {
//        Loanee loanee = new Loanee();
//        try {
//            // Mock OrganizationIdentity
//            OrganizationIdentity mockOrganizationIdentity = new OrganizationIdentity();
//            mockOrganizationIdentity.setId(mockId);
//            when(organizationIdentityOutputPort.findById(firstLoanee.getOrganizationId()))
//                    .thenReturn(mockOrganizationIdentity);
//
//            // Mock OrganizationEmployeeIdentity
//            OrganizationEmployeeIdentity mockOrganizationEmployeeIdentity = new OrganizationEmployeeIdentity();
//            mockOrganizationEmployeeIdentity.setId(mockId);
//            mockOrganizationEmployeeIdentity.setOrganization(mockOrganizationIdentity.getId());
//            when(organizationEmployeeIdentityOutputPort.findByEmployeeId(firstLoanee.getCreatedBy()))
//                    .thenReturn(mockOrganizationEmployeeIdentity);
//
//
//            // Set a valid user identity
//            UserIdentity userIdentity = UserIdentity.builder()
//                    .email("qudus55@gmail.com")
//                    .firstName("qudus")
//                    .lastName("lekan")
//                    .role(IdentityRole.LOANEE)
//                    .build();
//            firstLoanee.setUser(userIdentity);
//
//            // Mock save method of loaneeOutputPort
//            when(loaneeOutputPort.save(firstLoanee)).thenAnswer(invocation -> {
//                Loanee loaneeToSave = invocation.getArgument(0);
//                // Ensure the user field is not null
//                if (loaneeToSave.getUser() == null) {
//                    loaneeToSave.setUser(userIdentity);
//                }
//                return loaneeToSave;
//            });
//
//            // Call the service method
//            loanee = loaneeService.addLoaneeToCohort(firstLoanee);
//
//        } catch (MeedlException exception) {
//            log.error("{} {}", exception.getClass().getName(), exception.getMessage());
//        }
//
//        // Validate that the user identity is set correctly
//        assertEquals(firstLoanee.getUser().getFirstName(), loanee.getUser().getFirstName());
//    }
//
//
//
//
//
//}
