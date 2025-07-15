package africa.nkwadoma.nkwadoma.application.ports.output.education;

import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.education.Program;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;
import org.springframework.data.domain.*;

import java.util.*;

public interface ProgramOutputPort {
    Page<Program> findProgramByNameWithinOrganization(Program program, String organizationId) throws MeedlException;
    Page<Program> findProgramByName(String programName,int pageNumber, int pageSize) throws MeedlException;
    Program saveProgram(Program program) throws MeedlException;
    boolean programExistsInOrganization(Program program) throws MeedlException;
    void deleteProgram(String programId) throws MeedlException;
    Program findProgramById(String programId) throws MeedlException;
    Page<Program> findAllPrograms(String meedlUserId, int pageSize, int pageNumber) throws MeedlException;
    OrganizationIdentity findCreatorOrganization(String meedlUserId) throws MeedlException;
    List<Program> findAllProgramsByOrganizationId(String organizationId);

    Page<Program> findAllProgramByOrganizationId(String organizationId, int pageSize, int pageNumber) throws MeedlException;

}
