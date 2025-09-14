package com.capstone.domain.user.entity;

import com.capstone.domain.project.entity.InviteCode;
import com.capstone.global.entity.BaseDocument;
import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "pending_user")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PendingUser extends BaseDocument {
    @Id
    private String id;
    private String userId;
    private String email;
    private InviteCode inviteCode;
}