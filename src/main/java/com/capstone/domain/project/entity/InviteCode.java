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
    private Date expiresAt;

    public static InviteCode of(String uuid, String projectId){
        return InviteCode.builder()
                .code(uuid)
                .projectId(projectId)
                .expiresAt(new Date(System.currentTimeMillis() + 600 * 1000))
                .build();
    }
}
