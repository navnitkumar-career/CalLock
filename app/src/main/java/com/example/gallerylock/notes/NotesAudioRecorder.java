package com.example.gallerylock.notes;

import android.media.AudioRecord;
import android.os.Environment;

import com.fasterxml.jackson.core.util.MinimalPrettyPrinter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/* loaded from: classes2.dex */
public class NotesAudioRecorder {
    private static final String AUDIO_RECORDER_FILE_EXT_WAV = ".wav";
    private static final String AUDIO_RECORDER_FOLDER = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "AudioRecorder/";
    private static final String AUDIO_RECORDER_TEMP_FILE = "record_temp.raw";
    private static final int RECORDER_AUDIO_ENCODING = 2;
    private static final int RECORDER_BPP = 16;
    private static final int RECORDER_CHANNELS = 12;
    private static final int RECORDER_SAMPLERATE = 8000;
    private AudioRecord recorder = null;
    private int bufferSize = 0;
    private Thread recordingThread = null;
    private boolean isRecording = false;

    public void startRecording(final boolean z) {
        AudioRecord audioRecord = new AudioRecord(1, RECORDER_SAMPLERATE, 12, 2, this.bufferSize);
        this.recorder = audioRecord;
        audioRecord.startRecording();
        this.isRecording = true;
        Thread thread = new Thread(new Runnable() { // from class: net.newsoftwares.hidepicturesvideos.notes.NotesAudioRecorder.1
            @Override // java.lang.Runnable
            public void run() {
                NotesAudioRecorder.this.writeAudioDataToFile(z);
            }
        }, "AudioRecorder Thread");
        this.recordingThread = thread;
        thread.start();
    }

    public void writeAudioDataToFile(boolean z) {
        FileOutputStream fileOutputStream;
        byte[] bArr = new byte[this.bufferSize];
        try {
            fileOutputStream = new FileOutputStream(getTempFilename(), z);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            fileOutputStream = null;
        }
        if (fileOutputStream != null) {
            while (this.isRecording) {
                if (-3 != this.recorder.read(bArr, 0, this.bufferSize)) {
                    try {
                        fileOutputStream.write(bArr);
                    } catch (IOException e2) {
                        e2.printStackTrace();
                    }
                }
            }
            try {
                fileOutputStream.close();
            } catch (IOException e3) {
                e3.printStackTrace();
            }
        }
    }

    public String getFilename() {
        NotesCommon.GetDate();
        String Gettime = NotesCommon.Gettime();
        String replace = Gettime.replace(MinimalPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR, "");
        String replace2 = Gettime.replace(":", "");
        String str = Environment.getExternalStorageDirectory().getPath() + AUDIO_RECORDER_FOLDER;
        File file = new File(str);
        if (!file.exists()) {
            file.mkdirs();
        }
        File file2 = new File(str, "Recording_" + replace + "_" + replace2 + AUDIO_RECORDER_FILE_EXT_WAV);
        if (!file2.exists()) {
            try {
                file2.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file2.getAbsolutePath();
    }

    public String getTempFilename() {
        String path = Environment.getExternalStorageDirectory().getPath();
        File file = new File(path, AUDIO_RECORDER_FOLDER);
        if (!file.exists()) {
            file.mkdirs();
        }
        File file2 = new File(path, AUDIO_RECORDER_TEMP_FILE);
        if (file2.exists()) {
            file2.delete();
        }
        return file.getAbsolutePath() + "/" + AUDIO_RECORDER_TEMP_FILE;
    }

    public void stopRecording(boolean z) {
        AudioRecord audioRecord = this.recorder;
        if (audioRecord != null) {
            this.isRecording = false;
            audioRecord.stop();
            this.recorder.release();
            this.recorder = null;
            this.recordingThread = null;
        }
        if (z) {
            CombineWaveFile(getTempFilename(), getFilename());
            deleteTempFile();
        }
    }

    private void deleteTempFile() {
        new File(getTempFilename()).delete();
    }

    public void CombineWaveFile(String str, String str2) {
        int minBufferSize = AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE, 12, 2);
        this.bufferSize = minBufferSize;
        long j = 32000;
        byte[] bArr = new byte[minBufferSize];
        try {
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "myMix.wav");
            if (!file.exists()) {
                file.createNewFile();
            }
            FileInputStream fileInputStream = new FileInputStream(str);
            FileInputStream fileInputStream2 = new FileInputStream(str2);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            long size = fileInputStream2.getChannel().size() + fileInputStream.getChannel().size();
            WriteWaveFileHeader(fileOutputStream, size, size + 36, 8000L, 2, j);
            while (fileInputStream.read(bArr) != -1) {
                fileOutputStream.write(bArr);
            }
            while (fileInputStream2.read(bArr) != -1) {
                fileOutputStream.write(bArr);
            }
            fileOutputStream.close();
            fileInputStream.close();
            fileInputStream2.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
    }

    public void WriteWaveFileHeader(FileOutputStream fileOutputStream, long j, long j2, long j3, int i, long j4) throws IOException {
        fileOutputStream.write(new byte[]{82, 73, 70, 70, (byte) (j2 & 255), (byte) ((j2 >> 8) & 255), (byte) ((j2 >> 16) & 255), (byte) ((j2 >> 24) & 255), 87, 65, 86, 69, 102, 109, 116, 32, 16, 0, 0, 0, 1, 0, (byte) i, 0, (byte) (j3 & 255), (byte) ((j3 >> 8) & 255), (byte) ((j3 >> 16) & 255), (byte) ((j3 >> 24) & 255), (byte) (j4 & 255), (byte) ((j4 >> 8) & 255), (byte) ((j4 >> 16) & 255), (byte) ((j4 >> 24) & 255), 4, 0, 16, 0, 100, 97, 116, 97, (byte) (j & 255), (byte) ((j >> 8) & 255), (byte) ((j >> 16) & 255), (byte) (255 & (j >> 24))}, 0, 44);
    }
}
