package com.worksonourmachines.student.profile.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.openapitools.api.StudentApiV1;
import org.openapitools.model.SharedMarketplaceLocation;
import org.openapitools.model.SharedStudentLearningGoal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration;
import org.springframework.boot.security.autoconfigure.web.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.security.autoconfigure.web.servlet.ServletWebSecurityAutoConfiguration;
import org.springframework.boot.security.oauth2.server.resource.autoconfigure.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import com.worksonourmachines.server.common.security.CommonSecurityConfiguration;
import com.worksonourmachines.student.goal.service.LearningGoalService;
import com.worksonourmachines.student.profile.service.StudentProfileService;

@WebMvcTest(StudentProfileController.class)
@Import({CommonSecurityConfiguration.class, StudentProfileControllerTest.TestBeans.class})
@ImportAutoConfiguration({
    SecurityAutoConfiguration.class,
    SecurityFilterAutoConfiguration.class,
    ServletWebSecurityAutoConfiguration.class,
    OAuth2ResourceServerAutoConfiguration.class
})
class StudentProfileControllerTest {

    private final MockMvc mockMvc;
    private final JwtDecoder jwtDecoder;
    private final StudentProfileService studentProfileService;
    private final LearningGoalService learningGoalService;

    @Autowired
    StudentProfileControllerTest(
            MockMvc mockMvc,
            JwtDecoder jwtDecoder,
            StudentProfileService studentProfileService,
            LearningGoalService learningGoalService) {
        this.mockMvc = mockMvc;
        this.jwtDecoder = jwtDecoder;
        this.studentProfileService = studentProfileService;
        this.learningGoalService = learningGoalService;
    }

    @Test
    void getMyProfileWithoutBearerTokenReturnsUnauthorizedErrorBody() throws Exception {
        this.mockMvc.perform(get(StudentApiV1.PATH_GET_MY_PROFILE))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("unauthorized"))
                .andExpect(jsonPath("$.message").value("Access is unauthorized."));
    }

    @Test
    void updateMyProfileWithInvalidBodyReturnsBadRequestErrorBody() throws Exception {
        authenticateStudent();
        when(studentProfileService.updateCurrentStudentProfile(any()))
                .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST));

        this.mockMvc.perform(put(StudentApiV1.PATH_UPDATE_MY_PROFILE)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer student-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "displayName": " ",
                                  "bio": "Interested in math",
                                  "languages": ["en"]
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("bad_request"))
                .andExpect(jsonPath("$.message").value("The server could not understand the request due to invalid syntax."));
    }

    @Test
    void getGoalReturnsGoalOwnedByAuthenticatedStudent() throws Exception {
        authenticateStudent();
        String goalId = "22222222-2222-2222-2222-222222222201";
        SharedStudentLearningGoal goal = new SharedStudentLearningGoal(
                goalId,
                "11111111-1111-1111-1111-111111111201",
                "Prepare for the distributed systems exam.",
                OffsetDateTime.parse("2026-09-30T12:00:00Z"),
                4,
                List.of(SharedMarketplaceLocation.ONLINE));
        goal.setBudgetEur(120);
        when(learningGoalService.getGoal(goalId)).thenReturn(goal);

        this.mockMvc.perform(get(goalPath(goalId))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer student-token"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(goalId))
                .andExpect(jsonPath("$.moduleId").value("11111111-1111-1111-1111-111111111201"))
                .andExpect(jsonPath("$.selfAssessedLevel").value(4))
                .andExpect(jsonPath("$.budgetEur").value(120))
                .andExpect(jsonPath("$.locations[0]").value("online"));
    }

    @Test
    void getGoalWithoutBearerTokenReturnsUnauthorizedErrorBody() throws Exception {
        this.mockMvc.perform(get(goalPath("22222222-2222-2222-2222-222222222201")))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("unauthorized"))
                .andExpect(jsonPath("$.message").value("Access is unauthorized."));
    }

    @Test
    void getGoalNotFoundReturnsStandardErrorBody() throws Exception {
        authenticateStudent();
        String goalId = "22222222-2222-2222-2222-222222222299";
        when(learningGoalService.getGoal(goalId)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        this.mockMvc.perform(get(goalPath(goalId))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer student-token"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("not_found"))
                .andExpect(jsonPath("$.message").value("The requested resource was not found."));
    }

    @Test
    void listMyGoalsReturnsAuthenticatedStudentsGoals() throws Exception {
        authenticateStudent();
        SharedStudentLearningGoal goal = new SharedStudentLearningGoal(
                "22222222-2222-2222-2222-222222222201",
                "11111111-1111-1111-1111-111111111201",
                "Prepare for the distributed systems exam.",
                OffsetDateTime.parse("2026-09-30T12:00:00Z"),
                4,
                List.of(SharedMarketplaceLocation.ONLINE));
        when(learningGoalService.listMyGoals()).thenReturn(List.of(goal));

        this.mockMvc.perform(get(StudentApiV1.PATH_LIST_MY_GOALS)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer student-token"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value("22222222-2222-2222-2222-222222222201"))
                .andExpect(jsonPath("$[0].description").value("Prepare for the distributed systems exam."));
    }

    @Test
    void listMyGoalsWithoutBearerTokenReturnsUnauthorizedErrorBody() throws Exception {
        this.mockMvc.perform(get(StudentApiV1.PATH_LIST_MY_GOALS))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("unauthorized"))
                .andExpect(jsonPath("$.message").value("Access is unauthorized."));
    }

    private void authenticateStudent() {
        when(this.jwtDecoder.decode("student-token")).thenReturn(Jwt.withTokenValue("student-token")
                .header("alg", "none")
                .subject(UUID.randomUUID().toString())
                .claim("name", "Test Student")
                .build());
    }

    private static String goalPath(String id) {
        return StudentApiV1.PATH_GET_GOAL.replace("{id}", id);
    }

    @TestConfiguration
    static class TestBeans {
        @Bean
        StudentProfileService studentProfileService() {
            return mock(StudentProfileService.class);
        }

        @Bean
        LearningGoalService learningGoalService() {
            return mock(LearningGoalService.class);
        }

        @Bean
        JwtDecoder jwtDecoder() {
            return mock(JwtDecoder.class);
        }
    }
}
