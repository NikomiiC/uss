package com.example.uss_springboot;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SolrJUpdate {

    private String urlString = "http://localhost:8983/solr/uss";
    private SolrClient solr = new HttpSolrClient.Builder(urlString).build();
    SolrQuery query = new SolrQuery();

    public SolrJUpdate() {

    }

    public Boolean tryDelete(String id) {
        query.setQuery("a_docId:" + id);
        SolrDocumentList solrDocumentList = new SolrDocumentList();
        //query.setRequestHandler("/spellCheckCompRH");
        try {
            QueryResponse response = solr.query(query);
            solrDocumentList = response.getResults();
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Find the document and delete it
        SolrDocument oldDoc = solrDocumentList.get(0);

        String actualId = (String) oldDoc.getFieldValue("id");
        try {
            solr.deleteById(actualId);
            solr.commit();
        } catch (SolrServerException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public Boolean tryUpdateCount(String id) {
        query.setQuery("a_docId:" + id);
        SolrDocumentList solrDocumentList = new SolrDocumentList();
        //query.setRequestHandler("/spellCheckCompRH");
        try {
            QueryResponse response = solr.query(query);
            solrDocumentList = response.getResults();
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // There should only be 1 document with docId
        if (solrDocumentList.size() > 1) {
            return false;
        }

        // Update the document with count
        SolrDocument oldDoc = solrDocumentList.get(0);
        SolrInputDocument newDoc = new SolrInputDocument();

        String actualId = (String) oldDoc.getFieldValue("id");

        newDoc.addField("a_docId", oldDoc.getFieldValue("a_docId"));
        newDoc.addField("a_reviewer_name", oldDoc.getFieldValue("a_reviewer_name"));
        newDoc.addField("a_rating", oldDoc.getFieldValue("a_rating"));
        newDoc.addField("a_reviewer_location", oldDoc.getFieldValue("a_reviewer_location"));
        newDoc.addField("a_comment_date", oldDoc.getFieldValue("a_comment_date"));
        newDoc.addField("a_reviewer_contribution", oldDoc.getFieldValue("a_reviewer_contribution"));
        newDoc.addField("a_comment_upvotes", oldDoc.getFieldValue("a_comment_upvotes"));
        newDoc.addField("a_title_comment", oldDoc.getFieldValue("a_title_comment"));
        newDoc.addField("a_content_comment", oldDoc.getFieldValue("a_content_comment"));
        newDoc.addField("a_url", oldDoc.getFieldValue("a_url"));

        // Set increments for the count
        Object value = oldDoc.getFieldValue("a_count");
        int actualValue = 0;
        if (value instanceof ArrayList){
            List<Long> tempList = (ArrayList<Long>) value;
            actualValue = tempList.get(0).intValue();
        }

        actualValue++;

        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("set", actualValue);
        newDoc.addField("a_count", map);

        try {
            solr.deleteById(actualId);
            solr.add(newDoc);
            solr.commit();
        } catch (SolrServerException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
