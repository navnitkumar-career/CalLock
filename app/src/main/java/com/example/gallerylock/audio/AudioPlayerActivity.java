package com.example.gallerylock.audio;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.example.gallerylock.Flaes;
import com.example.gallerylock.R;
import com.example.gallerylock.securitylocks.SecurityLocksCommon;
import com.example.gallerylock.storageoption.StorageOptionsCommon;
import com.example.gallerylock.utilities.Common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

/* loaded from: classes2.dex */
public class AudioPlayerActivity extends BaseActivity implements MediaPlayer.OnCompletionListener, SeekBar.OnSeekBarChangeListener {
    public static ProgressDialog AudioDecrytionDialog;
    private SeekBar audioProgressBar;
    private LinearLayout btnPlayerForwardTrack;
    private LinearLayout btnPlayerPlayPause;
    private LinearLayout btnPlayerPreviousTrack;
    private TextView songCurrentDurationLabel;
    private TextView songTotalDurationLabel;
    private Toolbar toolbar;
    TextView toolbar_title;
    private TextView txtSongName;
    private Handler mHandler = new Handler();
    private boolean IsStop = false;
    ArrayList<AudioEnt> audioEntList = new ArrayList<>();
    private String AppPath = "";
    Handler handle = new Handler() { // from class: net.newsoftwares.hidepicturesvideos.audio.AudioPlayerActivity.1
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            if (message.what == 1) {
                AudioPlayerActivity.this.hideAudioDecrytionProgress();
                File file = new File(AudioPlayerActivity.this.AppPath);
                AudioPlayerActivity.this.DecrpytAudioFile(file, new File(StorageOptionsCommon.STORAGEPATH + "/" + StorageOptionsCommon.AUDIOS_TEMP_FOLDER + AudioPlayerActivity.this.audioEntList.get(Common.CurrentTrackIndex).getAudioName()), Common.CurrentTrackIndex);
            }
            super.handleMessage(message);
        }
    };
    private Runnable mUpdateTimeTask = new Runnable() { // from class: net.newsoftwares.hidepicturesvideos.audio.AudioPlayerActivity.3
        @Override // java.lang.Runnable
        public void run() {
            long duration = Common.mediaplayer.getDuration();
            long currentPosition = Common.mediaplayer.getCurrentPosition();
            AudioPlayerActivity.this.songTotalDurationLabel.setText("" + Common.milliSecondsToTimer(duration));
            AudioPlayerActivity.this.songCurrentDurationLabel.setText("" + Common.milliSecondsToTimer(currentPosition));
            int progressPercentage = Common.getProgressPercentage(currentPosition, duration);
            AudioPlayerActivity.this.audioProgressBar.setProgress(progressPercentage);
            if (progressPercentage != 100) {
                AudioPlayerActivity.this.mHandler.postDelayed(this, 100L);
            } else if (Common.CurrentTrackIndex < AudioPlayerActivity.this.audioEntList.size() - 1) {
                AudioPlayerActivity.this.PlaySong(Common.CurrentTrackIndex + 1);
                Common.CurrentTrackIndex++;
                if (AudioPlayerActivity.this.audioEntList.get(Common.CurrentTrackIndex).getAudioName().length() > 20) {
                    AudioPlayerActivity.this.txtSongName.setText(AudioPlayerActivity.this.audioEntList.get(Common.CurrentTrackIndex).getAudioName().substring(0, 19));
                } else {
                    AudioPlayerActivity.this.txtSongName.setText(AudioPlayerActivity.this.audioEntList.get(Common.CurrentTrackIndex).getAudioName());
                }
            } else {
                AudioPlayerActivity.this.PlaySong(0);
                Common.CurrentTrackIndex = 0;
                if (AudioPlayerActivity.this.audioEntList.get(Common.CurrentTrackIndex).getAudioName().length() > 20) {
                    AudioPlayerActivity.this.txtSongName.setText(AudioPlayerActivity.this.audioEntList.get(Common.CurrentTrackIndex).getAudioName().substring(0, 19));
                } else {
                    AudioPlayerActivity.this.txtSongName.setText(AudioPlayerActivity.this.audioEntList.get(Common.CurrentTrackIndex).getAudioName());
                }
            }
        }
    };

    @Override // android.widget.SeekBar.OnSeekBarChangeListener
    public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
    }

    private void showAudioDecryptionProgress() {
        AudioDecrytionDialog = ProgressDialog.show(this, null, "Audio Decryption, \nPlease wait your audio file is being decrypted...", true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void hideAudioDecrytionProgress() {
        ProgressDialog progressDialog = AudioDecrytionDialog;
        if (progressDialog != null && progressDialog.isShowing()) {
            AudioDecrytionDialog.dismiss();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // net.newsoftwares.hidepicturesvideos.audio.BaseActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_audioplayer);
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.toolbar_title = (TextView) findViewById(R.id.toolbar_title);
        setSupportActionBar(this.toolbar);
        getSupportActionBar().setTitle("Audio Player");
        this.toolbar.setNavigationIcon(R.drawable.back_top_bar_icon);
        SecurityLocksCommon.IsAppDeactive = true;
        getWindow().addFlags(128);
        this.txtSongName = (TextView) findViewById(R.id.txtSongTitle);
        this.btnPlayerPreviousTrack = (LinearLayout) findViewById(R.id.llPlayerPreviousTrack);
        this.btnPlayerForwardTrack = (LinearLayout) findViewById(R.id.llPlayerForwardTrack);
        this.btnPlayerPlayPause = (LinearLayout) findViewById(R.id.llPlayerPlayPause);
        this.audioProgressBar = (SeekBar) findViewById(R.id.audioProgressbar);
        this.songCurrentDurationLabel = (TextView) findViewById(R.id.songCurrentDurationLabel);
        this.songTotalDurationLabel = (TextView) findViewById(R.id.songTotalDurationLabel);
        if (Common.voiceplayer.isPlaying()) {
            Common.voiceplayer.stop();
        }
        this.audioProgressBar.setOnSeekBarChangeListener(this);
        Common.mediaplayer.setOnCompletionListener(this);
        AudioDAL audioDAL = new AudioDAL(this);
        audioDAL.OpenRead();
        this.audioEntList = (ArrayList) audioDAL.GetAudiosByAlbumId(Common.FolderId, Common.sortType);
        audioDAL.close();
        if (Common.CurrentTrackId == this.audioEntList.get(Common.CurrentTrackNextIndex).getId() && Common.mediaplayer.isPlaying() && Common.CurrentTrackIndex == Common.CurrentTrackNextIndex) {
            if (this.audioEntList.get(Common.CurrentTrackIndex).getAudioName().length() > 20) {
                this.txtSongName.setText(this.audioEntList.get(Common.CurrentTrackIndex).getAudioName().substring(0, 19));
            } else {
                this.txtSongName.setText(this.audioEntList.get(Common.CurrentTrackIndex).getAudioName());
            }
            updateProgressBar();
            return;
        }
        Common.CurrentTrackIndex = Common.CurrentTrackNextIndex;
        PlaySong(Common.CurrentTrackIndex);
    }

    public void btnPlayerPlayPauseClick(View view) {
        if (Common.mediaplayer.isPlaying()) {
            Common.mediaplayer.pause();
            if (Common.isTablet10Inch(getApplicationContext())) {
                this.btnPlayerPlayPause.setBackgroundResource(R.drawable.btn_play);
            } else if (Common.isTablet7Inch(getApplicationContext())) {
                this.btnPlayerPlayPause.setBackgroundResource(R.drawable.btn_play);
            } else {
                this.btnPlayerPlayPause.setBackgroundResource(R.drawable.btn_play);
            }
        } else if (Common.mediaplayer.isPlaying() || !this.IsStop) {
            Common.mediaplayer.start();
            if (Common.isTablet10Inch(getApplicationContext())) {
                this.btnPlayerPlayPause.setBackgroundResource(R.drawable.btn_pause);
            } else if (Common.isTablet7Inch(getApplicationContext())) {
                this.btnPlayerPlayPause.setBackgroundResource(R.drawable.btn_pause);
            } else {
                this.btnPlayerPlayPause.setBackgroundResource(R.drawable.btn_pause);
            }
        } else {
            this.IsStop = false;
            PlaySong(Common.CurrentTrackIndex);
            if (Common.isTablet10Inch(getApplicationContext())) {
                this.btnPlayerPlayPause.setBackgroundResource(R.drawable.btn_pause);
            } else if (Common.isTablet7Inch(getApplicationContext())) {
                this.btnPlayerPlayPause.setBackgroundResource(R.drawable.btn_pause);
            } else {
                this.btnPlayerPlayPause.setBackgroundResource(R.drawable.btn_pause);
            }
        }
    }

    public void btnPlayerStopClick(View view) {
        if (Common.mediaplayer.isPlaying()) {
            Common.mediaplayer.stop();
            if (Common.isTablet10Inch(getApplicationContext())) {
                this.btnPlayerPlayPause.setBackgroundResource(R.drawable.btn_play);
            } else if (Common.isTablet7Inch(getApplicationContext())) {
                this.btnPlayerPlayPause.setBackgroundResource(R.drawable.btn_play);
            } else {
                this.btnPlayerPlayPause.setBackgroundResource(R.drawable.btn_play);
            }
            this.IsStop = true;
        }
    }

    public void btnPlayerPreviousTrackClick(View view) {
        this.btnPlayerPlayPause.setBackgroundResource(R.drawable.btn_pause);
        if (Common.CurrentTrackIndex > 0) {
            PlaySong(Common.CurrentTrackIndex - 1);
            Common.CurrentTrackIndex--;
            return;
        }
        PlaySong(this.audioEntList.size() - 1);
        Common.CurrentTrackIndex = this.audioEntList.size() - 1;
    }

    public String FileName(String str) {
        for (int length = str.length() - 1; length > 0; length--) {
            if (str.charAt(length) == " /".charAt(1)) {
                return str.substring(length + 1, str.length());
            }
        }
        return "";
    }

    public void btnPlayerForwardTrackClick(View view) {
        this.btnPlayerPlayPause.setBackgroundResource(R.drawable.btn_pause);
        if (Common.CurrentTrackIndex < this.audioEntList.size() - 1) {
            PlaySong(Common.CurrentTrackIndex + 1);
            Common.CurrentTrackIndex++;
            return;
        }
        PlaySong(0);
        Common.CurrentTrackIndex = 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Type inference failed for: r5v7, types: [net.newsoftwares.hidepicturesvideos.audio.AudioPlayerActivity$2] */
    public void PlaySong(int i) {
        this.AppPath = this.audioEntList.get(i).getFolderLockAudioLocation();
        final File file = new File(this.AppPath);
        final File file2 = new File(StorageOptionsCommon.STORAGEPATH + "/" + StorageOptionsCommon.AUDIOS_TEMP_FOLDER + this.audioEntList.get(i).getAudioName());
        File file3 = new File(file2.getParent());
        if (!file3.exists()) {
            file3.mkdirs();
        }
        Log.d("CurrentlyPlayFileName", this.audioEntList.get(i).getAudioName());
        if (!file.exists()) {
            return;
        }
        if (!file2.exists()) {
            try {
                file2.createNewFile();
                showAudioDecryptionProgress();
                new Thread() { // from class: net.newsoftwares.hidepicturesvideos.audio.AudioPlayerActivity.2
                    @Override // java.lang.Thread, java.lang.Runnable
                    public void run() {
                        try {
                            Flaes.decryptUsingCipherStream_AES128(file, file2);
                            Message message = new Message();
                            message.what = 1;
                            AudioPlayerActivity.this.handle.sendMessage(message);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            FileInputStream fileInputStream = null;
            try {
                fileInputStream = new FileInputStream(file2);
            } catch (FileNotFoundException e2) {
                e2.printStackTrace();
            }
            Common.mediaplayer.stop();
            Common.mediaplayer.reset();
            try {
                Common.mediaplayer.setDataSource(fileInputStream.getFD());
            } catch (IOException e3) {
                e3.printStackTrace();
            } catch (IllegalArgumentException e4) {
                e4.printStackTrace();
            } catch (IllegalStateException e5) {
                e5.printStackTrace();
            }
            try {
                Common.mediaplayer.prepare();
            } catch (IOException e6) {
                e6.printStackTrace();
            } catch (IllegalStateException e7) {
                e7.printStackTrace();
            }
            Common.mediaplayer.start();
            updateProgressBar();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void DecrpytAudioFile(File file, File file2, int i) {
        FileInputStream fileInputStream;
        try {
            fileInputStream = new FileInputStream(file2);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            fileInputStream = null;
        }
        Common.mediaplayer.stop();
        Common.mediaplayer.reset();
        try {
            Common.mediaplayer.setDataSource(fileInputStream.getFD());
        } catch (IOException e2) {
            e2.printStackTrace();
        } catch (IllegalArgumentException e3) {
            e3.printStackTrace();
        } catch (IllegalStateException e4) {
            e4.printStackTrace();
        }
        try {
            Common.mediaplayer.prepare();
        } catch (IOException e5) {
            e5.printStackTrace();
        } catch (IllegalStateException e6) {
            e6.printStackTrace();
        }
        Common.mediaplayer.start();
        if (this.audioEntList.get(i).getAudioName().length() > 20) {
            this.txtSongName.setText(this.audioEntList.get(i).getAudioName().substring(0, 19));
        } else {
            this.txtSongName.setText(this.audioEntList.get(i).getAudioName());
        }
        Common.CurrentTrackId = this.audioEntList.get(i).getId();
        this.audioProgressBar.setProgress(0);
        this.audioProgressBar.setMax(100);
        updateProgressBar();
    }

    @Override // android.media.MediaPlayer.OnCompletionListener
    public void onCompletion(MediaPlayer mediaPlayer) {
        if (Common.CurrentTrackIndex < this.audioEntList.size() - 1) {
            PlaySong(Common.CurrentTrackIndex + 1);
            Common.CurrentTrackIndex++;
            return;
        }
        PlaySong(0);
        Common.CurrentTrackIndex = 0;
    }

    public void updateProgressBar() {
        this.mHandler.postDelayed(this.mUpdateTimeTask, 100L);
    }

    @Override // android.widget.SeekBar.OnSeekBarChangeListener
    public void onStartTrackingTouch(SeekBar seekBar) {
        this.mHandler.removeCallbacks(this.mUpdateTimeTask);
    }

    @Override // android.widget.SeekBar.OnSeekBarChangeListener
    public void onStopTrackingTouch(SeekBar seekBar) {
        this.mHandler.removeCallbacks(this.mUpdateTimeTask);
        Common.mediaplayer.seekTo(Common.progressToTimer(seekBar.getProgress(), Common.mediaplayer.getDuration()));
        updateProgressBar();
    }

    @Override // androidx.appcompat.app.AppCompatActivity, android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (i == 4) {
            Back();
        }
        return super.onKeyDown(i, keyEvent);
    }

    @Override // android.app.Activity
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() != 16908332) {
            return super.onOptionsItemSelected(menuItem);
        }
        Back();
        return true;
    }

    private void Back() {
        SecurityLocksCommon.IsAppDeactive = false;
        startActivity(new Intent(this, AudioActivity.class));
        finish();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // net.newsoftwares.hidepicturesvideos.audio.BaseActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onPause() {
        super.onPause();
        if (SecurityLocksCommon.IsAppDeactive) {
            finish();
            System.exit(0);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // net.newsoftwares.hidepicturesvideos.audio.BaseActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onResume() {
        super.onResume();
    }
}
