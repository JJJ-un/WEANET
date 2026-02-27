package com.weanet.server.dto;

import com.weanet.server.domain.Route;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@NoArgsConstructor
@Schema(description = "경로 저장 요청")
public class RouteSaveRequest {
    @Schema(description = "경로 별칭", example = "출퇴근길")
    private String name;

    private String departureName;
    private double departureLat;
    private double departureLng;

    private String destinationName;
    private double destinationLat;
    private double destinationLng;

    private int totalTime;
    private int totalFare;
    private int transferCount;

    @Schema(description = "저장할 상세 구간 리스트")
    private List<RouteStepSaveRequest> steps;

    public Route toEntity() {
        Route route = Route.builder()
                .name(name)
                .departureName(departureName)
                .departureLat(departureLat)
                .departureLng(departureLng)
                .destinationName(destinationName)
                .destinationLat(destinationLat)
                .destinationLng(destinationLng)
                .totalTime(totalTime)
                .totalFare(totalFare)
                .transferCount(transferCount)
                .build();

        if (steps != null) {
            steps.forEach(stepDto -> route.addStep(stepDto.toEntity(route)));
        }
        return route;
    }
}
