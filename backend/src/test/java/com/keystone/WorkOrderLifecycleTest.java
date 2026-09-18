package com.keystone;

import com.keystone.domain.WorkOrderStatus;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class WorkOrderLifecycleTest {
    @Test
    void terminalStatesCannotHaveFurtherTransitions() {
        assertFalse(isAllowed(WorkOrderStatus.CLOSED, WorkOrderStatus.IN_PROGRESS));
        assertFalse(isAllowed(WorkOrderStatus.CANCELLED, WorkOrderStatus.ASSIGNED));
    }

    private boolean isAllowed(WorkOrderStatus from, WorkOrderStatus to) {
        return switch (from) {
            case CLOSED, CANCELLED -> false;
            case NEW -> to == WorkOrderStatus.ASSIGNED || to == WorkOrderStatus.CANCELLED;
            case ASSIGNED -> to == WorkOrderStatus.IN_PROGRESS || to == WorkOrderStatus.CANCELLED;
            case IN_PROGRESS -> to == WorkOrderStatus.ON_HOLD || to == WorkOrderStatus.COMPLETED || to == WorkOrderStatus.CANCELLED;
            case ON_HOLD -> to == WorkOrderStatus.IN_PROGRESS || to == WorkOrderStatus.CANCELLED;
            case COMPLETED -> to == WorkOrderStatus.CLOSED;
        };
    }
}
