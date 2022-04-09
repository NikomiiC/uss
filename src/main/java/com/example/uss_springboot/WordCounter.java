package com.example.uss_springboot;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WordCounter {
    String urlString = "http://localhost:8983/solr/uss";
    SolrClient solr = new HttpSolrClient.Builder(urlString).build();
    SolrQuery query = new SolrQuery();

    public WordCounter(){

    }

    public static void main(String[] args) {

        WordCounter count = new WordCounter();
        int uniqueWords = count.getUniqueWords();
        int totalWords = count.getTotalWords();
        System.out.println("Total Words: " + totalWords);
        System.out.println("Unique Words: " + uniqueWords);
    }

    public int getUniqueWords() {
        int uniqueWords = 0;

        List<FacetField> documents = getFacetFields();

        if (!documents.isEmpty()) {
            uniqueWords = documents.get(0).getValues().size();
        }

        return uniqueWords;
    }

    public int getTotalWords() {
        int totalWords = 0;

        List<FacetField> documents = getFacetFields();

        if (!documents.isEmpty()) {
            for (FacetField.Count document : documents.get(0).getValues()) {
                totalWords += document.getCount();
            }
        }

        return totalWords;
    }

    public List<org.apache.solr.client.solrj.response.FacetField> getFacetFields() {
        // Query all words and only search for content comment
        query.addFacetField("a_content_comment");
        query.setFacetLimit(2000000);
        query.setQuery("*:*");

        List<org.apache.solr.client.solrj.response.FacetField> documents = new ArrayList<>();

        try {
            QueryResponse response = solr.query(query);

            documents = response.getFacetFields();
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return documents;
    }
}
