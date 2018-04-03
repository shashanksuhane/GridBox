package com.suhane.gridbox.repository.model.item;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by shashanksuhane on 03/04/18.
 */

public class Item {

    @SerializedName("uuid")
    @Expose
    private String uuid;
    @SerializedName("imageUrlString")
    @Expose
    private String imageUrlString;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getImageUrlString() {
        return imageUrlString;
    }

    public void setImageUrlString(String imageUrlString) {
        this.imageUrlString = imageUrlString;
    }

}
