package com.worksonourmachines.student;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import com.worksonourmachines.server.common.security.CommonSecurityConfiguration;

@SpringBootApplication
@Import(CommonSecurityConfiguration.class)
public class StudentApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudentApplication.class, args);
    }
}
