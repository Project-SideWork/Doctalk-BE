package com.capstone.domain.project.entity;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "invite_code")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InviteCode {
    @Id
    private String id;
    private String code;
    private String projectId;

    @Indexed(expireAfterSeconds = 6000)
    private Date createdAt;

    public static InviteCode of(String uuid, String projectId){
        return InviteCode.builder()
                .code(uuid)
                .projectId(projectId)
                .build();
    }
}
