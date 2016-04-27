package com.quiziz.drive.model;

import com.quiziz.drive.util.QuizizConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by pototo on 28/03/16.
 */
public class Setup {
    private String name;
    private String url;
    private boolean isLogo;

    public Setup(String name, String url){
        this.name = name;
        this.url = url;
    }

    public Setup(boolean isLogo) {
        this.isLogo = isLogo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isLogo() {
        return isLogo;
    }

    public void setLogo(boolean isLogo) {
        this.isLogo = isLogo;
    }
}
