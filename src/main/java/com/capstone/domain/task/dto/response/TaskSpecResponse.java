package com.capstone.domain.task.dto.response;

import com.capstone.domain.task.entity.Task;
import com.capstone.domain.task.message.TaskStatus;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
public record  TaskSpecResponse(
        String taskId,
        String title,
        TaskStatus status,
        String content,
        LocalDate deadline,
        List<AttachmentDto> attachmentList,
        List<String> coworkers
) {
    public static TaskSpecResponse from(Task task, List<AttachmentDto> attachments,String content){
        return TaskSpecResponse.builder()
                .taskId(task.getId())
                .title(task.getTitle())
                .status(task.getStatus())
                .deadline(task.getDeadline())
                .coworkers(task.getEditors())
                .attachmentList(attachments)
                .content(content)
                .build();
    }
}
