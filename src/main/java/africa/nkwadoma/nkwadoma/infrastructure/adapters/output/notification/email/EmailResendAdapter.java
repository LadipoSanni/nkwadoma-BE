package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.email;

import africa.nkwadoma.nkwadoma.application.ports.input.notification.EmailResendUseCase;
import africa.nkwadoma.nkwadoma.application.ports.input.loanmanagement.ViewLoanReferralsUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.LoanReferralOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanReferral;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailResendAdapter implements EmailResendUseCase {
    private final LoanReferralOutputPort loanReferralOutputPort;
    private final ViewLoanReferralsUseCase viewLoanReferralsUseCase;
//    private final SendLoaneeEmailUsecase sendLoaneeEmailUsecase;

//    @Scheduled(fixedRate = 1800000)
//    @PostConstruct
    public void scheduledReferralEmailResend(){
        List<LoanReferral> allLoanReferral = loanReferralOutputPort.viewAll();

        resendReferralEmail(allLoanReferral);
    }
    @Override
    public void resendReferralEmail(String loaneeEmail) throws MeedlException {
        LoanReferral loanReferral = loanReferralOutputPort.findByEmail(loaneeEmail);
        log.info("Loan referral found at the resend adapter");
        List<LoanReferral> loanReferrals = List.of(loanReferral);
        log.info("About to initiate manual resend...");
        resendReferralEmail(loanReferrals);
    }
    public void resendReferralEmail(List<LoanReferral> allLoanReferral){
        log.info("Started chron job");
        log.info("All found loan referral for chron job {}", allLoanReferral);
        for (LoanReferral loanReferral : allLoanReferral){
            try {
                LoanReferral foundLoanReferral = loanReferralOutputPort.findById(loanReferral.getId());
                log.info("Loan referral viewed {}", foundLoanReferral);
                refer(loanReferral.getCohortLoanee().getLoanee(), foundLoanReferral.getId());
                log.info("Loanee has been referred. {}", loanReferral.getCohortLoanee().getLoanee().getUserIdentity().getEmail());
            }catch (MeedlException e){
                log.error("A terrible error occurred while trying to resend referral email.",e);
            }
        }
        log.info("Loan referral email resend finished.");
    }
    private void refer(Loanee loanee, String loanReferralId) throws MeedlException {
//        sendLoaneeEmailUsecase.referLoaneeEmail(loanee,loanReferralId);
    }
}
