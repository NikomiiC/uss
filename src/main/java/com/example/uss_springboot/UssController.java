package com.example.uss_springboot;

import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@RestController
public class UssController {
    Query_MoveToJQuery qm = new Query_MoveToJQuery();
    SolrJQuery solrJQuery = new SolrJQuery();
    SolrDocumentList solrDocumentListReturn = new SolrDocumentList();
    final static String FIELD_NAME = "CHECK_FIELD";
    final static String FIELD_VALUE = "INVALID";

    @Value("${Opposition.test}")//TODO cant get from properties, will check ltR
    String opposition_Str;

    @GetMapping("/test")
    public String test(){
//        SolrJQuery sj = new SolrJQuery("+worst -place");
//        return "testing"

        return opposition_Str;
    }

    @GetMapping("/documents")
    public List<UssDocument> getQuery(String query){
        List<UssDocument> ussDocuments = new ArrayList<UssDocument>();
        // Goes through the collection
        solrDocumentListReturn = solrJQuery.MixQuery(query);
        if(solrDocumentListReturn.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "no query found");
        }
        else if(solrDocumentListReturn.size() == 1 &&
                solrDocumentListReturn.get(0).getFirstValue(FIELD_NAME).equals(FIELD_VALUE)){
            //INVALID SEARCH ie. empty input
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid query");
        }

        // Stores them into a new document format for outputting
        for (int i = 0; i < solrDocumentListReturn.size(); i++){
            SolrDocument temp = solrDocumentListReturn.get(i);

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
            Object contributions = temp.getFieldValue("a_reviewer_contributions");
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

            ussDocuments.add(new UssDocument(stringId, stringUser, stringRating, stringCountry, stringDate, stringContributions, stringCommentLike, stringTitleComment, stringContentComment, stringUrl));
        }

        // return output
        return ussDocuments;
    }

    @GetMapping("/nicole")
    public String nicoleTest(){
        String sampleQuery = "";
        String displayStr = "";
        solrDocumentListReturn = solrJQuery.MixQuery(sampleQuery);
        if(solrDocumentListReturn.isEmpty()){
            displayStr = "Result not found!";
        }
        else if(solrDocumentListReturn.size() == 1 &&
                solrDocumentListReturn.get(0).getFirstValue(FIELD_NAME).equals(FIELD_VALUE)){
            //INVALID SEARCH ie. empty input
            displayStr = "invalid query";
        }
        else{
            displayStr = "valid query";
        }
        return displayStr;
    }

    @GetMapping("/glenn")
    public String glennTest(){
        String sampleQuery = "where are the cheap rides";
        String displayStr = "";
        solrDocumentListReturn = qm.StopWordsQuery(sampleQuery);
        if(solrDocumentListReturn.isEmpty()){
            displayStr = "Result not found!";
        }
        else if(solrDocumentListReturn.size() == 1 &&
                solrDocumentListReturn.get(0).getFirstValue(FIELD_NAME).equals(FIELD_VALUE)){
            //INVALID SEARCH ie. empty input
            displayStr = "invalid query";
        }
        else{
            displayStr = "valid query";
        }
        return displayStr;
    }

}

