package com.company.platform.entity;

import java.util.EnumSet;

public enum ComplaintStatus {
    NEW,
    IN_PROGRESS,
    WAITING,
    RESOLVED,
    CLOSED,
    REJECTED;

    public boolean canTransitionTo(ComplaintStatus target) {
        if (target == this) {
            return true;
        }
        return switch (this) {
            case NEW -> EnumSet.of(IN_PROGRESS, REJECTED).contains(target);
            case IN_PROGRESS -> EnumSet.of(WAITING, RESOLVED, REJECTED).contains(target);
            case WAITING -> EnumSet.of(IN_PROGRESS, RESOLVED).contains(target);
            case RESOLVED -> target == CLOSED;
            case CLOSED, REJECTED -> false;
        };
    }
}
