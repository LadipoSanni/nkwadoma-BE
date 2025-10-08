package africa.nkwadoma.nkwadoma.domain.model.loan;

import africa.nkwadoma.nkwadoma.domain.enums.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import lombok.*;
import lombok.extern.slf4j.*;

import java.math.BigDecimal;
import java.util.Set;

@Getter
@Setter
@Slf4j
public class Vendor {
    private String id;
//    private Product product;
    private String vendorName;
    private Set<String> providerServices;
    private String createdAt;
    private String termsAndConditions;
    private BigDecimal costOfService;
    private int duration;

    private int pageSize;
    private int pageNumber;

    public void validateId() throws MeedlException {
        MeedlValidator.validateUUID(id, "Vendor id is required");
    }

    public void validateProviderServices() throws MeedlException {
        if (MeedlValidator.isEmptyCollection(this.providerServices)){
            log.error("No provider services was listed {}", this.providerServices);
            throw new MeedlException(
                    String.format("Provider with name %s, has no provider services was selected", vendorName)
            );
        }
    }
}