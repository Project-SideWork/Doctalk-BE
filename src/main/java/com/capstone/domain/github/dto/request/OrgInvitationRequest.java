package com.capstone.domain.github.dto.request;

import java.util.List;

public record OrgInvitationRequest(
        List<String> email
) {
}
