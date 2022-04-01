package com.example.uss_springboot;

import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

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
    public List<UssDocument> getAll(){
        // TODO fill with the necessary list
        List<UssDocument> ussDocuments = new ArrayList<UssDocument>();
        ussDocuments.add(new UssDocument("test", "test", "test", "test", "test", "test"));
        return ussDocuments;
    }

    @PostMapping("/documents")
    public List<UssDocument> getQuery(@RequestBody UssQuery query){
        // TODO link up the querying portion
        return null;
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

