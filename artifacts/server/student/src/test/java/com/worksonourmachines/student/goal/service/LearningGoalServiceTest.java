package com.worksonourmachines.student.goal.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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
import org.openapitools.model.SharedStudentLearningGoalInput;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import com.worksonourmachines.server.common.security.AuthenticatedUser;
import com.worksonourmachines.student.goal.mapper.LearningGoalMapper;
import com.worksonourmachines.student.goal.persistence.entity.LearningGoalEntity;
import com.worksonourmachines.student.goal.persistence.entity.LearningGoalLocation;
import com.worksonourmachines.student.goal.persistence.repository.LearningGoalRepository;
import com.worksonourmachines.student.profile.persistence.repository.StudentProfileRepository;

class LearningGoalServiceTest {

    private static final UUID STUDENT_ID = UUID.fromString("11111111-1111-1111-1111-111111111101");
    private static final UUID GOAL_ID = UUID.fromString("22222222-2222-2222-2222-222222222201");

    private final AuthenticatedUser authenticatedUser = org.mockito.Mockito.mock(AuthenticatedUser.class);
    private final LearningGoalRepository repository = org.mockito.Mockito.mock(LearningGoalRepository.class);
    private final StudentProfileRepository studentProfileRepository = org.mockito.Mockito.mock(StudentProfileRepository.class);
    private final LearningGoalService service = new LearningGoalService(
            authenticatedUser,
            repository,
            new LearningGoalMapper(),
            studentProfileRepository);

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

    @Test
    void listsAuthenticatedStudentsGoalsInRepositoryOrder() {
        LearningGoalEntity firstGoal = goal(
                GOAL_ID,
                "2026-09-30T12:00:00Z",
                "Prepare for the distributed systems exam.");
        UUID secondGoalId = UUID.fromString("22222222-2222-2222-2222-222222222202");
        LearningGoalEntity secondGoal = goal(
                secondGoalId,
                "2026-10-15T12:00:00Z",
                "Prepare for the databases exam.");
        when(authenticatedUser.id()).thenReturn(STUDENT_ID);
        when(repository.findAllByStudentIdOrderByTargetDateAscIdAsc(STUDENT_ID))
                .thenReturn(List.of(firstGoal, secondGoal));

        List<SharedStudentLearningGoal> result = service.listMyGoals();

        assertEquals(List.of(GOAL_ID.toString(), secondGoalId.toString()), result.stream()
                .map(SharedStudentLearningGoal::getId)
                .toList());
        verify(repository).findAllByStudentIdOrderByTargetDateAscIdAsc(STUDENT_ID);
    }

    @Test
    void listsEmptyCollectionWhenAuthenticatedStudentHasNoGoals() {
        when(authenticatedUser.id()).thenReturn(STUDENT_ID);
        when(repository.findAllByStudentIdOrderByTargetDateAscIdAsc(STUDENT_ID)).thenReturn(List.of());

        List<SharedStudentLearningGoal> result = service.listMyGoals();

        assertEquals(List.of(), result);
    }

    @Test
    void createsGoalForAuthenticatedStudent() {
        SharedStudentLearningGoalInput input = goalInput();
        when(authenticatedUser.id()).thenReturn(STUDENT_ID);
        when(studentProfileRepository.existsById(STUDENT_ID)).thenReturn(true);
        when(repository.save(any(LearningGoalEntity.class))).thenAnswer(invocation -> {
            LearningGoalEntity entity = invocation.getArgument(0);
            ReflectionTestUtils.setField(entity, "id", GOAL_ID);
            return entity;
        });

        SharedStudentLearningGoal result = service.createGoal(input);

        assertEquals(GOAL_ID.toString(), result.getId());
        assertEquals(STUDENT_ID, capturedSavedGoal().getStudentId());
        assertEquals("11111111-1111-1111-1111-111111111201", result.getModuleId());
        assertEquals("Prepare for the distributed systems exam.", result.getDescription());
        assertEquals(OffsetDateTime.parse("2026-09-30T12:00:00Z"), result.getTargetDate());
        assertEquals(4, result.getSelfAssessedLevel());
        assertEquals(120, result.getBudgetEur());
        assertEquals(List.of(SharedMarketplaceLocation.ONLINE, SharedMarketplaceLocation.MUNICH), result.getLocations());
    }

