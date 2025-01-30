package africa.nkwadoma.nkwadoma.application.ports.output.education;

import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.education.Program;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education.ProgramEntity;
import org.springframework.data.domain.*;

import java.util.*;

public interface ProgramOutputPort {
    List<Program> findProgramByName(String programName, String organizationId) throws MeedlException;
    List<Program> findProgramByName(String programName) throws MeedlException;
    Program saveProgram(Program program) throws MeedlException;
    boolean programExistsInOrganization(Program program) throws MeedlException;
    void deleteProgram(String programId) throws MeedlException;
    Program findProgramById(String programId) throws MeedlException;
    Page<Program> findAllPrograms(String meedlUserId, int pageSize, int pageNumber) throws MeedlException;
    OrganizationIdentity findCreatorOrganization(String meedlUserId) throws MeedlException;
    List<Program> findAllProgramsByOrganizationId(String organizationId);
}
