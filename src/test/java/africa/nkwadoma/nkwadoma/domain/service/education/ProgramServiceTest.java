package africa.nkwadoma.nkwadoma.domain.service.education;

import africa.nkwadoma.nkwadoma.application.ports.output.education.ProgramOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.*;
import africa.nkwadoma.nkwadoma.domain.enums.constants.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.education.Program;
import lombok.extern.slf4j.*;
import org.apache.commons.lang3.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.math.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Slf4j
class ProgramServiceTest {
    @InjectMocks
    private ProgramService programService;
    @Mock
    private ProgramOutputPort programOutputPort;
    private Program program;
    private int pageSize = 10;
    private int pageNumber = 0;

    @BeforeEach
    void setUp() {
        program = Program.builder().id("yw9u9-vcc9wb-b9cne").name("My program").durationType(DurationType.YEARS).
                programDescription("A great program").organizationId("68t46").programStatus(ActivationStatus.ACTIVE).
                objectives("Program Objectives").createdBy("875565").deliveryType(DeliveryType.ONSITE).
                mode(ProgramMode.FULL_TIME).duration(BigInteger.ONE.intValue()).build();
    }

    @ParameterizedTest
    @ValueSource(strings = {StringUtils.SPACE})
    void addProgramWithEmptyProgramName(String programName) {
        program.setName(programName);
        assertThrows(MeedlException.class, ()->programService.createProgram(program));
    }

    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE})
    void addProgramWithInvalidCreatorId(String createdBy) {
        program.setCreatedBy(createdBy);
        assertThrows(MeedlException.class, ()->programService.createProgram(program));
    }

    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE})
    void addProgramWithEmptyOrganizationId(String organizationId) {
        program.setOrganizationId(organizationId);
        assertThrows(MeedlException.class, ()->programService.createProgram(program));
    }

    @Test
    void addProgramWithExistingName() {
        try {
            when(programOutputPort.saveProgram(program)).thenReturn(program);
            Program createdProgram = programService.createProgram(program);
            assertNotNull(createdProgram);
            verify(programOutputPort, times(1)).saveProgram(program);

            when(programOutputPort.programExists(program.getName())).thenThrow(ResourceAlreadyExistsException.class);
            verify(programOutputPort, times(1)).programExists(program.getName());
            assertThrows(ResourceAlreadyExistsException.class, ()-> programService.createProgram(program));
        } catch (MeedlException e) {
            log.error("Error creating program", e);
        }
    }

    @Test
    void addProgram() {
        try {
            when(programOutputPort.saveProgram(program)).thenReturn(program);
            Program addedProgram = programService.createProgram(program);
            verify(programOutputPort, times(1)).saveProgram(program);

            assertEquals(addedProgram.getProgramDescription(), program.getProgramDescription());
            assertEquals(addedProgram.getDurationType(), program.getDurationType());
            assertEquals(addedProgram.getName(), program.getName());
            assertEquals(addedProgram.getObjectives(), program.getObjectives());
            assertEquals(addedProgram.getProgramStatus(), program.getProgramStatus());
            assertEquals(addedProgram.getDuration(), program.getDuration());
            assertEquals(addedProgram.getMode(), program.getMode());
            assertEquals(addedProgram.getCreatedAt(), program.getCreatedAt());
        } catch (MeedlException e) {
            log.error("Error creating program: {}", e.getMessage());
        }
    }

    @Test
    void updateProgram() {
        try {
            when(programOutputPort.saveProgram(program)).thenReturn(program);
            Program addedProgram = programService.createProgram(program);

            log.info("Program: {}", addedProgram);
            addedProgram.setId(program.getId());
            addedProgram.setName("New program name");
            addedProgram.setProgramDescription("New program description");
            addedProgram.setDuration(3);
            addedProgram.setMode(ProgramMode.PART_TIME);
            addedProgram.setDeliveryType(DeliveryType.ONLINE);

            Program updatedProgram = programService.updateProgram(addedProgram);

            verify(programOutputPort, times(2)).saveProgram(addedProgram);
            assertNotNull(updatedProgram);
            assertEquals(updatedProgram.getProgramDescription(), addedProgram.getProgramDescription());
        } catch (MeedlException e) {
            log.error("Error updating program", e);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE})
    void updateProgramWithEmptyProgramId(String programId) {
        program.setId(programId);
        assertThrows(MeedlException.class, ()->programService.updateProgram(program));
    }

    @Test
    void updateProgramWithNullProgramId() {
        program.setId(null);
        MeedlException exception = assertThrows(MeedlException.class, () -> programService.updateProgram((program)));
        assertEquals(exception.getMessage(), MeedlMessages.EMPTY_INPUT_FIELD_ERROR.getMessage());
    }

    @Test
    void updateProgramWithNullProgram() {
        MeedlException exception = assertThrows(MeedlException.class, () -> programService.updateProgram((null)));
        assertEquals(exception.getMessage(), MeedlMessages.INVALID_OBJECT.getMessage());
    }


    @Test
    void updateNonExistingProgram() {
        Program nonexistingProgram = Program.builder().id("non existing id").name("Non existing name").build();
        try {
            when(programOutputPort.findProgramById(nonexistingProgram.getId())).thenThrow(ResourceNotFoundException.class);
        } catch (MeedlException e) {
            log.error("", e);
        }
        assertThrows(MeedlException.class, () -> programService.updateProgram((nonexistingProgram)));
    }

    @Test
    void viewAllPrograms() {
        try {
            when(programOutputPort.findAllPrograms(program.getOrganizationId(), pageSize, pageNumber)).
                    thenReturn(new PageImpl<>(List.of(program)));
            Page<Program> programs = programService.viewAllPrograms(program);
            List<Program> programsList = programs.toList();

            verify(programOutputPort, times(1)).
                    findAllPrograms(program.getOrganizationId(), pageSize, pageNumber);
            assertNotNull(programs);
            assertNotNull(programsList);
            assertEquals(programsList.get(0).getId(), program.getId());
            assertEquals(programsList.get(0).getOrganizationId(), program.getOrganizationId());
            assertEquals(programsList.get(0).getName(), program.getName());
            assertEquals(programsList.get(0).getDuration(), program.getDuration());
            assertEquals(programsList.get(0).getNumberOfCohort(), program.getNumberOfCohort());
            assertEquals(programsList.get(0).getNumberOfTrainees(), program.getNumberOfTrainees());
            assertEquals(BigDecimal.ZERO, programsList.get(0).getTotalAmountDisbursed());
            assertEquals(BigDecimal.ZERO, programsList.get(0).getTotalAmountOutstanding());
            assertEquals(BigDecimal.ZERO, programsList.get(0).getTotalAmountRepaid());
        } catch (MeedlException e) {
            log.error("Error viewing all programs", e);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE})
    void viewAllProgramsWithNullOrganizationId(String organizationId) {
        program.setOrganizationId(organizationId);
        MeedlException meedlException = assertThrows(MeedlException.class, () -> programService.viewAllPrograms(program));
        assertEquals("field cannot be null or empty", meedlException.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"   tf8980w", "grvboiwv    "})
    void viewAllProgramsWithSpaces(String organizationId) {
        try {
            program.setOrganizationId(organizationId);
            when(programOutputPort.findAllPrograms(program.getOrganizationId().trim(), pageSize, pageNumber)).
                    thenReturn(new PageImpl<>(List.of(program)));
            Page<Program> programs = programService.viewAllPrograms(program);
            List<Program> programsList = programs.toList();

            verify(programOutputPort, times(1)).
                    findAllPrograms(program.getOrganizationId().trim(), pageSize, pageNumber);
            assertNotNull(programs);
            assertNotNull(programsList);
            assertEquals(programsList.get(0).getId(), program.getId());
            assertEquals(programsList.get(0), program);
        } catch (MeedlException e) {
            log.error("Error viewing all programs", e);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"   tf8980w", "grvboiwv    "})
    void viewProgramsWithSpaces(String organizationId) {
        try {
            program.setOrganizationId(organizationId);
            when(programOutputPort.findAllPrograms(program.getOrganizationId().trim(), pageSize, pageNumber)).
                    thenReturn(new PageImpl<>(List.of(program)));
            Page<Program> programs = programService.viewAllPrograms(program);
            List<Program> programsList = programs.toList();

            assertNotNull(programs);
            assertNotNull(programsList);
            assertEquals(programsList.get(0).getId(), program.getId());
            assertEquals(programsList.get(0), program);
            verify(programOutputPort, times(1)).
                    findAllPrograms(program.getOrganizationId().trim(), pageSize, pageNumber);
        } catch (MeedlException e) {
            log.error("Error viewing all programs", e);
        }
    }

    @Test
    void viewProgramByName() {
        try {
            when(programOutputPort.findProgramByName(program.getName())).thenReturn(program);
            Program foundProgram = programService.viewProgramByName(program);
            assertNotNull(foundProgram);
            assertEquals(foundProgram, program);
            verify(programOutputPort, times(1)).findProgramByName(program.getName());
        } catch (MeedlException e) {
            log.error("Error viewing program by name", e);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"   tf8980w", "grvboiwv    "})
    void viewProgramByNameWithSpaces(String programWithSpace) {
        try {
            program.setName(programWithSpace);
            when(programOutputPort.findProgramByName(programWithSpace.trim())).thenReturn(program);

            Program foundProgram = programService.viewProgramByName(program);
            assertNotNull(foundProgram);
            verify(programOutputPort, times(1)).findProgramByName(program.getName().trim());
        } catch (MeedlException e) {
            log.error("Error viewing program by name", e);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {StringUtils.SPACE})
    void viewProgramWithNullOrEmptyName(String programWithSpace) {
        program.setName(programWithSpace);
        assertThrows(MeedlException.class, ()-> programService.viewProgramByName(program));
    }
}