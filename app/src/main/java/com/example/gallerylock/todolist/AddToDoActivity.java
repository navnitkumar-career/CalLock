package com.example.gallerylock.todolist;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.gallerylock.R;
import com.example.gallerylock.common.Constants;
import com.example.gallerylock.common.ValidationCommon;
import com.example.gallerylock.panicswitch.AccelerometerListener;
import com.example.gallerylock.panicswitch.AccelerometerManager;
import com.example.gallerylock.panicswitch.PanicSwitchActivityMethods;
import com.example.gallerylock.panicswitch.PanicSwitchCommon;
import com.example.gallerylock.securitylocks.SecurityLocksCommon;
import com.example.gallerylock.storageoption.StorageOptionsCommon;
import com.example.gallerylock.utilities.Common;
import com.example.gallerylock.utilities.Utilities;
import com.flask.colorpicker.ColorPickerView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes2.dex */
public class AddToDoActivity extends AppCompatActivity implements AccelerometerListener, SensorEventListener {
    Constants constants;
    ToDoDAL dal;
    EditText et_ToDoTitle;
    LayoutInflater inflater;
    LinearLayout ll_addTask;
    LinearLayout ll_anchor;
    LinearLayout ll_main;
    ToDoAddOnClickListeners onClickListener;
    List<ToDoRowViewsPojo> rowViewsList;
    private SensorManager sensorManager;
    TableLayout tl_TodoTasks;
    ToDoPojo toDo;
    Toolbar toolbar;
    String toDoTitle = "";
    String toDoColor = "#33aac0ff";
    String tempTitle = "";
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
        setContentView(R.layout.activity_todo_add);
        getWindow().addFlags(128);
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.tl_TodoTasks = (TableLayout) findViewById(R.id.tl_TodoTasks);
        this.ll_addTask = (LinearLayout) findViewById(R.id.ll_addTask);
        this.ll_anchor = (LinearLayout) findViewById(R.id.ll_anchor);
        this.ll_main = (LinearLayout) findViewById(R.id.ll_main);
        this.sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        this.et_ToDoTitle = (EditText) findViewById(R.id.et_ToDoTitle);
        this.dal = new ToDoDAL(this);
        this.rowViewsList = new ArrayList();
        this.onClickListener = new ToDoAddOnClickListeners();
        this.toDo = new ToDoPojo();
        this.constants = new Constants();
        SecurityLocksCommon.IsAppDeactive = true;
        try {
            setSupportActionBar(this.toolbar);
            this.toolbar.setNavigationIcon(R.drawable.back_top_bar_icon);
            getSupportActionBar().setTitle("");
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.ll_addTask.setOnClickListener(this.onClickListener);
        try {
            if (Common.ToDoListEdit) {
                fillDataInToDoList();
            } else {
                addNewRow("", false);
                ToDoDAL toDoDAL = this.dal;
                this.constants.getClass();
                String str = "To do " + (toDoDAL.GetToDoDbFileIntegerEntity("SELECT \t    COUNT(*)  \t\t\t\t\t\t   FROM  TableToDo") + 1) + "";
                this.tempTitle = str;
                this.et_ToDoTitle.setHint(str);
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    public void fillDataInToDoList() {
        ToDoReadFromXML toDoReadFromXML = new ToDoReadFromXML();
        ToDoPojo ReadToDoList = toDoReadFromXML.ReadToDoList(StorageOptionsCommon.STORAGEPATH + StorageOptionsCommon.TODOLIST + Common.ToDoListName + StorageOptionsCommon.NOTES_FILE_EXTENSION);
        this.toDo = ReadToDoList;
        if (ReadToDoList != null) {
            try {
                this.et_ToDoTitle.setText(ReadToDoList.getTitle());
                this.toDoColor = this.toDo.getTodoColor();
                Iterator<ToDoTask> it = this.toDo.getToDoList().iterator();
                while (it.hasNext()) {
                    ToDoTask next = it.next();
                    addNewRow(next.getToDo(), next.isChecked());
                }
                this.ll_main.setBackgroundColor(Color.parseColor(this.toDoColor));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void addNewRow(String str, boolean z) {
        TableRow tableRow = new TableRow(getApplicationContext());
        ToDoRowViewsPojo toDoRowViewsPojo = new ToDoRowViewsPojo();
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(-1, -1, 1.0f);
        tableRow.setLayoutParams(layoutParams);
        View inflate = this.inflater.inflate(R.layout.tablerow_todo_add, (ViewGroup) null);
        tableRow.addView(inflate, layoutParams);
        this.tl_TodoTasks.addView(tableRow);
        ImageView imageView = (ImageView) inflate.findViewById(R.id.iv_RowUp);
        ImageView imageView2 = (ImageView) inflate.findViewById(R.id.iv_RowDown);
        ImageView imageView3 = (ImageView) inflate.findViewById(R.id.iv_RowDel);
        InputFilter[] inputFilterArr = {new InputFilter.LengthFilter(30)};
        EditText editText = (EditText) inflate.findViewById(R.id.et_text);
        editText.requestFocus();
        editText.setInputType(65536);
        editText.setImeOptions(5);
        editText.setFilters(inputFilterArr);
        imageView.setOnClickListener(this.onClickListener);
        imageView2.setOnClickListener(this.onClickListener);
        imageView3.setOnClickListener(this.onClickListener);
        editText.setOnClickListener(this.onClickListener);
        toDoRowViewsPojo.setEt_text(editText);
        toDoRowViewsPojo.setIv_rowUp(imageView);
        toDoRowViewsPojo.setIv_rowDown(imageView2);
        toDoRowViewsPojo.setIv_rowDel(imageView3);
        if (Common.ToDoListEdit) {
            editText.setText(str);
            toDoRowViewsPojo.setChecked(z);
        }
        this.rowViewsList.add(toDoRowViewsPojo);
    }

    public TableLayout swapDownTableRow(View view) {
        TableLayout tableLayout = this.tl_TodoTasks;
        TableRow tableRow = (TableRow) ((LinearLayout) ((LinearLayout) view.getParent()).getParent()).getParent();
        int indexOfChild = tableLayout.indexOfChild(tableRow);
        int i = indexOfChild + 1;
        TableRow tableRow2 = (TableRow) tableLayout.getChildAt(i);
        if (indexOfChild != this.tl_TodoTasks.getChildCount() - 1) {
            try {
                tableLayout.removeView(tableRow);
                tableLayout.addView(tableRow, i);
                tableRow.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_row_up));
                tableLayout.removeView(tableRow2);
                tableLayout.addView(tableRow2, indexOfChild);
                tableRow2.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_row_down));
                return tableLayout;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                this.rowViewsList.remove(indexOfChild);
                this.rowViewsList.add(i, this.rowViewsList.get(indexOfChild));
            }
        }
        return this.tl_TodoTasks;
    }

    public TableLayout swapUpTableRow(View view) {
        TableLayout tableLayout = this.tl_TodoTasks;
        TableRow tableRow = (TableRow) ((LinearLayout) ((LinearLayout) view.getParent()).getParent()).getParent();
        int indexOfChild = tableLayout.indexOfChild(tableRow);
        int i = indexOfChild - 1;
        TableRow tableRow2 = (TableRow) tableLayout.getChildAt(i);
        if (indexOfChild > 0) {
            try {
                tableLayout.removeView(tableRow);
                tableLayout.addView(tableRow, i);
                tableRow.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_row_down));
                tableLayout.removeView(tableRow2);
                tableLayout.addView(tableRow2, indexOfChild);
                tableRow2.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_row_up));
                return tableLayout;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                this.rowViewsList.remove(indexOfChild);
                this.rowViewsList.add(i, this.rowViewsList.get(indexOfChild));
            }
        }
        return this.tl_TodoTasks;
    }

    public boolean SaveToDoList() {
        boolean z;
        String currentDateTime2 = Utilities.getCurrentDateTime2();
        String title = this.toDo.getTitle();
        this.toDo.setTitle(this.toDoTitle);
        this.toDo.setTodoColor(this.toDoColor);
        this.toDo.setDateModified(currentDateTime2);
        ArrayList<ToDoTask> arrayList = new ArrayList<>();
        for (ToDoRowViewsPojo toDoRowViewsPojo : this.rowViewsList) {
            ToDoTask toDoTask = new ToDoTask();
            toDoTask.setToDo(toDoRowViewsPojo.getEt_text().getText().toString());
            toDoTask.setChecked(toDoRowViewsPojo.isChecked());
            arrayList.add(toDoTask);
            if (toDoRowViewsPojo.getEt_text().getText().toString().trim().equals("")) {
                Toast.makeText(this, "Can't save empty field", 0).show();
                Utilities.hideKeyboard(this.ll_addTask, this);
                return false;
            }
        }
        this.toDo.setToDoList(arrayList);
        ToDoWriteInXML toDoWriteInXML = new ToDoWriteInXML();
        if (Common.ToDoListEdit) {
            z = toDoWriteInXML.saveToDoList(this, this.toDo, title, Common.ToDoListEdit);
        } else {
            this.toDo.setDateCreated(currentDateTime2);
            z = toDoWriteInXML.saveToDoList(this, this.toDo, this.toDoTitle, Common.ToDoListEdit);
        }
        if (z) {
            ToDoDB_Pojo toDoDB_Pojo = new ToDoDB_Pojo();
            toDoDB_Pojo.setToDoFileName(this.toDoTitle);
            toDoDB_Pojo.setToDoFileLocation(StorageOptionsCommon.STORAGEPATH + StorageOptionsCommon.TODOLIST + this.toDoTitle + StorageOptionsCommon.NOTES_FILE_EXTENSION);
            toDoDB_Pojo.setToDoFileColor(this.toDoColor);
            toDoDB_Pojo.setToDoFileCreatedDate(currentDateTime2);
            toDoDB_Pojo.setToDoFileModifiedDate(currentDateTime2);
            toDoDB_Pojo.setToDoFileIsDecoy(SecurityLocksCommon.IsFakeAccount);
            toDoDB_Pojo.setToDoFileTask1(arrayList.get(0).getToDo());
            toDoDB_Pojo.setToDoFileTask1IsChecked(arrayList.get(0).isChecked());
            if (arrayList.size() >= 2) {
                toDoDB_Pojo.setToDoFileTask2(arrayList.get(1).getToDo());
                toDoDB_Pojo.setToDoFileTask2IsChecked(arrayList.get(1).isChecked());
            }
            if (Common.ToDoListEdit) {
                ToDoDAL toDoDAL = this.dal;
                this.constants.getClass();
                toDoDAL.updateToDoFileInfoInDatabase(toDoDB_Pojo, "ToDoId", String.valueOf(Common.ToDoListId));
            } else {
                this.dal.addToDoInfoInDatabase(toDoDB_Pojo);
            }
            ToDoDAL toDoDAL2 = this.dal;
            StringBuilder sb = new StringBuilder();
            this.constants.getClass();
            sb.append("SELECT \t     * \t\t\t\t\t\t   FROM  TableToDo WHERE ");
            this.constants.getClass();
            sb.append("ToDoName");
            sb.append("='");
            sb.append(this.toDoTitle);
            sb.append("'");
            Common.ToDoListId = toDoDAL2.GetToDoDbFileIntegerEntity(sb.toString());
            if (Common.ToDoListEdit) {
                Common.ToDoListEdit = false;
                SecurityLocksCommon.IsAppDeactive = false;
                startActivity(new Intent(this, ViewToDoActivity.class));
                finish();
                overridePendingTransition(17432576, 17432577);
            } else {
                SecurityLocksCommon.IsAppDeactive = false;
                startActivity(new Intent(this, ToDoActivity.class));
                finish();
                overridePendingTransition(17432576, 17432577);
            }
        } else {
            Toast.makeText(this, "Failed to Save ToDo", 0).show();
        }
        return true;
    }

    public void setToDoColor() {
        final Dialog dialog = new Dialog(this, R.style.FullHeightDialog);
        dialog.setContentView(R.layout.dialog_note_color_picker);
        final ColorPickerView colorPickerView = (ColorPickerView) dialog.findViewById(R.id.color_picker_view);
        colorPickerView.setAlpha(0.3f);
        colorPickerView.setDensity(4);
        ((TextView) dialog.findViewById(R.id.tv_no)).setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.todolist.AddToDoActivity.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        ((TextView) dialog.findViewById(R.id.tv_yes)).setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.todolist.AddToDoActivity.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                int selectedColor = 0;
                try {
                    Log.i("color", String.valueOf(colorPickerView.getSelectedColor()));
                    String str = "#33" + Integer.toHexString(selectedColor).substring(2);
                    Log.i("scolor", str);
                    AddToDoActivity.this.ll_main.setBackgroundColor(Color.parseColor(str));
                    AddToDoActivity.this.toDoColor = str;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void showDiscardDialog() {
        final Dialog dialog = new Dialog(this, R.style.FullHeightDialog);
        dialog.setContentView(R.layout.confirmation_message_box);
        ((TextView) dialog.findViewById(R.id.tvmessagedialogtitle)).setText(R.string.discard_changes);
        ((LinearLayout) dialog.findViewById(R.id.ll_Ok)).setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.todolist.AddToDoActivity.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (Common.ToDoListEdit) {
                    Common.ToDoListEdit = false;
                    SecurityLocksCommon.IsAppDeactive = false;
                    AddToDoActivity.this.startActivity(new Intent(AddToDoActivity.this, ViewToDoActivity.class));
                    AddToDoActivity.this.finish();
                    AddToDoActivity.this.overridePendingTransition(17432576, 17432577);
                } else {
                    SecurityLocksCommon.IsAppDeactive = false;
                    AddToDoActivity.this.startActivity(new Intent(AddToDoActivity.this, ToDoActivity.class));
                    AddToDoActivity.this.finish();
                    AddToDoActivity.this.overridePendingTransition(17432576, 17432577);
                }
                dialog.cancel();
            }
        });
        ((LinearLayout) dialog.findViewById(R.id.ll_Cancel)).setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.todolist.AddToDoActivity.4
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override // android.app.Activity
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_color, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override // android.app.Activity
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case 16908332:
                if (!this.hasModified) {
                    if (!Common.ToDoListEdit) {
                        SecurityLocksCommon.IsAppDeactive = false;
                        startActivity(new Intent(this, ToDoActivity.class));
                        finish();
                        overridePendingTransition(17432576, 17432577);
                        break;
                    } else {
                        Common.ToDoListEdit = false;
                        SecurityLocksCommon.IsAppDeactive = false;
                        startActivity(new Intent(this, ViewToDoActivity.class));
                        finish();
                        overridePendingTransition(17432576, 17432577);
                        break;
                    }
                } else {
                    showDiscardDialog();
                    break;
                }
            case R.id.action_menu_add /* 2131296322 */:
                String trim = this.et_ToDoTitle.getText().toString().trim();
                this.toDoTitle = trim;
                if (trim.trim().equals("")) {
                    this.toDoTitle = this.tempTitle;
                }
                if (!ValidationCommon.isNoSpecialCharsInNameExceptBrackets(this.toDoTitle)) {
                    Toast.makeText(this, "Todo name is incorrect", 0).show();
                    break;
                } else {
                    Common.ToDoListName = this.toDoTitle;
                    SaveToDoList();
                    break;
                }
            case R.id.action_menu_color /* 2131296323 */:
                setToDoColor();
                break;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onBackPressed() {
        if (this.hasModified) {
            showDiscardDialog();
        } else if (Common.ToDoListEdit) {
            Common.ToDoListEdit = false;
            SecurityLocksCommon.IsAppDeactive = false;
            startActivity(new Intent(this, ViewToDoActivity.class));
            finish();
        } else {
            SecurityLocksCommon.IsAppDeactive = false;
            startActivity(new Intent(this, ToDoActivity.class));
            finish();
            overridePendingTransition(17432576, 17432577);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public class ToDoAddOnClickListeners implements View.OnClickListener {
        private ToDoAddOnClickListeners() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            int id = view.getId();
            if (id != R.id.ll_addTask) {
                switch (id) {
                    case R.id.iv_RowDel /* 2131296572 */:
                        if (AddToDoActivity.this.rowViewsList.size() != 1) {
                            TableRow tableRow = (TableRow) ((LinearLayout) ((LinearLayout) view.getParent()).getParent()).getParent();
                            try {
                                AddToDoActivity.this.rowViewsList.remove(AddToDoActivity.this.tl_TodoTasks.indexOfChild(tableRow));
                                AddToDoActivity.this.tl_TodoTasks.removeView(tableRow);
                                AddToDoActivity.this.tl_TodoTasks.requestLayout();
                                AddToDoActivity.this.hasModified = true;
                                return;
                            } catch (Exception e) {
                                e.printStackTrace();
                                return;
                            }
                        } else {
                            return;
                        }
                    case R.id.iv_RowDown /* 2131296573 */:
                        view.startAnimation(AnimationUtils.loadAnimation(AddToDoActivity.this.getApplicationContext(), R.anim.anim_pulse));
                        AddToDoActivity addToDoActivity = AddToDoActivity.this;
                        addToDoActivity.tl_TodoTasks = addToDoActivity.swapDownTableRow(view);
                        AddToDoActivity.this.hasModified = true;
                        return;
                    case R.id.iv_RowUp /* 2131296574 */:
                        view.startAnimation(AnimationUtils.loadAnimation(AddToDoActivity.this.getApplicationContext(), R.anim.anim_pulse));
                        AddToDoActivity addToDoActivity2 = AddToDoActivity.this;
                        addToDoActivity2.tl_TodoTasks = addToDoActivity2.swapUpTableRow(view);
                        AddToDoActivity.this.hasModified = true;
                        return;
                    default:
                        return;
                }
            } else {
                AddToDoActivity.this.addNewRow("", false);
                AddToDoActivity.this.hasModified = true;
            }
        }
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
