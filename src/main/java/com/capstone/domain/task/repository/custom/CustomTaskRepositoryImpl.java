package com.capstone.domain.task.repository.custom;

import com.capstone.domain.task.dto.request.TaskRequest;
import com.capstone.domain.task.entity.Task;
import com.capstone.domain.task.exception.VersionNotFoundException;
import com.capstone.domain.task.message.TaskMessages;
import com.capstone.domain.task.message.TaskStatus;
import com.capstone.global.util.DateTimeUtil;
import com.mongodb.client.result.UpdateResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;


@RequiredArgsConstructor
public class CustomTaskRepositoryImpl implements CustomTaskRepository{
    private final MongoTemplate mongoTemplate;

    @Override
    public String modifyVersion(TaskRequest taskDto){
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(taskDto.taskId())
                .and("versionHistory.version").is(taskDto.version()));

        Update update = new Update();
        update.set("versionHistory.$.modifiedBy", taskDto.modifiedBy());
        update.set("versionHistory.$.content", taskDto.content());
        update.set("versionHistory.$.modifiedDateTime", DateTimeUtil.getCurrentFormattedDateTime());

        UpdateResult result = mongoTemplate.updateFirst(query, update, Task.class);

        if (result.getMatchedCount() == 0) {
            throw new VersionNotFoundException(TaskMessages.VERSION_NOT_FOUND);
        }

        return TaskMessages.VERSION_MODIFIED;
    }
    @Override
    public List<Task> findByIds(List<String> taskIds) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").in(taskIds));
        return mongoTemplate.find(query, Task.class);
    }

    @Override
    public List<Task> findByUserEmailAndSortDeadLine(String email) {
        Query query = new Query();
        query.addCriteria(Criteria.where("editors").in(email));
        query.with(Sort.by(Sort.Direction.ASC,"deadline"));
        return mongoTemplate.find(query, Task.class);
    }

    @Override
    public List<Task> findByUserEmail(String email) {
        Query query = new Query();
        query.addCriteria(Criteria.where("editors").in(email));
        return mongoTemplate.find(query, Task.class);
    }

    @Override
    public List<Task> findByProjectId(String projectId) {
        Query query= new Query();
        query.addCriteria(Criteria.where("projectId").is(projectId));
        return mongoTemplate.find(query, Task.class);
    }

    @Override
    public List<Task> findByProjectIds(List<String> projectIds) {
        if (projectIds == null || projectIds.isEmpty()) {
            return List.of();
        }
        Query query = new Query();
        query.addCriteria(Criteria.where("projectId").in(projectIds));
        return mongoTemplate.find(query, Task.class);
    }

    @Override
    public double rateDueDateCompletion(String projectId, String userName) {
        Query query = new Query(
                Criteria.where("projectId").is(projectId)
                        .and("editors").in(userName)
                        .and("status").is(TaskStatus.COMPLETED)
                        .and("deadline").ne(null)
        );
        List<Task> completedTasks = mongoTemplate.find(query, Task.class);

        if(completedTasks.isEmpty()) return 0;
        long total = completedTasks.size();

        long onTime = completedTasks.stream()
                .filter(Task::isCompletedOnTime)
                .count();

        double rate = (double) onTime / total * 100;
        return Math.round(rate * 10) / 10.0;
    }
}
