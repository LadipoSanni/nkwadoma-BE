package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.education;

import africa.nkwadoma.nkwadoma.application.ports.output.education.ProgramLoanDetailOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.constants.ProgramMessages;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.education.ProgramLoanDetail;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education.ProgramLoanDetailEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.education.ProgramLoanDetailMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.education.ProgramLoanDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ProgramLoanDetailPersistenceAdapter implements ProgramLoanDetailOutputPort {

    private final ProgramLoanDetailMapper programLoanDetailMapper;
    private final ProgramLoanDetailRepository programLoanDetailRepository;

    @Override
    public ProgramLoanDetail save(ProgramLoanDetail programLoanDetail) throws MeedlException {
        MeedlValidator.validateObjectInstance(programLoanDetail,"Program loan detail cannot be empty");
        programLoanDetail.validate();

        ProgramLoanDetailEntity programLoanDetailEntity =
                programLoanDetailMapper.toProgramLoanEntity(programLoanDetail);

        programLoanDetailEntity = programLoanDetailRepository.save(programLoanDetailEntity);
        return programLoanDetailMapper.toProgramLoanDetail(programLoanDetailEntity);
    }

    @Override
    public ProgramLoanDetail findByProgramId(String id) throws MeedlException {
        MeedlValidator.validateUUID(id, ProgramMessages.INVALID_PROGRAM_ID.getMessage());

        ProgramLoanDetailEntity programLoanDetailEntity =
                programLoanDetailRepository.findByProgramId(id);
        return programLoanDetailMapper.toProgramLoanDetail(programLoanDetailEntity);
    }

    @Override
    public void delete(String  loanDetailsId) throws MeedlException {
        MeedlValidator.validateUUID(loanDetailsId,"Program loan detail id cannot be empty");
        programLoanDetailRepository.deleteById(loanDetailsId);
    }

    @Transactional
    @Override
    public void deleteByProgramId(String id) throws MeedlException {
        MeedlValidator.validateUUID(id, ProgramMessages.INVALID_PROGRAM_ID.getMessage());
        programLoanDetailRepository.deleteProgramLoanDetailEntityByProgramId(id);
    }
}
