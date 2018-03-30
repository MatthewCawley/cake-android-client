package com.waracle.androidtest.ViewHolders;

import android.widget.ImageView;
import android.widget.TextView;

public class CakeListItemViewHolder {

    TextView titleTextView;
    TextView descriptionTextView;
    ImageView imageView;

    public TextView getTitleTextView() {
        return titleTextView;
    }


    public void setTitleTextView(TextView titleTextView) {
        this.titleTextView = titleTextView;
    }

    public TextView getDescriptionTextView() {
        return descriptionTextView;
    }

    public void setDescriptionTextView(TextView descriptionTextView) {
        this.descriptionTextView = descriptionTextView;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }
}
