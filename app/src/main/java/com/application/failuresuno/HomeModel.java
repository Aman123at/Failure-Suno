package com.application.failuresuno;

public class HomeModel {

    String id, posttype, category, userid, title, postdata,reported, likes, posttimestamp, thumbnail;

    public HomeModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPosttype() {
        return posttype;
    }

    public void setPosttype(String posttype) {
        this.posttype = posttype;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPostdata() {
        return postdata;
    }

    public void setPostdata(String postdata) {
        this.postdata = postdata;
    }

    public String getReported() {
        return reported;
    }

    public void setReported(String reported) {
        this.reported = reported;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public String getposttimestamp() {
        return posttimestamp;
    }

    public void setposttimestamp(String posttimestamp) {
        this.posttimestamp = posttimestamp;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public HomeModel(String id, String posttype, String category, String userid, String title, String postdata, String reported, String likes, String posttimestamp, String thumbnail) {
        this.id = id;
        this.posttype = posttype;
        this.category = category;
        this.userid = userid;
        this.title = title;
        this.postdata = postdata;
        this.reported = reported;
        this.likes = likes;
        this.posttimestamp = posttimestamp;
        this.thumbnail = thumbnail;
    }
}