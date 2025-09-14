package com.capstone.domain.project.entity;

import com.capstone.global.entity.BaseDocument;
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
public class InviteCode extends BaseDocument {
    @Id
    private String id;
    private String code;
    private String projectId;

    @Indexed(expireAfterSeconds = 0)
    private Date expireAt;

    public static InviteCode of(String uuid, String projectId, long ttlSec){
        return InviteCode.builder()
                .code(uuid)
                .projectId(projectId)
                .expireAt(new Date(System.currentTimeMillis() + ttlSec * 1000))
                .build();
    }
}
