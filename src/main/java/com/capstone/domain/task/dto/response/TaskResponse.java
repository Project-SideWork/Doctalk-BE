package com.capstone.domain.task.dto.response;

import com.capstone.domain.task.entity.Task;
import com.capstone.domain.task.entity.Version;
import com.capstone.domain.task.message.TaskStatus;
import lombok.Builder;

import java.util.List;

@Builder
public record TaskResponse(
        String title,
        String currentVersion,
        TaskStatus status,
        List<String> editors,
        List<Version> versionHistory
) {
    public static TaskResponse from(Task task){
        return TaskResponse.builder()
                .title(task.getTitle())
                .currentVersion(task.getCurrentVersion())
                .status(task.getStatus())
                .editors(task.getEditors())
                .versionHistory(task.getVersionHistory())
                .build();
    }
}
