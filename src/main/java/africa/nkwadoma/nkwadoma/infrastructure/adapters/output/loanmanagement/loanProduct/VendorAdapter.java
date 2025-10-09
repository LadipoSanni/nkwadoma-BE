package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.loanmanagement.loanProduct;

import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.loanProduct.VendorOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.Vendor;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.loanManagement.loanProduct.VendorMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity.VendorEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.VendorEntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public Page<Vendor> viewAllVendor(Vendor vendor) throws MeedlException {
        MeedlValidator.validateObjectInstance(vendor, "Vendor cannot be empty");
        MeedlValidator.validatePageSize(vendor.getPageSize());
        MeedlValidator.validatePageNumber(vendor.getPageNumber());
        Pageable pageRequest = PageRequest.of(vendor.getPageNumber(), vendor.getPageSize(), Sort.by(Sort.Order.desc("createdAt")));

        Page<VendorEntity> vendorEntities;
        if (MeedlValidator.isNotEmptyString(vendor.getVendorName())) {
            log.info("Vendor search initiated at the adapter for {}", vendor.getVendorName());
            vendorEntities = vendorEntityRepository.findAllByVendorName(vendor.getVendorName(), pageRequest);
        } else if (vendor.getVendorName() == null){
            log.info("Viewing all vendors at the adapter");
            vendorEntities = vendorEntityRepository.findAll(pageRequest);
        }else {
            log.info("Vendor name is an empty string or blank {}, size {}", vendor.getVendorName(), vendor.getVendorName().length());
            vendorEntities = Page.empty();
        }
        log.info("Vendor entities found {}", vendorEntities);
        return vendorEntities.map(vendorMapper::map);

    }


    @Override
    public Page<String> viewAllProviderService(Vendor vendor) throws MeedlException {
        MeedlValidator.validatePageSize(vendor.getPageSize());
        MeedlValidator.validatePageNumber(vendor.getPageNumber());
        Pageable pageRequest = PageRequest.of(vendor.getPageNumber(), vendor.getPageSize());
        Page<String> providerServices;
        if (MeedlValidator.isNotEmptyString(vendor.getVendorName())) {
            log.info("Provider service search initiated at the adapter for {}", vendor.getVendorName());
            providerServices = vendorEntityRepository.findAllProviderServiceByName(vendor.getVendorName(), pageRequest);
        } else if (vendor.getVendorName() == null){
            log.info("Viewing all provider service at the adapter");
            providerServices = vendorEntityRepository.findAllProviderService(pageRequest);
        }else {
            log.info("Service provider name is an empty string or blank {}, size {}", vendor.getVendorName(), vendor.getVendorName().length());
            providerServices = Page.empty();
        }
        return providerServices;
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
