package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.investmentVehicle;

import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityManagerOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.investmentVehicle.FinancierOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.investmentVehicle.InvestmentVehicleOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.InvestmentVehicle;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.Financier;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Component
public class FinancierAdapter implements FinancierOutputPort {
    private final UserIdentityOutputPort userIdentityOutputPort;
    private final InvestmentVehicleOutputPort investmentVehicleOutputPort;
    private final IdentityManagerOutputPort identityManagerOutputPort;


    @Override
    public String inviteFinancier(Financier financier) throws MeedlException {
        MeedlValidator.validateObjectInstance(financier, "Financier can not be empty.");
        financier.validateIndividuals();
        InvestmentVehicle investmentVehicle = investmentVehicleOutputPort.findById(financier.getInvestmentVehicleId());
        addFinancierToVehicle(financier, investmentVehicle);
        return "Financier invited.";
    }

    private void addFinancierToVehicle(Financier investmentVehicleFinancier, InvestmentVehicle investmentVehicle) {
        investmentVehicleFinancier.getIndividuals().stream().forEach(investor ->{
            try {
                UserIdentity foundInvestor = userIdentityOutputPort.findByEmail(investor.getEmail());
                log.info("User {} exists on platform and can be added to investment vehicle.",investor.getEmail());
                createInvestmentVehicleFinancier(investmentVehicle, foundInvestor);
            } catch (MeedlException e) {
                try {
                    UserIdentity userIdentity = saveFinancier(investmentVehicleFinancier, investor);
                } catch (MeedlException ex) {
                    throw new RuntimeException(ex);
                }
                throw new RuntimeException(e);
            }
        });
    }

    private void createInvestmentVehicleFinancier(InvestmentVehicle investmentVehicle, UserIdentity investor) {

    }

    private UserIdentity saveFinancier(Financier investmentVehicleFinancier, UserIdentity investor) throws MeedlException {
        log.info("User {} does not exist on platform and cannot be added to investment vehicle.", investor.getEmail());
        investor.setRole(IdentityRole.FINANCIER);
        investor.setCreatedBy(investmentVehicleFinancier.getCreatedBy());
        UserIdentity userIdentity = identityManagerOutputPort.createUser(investor);
        userIdentity.setCreatedAt(LocalDateTime.now());
        return userIdentityOutputPort.save(userIdentity);
    }
}
