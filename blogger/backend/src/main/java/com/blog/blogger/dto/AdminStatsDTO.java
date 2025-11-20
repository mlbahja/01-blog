package com.blog.blogger.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AdminStatsDTO - Data Transfer Object for admin dashboard statistics
 *
 * Contains system-wide statistics for admin dashboard
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminStatsDTO {
    private Long totalUsers;
    private Long totalPosts;
    private Long totalComments;
    private Long activeUsers;           // Users who logged in within last 30 days
    private Long bannedUsers;
    private Long adminUsers;
    private Long postsToday;
    private Long commentsToday;
    private Long newUsersThisWeek;
}
