package com.capstone.domain.project.entity;

import com.capstone.global.entity.BaseDocument;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection= "project")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Project extends BaseDocument {
    @Id
    private String id;
    private String projectName;
    private String imageId;
    private String description;
    private List<String> taskIds = new ArrayList<>();
    private List<String> documentIds = new ArrayList<>();
    private List<ProjectOrganization> projectOrganizations;

    public static Project create(String projectName, String description, List<ProjectOrganization> projectOrganizations) {
        return Project.builder()
                .projectName(projectName)
                .description(description)
                .projectOrganizations(projectOrganizations)
                .build();
    }

    public void updateProjectInfoWithImage(String projectName, String description,String imageId) {
        this.projectName = projectName;
        this.description = description;
        this.imageId = imageId;

    }
    public void updateProjectInfo(String projectName, String description) {
        this.projectName = projectName;
        this.description = description;
    }


    public void addProjectOrganization(ProjectOrganization organization){
        if(this.projectOrganizations == null) {
            this.projectOrganizations = new ArrayList<>();
        }
        if (!this.projectOrganizations.contains(organization)) {
            this.projectOrganizations.add(organization);
        }
    }
}
