package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.education;

import africa.nkwadoma.nkwadoma.application.ports.output.education.CohortLoaneeOutputPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@RequiredArgsConstructor
public class CohortLoaneePersistenceAdapter implements CohortLoaneeOutputPort {


    @Override
    public CohortLoanee save(Object o) {
        return null;
    }
}
