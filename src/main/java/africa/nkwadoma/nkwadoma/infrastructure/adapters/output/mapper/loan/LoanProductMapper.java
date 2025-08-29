package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.loan;

import africa.nkwadoma.nkwadoma.domain.model.loan.LoanProduct;
import africa.nkwadoma.nkwadoma.domain.model.loan.Vendor;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity.LoanProductEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity.VendorEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface LoanProductMapper {
    @Mapping(source = "totalAmountAvailable", target = "totalAmountAvailable", defaultValue = "0.0")
    @Mapping(source = "totalAmountRepaid", target = "totalAmountRepaid", defaultValue = "0.0")
    @Mapping(source = "totalAmountEarned", target = "totalAmountEarned", defaultValue = "0.0")
    @Mapping(source = "totalAmountDisbursed", target = "totalAmountDisbursed", defaultValue = "0.0")
    @Mapping(source = "minRepaymentAmount", target = "minRepaymentAmount", defaultValue = "0.0")
    @Mapping(source = "obligorLoanLimit", target = "obligorLoanLimit", defaultValue = "0.0")
    LoanProductEntity mapLoanProductToEntity(LoanProduct loanProduct);

    LoanProduct mapEntityToLoanProduct(LoanProductEntity entity);

    LoanProduct mapLoanProductToLoanProduct(LoanProduct loanProduct);

    VendorEntity mapVendorToVendorEntity(Vendor vendor);

    Vendor mapVendorEntityToVendor(VendorEntity vendorEntity);

    LoanProduct updateLoanProduct(@MappingTarget LoanProduct loanProduct, LoanProduct product);

}
