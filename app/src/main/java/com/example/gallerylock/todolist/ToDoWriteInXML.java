package com.example.gallerylock.todolist;

import android.app.Activity;
import android.util.Xml;
import android.widget.Toast;

import com.example.gallerylock.R;
import com.example.gallerylock.common.Constants;
import com.example.gallerylock.storageoption.StorageOptionsCommon;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.StringWriter;
import java.util.Iterator;

/* loaded from: classes2.dex */
public class ToDoWriteInXML {
    Constants constants;
    ToDoDAL dal;
    File newFile;
    File oldFile;
    String toDoName;

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

    public boolean saveToDoList(Activity activity, ToDoPojo toDoPojo, String str, boolean z) {
        this.constants = new Constants();
        this.dal = new ToDoDAL(activity);
        this.toDoName = toDoPojo.getTitle();
        File file = new File(StorageOptionsCommon.STORAGEPATH + StorageOptionsCommon.TODOLIST);
        if (!file.exists()) {
            file.mkdirs();
        }
        String absolutePath = file.getAbsolutePath();
        this.newFile = new File(absolutePath, this.toDoName + StorageOptionsCommon.NOTES_FILE_EXTENSION);
        String absolutePath2 = file.getAbsolutePath();
        this.oldFile = new File(absolutePath2, str + StorageOptionsCommon.NOTES_FILE_EXTENSION);
        try {
            if (!z) {
                try {
                    if (this.newFile.exists()) {
                        ToDoDAL toDoDAL = this.dal;
                        StringBuilder sb = new StringBuilder();
                        this.constants.getClass();
                        sb.append("SELECT \t     * \t\t\t\t\t\t   FROM  TableToDo WHERE ");
                        this.constants.getClass();
                        sb.append("ToDoName");
                        sb.append("='");
                        sb.append(this.toDoName);
                        sb.append("'");
                        if (toDoDAL.IsFileAlreadyExist(sb.toString())) {
                            Toast.makeText(activity, (int) R.string.toast_exists, 0).show();
                            return false;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                this.newFile.createNewFile();
            } else if (!this.toDoName.equals(str)) {
                if (this.oldFile.exists()) {
                    this.oldFile.renameTo(this.newFile);
                }
            } else if (!this.newFile.exists()) {
                this.newFile.createNewFile();
            }
            XmlSerializer newSerializer = Xml.newSerializer();
            StringWriter stringWriter = new StringWriter();
            newSerializer.setOutput(stringWriter);
            newSerializer.startDocument(null, true);
            newSerializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
            newSerializer.startTag(null, Tags.ToDoList.toString());
            newSerializer.startTag(null, Tags.ToDoTitle.toString());
            newSerializer.text(toDoPojo.getTitle());
            newSerializer.endTag(null, Tags.ToDoTitle.toString());
            newSerializer.startTag(null, Tags.ToDoColor.toString());
            newSerializer.text(toDoPojo.getTodoColor());
            newSerializer.endTag(null, Tags.ToDoColor.toString());
            newSerializer.startTag(null, Tags.ToDoDateCreated.toString());
            newSerializer.text(toDoPojo.getDateCreated());
            newSerializer.endTag(null, Tags.ToDoDateCreated.toString());
            newSerializer.startTag(null, Tags.ToDoDateModified.toString());
            newSerializer.text(toDoPojo.getDateModified());
            newSerializer.endTag(null, Tags.ToDoDateModified.toString());
            newSerializer.startTag(null, Tags.ToDoTasks.toString());
            Iterator<ToDoTask> it = toDoPojo.getToDoList().iterator();
            while (it.hasNext()) {
                ToDoTask next = it.next();
                newSerializer.startTag(null, Tags.ToDoTask.toString()).attribute(null, Tags.isCompleted.toString(), String.valueOf(next.isChecked()));
                newSerializer.text(next.getToDo());
                newSerializer.endTag(null, Tags.ToDoTask.toString());
            }
            newSerializer.endTag(null, Tags.ToDoTasks.toString());
            newSerializer.endTag(null, Tags.ToDoList.toString());
            newSerializer.endDocument();
            newSerializer.flush();
            FileOutputStream fileOutputStream = new FileOutputStream(this.newFile);
            fileOutputStream.write(stringWriter.toString().getBytes());
            fileOutputStream.flush();
            fileOutputStream.close();
            return true;
        } catch (Exception e2) {
            e2.printStackTrace();
            return false;
        }
    }
}
