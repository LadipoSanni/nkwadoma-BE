package africa.nkwadoma.nkwadoma.application.ports.output.meedlportfolio;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.meedlPortfolio.PlatformRequest;
import org.springframework.data.domain.Page;

import java.util.List;

public interface PlatformRequestOutputPort {
    PlatformRequest save(PlatformRequest platformRequest) throws MeedlException;

    Page<PlatformRequest> viewAll(PlatformRequest platformRequest) throws MeedlException;

    PlatformRequest viewDetail(PlatformRequest platformRequest) throws MeedlException;

    void deleteById(PlatformRequest platformRequest) throws MeedlException;
}
