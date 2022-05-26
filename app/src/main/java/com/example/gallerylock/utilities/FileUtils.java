package com.example.gallerylock.utilities;

import android.os.Environment;
import android.util.Log;

import com.fasterxml.jackson.core.util.MinimalPrettyPrinter;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Vector;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/* loaded from: classes2.dex */
public class FileUtils {
    private static final String DIRECTORY = Environment.getExternalStorageDirectory().getPath();
    private static final String DIRECTORY2 = "/mnt/extSdCard";
    private static final String DIRECTORY3 = "/storage/sdcard1";
    ArrayList<String> filterRemove = new ArrayList<>();

    public ArrayList<File> FindFiles(String[] strArr) {
        ArrayList<File> arrayList = new ArrayList<>();
        FilenameFilter[] filenameFilterArr = new FilenameFilter[strArr.length];
        int i = 0;
        for (final String str : strArr) {
            filenameFilterArr[i] = new FilenameFilter() { // from class: net.newsoftwares.hidepicturesvideos.utilities.FileUtils.1
                @Override // java.io.FilenameFilter
                public boolean accept(File file, String str2) {
                    return str2.endsWith("." + str);
                }
            };
            i++;
        }
        for (File file : listFilesAsArray(new File(DIRECTORY), filenameFilterArr, -1)) {
            arrayList.add(file);
        }
        File file2 = new File(DIRECTORY2);
        if (file2.exists() && file2.isDirectory()) {
            for (File file3 : listFilesAsArray(new File(DIRECTORY2), filenameFilterArr, -1)) {
                arrayList.add(file3);
            }
        }
        File file4 = new File(DIRECTORY3);
        if (file4.exists() && file4.isDirectory()) {
            for (File file5 : listFilesAsArray(new File(DIRECTORY3), filenameFilterArr, -1)) {
                arrayList.add(file5);
            }
        }
        return arrayList;
    }

    public static void deleteDirectory(File file) throws IOException {
        if (file.exists()) {
            if (!isSymlink(file)) {
                org.apache.commons.io.FileUtils.cleanDirectory(file);
            }
            if (!file.delete()) {
                throw new IOException("Unable to delete directory " + file + ".");
            }
        }
    }

