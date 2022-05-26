package com.example.gallerylock.todolist;

import org.apache.commons.io.IOUtils;
import org.apache.http.protocol.HTTP;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/* loaded from: classes2.dex */
public class ToDoReadFromXML {
    ToDoPojo toDoPojo;
    ToDoTask toDoTask;
    ArrayList<ToDoTask> toDoTasksList;
    Boolean inDataTag = false;
    String CurrentTag = "";

    /* loaded from: classes2.dex */
    public enum Tags {
        ToDoList,
        ToDoTitle,
        ToDoColor,
        ToDoDateCreated,
        ToDoDateModified,
        ToDoTasks,
        ToDoTask,
        isCompleted
    }

    public ToDoPojo ReadToDoList(String str) {
        try {
            File file = new File(str);
            XmlPullParser newPullParser = XmlPullParserFactory.newInstance().newPullParser();
            newPullParser.setInput(new InputStreamReader(new ByteArrayInputStream(IOUtils.toString(new FileInputStream(file), HTTP.UTF_8).getBytes())));
            for (int eventType = newPullParser.getEventType(); eventType != 1; eventType = newPullParser.next()) {
                if (eventType != 2) {
                    char c = 0;
                    if (eventType == 3) {
                        if (newPullParser.getName() == Tags.ToDoList.toString()) {
                            this.inDataTag = false;
                        }
                        this.CurrentTag = "";
                    } else if (eventType == 4) {
                        if (this.inDataTag.booleanValue() && this.toDoPojo != null) {
                            String str2 = this.CurrentTag;
                            switch (str2.hashCode()) {
                                case -2147234005:
                                    if (str2.equals("ToDoTask")) {
                                        c = 4;
                                        break;
                                    }
                                    c = 65535;
                                    break;
                                case -2139505294:
                                    if (str2.equals("ToDoTitle")) {
                                        break;
                                    }
                                    c = 65535;
                                    break;
                                case -385721283:
                                    if (str2.equals("ToDoDateModified")) {
                                        c = 2;
                                        break;
                                    }
                                    c = 65535;
                                    break;
                                case 66135796:
                                    if (str2.equals("ToDoDateCreated")) {
                                        c = 1;
                                        break;
                                    }
                                    c = 65535;
                                    break;
                                case 2139933309:
                                    if (str2.equals("ToDoColor")) {
                                        c = 3;
                                        break;
                                    }
                                    c = 65535;
                                    break;
                                default:
                                    c = 65535;
                                    break;
                            }
                            if (c == 0) {
                                this.toDoPojo.setTitle(newPullParser.getText());
                            } else if (c == 1) {
                                this.toDoPojo.setDateCreated(newPullParser.getText());
                            } else if (c == 2) {
                                this.toDoPojo.setDateModified(newPullParser.getText());
                            } else if (c == 3) {
                                this.toDoPojo.setTodoColor(newPullParser.getText());
                            } else if (c == 4) {
                                this.toDoTask.setToDo(newPullParser.getText());
                                this.toDoTasksList.add(this.toDoTask);
                                this.toDoTask = null;
                            }
                        }
                    }
                } else {
                    String name = newPullParser.getName();
                    this.CurrentTag = name;
                    if (name.equals(Tags.ToDoList.toString())) {
                        this.inDataTag = true;
                        this.toDoPojo = new ToDoPojo();
                    } else if (this.CurrentTag.equals(Tags.ToDoTasks.toString())) {
                        this.toDoTasksList = new ArrayList<>();
                    } else if (this.CurrentTag.equals(Tags.ToDoTask.toString())) {
                        ToDoTask toDoTask = new ToDoTask();
                        this.toDoTask = toDoTask;
                        toDoTask.setChecked(Boolean.parseBoolean(newPullParser.getAttributeValue(null, Tags.isCompleted.toString())));
                    }
                }
            }
        } catch (IOException | XmlPullParserException e) {
            e.printStackTrace();
        }
        this.toDoPojo.setToDoList(this.toDoTasksList);
        return this.toDoPojo;
    }
}
