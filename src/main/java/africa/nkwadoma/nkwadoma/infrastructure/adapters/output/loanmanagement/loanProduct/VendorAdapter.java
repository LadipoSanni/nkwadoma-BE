package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.loanmanagement.loanProduct;

import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.loanProduct.VendorOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.Vendor;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.loanManagement.loanProduct.VendorMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.VendorEntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Component
public class VendorAdapter implements VendorOutputPort {
    private final VendorMapper vendorMapper;
    private final VendorEntityRepository vendorEntityRepository;
    @Override
    public List<Vendor> saveVendors(List<Vendor> vendors) throws MeedlException {
        MeedlValidator.validateCollection(vendors, "Vendors to save can not be empty");
        return vendors.stream()
                .map(vendorMapper::map)
                .map(vendorEntityRepository::save)
                .map(vendorMapper::map)
                .toList();
    }

    @Override
    public void deleteById(String vendorId) throws MeedlException {
        MeedlValidator.validateUUID(vendorId, "Vendor id is required");
        vendorEntityRepository.deleteById(vendorId);
    }

    @Override
    public void deleteMultipleById(List<String> vendorIds) throws MeedlException {
        MeedlValidator.validateCollection(vendorIds, "Vendors id is required before deleting");
        vendorEntityRepository.deleteAllById(vendorIds);
    }
}
