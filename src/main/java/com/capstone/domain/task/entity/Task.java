package com.capstone.domain.task.entity;

import com.capstone.domain.task.message.TaskStatus;
import com.capstone.global.entity.BaseDocument;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;

@Document(collection = "task")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Task extends BaseDocument {
    @Id
    private String id;
    private String projectId;
    private String title;
    private String currentVersion;
    private TaskStatus status;
    private List<String> editors;
    private List<Version> versionHistory;
    private LocalDate deadline;
    private LocalDate completedAt;

    public void updateStatus(TaskStatus status){
        this.status = status;
    }

    public void assignCompletedAt(){
        this.completedAt = LocalDate.now();
    }

    public void resetCompletedAt(){
        this.completedAt = null;
    }

    public void updateCurrentVersion(String currentVersion){
        this.currentVersion = currentVersion;
    }

    public void addNewVersion(Version version){
        this.versionHistory.add(version);
    }

    public void updateInfo(String title, LocalDate deadline, String currentVersion,List<String> editors)
    {
        this.title = title;
        this.deadline = deadline;
        this.currentVersion = currentVersion;
        this.editors = editors;
    }

    public boolean isCompletedOnTime() {
        if (completedAt == null || deadline == null) return false;
        return !completedAt.isAfter(deadline);
    }
}
