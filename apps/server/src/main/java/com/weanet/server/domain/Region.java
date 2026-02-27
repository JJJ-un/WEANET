package com.weanet.server.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@lombok.EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class Region extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @lombok.EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; // 지역 명칭 (예: 강남역, 우리집)

    @Column(nullable = false)
    private double lat; // 위도

    @Column(nullable = false)
    private double lng; // 경도

    @Column(nullable = false)
    private int nx; // 기상청 격자 X

    @Column(nullable = false)
    private int ny; // 기상청 격자 Y

    @Builder
    public Region(String name, double lat, double lng, int nx, int ny) {
        validate(name, lat, lng);
        this.name = name;
        this.lat = lat;
        this.lng = lng;
        this.nx = nx;
        this.ny = ny;
    }

    private void validate(String name, double lat, double lng) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("지역 이름은 필수입니다.");
        if (lat < -90 || lat > 90) throw new IllegalArgumentException("위도 범위가 올바르지 않습니다.");
        if (lng < -180 || lng > 180) throw new IllegalArgumentException("경도 범위가 올바르지 않습니다.");
    }

    public void updateCoordinates(double lat, double lng, int nx, int ny) {
        validate(this.name, lat, lng);
        this.lat = lat;
        this.lng = lng;
        this.nx = nx;
        this.ny = ny;
    }
}
