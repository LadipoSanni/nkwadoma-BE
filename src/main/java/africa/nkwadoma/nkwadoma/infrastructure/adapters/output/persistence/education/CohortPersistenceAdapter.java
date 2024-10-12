package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.education;

import africa.nkwadoma.nkwadoma.application.ports.output.education.CohortOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.constants.MeedlMessages;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.exceptions.education.EducationException;
import africa.nkwadoma.nkwadoma.domain.model.education.Cohort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Slf4j
public class CohortPersistenceAdapter implements CohortOutputPort {
    @Override
    public Cohort saveCohort(Cohort cohort) throws MeedlException {
        if (ObjectUtils.isEmpty(cohort)) {
            throw new EducationException(MeedlMessages.INVALID_REQUEST.getMessage());
        }
        cohort.validate();
        return null;
    }
}
