package com.superbuilt.mini.notification;

import com.superbuilt.mini.action.Action;
import com.superbuilt.mini.issue.Issue;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    public void handleIssueNotification(
            Issue issue,
            Action action
    ) {
        System.out.println(
                "Issue " + issue.getId()
                        + " and action " + action.getId()
                        + " will be included in the next morning brief."
        );
    }
}