package com.weanet.server.api.report.domain;

import com.weanet.server.api.route.domain.Route;
import com.weanet.server.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@lombok.EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class Report extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @lombok.EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;

    @Column(nullable = false)
    private String content; // 제보 내용

    private String reporter; // 제보자

    @Builder
    public Report(Route route, String content, String reporter) {
        validate(route, content);
        this.route = route;
        this.content = content;
        this.reporter = reporter;
    }

    private void validate(Route route, String content) {
        if (route == null) throw new IllegalArgumentException("대상 경로는 필수입니다.");
        if (content == null || content.isBlank()) throw new IllegalArgumentException("제보 내용은 필수입니다.");
        if (content.length() > 500) throw new IllegalArgumentException("제보 내용은 500자 이내로 작성 가능합니다.");
    }
}
