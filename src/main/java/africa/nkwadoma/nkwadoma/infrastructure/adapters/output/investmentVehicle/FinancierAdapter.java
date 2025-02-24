package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.investmentVehicle;

import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.investmentVehicle.FinancierOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.investmentVehicle.InvestmentVehicleOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.InvestmentVehicle;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.InvestmentVehicleFinancier;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class FinancierAdapter implements FinancierOutputPort {
    private final UserIdentityOutputPort userIdentityOutputPort;
    private final InvestmentVehicleOutputPort investmentVehicleOutputPort;


    @Override
    public String inviteFinancier(InvestmentVehicleFinancier investmentVehicleFinancier) throws MeedlException {
        MeedlValidator.validateObjectInstance(investmentVehicleFinancier, "Financier can not be empty.");
        investmentVehicleFinancier.validateIndividuals();
        InvestmentVehicle investmentVehicle = investmentVehicleOutputPort.findById(investmentVehicleFinancier.getInvestmentVehicleId());
        addFinancierToVehicle(investmentVehicleFinancier, investmentVehicle);
        return "Financier invited.";
    }

    private void addFinancierToVehicle(InvestmentVehicleFinancier investmentVehicleFinancier, InvestmentVehicle investmentVehicle) {
        investmentVehicleFinancier.getIndividuals().stream().forEach(investor ->{
            try {
                UserIdentity foundInvestor = userIdentityOutputPort.findByEmail(investor.getEmail());
                log.info("User {} exists on platform and can be added to investment vehicle.",investor.getEmail());
                createInvestorInvestmentVehicle(investmentVehicle.getId(), foundInvestor.getId());
            } catch (MeedlException e) {
                UserIdentity userIdentity = saveFinancierToDB(investmentVehicleFinancier, investor);
                throw new RuntimeException(e);
            }
        });
    }

    private void createInvestorInvestmentVehicle(String investmentVehicleId, String investorId) {

    }

    private UserIdentity saveFinancierToDB(InvestmentVehicleFinancier investmentVehicleFinancier, UserIdentity investor) throws MeedlException {
        log.info("User {} does not exist on platform and cannot be added to investment vehicle.", investor.getEmail());
        investor.setRole(IdentityRole.FINANCIER);
        investor.setCreatedBy(investmentVehicleFinancier.getCreatedBy());
        return userIdentityOutputPort.save(investor);
    }
}
