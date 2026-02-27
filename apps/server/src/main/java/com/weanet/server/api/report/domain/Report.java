package com.weanet.server.api.report.domain;

import com.weanet.server.api.common.domain.BaseEntity;
import com.weanet.server.api.route.domain.TransportType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Report extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long routeId;

    @Enumerated(EnumType.STRING)
    private TransportType transportType;

    private String lineName;

    @Column(nullable = false)
    private String content;

    private String status; // 혼잡, 지연, 사고 등
}
