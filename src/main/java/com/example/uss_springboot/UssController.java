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
    SolrJUpdate solrJUpdate = new SolrJUpdate();
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
    public List<UssDocument> getQuery(String query, String filter, String sort){//sss&field=a_docid:>1 a_rating:>3
        List<UssDocument> ussDocuments = new ArrayList<UssDocument>();
        System.out.println("QUERY IS: " + query + "FILTER IS: "+ filter + "SORT IS: " + sort);
        // Goes through the collection
        solrDocumentListReturn = solrJQuery.mixQuery(query,
                ConvertString.convertField(filter),
                ConvertString.convertSort(sort));

        if(solrDocumentListReturn.getNumFound() == (long) 0){
            ussDocuments.add(new UssDocument(solrJQuery.afterSpellCheck));
            return ussDocuments;
        }
        else if(solrDocumentListReturn.size() == 1 &&
                solrDocumentListReturn.get(0).getFirstValue(FIELD_NAME).equals(FIELD_VALUE)){
            //INVALID SEARCH ie. empty input
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid query");
        }

        // Stores them into a new document format for outputting
        for (int i = 0; i < solrDocumentListReturn.size(); i++){
            SolrDocument temp = solrDocumentListReturn.get(i);

            ussDocuments.add(UssDocument.CreateOutput(temp));
        }

        // return output
        return ussDocuments;
    }

    @GetMapping("/documents/count")
    public Boolean updateCount(String id) {
        Boolean update = solrJUpdate.tryUpdateCount(id);

        if (!update){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not able to update");
        }

        return true;
    }

    @DeleteMapping("/documents")
    public Boolean deleteDocument(String id) {
        Boolean update = solrJUpdate.tryDelete(id);

        if (!update){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not able to delete");
        }

        return true;
    }

    @GetMapping("/nicole")
    public String nicoleTest(){
        String sampleQuery = "where to fjnd a delicuis food in uss";
        String displayStr = "";
        solrDocumentListReturn = solrJQuery.mixQuery(sampleQuery,"", "");
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

