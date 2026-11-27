package org.example.shelftoshelf.Controller;

import org.example.shelftoshelf.Dto.Response.StatisticsResponseDto;
import org.example.shelftoshelf.Service.StatisticsService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    // Yalnız ADMIN üçün statistika
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public StatisticsResponseDto getStatistics() {
        return statisticsService.getStatistics();
    }
}
