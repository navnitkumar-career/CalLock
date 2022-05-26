package com.example.gallerylock.wallet;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/* loaded from: classes2.dex */
public class WalletCategoryReadXml {
    public static WalletCategoriesPojo ReadCategoryFromXml(String str) {
        Exception e;
        WalletCategoriesPojo walletCategoriesPojo = null;
        try {
            XmlPullParser newPullParser = XmlPullParserFactory.newInstance().newPullParser();
            InputStreamReader inputStreamReader = new InputStreamReader(new ByteArrayInputStream(str.getBytes()));
            ArrayList arrayList = new ArrayList();
            newPullParser.setInput(inputStreamReader);
            Boolean bool = false;
            WalletCategoriesPojo walletCategoriesPojo2 = null;
            WalletCategoriesFieldPojo walletCategoriesFieldPojo = null;
            String str2 = "";
            for (int eventType = newPullParser.getEventType(); eventType != 1; eventType = newPullParser.next()) {
                if (eventType == 2) {
                    str2 = newPullParser.getName();
                    if (str2.equals("CategoryInfo")) {
                        bool = true;
                        walletCategoriesPojo2 = new WalletCategoriesPojo();
                    } else if (str2.equals("Field")) {
                        walletCategoriesFieldPojo = new WalletCategoriesFieldPojo();
                    }
                } else if (eventType == 3) {
                    if (newPullParser.getName() == "CategoryInfo") {
                        bool = false;
                    }
                    str2 = "";
                } else if (eventType == 4) {
                    try {
                        if (bool.booleanValue() && walletCategoriesPojo2 != null) {
                            char c = 65535;
                            switch (str2.hashCode()) {
                                case -900498653:
                                    if (str2.equals("IsSecured")) {
                                        c = 3;
                                        break;
                                    }
                                    break;
                                case 67875034:
                                    if (str2.equals("Field")) {
                                        c = 2;
                                        break;
                                    }
                                    break;
                                case 590368969:
                                    if (str2.equals("CategoryName")) {
                                        c = 0;
                                        break;
                                    }
                                    break;
                                case 593035193:
                                    if (str2.equals("IconIndex")) {
                                        c = 1;
                                        break;
                                    }
                                    break;
                            }
                            if (c == 0) {
                                walletCategoriesPojo2.setCategoryName(newPullParser.getText());
                            } else if (c == 1) {
                                walletCategoriesPojo2.setCategoryIconIndex(Integer.parseInt(newPullParser.getText()));
                            } else if (c == 2) {
                                walletCategoriesFieldPojo.setFieldName(newPullParser.getText());
                            } else if (c == 3 && walletCategoriesFieldPojo != null) {
                                walletCategoriesFieldPojo.setSecured(Boolean.parseBoolean(newPullParser.getText()));
                                arrayList.add(walletCategoriesFieldPojo);
                                walletCategoriesFieldPojo = null;
                            }
                        }
                    } catch (Exception e2) {
                        e = e2;
                        walletCategoriesPojo = walletCategoriesPojo2;
                        e.printStackTrace();
                        return walletCategoriesPojo;
                    }
                }
            }
            walletCategoriesPojo2.setCategoryFields(arrayList);
            return walletCategoriesPojo2;
        } catch (Exception e3) {
            e = e3;
        }
        return walletCategoriesPojo;
    }
}
