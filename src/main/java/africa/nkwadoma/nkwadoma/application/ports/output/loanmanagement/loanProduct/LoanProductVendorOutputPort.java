package africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.loanProduct;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanProduct;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanProductVendor;
import africa.nkwadoma.nkwadoma.domain.model.loan.Vendor;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity.LoanProductVendorEntity;

import java.util.List;

public interface LoanProductVendorOutputPort {
    List<LoanProductVendor> save(List<Vendor> vendors, LoanProduct loanProduct) throws MeedlException;

    void deleteById(String loanProductVendorId) throws MeedlException;

//    LoanProductVendor findByLoanProductVendorId(String loanProductVendorId) throws MeedlException;

    List<Vendor> getVendorsByLoanProductId(String loanProductId) throws MeedlException;

}
