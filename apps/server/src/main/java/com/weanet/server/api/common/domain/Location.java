package com.weanet.server.api.common.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Location {

    @Column(nullable = false)
    private String name; // 지점 명칭

    @Column(nullable = false)
    private double lat; // 위도

    @Column(nullable = false)
    private double lng; // 경도

    @Builder
    public Location(String name, double lat, double lng) {
        validate(name, lat, lng);
        this.name = name;
        this.lat = lat;
        this.lng = lng;
    }

    private void validate(String name, double lat, double lng) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("장소 명칭은 필수입니다.");
        }
        if (lat < -90 || lat > 90) {
            throw new IllegalArgumentException("위도 범위가 올바르지 않습니다 (-90 ~ 90).");
        }
        if (lng < -180 || lng > 180) {
            throw new IllegalArgumentException("경도 범위가 올바르지 않습니다 (-180 ~ 180).");
        }
    }
}
