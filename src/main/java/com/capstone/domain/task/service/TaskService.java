package com.capstone.domain.task.service;

import com.capstone.domain.project.entity.Project;
import com.capstone.domain.project.exception.ProjectNotFoundException;
import com.capstone.domain.project.repository.ProjectRepository;
import com.capstone.domain.task.dto.request.TaskRequest;
import com.capstone.domain.task.dto.response.AttachmentDto;
import com.capstone.domain.task.dto.response.TaskResponse;
import com.capstone.domain.task.dto.response.TaskSpecResponse;
import com.capstone.domain.task.dto.response.TaskVersionResponse;
import com.capstone.domain.task.entity.Attachment;
import com.capstone.domain.task.entity.Task;
import com.capstone.domain.task.entity.Version;
import com.capstone.domain.task.message.TaskStatus;
import com.capstone.domain.task.repository.TaskRepository;
import com.capstone.domain.task.util.TaskUtil;
import com.capstone.domain.log.entity.LogEntity;
import com.capstone.domain.log.repository.LogRepository;
import com.capstone.domain.task.util.VersionUtil;
import com.capstone.global.kafka.dto.TaskChangePayload;
import com.capstone.global.kafka.dto.detail.TaskChangeDetail;
import com.capstone.global.kafka.service.KafkaProducerService;
import com.capstone.global.kafka.topic.KafkaEventTopic;
import com.capstone.global.response.exception.GlobalException;
import com.capstone.global.response.status.ErrorStatus;
import com.capstone.global.security.CustomUserDetails;
import io.micrometer.core.annotation.Timed;
import io.micrometer.observation.annotation.Observed;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;


@Slf4j
@Service
@AllArgsConstructor
@Observed
public class TaskService {
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final LogRepository logRepository;
    private final KafkaProducerService kafkaProducerService;
    private final TaskUtil taskUtil;


    @Transactional
    public Task saveTask(TaskRequest taskDto, CustomUserDetails userDetails){
        validateStatus(taskDto.status());
        Project project = projectRepository.findById(taskDto.projectId()).orElseThrow(
                ProjectNotFoundException::new
        );
        Task saved = taskRepository.save(taskDto.toTask());
        projectRepository.addTaskIdAtomically(project.getId(), saved.getId());
        kafkaProducerService.sendEvent(KafkaEventTopic.TASK_CREATED,TaskChangePayload.from(saved, null, null, userDetails.getEmail(), taskDto.editors()));

        return saved;
    }

    public TaskSpecResponse loadVersionContent(String taskId) {
        Task task = findTaskByIdOrThrow(taskId);
        Version version = VersionUtil.getCurrentVersionEntity(task);
        List<AttachmentDto> attachmentDtos = convertAttachmentsToDto(version.getAttachmentList());
        return TaskSpecResponse.from(task, attachmentDtos, version.getContent());

    }

    @Transactional
    public TaskVersionResponse saveVersion(TaskRequest taskDto, String fileId,String fileName ,CustomUserDetails customUserDetails){
        Version version = taskUtil.createOrGetVersion(taskDto, fileId, fileName);
        Task task = findTaskByIdOrThrow(taskDto.taskId());
        TaskChangeDetail beforeChange = TaskChangeDetail.from(task);

        task.addNewVersion(version);
        task.updateInfo(taskDto.title(), LocalDate.parse(Objects.requireNonNull(taskDto.deadline())),taskDto.version(),taskDto.editors());

        TaskChangeDetail afterChange = TaskChangeDetail.from(task);


        taskRepository.save(task);

        kafkaProducerService.sendEvent(KafkaEventTopic.TASK_CREATED, TaskChangePayload.from(task, beforeChange, afterChange, customUserDetails.getEmail(), taskDto.editors()));
        return TaskVersionResponse.from(version, taskDto.taskId(),task.getTitle(),task.getDeadline(),task.getEditors());

    }
    private List<AttachmentDto> convertAttachmentsToDto(List<Attachment> attachments) {
        return attachments.stream()
            .map(attachment -> AttachmentDto.from(attachment.getFileId(), attachment.getFileName()))
            .toList();
    }

    @Transactional
    public Task dropTask(String id, CustomUserDetails userDetails){
        Task task = findTaskByIdOrThrow(id);
        taskRepository.delete(task);

        kafkaProducerService.sendEvent(KafkaEventTopic.TASK_DELETED,TaskChangePayload.from(task, null, null, userDetails.getEmail(), task.getEditors()));

        return task;
    }

    @Transactional
    public Task updateStatus(String id, String status, CustomUserDetails userDetails){
        validateStatus(status);

        Task task = findTaskByIdOrThrow(id);
        TaskChangeDetail beforeChange = TaskChangeDetail.from(task);

        task.updateStatus(status);
        taskRepository.save(task);
        TaskChangeDetail afterChange = TaskChangeDetail.from(task);

        kafkaProducerService.sendEvent(KafkaEventTopic.TASK_UPDATED,TaskChangePayload.from(task, beforeChange, afterChange, userDetails.getEmail(), task.getEditors()));

        return task;
    }

    @Observed(name = "version.list", contextualName = "List Version")
    public List<TaskVersionResponse> listVersions(String taskId){
        Task task = findTaskByIdOrThrow(taskId);
        List<Version> versionList =task.getVersionHistory();
        return versionList.stream()
                .map(version ->
                {
                    return TaskVersionResponse.from(version,task.getId(),task.getTitle(),task.getDeadline(),task.getEditors());
                })
                .toList();

    }

    @Transactional
    public TaskResponse rollbackVersion(String taskId, String version){
        Task task = findTaskByIdOrThrow(taskId);
        task.updateCurrentVersion(version);
        taskRepository.save(task);
        return TaskResponse.from(task);
    }


    public List<LogEntity> findLogsByTaskId(String taskId){
        return logRepository.findAllByTaskId(taskId);
    }

    public Task findTaskByIdOrThrow(String id){
        return taskRepository.findById(id)
                .orElseThrow(() -> new GlobalException(ErrorStatus.TASK_NOT_FOUND));
    }

    public List<Task> listByDeadLine(CustomUserDetails customUserDetails)
    {
        String email = customUserDetails.getEmail();
        List<Task>taskList=taskRepository.findByUserEmailAndSortDeadLine(email);
        return taskList;

    }

    @Observed(name = "task.list", contextualName = "List Tasks")
    @Timed(value = "task.list", description = "List Tasks by Project ID")
    public List<TaskSpecResponse> listTask(String projectId){

        List<Task> taskList = taskRepository.findByProjectId(projectId);


        return taskList.stream()
                .map(task -> {
                    Version version = VersionUtil.getCurrentVersionEntity(task);
                    List<AttachmentDto> attachmentDtos = convertAttachmentsToDto(version.getAttachmentList());
                    return TaskSpecResponse.from(task, attachmentDtos, version.getContent());
                })
                .toList();
    }

    public void validateStatus(String status){
        if (!TaskStatus.isValid(status)){
            throw new GlobalException(ErrorStatus.INVALID_STATUS);
        }
    }
}
