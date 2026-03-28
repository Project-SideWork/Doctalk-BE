package com.capstone.global.kafka.dto;

import com.capstone.domain.project.entity.Project;
import com.capstone.global.kafka.dto.detail.ProjectChangeDetail;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;


@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectAuthPayload extends CommonChangePayload {
    private String userEmail;
    private String projectName;
    private String newRole;

    public static ProjectAuthPayload from(String userEmail, Project project, String newRole) {
        return ProjectAuthPayload.builder()
                .userEmail(userEmail)
                .projectName(project.getProjectName())
                .newRole(newRole)
                .build();
    }
}
