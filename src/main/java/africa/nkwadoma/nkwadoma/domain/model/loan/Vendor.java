package africa.nkwadoma.nkwadoma.domain.model.loan;

import africa.nkwadoma.nkwadoma.domain.enums.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import lombok.*;
import lombok.extern.slf4j.*;

import java.math.BigDecimal;

@Getter
@Setter
@Slf4j
public class Vendor {
    private String id;
    private Product product;
    private String vendorName;
    private String termsAndConditions;
    private BigDecimal costOfService;
    private int duration;

    public void validateId() throws MeedlException {
        MeedlValidator.validateUUID(id, "Vendor id is required");
    }
}