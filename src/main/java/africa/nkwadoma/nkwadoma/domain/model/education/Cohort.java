package africa.nkwadoma.nkwadoma.domain.model.education;

import africa.nkwadoma.nkwadoma.domain.enums.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.constants.MeedlMessages;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.exceptions.education.EducationException;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import jakarta.persistence.Lob;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.validator.routines.EmailValidator;

import java.time.LocalDateTime;

@Getter
@Setter
@Slf4j
public class Cohort {
    private String id;
//    private String organizationId;
    private String programId;
    private String cohortDescription;
    private String name;
    private ActivationStatus cohortStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
    private String imageUrl;
    private LocalDateTime startDate;
    private LocalDateTime expectedEndDate;

    public void validate() throws MeedlException {
        MeedlValidator.validateDataElement(programId);
        MeedlValidator.validateDataElement(name);
        MeedlValidator.validateDataElement(createdBy);
        if (EmailValidator.getInstance().isValid(createdBy)) {
            throw new EducationException(MeedlMessages.INVALID_CREATED_BY.getMessage());
        }

    }
}