    @Test
    void returnsBadRequestForInvalidCreateInputWithoutWriting() {
        SharedStudentLearningGoalInput input = goalInput();
        input.setDescription(" ");

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.createGoal(input));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verifyNoInteractions(authenticatedUser, repository);
    }

    @Test
    void updatesGoalOwnedByAuthenticatedStudent() {
        LearningGoalEntity entity = goal();
        SharedStudentLearningGoalInput input = new SharedStudentLearningGoalInput(
                " 11111111-1111-1111-1111-111111111299 ",
                " Prepare for the advanced distributed systems exam. ",
                OffsetDateTime.parse("2026-10-31T12:00:00Z"),
                5,
                List.of(SharedMarketplaceLocation.GARCHING));
        when(authenticatedUser.id()).thenReturn(STUDENT_ID);
        when(repository.findByIdAndStudentId(GOAL_ID, STUDENT_ID)).thenReturn(Optional.of(entity));
        when(repository.save(entity)).thenReturn(entity);

        SharedStudentLearningGoal result = service.updateGoal(GOAL_ID.toString(), input);

        assertEquals(GOAL_ID.toString(), result.getId());
        assertEquals(STUDENT_ID, entity.getStudentId());
        assertEquals("11111111-1111-1111-1111-111111111299", result.getModuleId());
        assertEquals("Prepare for the advanced distributed systems exam.", result.getDescription());
        assertEquals(OffsetDateTime.parse("2026-10-31T12:00:00Z"), result.getTargetDate());
        assertEquals(5, result.getSelfAssessedLevel());
        assertNull(result.getBudgetEur());
        assertEquals(List.of(SharedMarketplaceLocation.GARCHING), result.getLocations());
        verify(repository).save(entity);
    }

    @Test
    void returnsNotFoundWhenUpdatingMissingOrForeignGoal() {
        when(authenticatedUser.id()).thenReturn(STUDENT_ID);
        when(repository.findByIdAndStudentId(GOAL_ID, STUDENT_ID)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.updateGoal(GOAL_ID.toString(), goalInput()));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void returnsBadRequestForInvalidUpdateInputWithoutReadingOrWriting() {
        SharedStudentLearningGoalInput input = goalInput();
        input.setSelfAssessedLevel(6);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.updateGoal(GOAL_ID.toString(), input));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verifyNoInteractions(authenticatedUser, repository);
    }

    @Test
    void deletesGoalOwnedByAuthenticatedStudent() {
        LearningGoalEntity entity = goal();
        when(authenticatedUser.id()).thenReturn(STUDENT_ID);
        when(repository.findByIdAndStudentId(GOAL_ID, STUDENT_ID)).thenReturn(Optional.of(entity));

        service.deleteGoal(GOAL_ID.toString());

        verify(repository).delete(entity);
    }

    @Test
    void returnsNotFoundWhenDeletingMissingOrForeignGoal() {
        when(authenticatedUser.id()).thenReturn(STUDENT_ID);
        when(repository.findByIdAndStudentId(GOAL_ID, STUDENT_ID)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.deleteGoal(GOAL_ID.toString()));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void returnsNotFoundForMalformedDeleteGoalIdWithoutQueryingPersistence() {
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.deleteGoal("1-1-1-1-1"));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verifyNoInteractions(authenticatedUser, repository);
    }

    private LearningGoalEntity capturedSavedGoal() {
        org.mockito.ArgumentCaptor<LearningGoalEntity> captor =
                org.mockito.ArgumentCaptor.forClass(LearningGoalEntity.class);
        verify(repository).save(captor.capture());
        return captor.getValue();
    }

    private static SharedStudentLearningGoalInput goalInput() {
        SharedStudentLearningGoalInput input = new SharedStudentLearningGoalInput(
                " 11111111-1111-1111-1111-111111111201 ",
                " Prepare for the distributed systems exam. ",
                OffsetDateTime.parse("2026-09-30T12:00:00Z"),
                4,
                List.of(SharedMarketplaceLocation.ONLINE, SharedMarketplaceLocation.MUNICH));
        input.setBudgetEur(120);
        return input;
    }

    private static LearningGoalEntity goal() {
        return goal(
                GOAL_ID,
                "2026-09-30T12:00:00Z",
                "Prepare for the distributed systems exam.");
    }

    private static LearningGoalEntity goal(UUID id, String targetDate, String description) {
        LearningGoalEntity entity = new LearningGoalEntity(
                STUDENT_ID,
                "11111111-1111-1111-1111-111111111201",
                description,
                OffsetDateTime.parse(targetDate),
                4,
                120,
                List.of(LearningGoalLocation.ONLINE, LearningGoalLocation.MUNICH));
        ReflectionTestUtils.setField(entity, "id", id);
        return entity;
    }
}
