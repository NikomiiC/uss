package com.example.uss_springboot;

import org.apache.solr.client.solrj.beans.Field;
import org.apache.solr.common.SolrDocument;

import java.util.ArrayList;
import java.util.List;

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
    private int Count;
    private String SpellCheck;

    public UssDocument(){}

    public UssDocument(String docId, String user, String rating, String country, String date, String contributions, String commentLike, String titleComment, String contentComment, String url, int count){
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
        Count = count;
    }

    public UssDocument(String spellCheck) {
        SpellCheck = spellCheck;
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

    @Field("a_count")
    public void setCount(int count) {
        Count = count;
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

    public int getCount() {
        return Count;
    }

    public String getSpellCheck() { return SpellCheck; }

    public static UssDocument CreateOutput(SolrDocument temp){
        Object id = temp.getFieldValue("a_docId");
        String stringId = id.toString();
        Object user = temp.getFieldValue("a_reviewer_name");
        String stringUser = user.toString();
        Object rating = temp.getFieldValue("a_rating");
        String stringRating = rating.toString();
        Object country = temp.getFieldValue("a_reviewer_location");
        String stringCountry = country.toString();
        Object date = temp.getFieldValue("a_comment_date");
        String stringDate = date.toString();
        Object contributions = temp.getFieldValue("a_reviewer_contribution");
        String stringContributions;
        if (contributions == null) {
            stringContributions = "";
        } else {
            stringContributions = contributions.toString();
        }
        Object commentLike = temp.getFieldValue("a_comment_upvotes");
        String stringCommentLike;
        if (commentLike == null) {
            stringCommentLike = "";
        } else {
            stringCommentLike = commentLike.toString();
        }
        Object titleComment = temp.getFieldValue("a_title_comment");
        String stringTitleComment = titleComment.toString();
        Object contentComment = temp.getFieldValue("a_content_comment");
        String stringContentComment = contentComment.toString();
        Object url = temp.getFieldValue("a_url");
        String stringUrl = url.toString();
        Object count = temp.getFieldValue("a_count");
        Integer integerCount = 0;
        if (count != null) {
            if (count instanceof ArrayList){
                List<Long> tempList = (ArrayList<Long>) count;

                integerCount = tempList.get(0).intValue();
            }
        }

        return new UssDocument(stringId, stringUser, stringRating, stringCountry, stringDate, stringContributions, stringCommentLike, stringTitleComment, stringContentComment, stringUrl, integerCount);
    }
}
