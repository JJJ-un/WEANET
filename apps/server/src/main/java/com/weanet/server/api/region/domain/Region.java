package com.weanet.server.api.region.domain;

import com.weanet.server.global.common.BaseEntity;
import com.weanet.server.global.common.Location;
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

    @Embedded
    private Location location; // 지역 위치 정보 (명칭 + 좌표)

    @Column(nullable = false)
    private int nx; // 기상청 격자 X

    @Column(nullable = false)
    private int ny; // 기상청 격자 Y

    @Builder
    public Region(Location location, int nx, int ny) {
        if (location == null) throw new IllegalArgumentException("위치 정보는 필수입니다.");
        this.location = location;
        this.nx = nx;
        this.ny = ny;
    }

    public void updateLocation(Location location, int nx, int ny) {
        if (location == null) throw new IllegalArgumentException("위치 정보는 필수입니다.");
        this.location = location;
        this.nx = nx;
        this.ny = ny;
    }

    public String getName() {
        return location.getName();
    }

    public double getLat() {
        return location.getLat();
    }

    public double getLng() {
        return location.getLng();
    }
}
