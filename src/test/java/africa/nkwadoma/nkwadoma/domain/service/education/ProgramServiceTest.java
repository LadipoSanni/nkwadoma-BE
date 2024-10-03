package africa.nkwadoma.nkwadoma.domain.service.education;

import africa.nkwadoma.nkwadoma.application.ports.output.education.ProgramOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.MiddlException;
import africa.nkwadoma.nkwadoma.domain.exceptions.ProgramException;
import africa.nkwadoma.nkwadoma.domain.model.education.Program;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education.ProgramEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.ProgramRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigInteger;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProgramServiceTest {
    @InjectMocks
    private ProgramService programService;
    @Mock
    private ProgramOutputPort programOutputPort;
    private Program program;

    @BeforeEach
    void setUp() {
        program = Program.builder().name("My program").durationStatus(DurationStatus.YEARS).
                programDescription("A great program").organizationId("68t46").
                programType(ProgramType.VOCATIONAL).programStatus(ProgramStatus.ACTIVE).
                deliveryType(DeliveryType.ONSITE).mode(ProgramMode.FULL_TIME).duration(BigInteger.ONE.intValue()).
                build();
    }

    @Test
    void addProgramWithEmptyProgramName() {
        program.setName(null);
        assertThrows(ProgramException.class, ()->programService.addProgram(program));
    }
    @Test
    void addProgramWithEmptyProgramDescription() {
        program.setProgramDescription(null);
        try {
            when(programService.addProgram(program)).thenThrow(ProgramException.class);
        } catch (ProgramException e) {
            e.printStackTrace();
        }
        assertThrows(ProgramException.class, ()->programService.addProgram(program));
    }
    @Test
    void addProgramWithEmptyDurationStatus() {
        program.setDurationStatus(null);
        assertThrows(ProgramException.class, ()->programService.addProgram(program));
    }
    @Test
    void addProgramWithEmptyOrganizationId() {
        program.setOrganizationId(null);
        assertThrows(ProgramException.class, ()->programService.addProgram(program));
    }
    @Test
    void addProgramWithExistingName() {
        program.setName(program.getName());
        try {
            when(programService.addProgram(program)).thenThrow(ProgramException.class);
        } catch (ProgramException e) {
            e.printStackTrace();
        }
        assertThrows(ProgramException.class, ()->programService.addProgram(program));
    }
    @Test
    void addProgram() {
        try {
            when(programOutputPort.saveProgram(program)).thenReturn(program);
            Program addedProgram = programService.addProgram(program);

            assertEquals(addedProgram.getProgramDescription(), program.getProgramDescription());
            assertEquals(addedProgram.getDurationStatus(), program.getDurationStatus());
            assertEquals(addedProgram.getName(), program.getName());

            assertEquals(addedProgram.getProgramStatus(), program.getProgramStatus());
            assertEquals(addedProgram.getDuration(), program.getDuration());
            assertEquals(addedProgram.getProgramType(), program.getProgramType());
            assertEquals(addedProgram.getMode(), program.getMode());
        } catch (MiddlException e) {
            e.printStackTrace();
        }
    }
}