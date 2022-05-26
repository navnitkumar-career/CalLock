package com.example.gallerylock.todolist;

import java.util.ArrayList;

/* loaded from: classes2.dex */
public class ToDoPojo {
    private ArrayList<ToDoTask> toDoList;
    private String todoColor;
    private String title = "";
    private String dateCreated = "";
    private String dateModified = "";

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String str) {
        this.title = str;
    }

    public String getDateCreated() {
        return this.dateCreated;
    }

    public void setDateCreated(String str) {
        this.dateCreated = str;
    }

    public String getDateModified() {
        return this.dateModified;
    }

    public void setDateModified(String str) {
        this.dateModified = str;
    }

    public ArrayList<ToDoTask> getToDoList() {
        return this.toDoList;
    }

    public void setToDoList(ArrayList<ToDoTask> arrayList) {
        this.toDoList = arrayList;
    }

    public void deleteTodoList() {
        this.toDoList = null;
    }

    public String getTodoColor() {
        return this.todoColor;
    }

    public void setTodoColor(String str) {
        this.todoColor = str;
    }
}
