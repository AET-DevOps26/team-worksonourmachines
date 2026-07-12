package com.worksonourmachines.student.goal.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.openapitools.model.SharedMarketplaceLocation;
import org.openapitools.model.SharedStudentLearningGoal;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import com.worksonourmachines.server.common.security.AuthenticatedUser;
import com.worksonourmachines.student.goal.mapper.LearningGoalMapper;
import com.worksonourmachines.student.goal.persistence.entity.LearningGoalEntity;
import com.worksonourmachines.student.goal.persistence.entity.LearningGoalLocation;
import com.worksonourmachines.student.goal.persistence.repository.LearningGoalRepository;

class LearningGoalServiceTest {

    private static final UUID STUDENT_ID = UUID.fromString("11111111-1111-1111-1111-111111111101");
    private static final UUID GOAL_ID = UUID.fromString("22222222-2222-2222-2222-222222222201");

    private final AuthenticatedUser authenticatedUser = org.mockito.Mockito.mock(AuthenticatedUser.class);
    private final LearningGoalRepository repository = org.mockito.Mockito.mock(LearningGoalRepository.class);
    private final LearningGoalService service = new LearningGoalService(
            authenticatedUser,
            repository,
            new LearningGoalMapper());

    @Test
    void getsGoalOwnedByAuthenticatedStudent() {
        LearningGoalEntity entity = goal();
        when(authenticatedUser.id()).thenReturn(STUDENT_ID);
        when(repository.findByIdAndStudentId(GOAL_ID, STUDENT_ID)).thenReturn(Optional.of(entity));

        SharedStudentLearningGoal result = service.getGoal(GOAL_ID.toString());

        assertEquals(GOAL_ID.toString(), result.getId());
        assertEquals("11111111-1111-1111-1111-111111111201", result.getModuleId());
        assertEquals("Prepare for the distributed systems exam.", result.getDescription());
        assertEquals(4, result.getSelfAssessedLevel());
        assertEquals(120, result.getBudgetEur());
        assertEquals(List.of(SharedMarketplaceLocation.ONLINE, SharedMarketplaceLocation.MUNICH), result.getLocations());
        verify(repository).findByIdAndStudentId(GOAL_ID, STUDENT_ID);
    }

    @Test
    void returnsNotFoundWhenGoalIsMissingOrOwnedByAnotherStudent() {
        when(authenticatedUser.id()).thenReturn(STUDENT_ID);
        when(repository.findByIdAndStudentId(GOAL_ID, STUDENT_ID)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.getGoal(GOAL_ID.toString()));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void returnsNotFoundForMalformedGoalIdWithoutQueryingPersistence() {
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.getGoal("1-1-1-1-1"));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verifyNoInteractions(authenticatedUser, repository);
    }

    private static LearningGoalEntity goal() {
        LearningGoalEntity entity = new LearningGoalEntity(
                STUDENT_ID,
                "11111111-1111-1111-1111-111111111201",
                "Prepare for the distributed systems exam.",
                OffsetDateTime.parse("2026-09-30T12:00:00Z"),
                4,
                120,
                List.of(LearningGoalLocation.ONLINE, LearningGoalLocation.MUNICH));
        ReflectionTestUtils.setField(entity, "id", GOAL_ID);
        return entity;
    }
}
