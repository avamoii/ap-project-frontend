package org.example.approjectfrontend.api;

/**
 * این کلاس یک Data Transfer Object (DTO) برای داده‌های امتیاز است که از API دریافت می‌شود.
 * فیلدهای این کلاس باید دقیقاً با JSON که از بک‌اند ارسال می‌شود مطابقت داشته باشد.
 */
public class RatingDTO {
    private int id;
    private int userId;
    private String userName;
    private int itemId;
    private int score;
    private String comment;

    // Getters
    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public int getItemId() {
        return itemId;
    }

    public int getScore() {
        return score;
    }

    public String getComment() {
        return comment;
    }
}