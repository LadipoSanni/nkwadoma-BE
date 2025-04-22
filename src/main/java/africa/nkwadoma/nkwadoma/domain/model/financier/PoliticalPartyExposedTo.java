package africa.nkwadoma.nkwadoma.domain.model.financier;

import africa.nkwadoma.nkwadoma.domain.enums.identity.Country;
import africa.nkwadoma.nkwadoma.domain.enums.identity.UserRelationship;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PoliticalPartyExposedTo {
    private String positionHeld;
    private Country country;
    private UserRelationship relationship;
    private String AdditionalInformation;

    public void validate() throws MeedlException {
        MeedlValidator.validateDataElement(this.positionHeld, "Position held is required.");
        MeedlValidator.validateObjectInstance(this.country, "State the country you held a position.");
    }
}
