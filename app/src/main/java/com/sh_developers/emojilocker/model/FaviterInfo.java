package com.sh_developers.emojilocker.model;

import org.litepal.crud.DataSupport;


public class FaviterInfo extends DataSupport {
    public String packageName;

    public FaviterInfo() {
    }

    public FaviterInfo(String packageName) {
        this.packageName = packageName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
}
