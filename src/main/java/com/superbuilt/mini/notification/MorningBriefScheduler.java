package com.superbuilt.mini.notification;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class MorningBriefScheduler {

    private final MorningBriefService morningBriefService;

    public MorningBriefScheduler(
            MorningBriefService morningBriefService
    ) {
        this.morningBriefService = morningBriefService;
    }

    @Scheduled(
            cron = "${app.morning-brief.cron}",
            zone = "${app.morning-brief.zone}"
    )
    public void sendDailyMorningBriefs() {
        morningBriefService.sendMorningBriefs();
    }
}