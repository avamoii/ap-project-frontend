package org.example.approjectfrontend.api;

import com.google.gson.annotations.SerializedName;

public class UpdateRatingRequest {

    @SerializedName("rating")
    private Integer rating;

    @SerializedName("comment")
    private String comment;

    // imageBase64 را هم می‌توانید اضافه کنید اگر می‌خواهید عکس هم ویرایش شود

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}