package com.worksonourmachines.student.profile.controller;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.openapitools.api.StudentApiV1;
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
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;

import com.worksonourmachines.server.common.security.CommonSecurityConfiguration;
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

    @Autowired
    StudentProfileControllerTest(MockMvc mockMvc, JwtDecoder jwtDecoder) {
        this.mockMvc = mockMvc;
        this.jwtDecoder = jwtDecoder;
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
        when(this.jwtDecoder.decode("student-token")).thenReturn(Jwt.withTokenValue("student-token")
                .header("alg", "none")
                .subject(UUID.randomUUID().toString())
                .claim("name", "Test Student")
                .build());

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

    @TestConfiguration
    static class TestBeans {
        @Bean
        StudentProfileService studentProfileService() {
            return mock(StudentProfileService.class);
        }

        @Bean
        JwtDecoder jwtDecoder() {
            return mock(JwtDecoder.class);
        }
    }
}
