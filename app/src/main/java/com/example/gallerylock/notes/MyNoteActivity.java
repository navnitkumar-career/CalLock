package com.example.gallerylock.notes;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.gallerylock.R;
import com.example.gallerylock.common.Constants;
import com.example.gallerylock.panicswitch.AccelerometerListener;
import com.example.gallerylock.panicswitch.AccelerometerManager;
import com.example.gallerylock.panicswitch.PanicSwitchActivityMethods;
import com.example.gallerylock.panicswitch.PanicSwitchCommon;
import com.example.gallerylock.securitylocks.SecurityLocksCommon;
import com.example.gallerylock.storageoption.StorageOptionsCommon;
import com.example.gallerylock.utilities.Utilities;
import com.fasterxml.jackson.core.util.MinimalPrettyPrinter;
import com.flask.colorpicker.ColorPickerView;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import pub.devrel.easypermissions.PermissionRequest;

/* loaded from: classes2.dex */
public class MyNoteActivity extends AppCompatActivity implements AccelerometerListener, SensorEventListener, EasyPermissions.PermissionCallbacks {
    String NotesContent;
    HashMap<String, String> NotesHashMap;
    AddNoteListeners addNoteListeners;
    AudioRecorder audioRecorder;
    Chronometer chronometer;
    Constants constants;
    long currentDuration;
    LinedEditText et_NoteContent;
    EditText et_Notetitle;
    ImageView iv_NotesRecordAudio;
    ImageView iv_NotesplayAudio;
    ImageView iv_next;
    ImageView iv_play;
    ImageView iv_previous;
    ScrollView ll_main;
    LinearLayout ll_notesRecordingPlayer;
    Handler mHandler;
    MediaPlayer mPlayer;
    NotesCommon notesCommon;
    NotesFilesDAL notesFilesDAL;
    String notesTitle;
    ReadNoteFromXML readNoteFromXML;
    SeekBar seekbar;
    private SensorManager sensorManager;
    private Toolbar toolbar;
    long totalDuration;
    TextView tv_recEndTime;
    TextView tv_recStartTime;
    String oldNoteTitle = "";
    boolean isRecording = false;
    boolean hasRecorded = false;
    boolean isPlaying = false;
    boolean isPlayerVisible = false;
    boolean hasInsertedLines = false;
    boolean hasModified = false;
    String recordingFilePath = "";
    String audioString = "";
    String noteColor = "#33aac0ff";
    ProgressDialog myProgressDialog = null;
    String[] PERMISSIONS = {"android.permission.RECORD_AUDIO"};
    Handler handle = new Handler() { // from class: net.newsoftwares.hidepicturesvideos.notes.MyNoteActivity.1
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            if (message.what == 1) {
                MyNoteActivity.this.et_Notetitle.setText(MyNoteActivity.this.NotesHashMap.get("Title"));
                MyNoteActivity.this.et_NoteContent.setText(MyNoteActivity.this.NotesHashMap.get("Text"));
                if (MyNoteActivity.this.audioString.length() > 0) {
                    MyNoteActivity.this.iv_NotesplayAudio.setVisibility(0);
                }
                MyNoteActivity.this.hideProgress();
            } else if (message.what == 2) {
                MyNoteActivity.this.hideProgress();
            } else if (message.what == 3) {
                MyNoteActivity myNoteActivity = MyNoteActivity.this;
                Toast.makeText(myNoteActivity, myNoteActivity.getResources().getString(R.string.note_file_exists), 0).show();
                Utilities.hideKeyboard(MyNoteActivity.this.ll_main, MyNoteActivity.this);
            }
            super.handleMessage(message);
        }
    };
    private Runnable mUpdateTimeTask = new Runnable() { // from class: net.newsoftwares.hidepicturesvideos.notes.MyNoteActivity.11
        @Override // java.lang.Runnable
        public void run() {
            MyNoteActivity myNoteActivity = MyNoteActivity.this;
            myNoteActivity.totalDuration = myNoteActivity.mPlayer.getDuration();
            MyNoteActivity myNoteActivity2 = MyNoteActivity.this;
            myNoteActivity2.currentDuration = myNoteActivity2.mPlayer.getCurrentPosition();
            MyNoteActivity.this.seekbar.setProgress(MyNoteActivity.this.notesCommon.getProgressPercentage(MyNoteActivity.this.currentDuration, MyNoteActivity.this.totalDuration));
            TextView textView = MyNoteActivity.this.tv_recEndTime;
            textView.setText("" + MyNoteActivity.this.notesCommon.milliSecondsToTimer(MyNoteActivity.this.totalDuration));
            TextView textView2 = MyNoteActivity.this.tv_recStartTime;
            textView2.setText("" + MyNoteActivity.this.notesCommon.milliSecondsToTimer(MyNoteActivity.this.currentDuration));
            MyNoteActivity.this.mHandler.postDelayed(this, 100L);
        }
    };

    @Override // net.newsoftwares.hidepicturesvideos.panicswitch.AccelerometerListener
    public void onAccelerationChanged(float f, float f2, float f3) {
    }

    @Override // android.hardware.SensorEventListener
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    @Override // pub.devrel.easypermissions.EasyPermissions.PermissionCallbacks
    public void onPermissionsGranted(int i, List<String> list) {
    }

    private void showProgress() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        this.myProgressDialog = progressDialog;
        progressDialog.setTitle(getResources().getString(R.string.please_wait));
        this.myProgressDialog.setMessage(getResources().getString(R.string.processing));
        this.myProgressDialog.show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void hideProgress() {
        ProgressDialog progressDialog = this.myProgressDialog;
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Type inference failed for: r4v69, types: [net.newsoftwares.hidepicturesvideos.notes.MyNoteActivity$2] */
    @Override
    // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_my_note);
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.et_Notetitle = (EditText) findViewById(R.id.et_Notetitle);
        this.et_NoteContent = (LinedEditText) findViewById(R.id.et_NoteContent);
        this.tv_recStartTime = (TextView) findViewById(R.id.tv_recStartTime);
        this.tv_recEndTime = (TextView) findViewById(R.id.tv_recEndTime);
        this.iv_NotesplayAudio = (ImageView) findViewById(R.id.iv_NotesplayAudio);
        this.iv_NotesRecordAudio = (ImageView) findViewById(R.id.iv_NotesRecordAudio);
        this.iv_previous = (ImageView) findViewById(R.id.iv_previous);
        this.iv_play = (ImageView) findViewById(R.id.iv_play);
        this.iv_next = (ImageView) findViewById(R.id.iv_next);
        this.seekbar = (SeekBar) findViewById(R.id.seekbar);
        this.ll_notesRecordingPlayer = (LinearLayout) findViewById(R.id.ll_notesRecordingPlayer);
        this.chronometer = (Chronometer) findViewById(R.id.chronometer);
        this.sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        this.addNoteListeners = new AddNoteListeners();
        this.NotesHashMap = new HashMap<>();
        this.readNoteFromXML = new ReadNoteFromXML();
        this.notesCommon = new NotesCommon();
        this.audioRecorder = new AudioRecorder(this);
        this.mHandler = new Handler();
        this.ll_main = (ScrollView) findViewById(R.id.ll_main);
        this.notesFilesDAL = new NotesFilesDAL(this);
        this.constants = new Constants();
        setSupportActionBar(this.toolbar);
        this.toolbar.setBackgroundColor(getResources().getColor(R.color.ColorAppTheme));
        this.toolbar.setNavigationIcon(R.drawable.ic_top_back_icon);
        SecurityLocksCommon.IsAppDeactive = true;
        this.iv_NotesplayAudio.setVisibility(8);
        ImageView imageView = this.iv_NotesplayAudio;
        AddNoteListeners addNoteListeners = this.addNoteListeners;
        addNoteListeners.getClass();
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((InputMethodManager) MyNoteActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(MyNoteActivity.this.et_NoteContent.getWindowToken(), 0);
                if (!MyNoteActivity.this.audioRecorder.hasRecording) {
                    Toast.makeText(MyNoteActivity.this, "No recording to play", 0).show();
                    Utilities.hideKeyboard(MyNoteActivity.this.ll_main, MyNoteActivity.this);
                } else if (MyNoteActivity.this.isPlaying || MyNoteActivity.this.isPlayerVisible) {
                    MyNoteActivity.this.ll_notesRecordingPlayer.setVisibility(8);
                    MyNoteActivity.this.isPlayerVisible = false;
                    MyNoteActivity.this.isPlaying = false;
                    MyNoteActivity.this.mPlayer.stop();
                    MyNoteActivity.this.mPlayer.release();
                    MyNoteActivity.this.mHandler.removeCallbacks(MyNoteActivity.this.mUpdateTimeTask);
                } else {
                    try {
                        MyNoteActivity.this.isPlaying = true;
                        MyNoteActivity.this.iv_play.setBackgroundResource(R.drawable.pause);
                        MyNoteActivity.this.ll_notesRecordingPlayer.setVisibility(0);
                        MyNoteActivity.this.isPlayerVisible = true;
                        MyNoteActivity.this.mPlayer = new MediaPlayer();
                        MyNoteActivity.this.mPlayer.setDataSource(MyNoteActivity.this.recordingFilePath);
                        MyNoteActivity.this.mPlayer.prepare();
                        MyNoteActivity.this.mPlayer.start();
                        MediaPlayer mediaPlayer = MyNoteActivity.this.mPlayer;
                        AddNoteListeners addNoteListeners = MyNoteActivity.this.addNoteListeners;
                        addNoteListeners.getClass();
                        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mediaPlayer) {
                                MyNoteActivity.this.isPlaying = false;
                                MyNoteActivity.this.iv_play.setBackgroundResource(R.drawable.ic_play_circle_outline_black_24dp);
                            }
                        });
                        MyNoteActivity.this.seekbar.setProgress(0);
                        MyNoteActivity.this.seekbar.setMax(100);
                        MyNoteActivity.this.updateProgressBar();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        ImageView imageView2 = this.iv_NotesRecordAudio;
        AddNoteListeners addNoteListeners2 = this.addNoteListeners;
        addNoteListeners2.getClass();
        imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((InputMethodManager) MyNoteActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(MyNoteActivity.this.et_NoteContent.getWindowToken(), 0);
                if (!MyNoteActivity.this.audioRecorder.hasRecording || MyNoteActivity.this.isRecording) {
                    MyNoteActivity.this.RecordOrStop();
                } else {
                    MyNoteActivity.this.showRecordingOverrideDialog();
                }
            }
        });
        ImageView imageView3 = this.iv_play;
        AddNoteListeners addNoteListeners3 = this.addNoteListeners;
        addNoteListeners3.getClass();
        imageView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.iv_next /* 2131296589 */:
                        MyNoteActivity.this.mPlayer.seekTo(MyNoteActivity.this.notesCommon.progressToTimer(MyNoteActivity.this.seekbar.getProgress(), MyNoteActivity.this.mPlayer.getDuration()) + 2000);
                        MyNoteActivity.this.updateProgressBar();
                        return;
                    case R.id.iv_notesFolder /* 2131296590 */:
                    default:
                        return;
                    case R.id.iv_play /* 2131296591 */:
                        MyNoteActivity.this.PlayPause();
                        return;
                    case R.id.iv_previous /* 2131296592 */:
                        MyNoteActivity.this.mPlayer.seekTo(MyNoteActivity.this.notesCommon.progressToTimer(MyNoteActivity.this.seekbar.getProgress(), MyNoteActivity.this.mPlayer.getDuration()) + NotificationManagerCompat.IMPORTANCE_UNSPECIFIED);
                        MyNoteActivity.this.updateProgressBar();
                        return;
                }
            }
        });
        ImageView imageView4 = this.iv_next;
        AddNoteListeners addNoteListeners4 = this.addNoteListeners;
        addNoteListeners4.getClass();
        imageView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.iv_next /* 2131296589 */:
                        MyNoteActivity.this.mPlayer.seekTo(MyNoteActivity.this.notesCommon.progressToTimer(MyNoteActivity.this.seekbar.getProgress(), MyNoteActivity.this.mPlayer.getDuration()) + 2000);
                        MyNoteActivity.this.updateProgressBar();
                        return;
                    case R.id.iv_notesFolder /* 2131296590 */:
                    default:
                        return;
                    case R.id.iv_play /* 2131296591 */:
                        MyNoteActivity.this.PlayPause();
                        return;
                    case R.id.iv_previous /* 2131296592 */:
                        MyNoteActivity.this.mPlayer.seekTo(MyNoteActivity.this.notesCommon.progressToTimer(MyNoteActivity.this.seekbar.getProgress(), MyNoteActivity.this.mPlayer.getDuration()) + NotificationManagerCompat.IMPORTANCE_UNSPECIFIED);
                        MyNoteActivity.this.updateProgressBar();
                        return;
                }
            }
        });
        ImageView imageView5 = this.iv_previous;
        AddNoteListeners addNoteListeners5 = this.addNoteListeners;
        addNoteListeners5.getClass();
        imageView5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.iv_next /* 2131296589 */:
                        MyNoteActivity.this.mPlayer.seekTo(MyNoteActivity.this.notesCommon.progressToTimer(MyNoteActivity.this.seekbar.getProgress(), MyNoteActivity.this.mPlayer.getDuration()) + 2000);
                        MyNoteActivity.this.updateProgressBar();
                        return;
                    case R.id.iv_notesFolder /* 2131296590 */:
                    default:
                        return;
                    case R.id.iv_play /* 2131296591 */:
                        MyNoteActivity.this.PlayPause();
                        return;
                    case R.id.iv_previous /* 2131296592 */:
                        MyNoteActivity.this.mPlayer.seekTo(MyNoteActivity.this.notesCommon.progressToTimer(MyNoteActivity.this.seekbar.getProgress(), MyNoteActivity.this.mPlayer.getDuration()) + NotificationManagerCompat.IMPORTANCE_UNSPECIFIED);
                        MyNoteActivity.this.updateProgressBar();
                        return;
                }
            }
        });
        SeekBar seekBar = this.seekbar;
        AddNoteListeners addNoteListeners6 = this.addNoteListeners;
        addNoteListeners6.getClass();
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStartTrackingTouch(SeekBar seekBar) {
                MyNoteActivity.this.mHandler.removeCallbacks(MyNoteActivity.this.mUpdateTimeTask);
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStopTrackingTouch(SeekBar seekBar) {
                MyNoteActivity.this.mHandler.removeCallbacks(MyNoteActivity.this.mUpdateTimeTask);
                MyNoteActivity.this.mPlayer.seekTo(MyNoteActivity.this.notesCommon.progressToTimer(seekBar.getProgress(), MyNoteActivity.this.mPlayer.getDuration()));
                MyNoteActivity.this.updateProgressBar();
            }
        });
        if (NotesCommon.isEdittingNote) {
            showProgress();
            new Thread() { // from class: net.newsoftwares.hidepicturesvideos.notes.MyNoteActivity.2
                @Override // java.lang.Thread, java.lang.Runnable
                public void run() {
                    try {
                        MyNoteActivity.this.initViewNote();
                        Message message = new Message();
                        message.what = 1;
                        MyNoteActivity.this.handle.sendMessage(message);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        } else {
            initAddNote();
        }
        new Handler().postDelayed(new Runnable() { // from class: net.newsoftwares.hidepicturesvideos.notes.MyNoteActivity.3
            @Override // java.lang.Runnable
            public void run() {
                try {
                    LinedEditText linedEditText = MyNoteActivity.this.et_NoteContent;
                    AddNoteListeners addNoteListeners7 = MyNoteActivity.this.addNoteListeners;
                    addNoteListeners7.getClass();
                    linedEditText.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void afterTextChanged(Editable editable) {
                            MyNoteActivity.this.hasModified = true;
                            Log.i("hasModified", "true in textwatch");
                        }
                    });
                    EditText editText = MyNoteActivity.this.et_Notetitle;
                    AddNoteListeners addNoteListeners8 = MyNoteActivity.this.addNoteListeners;
                    addNoteListeners8.getClass();
                    editText.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void afterTextChanged(Editable editable) {
                            MyNoteActivity.this.hasModified = true;
                            Log.i("hasModified", "true in textwatch");
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 500L);
        requestPermission(this.PERMISSIONS);
    }

    public void initAddNote() {
        this.et_NoteContent = this.notesCommon.setEditTextFullPage(this.et_NoteContent);
        this.audioRecorder.hasRecording = false;
    }

    public void initViewNote() {
        ReadNoteFromXML readNoteFromXML = this.readNoteFromXML;
        HashMap<String, String> ReadNote = readNoteFromXML.ReadNote(StorageOptionsCommon.STORAGEPATH + StorageOptionsCommon.NOTES + NotesCommon.CurrentNotesFolder + File.separator + NotesCommon.CurrentNotesFile + StorageOptionsCommon.NOTES_FILE_EXTENSION);
        this.NotesHashMap = ReadNote;
        this.audioString = ReadNote.get("audioData");
        this.oldNoteTitle = this.NotesHashMap.get("Title");
        if (this.audioString.length() > 0) {
            this.audioRecorder.createRecordingFolder();
            this.audioRecorder.createFirstRecording();
            String absolutePath = this.audioRecorder.firstRecordingFile.getAbsolutePath();
            this.notesCommon.getDecodedAudio(this.audioString, absolutePath);
            this.recordingFilePath = absolutePath;
            this.audioRecorder.hasRecording = true;
            this.iv_NotesplayAudio.setVisibility(0);
        }
        try {
            this.ll_main.setBackgroundColor(Color.parseColor(this.NotesHashMap.get("NoteColor")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void RecordOrStop() {
        if (!this.isRecording) {
            this.isRecording = true;
            this.iv_NotesRecordAudio.setImageResource(R.drawable.recorder_active_icon);
            this.audioRecorder.StartRecording();
            this.chronometer.setBase(SystemClock.elapsedRealtime());
            this.chronometer.setVisibility(0);
            this.chronometer.start();
            Toast.makeText(this, getResources().getString(R.string.recording_started), 0).show();
            return;
        }
        this.isRecording = false;
        this.hasModified = true;
        this.iv_NotesRecordAudio.setImageResource(R.drawable.ic_recorder_icon);
        this.recordingFilePath = this.audioRecorder.StopRecording();
        this.chronometer.stop();
        this.chronometer.setVisibility(4);
        this.iv_NotesplayAudio.clearAnimation();
        this.iv_NotesplayAudio.setVisibility(0);
        Toast.makeText(this, getResources().getString(R.string.recording_stoped), 0).show();
        this.iv_NotesplayAudio.startAnimation(AnimationUtils.loadAnimation(this, R.anim.anim_bounce));
    }

    public void DeleteExistingRecording() {
        File file = new File(this.recordingFilePath);
        if (file.exists() ? file.delete() : false) {
            this.audioRecorder.hasRecording = false;
            this.recordingFilePath = "";
            this.iv_NotesplayAudio.clearAnimation();
            this.iv_NotesplayAudio.setVisibility(8);
            RecordOrStop();
            return;
        }
        Toast.makeText(this, getResources().getString(R.string.recording_not_deleted), 0).show();
    }

    @Override // android.app.Activity
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_color, menu);
        this.toolbar.setTitle("");
        return super.onCreateOptionsMenu(menu);
    }

    /* JADX WARN: Type inference failed for: r1v4, types: [net.newsoftwares.hidepicturesvideos.notes.MyNoteActivity$4] */
    @Override // android.app.Activity
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case 16908332:
                if (!this.audioRecorder.hasRecording || !this.isPlaying || !this.isPlayerVisible) {
                    if (!this.isRecording && !this.hasModified) {
                        SecurityLocksCommon.IsAppDeactive = false;
                        startActivity(new Intent(this, NotesFilesActivity.class));
                        finish();
                        overridePendingTransition(17432576, 17432577);
                        break;
                    } else {
                        showDiscardDialog();
                        break;
                    }
                } else {
                    this.ll_notesRecordingPlayer.setVisibility(8);
                    this.isPlayerVisible = false;
                    this.isPlaying = false;
                    this.mPlayer.stop();
                    this.mPlayer.release();
                    this.mHandler.removeCallbacks(this.mUpdateTimeTask);
                    break;
                }
            case R.id.action_menu_add /* 2131296322 */:
                this.NotesContent = this.et_NoteContent.getText().toString();
                String trim = this.et_Notetitle.getText().toString().trim();
                this.notesTitle = trim;
                if (!trim.trim().equals("") && !this.notesCommon.hasNoData(this.NotesContent)) {
                    final String encodedAudio = this.notesCommon.getEncodedAudio(this.recordingFilePath);
                    final String currentDate = this.notesCommon.getCurrentDate();
                    if (this.notesTitle.equals("")) {
                        String[] split = this.NotesContent.trim().replaceAll("[!?,]", "").split("\\s+");
                        if (split.length > 0) {
                            this.notesTitle = split[0];
                        }
                        if (split.length > 1) {
                            String str = this.notesTitle;
                            this.notesTitle = str.concat(MinimalPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR + split[1]);
                        }
                    }
                    if (!this.notesTitle.equals("") && this.notesCommon.isNoSpecialCharsInName(this.notesTitle)) {
                        showProgress();
                        new Thread() { // from class: net.newsoftwares.hidepicturesvideos.notes.MyNoteActivity.4
                            @Override // java.lang.Thread, java.lang.Runnable
                            public void run() {
                                if (NotesCommon.isEdittingNote) {
                                    NotesFilesDAL notesFilesDAL = MyNoteActivity.this.notesFilesDAL;
                                    StringBuilder sb = new StringBuilder();
                                    MyNoteActivity.this.constants.getClass();
                                    sb.append("SELECT \t     * \t\t\t\t\t\t   FROM  TableNotesFile WHERE ");
                                    MyNoteActivity.this.constants.getClass();
                                    sb.append("NotesFileName");
                                    sb.append(" = '");
                                    sb.append(MyNoteActivity.this.oldNoteTitle);
                                    sb.append("' AND ");
                                    MyNoteActivity.this.constants.getClass();
                                    sb.append("NotesFileIsDecoy");
                                    sb.append(" = ");
                                    sb.append(SecurityLocksCommon.IsFakeAccount);
                                    NotesFileDB_Pojo notesFileInfoFromDatabase = notesFilesDAL.getNotesFileInfoFromDatabase(sb.toString());
                                    NotesFilesDAL notesFilesDAL2 = MyNoteActivity.this.notesFilesDAL;
                                    StringBuilder sb2 = new StringBuilder();
                                    MyNoteActivity.this.constants.getClass();
                                    sb2.append("SELECT \t     * \t\t\t\t\t\t   FROM  TableNotesFile WHERE ");
                                    MyNoteActivity.this.constants.getClass();
                                    sb2.append("NotesFileName");
                                    sb2.append(" = '");
                                    sb2.append(MyNoteActivity.this.notesTitle);
                                    sb2.append("' AND ");
                                    MyNoteActivity.this.constants.getClass();
                                    sb2.append("NotesFileIsDecoy");
                                    sb2.append(" = ");
                                    sb2.append(SecurityLocksCommon.IsFakeAccount);
                                    NotesFileDB_Pojo notesFileInfoFromDatabase2 = notesFilesDAL2.getNotesFileInfoFromDatabase(sb2.toString());
                                    int notesFileId = notesFileInfoFromDatabase.getNotesFileId();
                                    int notesFileId2 = notesFileInfoFromDatabase2.getNotesFileId();
                                    if (MyNoteActivity.this.oldNoteTitle.equals(MyNoteActivity.this.notesTitle)) {
                                        NotesCommon notesCommon = MyNoteActivity.this.notesCommon;
                                        MyNoteActivity myNoteActivity = MyNoteActivity.this;
                                        notesCommon.createNote(myNoteActivity, myNoteActivity.noteColor, MyNoteActivity.this.NotesContent, MyNoteActivity.this.notesTitle, MyNoteActivity.this.oldNoteTitle, encodedAudio, MyNoteActivity.this.NotesHashMap.get("note_datetime_c"), currentDate, true);
                                    } else if (notesFileId == notesFileId2 || notesFileInfoFromDatabase2.getNotesFileName() == null) {
                                        NotesCommon notesCommon2 = MyNoteActivity.this.notesCommon;
                                        MyNoteActivity myNoteActivity2 = MyNoteActivity.this;
                                        notesCommon2.createNote(myNoteActivity2, myNoteActivity2.noteColor, MyNoteActivity.this.NotesContent, MyNoteActivity.this.notesTitle, MyNoteActivity.this.oldNoteTitle, encodedAudio, MyNoteActivity.this.NotesHashMap.get("note_datetime_c"), currentDate, true);
                                    } else {
                                        Message message = new Message();
                                        message.what = 3;
                                        MyNoteActivity.this.handle.sendMessage(message);
                                    }
                                } else {
                                    NotesFilesDAL notesFilesDAL3 = MyNoteActivity.this.notesFilesDAL;
                                    StringBuilder sb3 = new StringBuilder();
                                    MyNoteActivity.this.constants.getClass();
                                    sb3.append("SELECT \t     * \t\t\t\t\t\t   FROM  TableNotesFile WHERE ");
                                    MyNoteActivity.this.constants.getClass();
                                    sb3.append("NotesFileName");
                                    sb3.append(" = '");
                                    sb3.append(MyNoteActivity.this.notesTitle);
                                    sb3.append("' AND ");
                                    MyNoteActivity.this.constants.getClass();
                                    sb3.append("NotesFileIsDecoy");
                                    sb3.append(" = ");
                                    sb3.append(SecurityLocksCommon.IsFakeAccount);
                                    if (!notesFilesDAL3.IsFileAlreadyExist(sb3.toString())) {
                                        NotesCommon notesCommon3 = MyNoteActivity.this.notesCommon;
                                        MyNoteActivity myNoteActivity3 = MyNoteActivity.this;
                                        String str2 = myNoteActivity3.noteColor;
                                        String str3 = MyNoteActivity.this.NotesContent;
                                        String str4 = MyNoteActivity.this.notesTitle;
                                        String str5 = MyNoteActivity.this.oldNoteTitle;
                                        String str6 = encodedAudio;
                                        String str7 = currentDate;
                                        notesCommon3.createNote(myNoteActivity3, str2, str3, str4, str5, str6, str7, str7, false);
                                    } else {
                                        Message message2 = new Message();
                                        message2.what = 3;
                                        MyNoteActivity.this.handle.sendMessage(message2);
                                    }
                                }
                                Message message3 = new Message();
                                message3.what = 2;
                                MyNoteActivity.this.handle.sendMessage(message3);
                            }
                        }.start();
                        break;
                    } else {
                        Utilities.hideKeyboard(this.ll_main, this);
                        Toast.makeText(this, (int) R.string.empty_note_name, 0).show();
                        break;
                    }
                } else {
                    Utilities.hideKeyboard(this.ll_main, this);
                    Toast.makeText(this, (int) R.string.empty_note, 0).show();
                    break;
                }
            case R.id.action_menu_color /* 2131296323 */:
                setNoteColor();
                break;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    public void setNoteColor() {
        final Dialog dialog = new Dialog(this, R.style.FullHeightDialog);
        dialog.setContentView(R.layout.dialog_note_color_picker);
        final ColorPickerView colorPickerView = (ColorPickerView) dialog.findViewById(R.id.color_picker_view);
        colorPickerView.setAlpha(0.5f);
        colorPickerView.setDensity(12);
        ((TextView) dialog.findViewById(R.id.tv_no)).setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.notes.MyNoteActivity.5
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        ((TextView) dialog.findViewById(R.id.tv_yes)).setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.notes.MyNoteActivity.6
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                int selectedColor = 0;
                try {
                    Log.i("color", String.valueOf(colorPickerView.getSelectedColor()));
                    String str = "#33" + Integer.toHexString(selectedColor).substring(2);
                    Log.i("scolor", str);
                    MyNoteActivity.this.ll_main.setBackgroundColor(Color.parseColor(str));
                    MyNoteActivity.this.noteColor = str;
                    MyNoteActivity.this.hasModified = true;
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
        ((LinearLayout) dialog.findViewById(R.id.ll_Ok)).setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.notes.MyNoteActivity.7
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                SecurityLocksCommon.IsAppDeactive = false;
                MyNoteActivity.this.startActivity(new Intent(MyNoteActivity.this, NotesFilesActivity.class));
                MyNoteActivity.this.finish();
                dialog.cancel();
            }
        });
        ((LinearLayout) dialog.findViewById(R.id.ll_Cancel)).setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.notes.MyNoteActivity.8
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public void onWindowFocusChanged(boolean z) {
        super.onWindowFocusChanged(z);
        if (!NotesCommon.isEdittingNote && !this.hasInsertedLines) {
            this.notesCommon.setEditTextFullPage(this.et_NoteContent);
            this.hasInsertedLines = true;
        }
    }

    /* loaded from: classes2.dex */
    private class AddNoteListeners {
        private AddNoteListeners() {
        }

        /* loaded from: classes2.dex */
        public class PlayAudioListeners implements View.OnClickListener {
            public PlayAudioListeners() {
            }

            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                ((InputMethodManager) MyNoteActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(MyNoteActivity.this.et_NoteContent.getWindowToken(), 0);
                if (!MyNoteActivity.this.audioRecorder.hasRecording) {
                    Toast.makeText(MyNoteActivity.this, "No recording to play", 0).show();
                    Utilities.hideKeyboard(MyNoteActivity.this.ll_main, MyNoteActivity.this);
                } else if (MyNoteActivity.this.isPlaying || MyNoteActivity.this.isPlayerVisible) {
                    MyNoteActivity.this.ll_notesRecordingPlayer.setVisibility(8);
                    MyNoteActivity.this.isPlayerVisible = false;
                    MyNoteActivity.this.isPlaying = false;
                    MyNoteActivity.this.mPlayer.stop();
                    MyNoteActivity.this.mPlayer.release();
                    MyNoteActivity.this.mHandler.removeCallbacks(MyNoteActivity.this.mUpdateTimeTask);
                } else {
                    try {
                        MyNoteActivity.this.isPlaying = true;
                        MyNoteActivity.this.iv_play.setBackgroundResource(R.drawable.pause);
                        MyNoteActivity.this.ll_notesRecordingPlayer.setVisibility(0);
                        MyNoteActivity.this.isPlayerVisible = true;
                        MyNoteActivity.this.mPlayer = new MediaPlayer();
                        MyNoteActivity.this.mPlayer.setDataSource(MyNoteActivity.this.recordingFilePath);
                        MyNoteActivity.this.mPlayer.prepare();
                        MyNoteActivity.this.mPlayer.start();
                        MediaPlayer mediaPlayer = MyNoteActivity.this.mPlayer;
                        AddNoteListeners addNoteListeners = MyNoteActivity.this.addNoteListeners;
                        addNoteListeners.getClass();
                        mediaPlayer.setOnCompletionListener(new RecordingCompleteListener());
                        MyNoteActivity.this.seekbar.setProgress(0);
                        MyNoteActivity.this.seekbar.setMax(100);
                        MyNoteActivity.this.updateProgressBar();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        /* loaded from: classes2.dex */
        public class RecordAudioListeners implements View.OnClickListener {
            public RecordAudioListeners() {
            }

            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                ((InputMethodManager) MyNoteActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(MyNoteActivity.this.et_NoteContent.getWindowToken(), 0);
                if (!MyNoteActivity.this.audioRecorder.hasRecording || MyNoteActivity.this.isRecording) {
                    MyNoteActivity.this.RecordOrStop();
                } else {
                    MyNoteActivity.this.showRecordingOverrideDialog();
                }
            }
        }

        /* loaded from: classes2.dex */
        public class EditTextChangeListener implements TextWatcher {
            @Override // android.text.TextWatcher
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override // android.text.TextWatcher
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public EditTextChangeListener() {
            }

            @Override // android.text.TextWatcher
            public void afterTextChanged(Editable editable) {
                MyNoteActivity.this.hasModified = true;
                Log.i("hasModified", "true in textwatch");
            }
        }

        /* loaded from: classes2.dex */
        public class RecordingCompleteListener implements MediaPlayer.OnCompletionListener {
            public RecordingCompleteListener() {
            }

            @Override // android.media.MediaPlayer.OnCompletionListener
            public void onCompletion(MediaPlayer mediaPlayer) {
                MyNoteActivity.this.isPlaying = false;
                MyNoteActivity.this.iv_play.setBackgroundResource(R.drawable.ic_play_circle_outline_black_24dp);
            }
        }

        /* loaded from: classes2.dex */
        public class AudioPlayerListener implements View.OnClickListener {
            public AudioPlayerListener() {
            }

            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.iv_next /* 2131296589 */:
                        MyNoteActivity.this.mPlayer.seekTo(MyNoteActivity.this.notesCommon.progressToTimer(MyNoteActivity.this.seekbar.getProgress(), MyNoteActivity.this.mPlayer.getDuration()) + 2000);
                        MyNoteActivity.this.updateProgressBar();
                        return;
                    case R.id.iv_notesFolder /* 2131296590 */:
                    default:
                        return;
                    case R.id.iv_play /* 2131296591 */:
                        MyNoteActivity.this.PlayPause();
                        return;
                    case R.id.iv_previous /* 2131296592 */:
                        MyNoteActivity.this.mPlayer.seekTo(MyNoteActivity.this.notesCommon.progressToTimer(MyNoteActivity.this.seekbar.getProgress(), MyNoteActivity.this.mPlayer.getDuration()) + NotificationManagerCompat.IMPORTANCE_UNSPECIFIED);
                        MyNoteActivity.this.updateProgressBar();
                        return;
                }
            }
        }

        /* loaded from: classes2.dex */
        public class SeekBarListener implements SeekBar.OnSeekBarChangeListener {
            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
            }

            public SeekBarListener() {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStartTrackingTouch(SeekBar seekBar) {
                MyNoteActivity.this.mHandler.removeCallbacks(MyNoteActivity.this.mUpdateTimeTask);
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStopTrackingTouch(SeekBar seekBar) {
                MyNoteActivity.this.mHandler.removeCallbacks(MyNoteActivity.this.mUpdateTimeTask);
                MyNoteActivity.this.mPlayer.seekTo(MyNoteActivity.this.notesCommon.progressToTimer(seekBar.getProgress(), MyNoteActivity.this.mPlayer.getDuration()));
                MyNoteActivity.this.updateProgressBar();
            }
        }
    }

    public void PlayPause() {
        if (this.mPlayer.isPlaying()) {
            this.mPlayer.pause();
            this.iv_play.setBackgroundResource(R.drawable.ic_play_circle_outline_black_24dp);
        } else if (!this.mPlayer.isPlaying()) {
            this.mPlayer.start();
            this.iv_play.setBackgroundResource(R.drawable.pause);
        } else {
            this.mPlayer.start();
        }
    }

    public void updateProgressBar() {
        this.mHandler.postDelayed(this.mUpdateTimeTask, 100L);
    }

    public void showRecordingOverrideDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getResources().getString(R.string.overwrite_audio));
        builder.setCancelable(true);
        builder.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.notes.MyNoteActivity.9
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                MyNoteActivity.this.iv_NotesplayAudio.clearAnimation();
                MyNoteActivity.this.iv_NotesplayAudio.setVisibility(8);
                MyNoteActivity.this.DeleteExistingRecording();
                dialogInterface.cancel();
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.notes.MyNoteActivity.10
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                MyNoteActivity.this.RecordOrStop();
                dialogInterface.cancel();
            }
        });
        builder.create().show();
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onBackPressed() {
        if (this.audioRecorder.hasRecording && this.isPlaying && this.isPlayerVisible) {
            this.ll_notesRecordingPlayer.setVisibility(8);
            this.isPlayerVisible = false;
            this.isPlaying = false;
            this.mPlayer.stop();
            this.mPlayer.release();
            this.mHandler.removeCallbacks(this.mUpdateTimeTask);
        } else if (this.isRecording || this.hasModified) {
            showDiscardDialog();
        } else {
            SecurityLocksCommon.IsAppDeactive = false;
            startActivity(new Intent(this, NotesFilesActivity.class));
            finish();
            overridePendingTransition(17432576, 17432577);
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
    @Override
    // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
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

    @AfterPermissionGranted(123)
    private void requestPermission(String[] strArr) {
        SecurityLocksCommon.IsAppDeactive = false;
        if (!EasyPermissions.hasPermissions(this, strArr)) {
            EasyPermissions.requestPermissions(new PermissionRequest.Builder(this, 123, strArr).setRationale("For the best NS Vault, please Allow Permission").setPositiveButtonText("ok").setNegativeButtonText("").build());
        }
    }

    @Override
    // androidx.fragment.app.FragmentActivity, android.app.Activity, androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback
    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        super.onRequestPermissionsResult(i, strArr, iArr);
        if (iArr.length > 0 && iArr[0] == 0) {
            Toast.makeText(getApplicationContext(), "Permission is granted ", 0).show();
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, "android.permission.RECORD_AUDIO")) {
            String[] strArr2 = {"android.permission.RECORD_AUDIO"};
            if (EasyPermissions.hasPermissions(this, strArr2)) {
                Toast.makeText(this, "Permission  again...", 0).show();
            } else {
                EasyPermissions.requestPermissions(new PermissionRequest.Builder(this, 123, strArr2).setRationale("For the best NS Vault experience, please Allow Permission").setPositiveButtonText("ok").setNegativeButtonText("").build());
            }
            Toast.makeText(getApplicationContext(), "Permission denied", 0).show();
        } else {
            EasyPermissions.onRequestPermissionsResult(i, strArr, iArr, this);
        }
    }

    @Override // pub.devrel.easypermissions.EasyPermissions.PermissionCallbacks
    public void onPermissionsDenied(int i, List<String> list) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, list)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
    }
}
