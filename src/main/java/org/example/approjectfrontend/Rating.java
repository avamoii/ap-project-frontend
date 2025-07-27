package org.example.approjectfrontend;

import javafx.beans.property.*;

/**
 * این کلاس مدل امتیاز (Rating) را برای استفاده در لایه UI (مانند TableView) نشان می‌دهد.
 * این کلاس از JavaFX Properties استفاده می‌کند تا به راحتی به کامپوننت‌های UI متصل شود.
 */
public class Rating {
    private final IntegerProperty id;
    private final IntegerProperty userId;
    private final StringProperty userName;
    private final IntegerProperty itemId;
    private final IntegerProperty score;
    private final StringProperty comment;

    public Rating(int id, int userId, String userName, int itemId, int score, String comment) {
        this.id = new SimpleIntegerProperty(id);
        this.userId = new SimpleIntegerProperty(userId);
        this.userName = new SimpleStringProperty(userName);
        this.itemId = new SimpleIntegerProperty(itemId);
        this.score = new SimpleIntegerProperty(score);
        this.comment = new SimpleStringProperty(comment);
    }

    // Getters for properties
    public IntegerProperty idProperty() {
        return id;
    }

    public StringProperty userNameProperty() {
        return userName;
    }

    public IntegerProperty scoreProperty() {
        return score;
    }

    public StringProperty commentProperty() {
        return comment;
    }

    // Standard getters
    public int getId() {
        return id.get();
    }

    public String getUserName() {
        return userName.get();
    }

    public int getScore() {
        return score.get();
    }

    public String getComment() {
        return comment.get();
    }
}
