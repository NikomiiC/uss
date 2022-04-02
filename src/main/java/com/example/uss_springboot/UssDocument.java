package com.example.uss_springboot;

import org.apache.solr.client.solrj.beans.Field;

public class UssDocument {

    private String DocId;
    private String User;
    private String Rating;
    private String Country;
    private String Date;
    private String Contributions;
    private String CommentLike;
    private String TitleComment;
    private String ContentComment;
    private String Url;

    public UssDocument(){}

    public UssDocument(String docId, String user, String rating, String country, String date, String contributions, String commentLike, String titleComment, String contentComment, String url){
        DocId = docId;
        User = user;
        Rating = rating;
        Country = country;
        Date = date;
        Contributions = contributions;
        CommentLike = commentLike;
        TitleComment = titleComment;
        ContentComment = contentComment;
        Url = url;
    }

    @Field("a_docId")
    public void setDocId(String docId){
        DocId = docId;
    }

    @Field("a_reviewer_name")
    public void setUser(String user){
        User = user;
    }

    @Field("a_rating")
    public void setRating(String rating) {
        Rating = rating;
    }

    @Field("a_reviewer_location")
    public void setCountry(String country) {
        Country = country;
    }

    @Field("a_comment_date")
    public void setDate(String date) {
        Date = date;
    }

    @Field("a_reviewer_contributions")
    public void setContributions(String contributions) {
        Contributions = contributions;
    }

    @Field("a_comment_upvotes")
    public void setCommentLike(String commentLike) {
        CommentLike = commentLike;
    }

    @Field("a_title_comment")
    public void setTitleComment(String titleComment) {
        TitleComment = titleComment;
    }

    @Field("a_content_comment")
    public void setContentComment(String contentComment) {
        ContentComment = contentComment;
    }

    @Field("a_url")
    public void setUrl(String url) {
        Url = url;
    }

    public String getDocId(){
        return DocId;
    }

    public String getUser(){
        return User;
    }

    public String getRating() {
        return Rating;
    }

    public String getCountry(){
        return Country;
    }

    public String getDate(){
        return Date;
    }

    public String getContributions() {
        return Contributions;
    }

    public String getCommentLike() {
        return CommentLike;
    }

    public String getTitleComment(){
        return TitleComment;
    }

    public String getContentComment(){
        return ContentComment;
    }

    public String getUrl() { return Url; }
}
