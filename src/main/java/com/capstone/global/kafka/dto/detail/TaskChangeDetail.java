package com.capstone.global.kafka.dto.detail;

import com.capstone.domain.task.entity.Task;
import com.capstone.domain.task.entity.Version;
import com.capstone.domain.task.message.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.List;


@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class TaskChangeDetail extends ChangeDetail<List<String>>{
    private String version;
    private TaskStatus status;
    private LocalDate deadline;

    public static TaskChangeDetail from(Task task) {
        return TaskChangeDetail.builder()
                .title(task.getTitle())
                .coworkers(task.getEditors())
                .version(task.getCurrentVersion())
                .status(task.getStatus())
                .deadline(task.getDeadline())
                .build();
    }
}
