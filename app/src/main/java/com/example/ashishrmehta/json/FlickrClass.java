package com.example.ashishrmehta.json;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Ashish.R.Mehta on 04-01-2018.
 */
public class FlickrClass {

    @SerializedName("photos")
    Photos photos;

    @SerializedName("stat")
    String stat;

    public Photos getPhotos() {
        return photos;
    }
}
