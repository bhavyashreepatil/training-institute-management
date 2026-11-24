package com.institute.management.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record BatchRosterResponse(
    BatchResponse batch,
    BigDecimal averageAttendancePercentage,
    List<EnrolmentResponse> enrolments
) {
}
