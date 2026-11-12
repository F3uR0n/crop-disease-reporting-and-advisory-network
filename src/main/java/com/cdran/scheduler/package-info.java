/**
 * <b>"Scheduler" system actor.</b> {@code @Scheduled} jobs that call services: EscalationTimeoutJob (Feature 9), FollowUpReminderJob (Feature 10), OutbreakDetectionJob (Feature 11), RiskScoreJob (Feature 14). Jobs contain no logic themselves - they only trigger service methods.
 */
package com.cdran.scheduler;
