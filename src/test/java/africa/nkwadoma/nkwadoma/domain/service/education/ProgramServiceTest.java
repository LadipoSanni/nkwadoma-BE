package africa.nkwadoma.nkwadoma.domain.service.education;

import africa.nkwadoma.nkwadoma.application.ports.output.education.ProgramOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.education.Program;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
                programType(ProgramType.VOCATIONAL).programStatus(ProgramStatus.ACTIVE).
                objectives("Program Objectives").
                deliveryType(DeliveryType.ONSITE).mode(ProgramMode.FULL_TIME).duration(BigInteger.ONE.intValue()).
                build();
    }

    @Test
    void addProgramWithEmptyProgramName() {
        program.setName(null);
        assertThrows(InvalidInputException.class, ()->programService.createProgram(program));
    }

    @Test
    void addProgramWithEmptyDurationStatus() {
        program.setDurationType(null);
        assertThrows(InvalidInputException.class, ()->programService.createProgram(program));
    }
    @Test
    void addProgramWithEmptyOrganizationId() {
        program.setOrganizationId(null);
        assertThrows(InvalidInputException.class, ()->programService.createProgram(program));
    }
    @Test
    void addProgramWithExistingName() {
        try {
            when(programOutputPort.findProgramByName(program.getName())).thenReturn(Optional.of(program));
            assertThrows(ResourceAlreadyExistsException.class, ()-> programService.createProgram(program));
        } catch (MiddlException e) {
            log.error(e.getMessage());
        }
    }

    @Test
    void addProgram() {
        try {
            when(programOutputPort.findProgramByName(program.getName())).thenReturn(Optional.of(program));
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
        } catch (MiddlException e) {
            log.info("Error creating program: {}", e.getMessage());
        }
    }
}