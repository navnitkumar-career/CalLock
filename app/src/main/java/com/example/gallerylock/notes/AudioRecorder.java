package com.example.gallerylock.notes;

import android.content.Context;
import android.graphics.Movie;
import android.media.MediaRecorder;
import android.util.Log;

import org.apache.commons.io.FileUtils;
import org.mp4parser.Container;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.LinkedList;

/* loaded from: classes2.dex */
public class AudioRecorder {
    public static boolean isRecordStarted = false;
    Context context;
    public File firstRecordingFile;
    public MediaRecorder recorder;
    public File recordingfolder;
    public File secondRecordingFile;
    public File tempRecordingFile;
    public String recordingFolderPath = "";
    public String firstRecording = "firstRecording";
    public String secondRecording = "secondRecording";
    public String tempRecording = "tempRecording";
    public String RecordingFileExtension = ".m4a";
    public String Recording = "Recordings/";
    public boolean hasRecording = false;

    public AudioRecorder(Context context) {
        this.context = context;
        createRecordingFolder();
    }

    public void StartRecording() {
        try {
            MediaRecorder mediaRecorder = new MediaRecorder();
            this.recorder = mediaRecorder;
            mediaRecorder.reset();
            this.recorder.setAudioSource(1);
            this.recorder.setOutputFormat(2);
            this.recorder.setAudioEncoder(3);
            if (!this.hasRecording) {
                createFirstRecording();
                this.recorder.setOutputFile(this.firstRecordingFile.getAbsolutePath());
            } else {
                createSecondRecording();
                this.recorder.setOutputFile(this.secondRecordingFile.getAbsolutePath());
            }
            this.recorder.prepare();
            this.recorder.start();
            isRecordStarted = true;
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("masl", e.toString());
        }
    }

    public String StopRecording() {
        if (isRecordStarted) {
            isRecordStarted = false;
            this.recorder.stop();
            this.recorder.reset();
            this.recorder.release();
            this.recorder = null;
            if (!this.hasRecording) {
                this.hasRecording = true;
            } else if (this.firstRecordingFile.exists() && this.firstRecordingFile != null && this.secondRecordingFile.exists() && this.secondRecordingFile != null) {
                return mergeM4Afiles(this.context);
            }
        }
        return this.firstRecordingFile.getAbsolutePath();
    }

    public String mergeM4Afiles(Context context) {
       /* try {
            Movie movie = new Movie();
            Movie[] movieArr = {MovieCreator.build(this.firstRecordingFile.getAbsolutePath()), MovieCreator.build(this.secondRecordingFile.getAbsolutePath())};
            LinkedList linkedList = new LinkedList();
            for (int i = 0; i < 2; i++) {
                for (Track track : movieArr[i].getTracks()) {
                    if (track.getHandler().equals("soun")) {
                        linkedList.add(track);
                    }
                }
            }
            if (linkedList.size() > 0) {
                movie.addTrack(new AppendTrack((Track[]) linkedList.toArray(new Track[linkedList.size()])));
            }
            Container build = new DefaultMp4Builder().build(movie);
            createTempRecording();
            FileChannel channel = new RandomAccessFile(this.tempRecordingFile, "rw").getChannel();
            build.writeContainer(channel);
            channel.close();
            deleteFirstRecording();
            createFirstRecording();
            FileUtils.copyFile(this.tempRecordingFile, this.firstRecordingFile);
            deleteTempRecording();
            return this.firstRecordingFile.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }*/
        return "";
    }

    public void createRecordingFolder() {
        this.recordingFolderPath = this.context.getFilesDir() + File.separator + this.Recording;
        File file = new File(this.context.getFilesDir(), this.Recording);
        this.recordingfolder = file;
        this.recordingFolderPath = file.getAbsolutePath();
        if (!this.recordingfolder.exists()) {
            this.recordingfolder.mkdirs();
        }
    }

    public void createFirstRecording() {
        try {
            String str = this.recordingFolderPath;
            File file = new File(str, this.firstRecording + this.RecordingFileExtension);
            this.firstRecordingFile = file;
            if (!file.exists()) {
                this.firstRecordingFile.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createSecondRecording() {
        try {
            String str = this.recordingFolderPath;
            File file = new File(str, this.secondRecording + this.RecordingFileExtension);
            this.secondRecordingFile = file;
            if (!file.exists()) {
                this.secondRecordingFile.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createTempRecording() {
        try {
            String str = this.recordingFolderPath;
            File file = new File(str, this.tempRecording + this.RecordingFileExtension);
            this.tempRecordingFile = file;
            if (file.exists()) {
                this.tempRecordingFile.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteFirstRecording() {
        File file = this.firstRecordingFile;
        if (file != null && file.exists()) {
            this.firstRecordingFile.delete();
        }
    }

    public void deleteSecondRecording() {
        File file = this.secondRecordingFile;
        if (file != null && file.exists()) {
            this.secondRecordingFile.delete();
        }
    }

    public void deleteTempRecording() {
        File file = this.tempRecordingFile;
        if (file != null && file.exists()) {
            this.tempRecordingFile.delete();
        }
    }
}
