package com.example.gallerylock.privatebrowser;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gallerylock.R;
import com.fasterxml.jackson.core.util.MinimalPrettyPrinter;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/* loaded from: classes2.dex */
public class SearchAdapter extends BaseAdapter implements Filterable {
    private static final int API = Build.VERSION.SDK_INT;
    private static final String ENCODING = "ISO-8859-1";
    private static final long INTERVAL_DAY = 86400000;
    private final Context mContext;
    private SearchFilter mFilter;
    private final String mSearchSubtitle;
    private boolean mIsExecuting = false;
    private List<HistoryItem> mFilteredList = new ArrayList();
    private List<HistoryItem> mSuggestions = new ArrayList();

    @Override // android.widget.Adapter
    public long getItemId(int i) {
        return 0L;
    }

    public SearchAdapter(Context context) {
        this.mContext = context;
        this.mSearchSubtitle = context.getString(R.string.suggestion);
        new Thread(new Runnable() { // from class: net.newsoftwares.hidepicturesvideos.privatebrowser.SearchAdapter.1
            @Override // java.lang.Runnable
            public void run() {
                SearchAdapter searchAdapter = SearchAdapter.this;
                searchAdapter.deleteOldCacheFiles(searchAdapter.mContext);
            }
        }).start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void deleteOldCacheFiles(Context context) {
        File file = null;
        String[] list = new File(context.getCacheDir().toString()).list(new NameFilter());
        long currentTimeMillis = System.currentTimeMillis() - INTERVAL_DAY;
        for (String str : list) {
            File file2 = new File(file.getPath() + str);
            if (currentTimeMillis > file2.lastModified()) {
                file2.delete();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public class NameFilter implements FilenameFilter {
        private static final String ext = ".sgg";

        private NameFilter() {
        }

        @Override // java.io.FilenameFilter
        public boolean accept(File file, String str) {
            return str.endsWith(ext);
        }
    }

    @Override // android.widget.Adapter
    public int getCount() {
        List<HistoryItem> list = this.mFilteredList;
        if (list != null) {
            return list.size();
        }
        return 0;
    }

    @Override // android.widget.Adapter
    public Object getItem(int i) {
        return this.mFilteredList.get(i);
    }

    @Override // android.widget.Adapter
    public View getView(int i, View view, ViewGroup viewGroup) {
        SuggestionHolder suggestionHolder;
        if (view == null) {
            view = ((Activity) this.mContext).getLayoutInflater().inflate(R.layout.two_line_autocomplete, viewGroup, false);
            suggestionHolder = new SuggestionHolder();
            suggestionHolder.mTitle = (TextView) view.findViewById(R.id.title);
            suggestionHolder.mUrl = (TextView) view.findViewById(R.id.url);
            suggestionHolder.mImage = (ImageView) view.findViewById(R.id.suggestionIcon);
            view.setTag(suggestionHolder);
        } else {
            suggestionHolder = (SuggestionHolder) view.getTag();
        }
        HistoryItem historyItem = this.mFilteredList.get(i);
        suggestionHolder.mTitle.setText(historyItem.getTitle());
        suggestionHolder.mUrl.setText(historyItem.getUrl());
        return view;
    }

    public void setContents(List<HistoryItem> list) {
        List<HistoryItem> list2 = this.mFilteredList;
        if (list2 != null) {
            list2.clear();
            this.mFilteredList.addAll(list);
        } else {
            this.mFilteredList = list;
        }
        notifyDataSetChanged();
    }

    @Override // android.widget.Filterable
    public Filter getFilter() {
        if (this.mFilter == null) {
            this.mFilter = new SearchFilter();
        }
        return this.mFilter;
    }

    /* loaded from: classes2.dex */
    public class SearchFilter extends Filter {
        public SearchFilter() {
        }

        @Override // android.widget.Filter
        protected FilterResults performFiltering(CharSequence charSequence) {
            FilterResults filterResults = new FilterResults();
            if (charSequence == null) {
                return filterResults;
            }
            String lowerCase = charSequence.toString().toLowerCase(Locale.getDefault());
            if (!SearchAdapter.this.mIsExecuting) {
                new RetrieveSearchSuggestions().execute(lowerCase);
            }
            return filterResults;
        }

        @Override // android.widget.Filter
        public CharSequence convertResultToString(Object obj) {
            return ((HistoryItem) obj).getUrl();
        }

        @Override // android.widget.Filter
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            SearchAdapter searchAdapter = SearchAdapter.this;
            searchAdapter.mFilteredList = searchAdapter.mSuggestions;
            SearchAdapter.this.notifyDataSetChanged();
        }
    }

    /* loaded from: classes2.dex */
    private class SuggestionHolder {
        ImageView mImage;
        TextView mTitle;
        TextView mUrl;

        private SuggestionHolder() {
        }
    }

    /* loaded from: classes2.dex */
    private class RetrieveSearchSuggestions extends AsyncTask<String, Void, List<HistoryItem>> {
        private XmlPullParserFactory mFactory;
        private XmlPullParser mXpp;

        private RetrieveSearchSuggestions() {
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public List<HistoryItem> doInBackground(String... strArr) {
            Throwable th;
            SearchAdapter.this.mIsExecuting = true;
            ArrayList arrayList = new ArrayList();
            int i = 0;
            String str = strArr[0];
            try {
                str = str.replace(MinimalPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR, "+");
                URLEncoder.encode(str, "ISO-8859-1");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            File downloadSuggestionsForQuery = SearchAdapter.this.downloadSuggestionsForQuery(str);
            if (!downloadSuggestionsForQuery.exists()) {
                return arrayList;
            }
            BufferedInputStream bufferedInputStream = null;
            try {
                BufferedInputStream bufferedInputStream2 = new BufferedInputStream(new FileInputStream(downloadSuggestionsForQuery));
                try {
                    if (this.mFactory == null) {
                        XmlPullParserFactory newInstance = XmlPullParserFactory.newInstance();
                        this.mFactory = newInstance;
                        newInstance.setNamespaceAware(true);
                    }
                    if (this.mXpp == null) {
                        this.mXpp = this.mFactory.newPullParser();
                    }
                    this.mXpp.setInput(bufferedInputStream2, "ISO-8859-1");
                    int eventType = this.mXpp.getEventType();
                    while (eventType != 1) {
                        if (eventType == 2) {
                            if ("suggestion".equals(this.mXpp.getName())) {
                                String attributeValue = this.mXpp.getAttributeValue(null, "data");
                                arrayList.add(new HistoryItem(SearchAdapter.this.mSearchSubtitle + " \"" + attributeValue + '\"', attributeValue, (int) R.drawable.ic_search));
                                i++;
                                if (i >= 5) {
                                    break;
                                }
                            } else {
                                continue;
                            }
                        }
                        eventType = this.mXpp.next();
                    }
                    try {
                        bufferedInputStream2.close();
                    } catch (IOException e2) {
                        e2.printStackTrace();
                    }
                    return arrayList;
                } catch (Exception unused) {
                    bufferedInputStream = bufferedInputStream2;
                    if (bufferedInputStream != null) {
                        try {
                            bufferedInputStream.close();
                        } catch (IOException e3) {
                            e3.printStackTrace();
                        }
                    }
                    return arrayList;
                } catch (Throwable th2) {
                    th = th2;
                    bufferedInputStream = bufferedInputStream2;
                    if (bufferedInputStream != null) {
                        try {
                            bufferedInputStream.close();
                        } catch (IOException e4) {
                            e4.printStackTrace();
                        }
                    }
                    throw th;
                }
            } catch (Exception unused2) {
            } catch (Throwable th3) {
                th = th3;
            }
            return null;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public void onPostExecute(List<HistoryItem> list) {
            SearchAdapter.this.mSuggestions = list;
            SearchAdapter searchAdapter = SearchAdapter.this;
            searchAdapter.mFilteredList = searchAdapter.mSuggestions;
            SearchAdapter.this.notifyDataSetChanged();
            SearchAdapter.this.mIsExecuting = false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public File downloadSuggestionsForQuery(String str) {
        File cacheDir = this.mContext.getCacheDir();
        File file = new File(cacheDir, str.hashCode() + ".sgg");
        if (System.currentTimeMillis() - INTERVAL_DAY < file.lastModified() || !isNetworkConnected(this.mContext)) {
            return file;
        }
        try {
            HttpURLConnection httpURLConnection = (HttpURLConnection) new URL("http://google.com/complete/search?q=" + str + "&output=toolbar&hl=en").openConnection();
            httpURLConnection.setDoInput(true);
            httpURLConnection.connect();
            InputStream inputStream = httpURLConnection.getInputStream();
            if (inputStream != null) {
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                while (true) {
                    int read = inputStream.read();
                    if (read == -1) {
                        break;
                    }
                    fileOutputStream.write(read);
                }
                fileOutputStream.flush();
                fileOutputStream.close();
            }
            file.setLastModified(System.currentTimeMillis());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    private boolean isNetworkConnected(Context context) {
        NetworkInfo activeNetworkInfo = getActiveNetworkInfo(context);
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private NetworkInfo getActiveNetworkInfo(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return null;
        }
        return connectivityManager.getActiveNetworkInfo();
    }
}
