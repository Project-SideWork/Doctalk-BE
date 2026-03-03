package com.capstone.domain.task.dto.request;

import com.capstone.domain.task.entity.Task;
import com.capstone.domain.task.message.TaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public record TaskRequest(String taskId,
                          @NotNull
                          String projectId,
                          @NotNull
                          @Schema(description = "영문 값으로 기입 <br> PENDING: 진행 전, PROGRESS: 진행 중, COMPLETED : 진행 완료")
                          String status,
                          @NotNull
                          String title,
                          @NotNull
                          String modifiedBy,
                          @NotNull
                          String version,
                          @NotNull
                          String content,
                          @Nullable
                          List<String> editors,
                          @Nullable
                          String deadline
                    ){

    public Task toTask() {
        List<String> updatedEditors = editors != null ? new ArrayList<>(editors) : new ArrayList<>();
        if (!updatedEditors.contains(modifiedBy)) {
            updatedEditors.add(modifiedBy);
        }
        return Task.builder()
                .title(this.title())
                .projectId(this.projectId)
                .status(TaskStatus.valueOf(status))
                .currentVersion(this.version)
                .versionHistory(new ArrayList<>())
                .editors(editors)
                .deadline(LocalDate.parse(Objects.requireNonNull(this.deadline)))
                .build();
    }
}
