package africa.nkwadoma.nkwadoma.domain.service.education;

import africa.nkwadoma.nkwadoma.application.ports.output.education.ProgramOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.education.Program;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigInteger;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Slf4j
class ProgramServiceTest {
    @InjectMocks
    private ProgramService programService;
    @Mock
    private ProgramOutputPort programOutputPort;
    private Program program;

    @BeforeEach
    void setUp() {
        program = Program.builder().name("My program").durationType(DurationType.YEARS).
                programDescription("A great program").organizationId("68t46").
                programType(ProgramType.VOCATIONAL).programStatus(ActivationStatus.ACTIVE).
                objectives("Program Objectives").createdBy("875565").
                deliveryType(DeliveryType.ONSITE).mode(ProgramMode.FULL_TIME).duration(BigInteger.ONE.intValue()).
                build();
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    void addProgramWithEmptyProgramName(String programName) {
        program.setName(programName);
        assertThrows(MeedlException.class, ()->programService.createProgram(program));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    void addProgramWithInvalidCreatorId(String createdBy) {
        program.setCreatedBy(createdBy);
        assertThrows(MeedlException.class, ()->programService.createProgram(program));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    void addProgramWithEmptyOrganizationId(String organizationId) {
        program.setOrganizationId(organizationId);
        assertThrows(MeedlException.class, ()->programService.createProgram(program));
    }

    @Test
    void addProgramWithExistingName() {
        try {
            when(programOutputPort.programExists(program.getName())).thenReturn(true);
            assertThrows(ResourceAlreadyExistsException.class, ()-> programService.createProgram(program));
        } catch (MeedlException e) {
            log.error(e.getMessage());
        }
    }

    @Test
    void addProgram() {
        try {
            when(programOutputPort.saveProgram(program)).thenReturn(program);
            Program addedProgram = programService.createProgram(program);

            assertEquals(addedProgram.getProgramDescription(), program.getProgramDescription());
            assertEquals(addedProgram.getDurationType(), program.getDurationType());
            assertEquals(addedProgram.getName(), program.getName());
            assertEquals(addedProgram.getObjectives(), program.getObjectives());
            assertEquals(addedProgram.getProgramStatus(), program.getProgramStatus());
            assertEquals(addedProgram.getDuration(), program.getDuration());
            assertEquals(addedProgram.getProgramType(), program.getProgramType());
            assertEquals(addedProgram.getMode(), program.getMode());
            assertEquals(addedProgram.getCreatedAt(), program.getCreatedAt());
        } catch (MeedlException e) {
            log.info("Error creating program: {}", e.getMessage());
        }
    }

    @Test
    void viewAllPrograms() {
        when(programOutputPort.findAllPrograms(program.getOrganizationId())).thenReturn(List.of(program));
        List<Program> programs = programService.viewAllPrograms(program);

        assertNotNull(programs);
        assertEquals(programs.get(0).getId(), program.getId());
        assertEquals(programs.get(0).getOrganizationId(), program.getOrganizationId());
        assertEquals(programs.get(0).getName(), program.getName());
        assertEquals(programs.get(0).getDuration(), program.getDuration());
        assertEquals(programs.get(0).getNumberOfCohort(), program.getNumberOfCohort());
        assertEquals(programs.get(0).getNumberOfTrainees(), program.getNumberOfTrainees());
        assertEquals(programs.get(0).getTotalAmountDisbursed(), program.getTotalAmountDisbursed());
        assertEquals(programs.get(0).getTotalAmountOutstanding(), program.getTotalAmountOutstanding());
        assertEquals(programs.get(0).getTotalAmountRepaid(), program.getTotalAmountRepaid());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    void viewAllProgramsWithNullOrganizationId(String organizationId) {
//        when(programOutputPort.findAllPrograms(organizationId)).thenThrow(MeedlException.class);
        MeedlException meedlException = assertThrows(MeedlException.class, () -> programService.viewAllPrograms(program));
        assertEquals("Organization ID cannot be null or empty", meedlException.getMessage());
    }
}