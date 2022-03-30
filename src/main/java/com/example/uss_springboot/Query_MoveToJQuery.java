package com.example.uss_springboot;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 1. biWord
 *    1. cut sentence to biWord term
 *       ie. what a fun place --> what a, a fun, fun place
 *    2. Loop each biword with single query
 *    3. Append result together
 * 2. create a list of passive list ie. but, however...
 *    1. ie. the service is good but it is expensive --> +the service is good -it is expensive
 * 3. remove stopwords in the query
 *    1. export solr stopwords, might need add more
 *    2. Delete stopwords in the query
 *       ie. the service is good but it is expensive --> service good but expensive
 *    3. redo for step 2
 */


public class Query_MoveToJQuery {



    @Value("#{'${Opposition}'.split(',')}")
    private List<String> listOfOpposition;

    @Value("#{'${StopWords}'.split(',')}")
    private List<String> listOfStopWords;

    String urlString = "http://localhost:8983/solr/uss";
    SolrClient solr = new HttpSolrClient.Builder(urlString).build();
    SolrQuery query = new SolrQuery();
    final static String FIELD_NAME = "CHECK_FIELD";
    final static String FIELD_VALUE = "INVALID";
    final static String FIELD_ID = "doc_id";//TODO: not sure if the field name is correct

    public SolrDocumentList BiQuery(String q){
        // split by space
        String[] arr_splitBySpace = q.split(" ");
        int noOfWord = arr_splitBySpace.length;
        ArrayList<String> biWordList = new ArrayList<>();
        SolrDocumentList solrDocumentList = new SolrDocumentList();

        //check size
        if(noOfWord == 1 || noOfWord == 2){
            //one term  || only one pair of biWord --> call SingleTermQuery()
            solrDocumentList = SingleTermQuery(q);
        }
        else if(noOfWord>2){
            for(int i = 1; i < noOfWord; i++){
                biWordList.add(arr_splitBySpace[i-1] + " " + arr_splitBySpace[i]);
            }
            //loop SingleTermQuery
            for(String eachBiWord : biWordList){

                SolrDocumentList new_solrDocumentList = new SolrDocumentList();
                new_solrDocumentList = SingleTermQuery(eachBiWord);

                // feel might take a long time.....
                //todo need find a fast way
                solrDocumentList = DuplicateCheck(solrDocumentList, new_solrDocumentList);
            }
        }
        else{
            //invalid search
            SolrDocument invalidSolrDocument = new SolrDocument();
            invalidSolrDocument.setField(FIELD_NAME, FIELD_VALUE);
            solrDocumentList.set(0,invalidSolrDocument);
        }


        return solrDocumentList;
    }

    private SolrDocumentList DuplicateCheck(SolrDocumentList solrDocumentList, SolrDocumentList new_solrDocumentList) {
        for(SolrDocument d : new_solrDocumentList){
            if(!solrDocumentList.contains(d)){
                solrDocumentList.add(d);
            }
        }
        return solrDocumentList;
    }

    public SolrDocumentList SingleTermQuery(String q) {

        query.setQuery(q);
        SolrDocumentList solrDocumentList = new SolrDocumentList();
        //query.setRequestHandler("/spellCheckCompRH");
        try {
            QueryResponse response = solr.query(query);
            solrDocumentList = response.getResults();
            System.out.println(solrDocumentList.toString());
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return solrDocumentList;
    }
}
