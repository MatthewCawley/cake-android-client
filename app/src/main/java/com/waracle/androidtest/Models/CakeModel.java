package com.waracle.androidtest.Models;

import android.support.annotation.DrawableRes;

import com.waracle.androidtest.R;

import org.json.JSONException;
import org.json.JSONObject;

public class CakeModel {
    private String title;
    private String description;
    private String imagePath;

    public CakeModel(JSONObject element) {
        try {
            this.title = element.getString("title");
            this.description = element.getString("desc");
            this.imagePath = element.getString("image");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getImagePath() {
        return imagePath;
    }

    public @DrawableRes int getPlaceHolderImage() {
        return  R.drawable.cake_placeholder;
    }
}
