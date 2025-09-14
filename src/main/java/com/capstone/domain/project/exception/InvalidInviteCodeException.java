package com.capstone.domain.project.exception;

import com.capstone.global.response.exception.GlobalException;
import com.capstone.global.response.status.ErrorStatus;

public class InvalidInviteCodeException extends GlobalException {
    public InvalidInviteCodeException() {
        super(ErrorStatus.CODE_NOT_FOUND);
    }
}
