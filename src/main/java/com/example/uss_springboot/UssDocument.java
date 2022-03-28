package com.example.uss_springboot;

public class UssDocument {

    private String DocId;
    private String User;
    private String Country;
    private String Date;
    private String TitleComment;
    private String ContentComment;

    public UssDocument(){}

    public UssDocument(String docId, String user, String country, String date, String titleComment, String contentComment){
        DocId = docId;
        User = user;
        Country = country;
        Date = date;
        TitleComment = titleComment;
        ContentComment = contentComment;
    }

    public String getDocId(){
        return DocId;
    }

    public String getUser(){
        return User;
    }

    public String getCountry(){
        return Country;
    }

    public String getDate(){
        return Date;
    }

    public String getTitleComment(){
        return TitleComment;
    }

    public String getContentComment(){
        return ContentComment;
    }
}
