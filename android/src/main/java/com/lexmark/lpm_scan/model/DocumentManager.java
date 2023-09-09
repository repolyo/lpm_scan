package com.lexmark.lpm_scan.model;
import android.content.Context;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
public class DocumentManager {

    private static DocumentManager instance;

    private List<Page> pages = new ArrayList<>();

    public static DocumentManager getInstance(Context context) {
        if (instance == null) {
            clearOldPages(context);
            instance = new DocumentManager();
        }
        return instance;
    }

    public List<Page> getPages() {
        return pages;
    }

    public void addPage(Page page) {
        pages.add(page);
    }

    private static void clearOldPages(Context context) {
        File folder = context.getExternalFilesDir(null);
        for (File file : folder.listFiles()) {
            file.delete();
        }
    }

}