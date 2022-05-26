package com.example.gallerylock.todolist;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.gallerylock.R;
import com.example.gallerylock.common.Constants;
import com.example.gallerylock.panicswitch.AccelerometerListener;
import com.example.gallerylock.panicswitch.AccelerometerManager;
import com.example.gallerylock.panicswitch.PanicSwitchActivityMethods;
import com.example.gallerylock.panicswitch.PanicSwitchCommon;
import com.example.gallerylock.securitylocks.SecurityLocksCommon;
import com.example.gallerylock.storageoption.StorageOptionsCommon;
import com.example.gallerylock.utilities.Common;
import com.example.gallerylock.utilities.Utilities;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes2.dex */
public class ViewToDoActivity extends AppCompatActivity implements AccelerometerListener, SensorEventListener {
    Constants constants;
    LayoutInflater inflater;
    LinearLayout ll_container;
    LinearLayout ll_main;
    List<ToDoCheckableRowPojo> rowList;
    private SensorManager sensorManager;
    TableLayout tl_TodoTasks;
    ToDoPojo toDoList;
    Toolbar toolbar;
    TextView tv_ToDoTitle;
    boolean isModified = false;
    boolean hasModified = false;

    @Override // net.newsoftwares.hidepicturesvideos.panicswitch.AccelerometerListener
    public void onAccelerationChanged(float f, float f2, float f3) {
    }

