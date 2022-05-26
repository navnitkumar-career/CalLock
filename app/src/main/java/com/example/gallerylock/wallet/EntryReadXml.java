package com.example.gallerylock.wallet;

import android.content.Context;

import com.example.gallerylock.Flaes;

import org.apache.commons.io.IOUtils;
import org.apache.http.protocol.HTTP;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/* loaded from: classes2.dex */
public class EntryReadXml {
    public static WalletEntryPojo parseXML(Context context, String str) {
        Exception e;
        WalletEntryPojo walletEntryPojo = null;
        try {
            Boolean bool = false;
            ArrayList arrayList = new ArrayList();
            XmlPullParser newPullParser = XmlPullParserFactory.newInstance().newPullParser();
            newPullParser.setInput(new InputStreamReader(new ByteArrayInputStream(Flaes.getdecodedstring(IOUtils.toString(new FileInputStream(str), HTTP.UTF_8)).getBytes())));
            WalletEntryPojo walletEntryPojo2 = null;
            WalletCategoriesFieldPojo walletCategoriesFieldPojo = null;
            String str2 = "";
            for (int eventType = newPullParser.getEventType(); eventType != 1; eventType = newPullParser.next()) {
                if (eventType == 2) {
                    str2 = newPullParser.getName();
                    if (str2.equals("EntryInfo")) {
                        bool = true;
                        walletEntryPojo2 = new WalletEntryPojo();
                    }
                    if (str2.equalsIgnoreCase("Field")) {
                        walletCategoriesFieldPojo = new WalletCategoriesFieldPojo();
                    }
                } else if (eventType == 3) {
                    if (newPullParser.getName() == "CategoryInfo") {
                        bool = false;
                    }
                    str2 = "";
                } else if (eventType == 4) {
                    try {
                        String text = newPullParser.getText();
                        if (bool.booleanValue() && walletEntryPojo2 != null) {
                            char c = 65535;
                            switch (str2.hashCode()) {
                                case -900498653:
                                    if (str2.equals("IsSecured")) {
                                        c = 4;
                                        break;
                                    }
                                    break;
                                case 2420395:
                                    if (str2.equals("Name")) {
                                        c = 2;
                                        break;
                                    }
                                    break;
                                case 82420049:
                                    if (str2.equals("Value")) {
                                        c = 3;
                                        break;
                                    }
                                    break;
                                case 590368969:
                                    if (str2.equals("CategoryName")) {
                                        c = 0;
                                        break;
                                    }
                                    break;
                                case 1524403709:
                                    if (str2.equals("EntryName")) {
                                        c = 1;
                                        break;
                                    }
                                    break;
                            }
                            if (c == 0) {
                                walletEntryPojo2.setCategoryName(newPullParser.getText());
                            } else if (c == 1) {
                                walletEntryPojo2.setEntryName(newPullParser.getText());
                            } else if (c == 2) {
                                walletCategoriesFieldPojo.setFieldName(text);
                            } else if (c != 3) {
                                if (c == 4) {
                                    walletCategoriesFieldPojo.setSecured(Boolean.valueOf(text).booleanValue());
                                    arrayList.add(walletCategoriesFieldPojo);
                                    walletCategoriesFieldPojo = null;
                                }
                            } else if (!text.equals("none")) {
                                walletCategoriesFieldPojo.setFieldValue(text);
                            } else {
                                walletCategoriesFieldPojo.setFieldValue("");
                            }
                        }
                    } catch (Exception e2) {
                        e = e2;
                        walletEntryPojo = walletEntryPojo2;
                        e.printStackTrace();
                        return walletEntryPojo;
                    }
                }
            }
            walletEntryPojo2.setFields(arrayList);
            return walletEntryPojo2;
        } catch (Exception e3) {
            e = e3;
        }
        return walletEntryPojo;
    }
}
