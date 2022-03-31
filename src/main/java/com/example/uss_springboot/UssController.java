package com.example.uss_springboot;

import org.apache.solr.common.SolrDocumentList;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class UssController {
    Query_MoveToJQuery qm = new Query_MoveToJQuery();
    SolrDocumentList solrDocumentListReturn = new SolrDocumentList();
    final static String FIELD_NAME = "CHECK_FIELD";
    final static String FIELD_VALUE = "INVALID";

    @GetMapping("/test")
    public String test(){
        SolrJQuery sj = new SolrJQuery("+worst -place");
        return "testing";
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
        solrDocumentListReturn = qm.BiQuery(sampleQuery);
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

    @GetMapping("/testtest")
    public String testTest(){
        return qm.TestGetListFromProperties();
    }
}

