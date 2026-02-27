package com.weanet.server.api.route.repository;

import com.weanet.server.api.route.domain.Route;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RouteRepository extends JpaRepository<Route, Long> {
}
