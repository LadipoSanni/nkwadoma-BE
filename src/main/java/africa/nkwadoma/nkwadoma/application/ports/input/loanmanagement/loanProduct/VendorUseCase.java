package africa.nkwadoma.nkwadoma.application.ports.input.loanmanagement.loanProduct;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.Vendor;
import org.springframework.data.domain.Page;

public interface VendorUseCase {
    Page<Vendor> viewAllVendors(Vendor vendor) throws MeedlException;

    Page<String> viewAllProviderService(Vendor vendor);
}
