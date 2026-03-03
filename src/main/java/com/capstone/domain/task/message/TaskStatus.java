package com.capstone.domain.task.message;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TaskStatus {
    PENDING("진행 전"),
    PROGRESS("진행 중"),
    COMPLETED("진행 완료");

    private final String value;

    public static boolean isValid(String value) {
        try {
            TaskStatus.valueOf(value);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
