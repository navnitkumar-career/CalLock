package com.example.gallerylock.privatebrowser;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHost;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* loaded from: classes2.dex */
public class WebAddress {
    static final int MATCH_GROUP_AUTHORITY = 2;
    static final int MATCH_GROUP_HOST = 3;
    static final int MATCH_GROUP_PATH = 5;
    static final int MATCH_GROUP_PORT = 4;
    static final int MATCH_GROUP_SCHEME = 1;
    static final Pattern sAddressPattern = Pattern.compile("(?:(http|https|file)\\:\\/\\/)?(?:([-A-Za-z0-9$_.+!*'(),;?&=]+(?:\\:[-A-Za-z0-9$_.+!*'(),;?&=]+)?)@)?([a-zA-Z0-9 -\ud7ff豈-\ufdcfﷰ-\uffef%_-][a-zA-Z0-9 -\ud7ff豈-\ufdcfﷰ-\uffef%_\\.-]*|\\[[0-9a-fA-F:\\.]+\\])?(?:\\:([0-9]*))?(\\/?[^#]*)?.*", 2);
    private String mAuthInfo;
    private String mHost;
    private String mPath;
    private int mPort;
    private String mScheme;

    public WebAddress(String str) {
        if (str != null) {
            this.mScheme = "";
            this.mHost = "";
            this.mPort = -1;
            this.mPath = "/";
            this.mAuthInfo = "";
            Matcher matcher = sAddressPattern.matcher(str);
            if (matcher.matches()) {
                String group = matcher.group(1);
                if (group != null) {
                    this.mScheme = group.toLowerCase(Locale.ROOT);
                }
                String group2 = matcher.group(2);
                if (group2 != null) {
                    this.mAuthInfo = group2;
                }
                String group3 = matcher.group(3);
                if (group3 != null) {
                    this.mHost = group3;
                }
                String group4 = matcher.group(4);
                if (group4 != null && !group4.isEmpty()) {
                    try {
                        this.mPort = Integer.parseInt(group4);
                    } catch (NumberFormatException e) {
                        throw new RuntimeException("Parsing of port number failed", e);
                    }
                }
                String group5 = matcher.group(5);
                if (group5 != null && !group5.isEmpty()) {
                    if (group5.charAt(0) == '/') {
                        this.mPath = group5;
                    } else {
                        this.mPath = IOUtils.DIR_SEPARATOR_UNIX + group5;
                    }
                }
                if (this.mPort == 443 && "".equals(this.mScheme)) {
                    this.mScheme = "https";
                } else if (this.mPort == -1) {
                    if ("https".equals(this.mScheme)) {
                        this.mPort = 443;
                    } else {
                        this.mPort = 80;
                    }
                }
                if ("".equals(this.mScheme)) {
                    this.mScheme = HttpHost.DEFAULT_SCHEME_NAME;
                    return;
                }
                return;
            }
            throw new IllegalArgumentException("Parsing of address '" + str + "' failed");
        }
        throw new IllegalArgumentException("address can't be null");
    }

    public String toString() {
        String str;
        String str2 = "";
        if ((this.mPort == 443 || !"https".equals(this.mScheme)) && (this.mPort == 80 || !HttpHost.DEFAULT_SCHEME_NAME.equals(this.mScheme))) {
            str = str2;
        } else {
            str = ':' + Integer.toString(this.mPort);
        }
        if (!this.mAuthInfo.isEmpty()) {
            str2 = this.mAuthInfo + '@';
        }
        return this.mScheme + "://" + str2 + this.mHost + str + this.mPath;
    }

    public void setScheme(String str) {
        this.mScheme = str;
    }

    public String getScheme() {
        return this.mScheme;
    }

    public void setHost(String str) {
        this.mHost = str;
    }

    public String getHost() {
        return this.mHost;
    }

    public void setPort(int i) {
        this.mPort = i;
    }

    public int getPort() {
        return this.mPort;
    }

    public void setPath(String str) {
        this.mPath = str;
    }

    public String getPath() {
        return this.mPath;
    }

    public void setAuthInfo(String str) {
        this.mAuthInfo = str;
    }

    public String getAuthInfo() {
        return this.mAuthInfo;
    }
}
