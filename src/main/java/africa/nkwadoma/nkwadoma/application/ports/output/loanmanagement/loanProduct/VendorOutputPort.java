package africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.loanProduct;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.Vendor;
import org.springframework.data.domain.Page;

import java.util.List;

public interface VendorOutputPort {

    List<Vendor> saveVendors(List<Vendor> vendors) throws MeedlException;

    Page<Vendor> viewAllVendor(Vendor vendor) throws MeedlException;

    void deleteById(String vendorId) throws MeedlException;

    void deleteMultipleById(List<String> vendorIds) throws MeedlException;

    Page<String> viewAllProviderService(Vendor vendor);
}
