package com.example.gallerylock.wallet;

import android.content.Context;
import android.content.res.AssetManager;

import com.example.gallerylock.common.Constants;
import com.example.gallerylock.securitylocks.SecurityLocksCommon;
import com.example.gallerylock.storageoption.StorageOptionsCommon;
import com.fasterxml.jackson.core.util.MinimalPrettyPrinter;

import org.apache.commons.io.IOUtils;
import org.apache.http.protocol.HTTP;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Pattern;

/* loaded from: classes2.dex */
public class WalletCommon {
    public static int WalletCurrentCategoryId = 0;
    public static int walletCategoryScrollIndex = 0;
    public static String walletCurrentCategoryName = "";
    public static String walletCurrentEntryName = "";

    public WalletCategoriesPojo getCurrentCategoryData(Context context, String str) {
        AssetManager assets = context.getAssets();
        try {
            return WalletCategoryReadXml.ReadCategoryFromXml(IOUtils.toString(assets.open("default-categories/Category_" + str + ".xml"), HTTP.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void createDefaultCategories(Context context) {
        WalletCategoriesDAL walletCategoriesDAL = new WalletCategoriesDAL(context);
        new Constants();
        if (walletCategoriesDAL.GetWalletCategoriesIntegerEntity("SELECT \t COUNT(*)\t\t\t\t\t   FROM TableWalletCategories WHERE WalletCategoriesFileIsDecoy = " + SecurityLocksCommon.IsFakeAccount) < 14) {
            addCategoryFromAssetToDb(context);
        }
    }

    public void addCategoryFromAssetToDb(Context context) {
        WalletCategoriesDAL walletCategoriesDAL = new WalletCategoriesDAL(context);
        AssetManager assets = context.getAssets();
        new Constants();
        try {
            for (String str : assets.list("default-categories")) {
                String FileName = FileName(str);
                if (!walletCategoriesDAL.IsWalletCategoryAlreadyExist("SELECT \t     * \t\t\t\t\t\t   FROM  TableWalletCategories WHERE WalletCategoriesFileIsDecoy = " + SecurityLocksCommon.IsFakeAccount + " AND WalletCategoriesFileName = '" + FileName + "'")) {
                    WalletCategoriesPojo currentCategoryData = getCurrentCategoryData(context, FileName);
                    WalletCategoriesFileDB_Pojo walletCategoriesFileDB_Pojo = new WalletCategoriesFileDB_Pojo();
                    walletCategoriesFileDB_Pojo.setCategoryFileName(currentCategoryData.getCategoryName());
                    walletCategoriesFileDB_Pojo.setCategoryFileCreatedDate(getCurrentDate());
                    walletCategoriesFileDB_Pojo.setCategoryFileModifiedDate(getCurrentDate());
                    walletCategoriesFileDB_Pojo.setCategoryFileLocation(StorageOptionsCommon.STORAGEPATH + StorageOptionsCommon.WALLET + FileName);
                    walletCategoriesFileDB_Pojo.setCategoryFileIconIndex(currentCategoryData.getCategoryIconIndex());
                    walletCategoriesFileDB_Pojo.setCategoryFileSortBy(0);
                    walletCategoriesFileDB_Pojo.setCategoryFileIsDecoy(SecurityLocksCommon.IsFakeAccount);
                    walletCategoriesDAL.OpenWrite();
                    walletCategoriesDAL.addWalletCategoriesInfoInDatabase(walletCategoriesFileDB_Pojo);
                    walletCategoriesDAL.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getCurrentDate() {
        return new SimpleDateFormat("EEE d MMM yyyy, HH:mm:ss aaa").format(Calendar.getInstance().getTime());
    }

    public static String FileName(String str) {
        String name = new File(str).getName();
        for (int length = name.length() - 1; length > 0; length--) {
            if (name.charAt(length) == "_".charAt(0)) {
                int lastIndexOf = name.lastIndexOf(".");
                return lastIndexOf > 0 ? name.substring(length + 1, lastIndexOf) : name;
            }
        }
        return "";
    }

    public String capitalizeFirstLetter(String str) {
        if (str.length() <= 0) {
            return "";
        }
        StringBuffer stringBuffer = new StringBuffer();
        for (String str2 : str.split(MinimalPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR)) {
            char[] charArray = str2.trim().toCharArray();
            charArray[0] = Character.toUpperCase(charArray[0]);
            stringBuffer.append(new String(charArray));
            stringBuffer.append(MinimalPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR);
        }
        return stringBuffer.toString().trim();
    }

    public boolean isNoSpecialCharsInName(String str) {
        return Pattern.compile("^[a-zA-Z.0-9 -]+$").matcher(str).matches();
    }
}