    @Override // android.hardware.SensorEventListener
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getWindow().addFlags(128);
        setContentView(R.layout.activity_todo_view);
        SecurityLocksCommon.IsAppDeactive = true;
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.inflater = (LayoutInflater) getSystemService("layout_inflater");
        this.tl_TodoTasks = (TableLayout) findViewById(R.id.tl_TodoTasks);
        this.tv_ToDoTitle = (TextView) findViewById(R.id.tv_ToDoTitle);
        this.ll_container = (LinearLayout) findViewById(R.id.ll_container);
        this.ll_main = (LinearLayout) findViewById(R.id.ll_main);
        this.sensorManager = (SensorManager) getSystemService("sensor");
        this.toDoList = new ToDoPojo();
        this.rowList = new ArrayList();
        this.constants = new Constants();
        try {
            setSupportActionBar(this.toolbar);
            this.toolbar.setNavigationIcon(R.drawable.back_top_bar_icon);
            getSupportActionBar().setTitle("");
        } catch (Exception e) {
            e.printStackTrace();
        }
        getToDoListFromFile();
        fillToDoDataInTableLayout();
    }

    public void getToDoListFromFile() {
        try {
            ToDoReadFromXML toDoReadFromXML = new ToDoReadFromXML();
            this.toDoList = toDoReadFromXML.ReadToDoList(StorageOptionsCommon.STORAGEPATH + StorageOptionsCommon.TODOLIST + Common.ToDoListName + StorageOptionsCommon.NOTES_FILE_EXTENSION);
            getSupportActionBar().setTitle(this.toDoList.getTitle());
            this.ll_main.setBackgroundColor(Color.parseColor(this.toDoList.getTodoColor()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void fillToDoDataInTableLayout() {
        ToDoPojo toDoPojo = this.toDoList;
        if (toDoPojo != null) {
            ArrayList<ToDoTask> toDoList = toDoPojo.getToDoList();
            int i = 0;
            while (i < toDoList.size()) {
                try {
                    ToDoCheckableRowPojo toDoCheckableRowPojo = new ToDoCheckableRowPojo();
                    TableRow tableRow = new TableRow(getApplicationContext());
                    TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(-1, -1, 1.0f);
                    tableRow.setLayoutParams(layoutParams);
                    View inflate = this.inflater.inflate(R.layout.tablerow_todo_view, (ViewGroup) null);
                    final TextView textView = (TextView) inflate.findViewById(R.id.tv_text);
                    final CheckBox checkBox = (CheckBox) inflate.findViewById(R.id.cb_done);
                    int i2 = i + 1;
                    ((TextView) inflate.findViewById(R.id.tv_rowNo)).setText(String.valueOf(i2));
                    checkBox.setChecked(toDoList.get(i).isChecked());
                    Utilities.strikeTextview(textView, toDoList.get(i).getToDo(), checkBox.isChecked());
                    tableRow.addView(inflate, layoutParams);
                    this.tl_TodoTasks.addView(tableRow);
                    toDoCheckableRowPojo.setCb_done(checkBox);
                    toDoCheckableRowPojo.setTv_text(textView);
                    this.rowList.add(toDoCheckableRowPojo);
                    checkBox.setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.todolist.ViewToDoActivity.1
                        @Override // android.view.View.OnClickListener
                        public void onClick(View view) {
                            boolean isChecked = checkBox.isChecked();
                            TextView textView2 = textView;
                            Utilities.strikeTextview(textView2, textView2.getText().toString(), isChecked);
                            ViewToDoActivity.this.isModified = true;
                        }
                    });
                    textView.setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.todolist.ViewToDoActivity.2
                        @Override // android.view.View.OnClickListener
                        public void onClick(View view) {
                            boolean isChecked = checkBox.isChecked();
                            checkBox.setChecked(!isChecked);
                            TextView textView2 = textView;
                            Utilities.strikeTextview(textView2, textView2.getText().toString(), !isChecked);
                            ViewToDoActivity.this.isModified = true;
                        }
                    });
                    i = i2;
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }

    public void SaveDataBeforeNavigation() {
        if (this.isModified) {
            ArrayList<ToDoTask> arrayList = new ArrayList<>();
            boolean z = true;
            for (int i = 0; i < this.rowList.size(); i++) {
                boolean isChecked = this.rowList.get(i).getCb_done().isChecked();
                ToDoTask toDoTask = new ToDoTask();
                toDoTask.setChecked(isChecked);
                toDoTask.setToDo(this.rowList.get(i).getTv_text().getText().toString());
                arrayList.add(toDoTask);
                if (!isChecked) {
                    z = false;
                }
            }
            if (arrayList.size() > 0) {
                String currentDateTime2 = Utilities.getCurrentDateTime2();
                this.toDoList.deleteTodoList();
                this.toDoList.setToDoList(arrayList);
                this.toDoList.setDateModified(currentDateTime2);
                ToDoWriteInXML toDoWriteInXML = new ToDoWriteInXML();
                ToDoPojo toDoPojo = this.toDoList;
                if (toDoWriteInXML.saveToDoList(this, toDoPojo, toDoPojo.getTitle(), true)) {
                    ToDoDAL toDoDAL = new ToDoDAL(this);
                    ToDoDB_Pojo toDoDB_Pojo = new ToDoDB_Pojo();
                    toDoDB_Pojo.setToDoFileModifiedDate(currentDateTime2);
                    toDoDB_Pojo.setToDoFileTask1(arrayList.get(0).getToDo());
                    toDoDB_Pojo.setToDoFileTask1IsChecked(arrayList.get(0).isChecked());
                    if (arrayList.size() >= 2) {
                        toDoDB_Pojo.setToDoFileTask2(arrayList.get(1).getToDo());
                        toDoDB_Pojo.setToDoFileTask2IsChecked(arrayList.get(1).isChecked());
                    }
                    toDoDB_Pojo.setToDoFinished(z);
                    this.constants.getClass();
                    toDoDAL.updateToDoFileTasksInDatabase(toDoDB_Pojo, "ToDoId", String.valueOf(Common.ToDoListId));
                }
            }
        }
    }

    public void deleteToDo() {
        final Dialog dialog = new Dialog(this, R.style.FullHeightDialog);
        dialog.setContentView(R.layout.confirmation_message_box);
        ((TextView) dialog.findViewById(R.id.tvmessagedialogtitle)).setText(R.string.delete_todo);
        ((LinearLayout) dialog.findViewById(R.id.ll_Ok)).setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.todolist.ViewToDoActivity.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                try {
                    dialog.dismiss();
                    File file = new File(StorageOptionsCommon.STORAGEPATH + StorageOptionsCommon.TODOLIST + Common.ToDoListName + StorageOptionsCommon.NOTES_FILE_EXTENSION);
                    if (file.exists() && file.delete()) {
                        new ToDoDAL(ViewToDoActivity.this).deleteToDoFileFromDatabase("ToDoId", String.valueOf(Common.ToDoListId));
                        SecurityLocksCommon.IsAppDeactive = false;
                        ViewToDoActivity.this.startActivity(new Intent(ViewToDoActivity.this, ToDoActivity.class));
                        ViewToDoActivity.this.finish();
                        ViewToDoActivity.this.overridePendingTransition(17432576, 17432577);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        ((LinearLayout) dialog.findViewById(R.id.ll_Cancel)).setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.todolist.ViewToDoActivity.4
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onBackPressed() {
        SaveDataBeforeNavigation();
        Common.ToDoListEdit = false;
        SecurityLocksCommon.IsAppDeactive = false;
        startActivity(new Intent(this, ToDoActivity.class));
        finish();
        overridePendingTransition(17432576, 17432577);
    }

    @Override // android.app.Activity
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_del, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override // android.app.Activity
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == 16908332) {
            SaveDataBeforeNavigation();
            Common.ToDoListEdit = false;
            SecurityLocksCommon.IsAppDeactive = false;
            startActivity(new Intent(this, ToDoActivity.class));
            finish();
            overridePendingTransition(17432576, 17432577);
        } else if (itemId == R.id.action_menu_del) {
            deleteToDo();
        } else if (itemId == R.id.action_menu_edit) {
            SaveDataBeforeNavigation();
            SecurityLocksCommon.IsAppDeactive = false;
            Common.ToDoListEdit = true;
            startActivity(new Intent(this, AddToDoActivity.class));
            finish();
            overridePendingTransition(17432576, 17432577);
        }
        return super.onOptionsItemSelected(menuItem);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onPause() {
        super.onPause();
        this.sensorManager.unregisterListener(this);
        if (AccelerometerManager.isListening()) {
            AccelerometerManager.stopListening();
        }
        if (SecurityLocksCommon.IsAppDeactive) {
            finish();
            System.exit(0);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onResume() {
        super.onResume();
        if (AccelerometerManager.isSupported(this)) {
            AccelerometerManager.startListening(this);
        }
        SensorManager sensorManager = this.sensorManager;
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(8), 3);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onStop() {
        super.onStop();
    }

    @Override // android.hardware.SensorEventListener
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == 8 && sensorEvent.values[0] == 0.0f && PanicSwitchCommon.IsPalmOnFaceOn) {
            PanicSwitchActivityMethods.SwitchApp(this);
        }
    }

    @Override // net.newsoftwares.hidepicturesvideos.panicswitch.AccelerometerListener
    public void onShake(float f) {
        if (PanicSwitchCommon.IsFlickOn || PanicSwitchCommon.IsShakeOn) {
            PanicSwitchActivityMethods.SwitchApp(this);
        }
    }
}
