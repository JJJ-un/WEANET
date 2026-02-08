package com.weanet.server.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id")
    private Route route;

    private String content; // 제보 내용
    private String reporter; // 제보자
    private LocalDateTime createdAt;

    @Builder
    public Report(Route route, String content, String reporter) {
        this.route = route;
        this.content = content;
        this.reporter = reporter;
        this.createdAt = LocalDateTime.now();
    }
}
