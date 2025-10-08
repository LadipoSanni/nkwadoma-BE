package africa.nkwadoma.nkwadoma.domain.service.loanmanagement.loanProduct;

import africa.nkwadoma.nkwadoma.application.ports.input.loanmanagement.loanProduct.VendorUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.loanProduct.VendorOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.Vendor;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class VendorService implements VendorUseCase {

    private final VendorOutputPort vendorOutputPort;
    @Override
    public Page<Vendor> viewAllVendors(Vendor vendor) throws MeedlException {
        MeedlValidator.validateObjectInstance(vendor, "Vendor cannot be empty");
        MeedlValidator.validatePageSize(vendor.getPageSize());
        MeedlValidator.validatePageNumber(vendor.getPageNumber());
        return vendorOutputPort.viewAllVendor(vendor);
    }

    @Override
    public Page<String> viewAllProviderService(Vendor vendor){
        return vendorOutputPort.viewAllProviderService(vendor);
    }
}
