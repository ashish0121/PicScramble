package com.example.ashishrmehta.json;
import com.google.gson.annotations.SerializedName;

public class Photo {

    @SerializedName("id")
    String id;

    @SerializedName("owner")
    String owner;

    @SerializedName("secret")
    String secret;

    @SerializedName("server")
    String server;

    @SerializedName("farm")
    String farm;

    @SerializedName("title")
    String title;

    @SerializedName("ispublic")
    String ispublic;

    @SerializedName("isfriend")
    String isfriend;

    @SerializedName("isfamily")
    String isfamily;

    @SerializedName("url_n")
    String url_n;

    @SerializedName("height_n")
    String height_n;

    @SerializedName("width_n")
    String width_n;

    public String getId() {
        return id;
    }

    public String getUrl_n() {
        return url_n;
    }

    public String getOwner() {return owner; }

    public String getTitle() {return title; }
}