    private void saveArray(String str, List<String> list) {
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(new GZIPOutputStream(new FileOutputStream(str)));
            objectOutputStream.writeObject(list);
            objectOutputStream.flush();
            objectOutputStream.close();
        } catch (IOException e) {
            e.getStackTrace();
        }
    }

    private List<String> loadArray(String str) {
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(new GZIPInputStream(new FileInputStream(str)));
            List<String> list = (List) objectInputStream.readObject();
            objectInputStream.close();
            return list;
        } catch (Exception e) {
            e.getStackTrace();
            return null;
        }
    }

    private File[] listFilesAsArray(File file, FilenameFilter[] filenameFilterArr, int i) {
        Collection<File> listFiles = listFiles(file, filenameFilterArr, i);
        return (File[]) listFiles.toArray(new File[listFiles.size()]);
    }

    private Collection<File> listFiles(File file, FilenameFilter[] filenameFilterArr, int i) {
        Vector vector = new Vector();
        File[] listFiles = file.listFiles();
        if (listFiles != null) {
            for (File file2 : listFiles) {
                for (FilenameFilter filenameFilter : filenameFilterArr) {
                    if (filenameFilterArr == null || filenameFilter.accept(file, file2.getName())) {
                        vector.add(file2);
                        Log.v("FileUtils", "Added: " + file2.getName());
                    }
                }
                if (i <= -1 || (i > 0 && file2.isDirectory())) {
                    int i2 = i - 1;
                    vector.addAll(listFiles(file2, filenameFilterArr, i2));
                    i = i2 + 1;
                }
            }
        }
        return vector;
    }

    public ArrayList<File> FindFilesForMiscellaneous(String[] strArr) {
        File[] listFilesAsArrayMiscellaneous;
        File[] listFilesAsArrayMiscellaneous2;
        ArrayList<File> arrayList = new ArrayList<>();
        SetRemoveFilesExt();
        FilenameFilter[] filenameFilterArr = new FilenameFilter[strArr.length];
        int i = 0;
        for (String str : strArr) {
            filenameFilterArr[i] = new FilenameFilter() { // from class: net.newsoftwares.hidepicturesvideos.utilities.FileUtils.2
                @Override // java.io.FilenameFilter
                public boolean accept(File file, String str2) {
                    return true;
                }
            };
            i++;
        }
        for (File file : listFilesAsArrayMiscellaneous(new File(DIRECTORY), filenameFilterArr, -1)) {
            if (!file.getParent().substring(0, 1).contains(".") && file.getName().contains(".") && !file.getName().substring(0, 1).contains(".")) {
                arrayList.add(file);
            }
        }
        File file2 = new File(DIRECTORY2);
        if (file2.exists() && file2.isDirectory()) {
            for (File file3 : listFilesAsArrayMiscellaneous(new File(DIRECTORY2), filenameFilterArr, -1)) {
                if (!file3.getParent().substring(0, 1).contains(".") && file3.getName().contains(".") && !file3.getName().substring(0, 1).contains(".")) {
                    arrayList.add(file3);
                }
            }
        }
        return arrayList;
    }

    private Collection<File> listFilesMiscellaneous(File file, FilenameFilter[] filenameFilterArr, int i) {
        Vector vector = new Vector();
        File[] listFiles = file.listFiles();
        if (listFiles != null) {
            for (File file2 : listFiles) {
                for (FilenameFilter filenameFilter : filenameFilterArr) {
                    if (filenameFilterArr == null || filenameFilter.accept(file, file2.getName())) {
                        String filename = filename(file2.getName().toLowerCase());
                        if (file2.isFile() && !file2.isDirectory() && !this.filterRemove.contains(filename)) {
                            vector.add(file2);
                        }
                    }
                }
                if (i <= -1 || (i > 0 && file2.isDirectory())) {
                    int i2 = i - 1;
                    vector.addAll(listFilesMiscellaneous(file2, filenameFilterArr, i2));
                    i = i2 + 1;
                }
            }
        }
        return vector;
    }

    private String filename(String str) {
        return str.substring(str.lastIndexOf(".") + 1, str.length());
    }

    private File[] listFilesAsArrayMiscellaneous(File file, FilenameFilter[] filenameFilterArr, int i) {
        Collection<File> listFilesMiscellaneous = listFilesMiscellaneous(file, filenameFilterArr, i);
        return (File[]) listFilesMiscellaneous.toArray(new File[listFilesMiscellaneous.size()]);
    }

    private void SetRemoveFilesExt() {
        this.filterRemove.add("jpg");
        this.filterRemove.add("jpeg");
        this.filterRemove.add("png");
        this.filterRemove.add("gif");
        this.filterRemove.add("bmp");
        this.filterRemove.add("webp");
        this.filterRemove.add("mp3");
        this.filterRemove.add("wav");
        this.filterRemove.add("m4a");
        this.filterRemove.add("3gp");
        this.filterRemove.add("webm");
        this.filterRemove.add("mp4");
        this.filterRemove.add("avi");
        this.filterRemove.add("ts");
        this.filterRemove.add("mkv");
        this.filterRemove.add("flv");
        this.filterRemove.add("pdf");
        this.filterRemove.add("doc");
        this.filterRemove.add("docx");
        this.filterRemove.add("ppt");
        this.filterRemove.add("pptx");
        this.filterRemove.add("xls");
        this.filterRemove.add("xlsx");
        this.filterRemove.add("csv");
        this.filterRemove.add("dbk");
        this.filterRemove.add("dot");
        this.filterRemove.add("dotx");
        this.filterRemove.add("gdoc");
        this.filterRemove.add("pdax");
        this.filterRemove.add("pda");
        this.filterRemove.add("rtf");
        this.filterRemove.add("rpt");
        this.filterRemove.add("stw");
        this.filterRemove.add("txt");
        this.filterRemove.add("uof");
        this.filterRemove.add("uoml");
        this.filterRemove.add("wps");
        this.filterRemove.add("wpt");
        this.filterRemove.add("wrd");
        this.filterRemove.add("xps");
        this.filterRemove.add("epub");
    }

    public static ArrayList<String> getExternalMountss() {
        String[] split;
        String[] split2;
        byte[] bArr = new byte[0];
        ArrayList<String> arrayList = new ArrayList<>();
        String str = "";
        try {
            Process start = new ProcessBuilder(new String[0]).command("mount").redirectErrorStream(true).start();
            start.waitFor();
            InputStream inputStream = start.getInputStream();
            while (inputStream.read(new byte[1024]) != -1) {
                str = str + new String(bArr);
            }
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (String str2 : str.split(IOUtils.LINE_SEPARATOR_UNIX)) {
            if (!str2.toLowerCase(Locale.US).contains("asec") && str2.matches("(?i).*vold.*(vfat|ntfs|exfat|fat32|ext3|ext4).*rw.*")) {
                for (String str3 : str2.split(MinimalPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR)) {
                    if (str3.startsWith("/") && !str3.toLowerCase(Locale.US).contains("vold")) {
                        arrayList.add(str3);
                    }
                }
            }
        }
        return arrayList;
    }

    public static boolean isSymlink(File file) throws IOException {
        if (file != null) {
            if (file.getParent() != null) {
                file = new File(file.getParentFile().getCanonicalFile(), file.getName());
            }
            return !file.getCanonicalFile().equals(file.getAbsoluteFile());
        }
        throw new NullPointerException("File must not be null");
    }
}
