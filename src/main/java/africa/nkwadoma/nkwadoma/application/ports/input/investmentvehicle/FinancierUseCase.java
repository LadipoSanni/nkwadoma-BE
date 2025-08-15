package africa.nkwadoma.nkwadoma.application.ports.input.investmentvehicle;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.financier.Financier;
import africa.nkwadoma.nkwadoma.domain.model.financier.FinancierVehicleDetail;
import africa.nkwadoma.nkwadoma.domain.model.investmentvehicle.InvestmentSummary;
import org.springframework.data.domain.Page;

import java.util.List;

public interface FinancierUseCase {
    String inviteFinancier(List<Financier> financier, String investmentVehicleId) throws MeedlException;

    Financier viewFinancierDetail(String userId, String financierId) throws MeedlException;

    Page<Financier> viewAllFinancier(Financier financier) throws MeedlException;

    Page<Financier> viewAllFinancierInInvestmentVehicle(Financier financier) throws MeedlException;

    Page<Financier> search(String name, Financier financier) throws MeedlException;

    void updateFinancierStatus(Financier financier);

    Financier investInVehicle(Financier financier) throws MeedlException;

    Financier completeKyc(Financier financier) throws MeedlException;

    FinancierVehicleDetail viewInvestmentDetailOfFinancier(String financierId, String userId) throws MeedlException;

    Page<Financier> viewAllFinancierInvestment(String sub, String financierId, int pageSize, int pageNumber) throws MeedlException;

    Page<Financier> searchFinancierInvestment(Financier financier) throws MeedlException;
    InvestmentSummary viewInvestmentDetailOfFinancier(String financierId, String investmentVehicleFinancierId, String userId) throws MeedlException;

    String inviteColleagueFinancier(String actorID,Financier financier) throws MeedlException;
}
