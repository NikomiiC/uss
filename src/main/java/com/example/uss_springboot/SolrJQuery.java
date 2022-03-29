package com.example.uss_springboot;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;

import java.io.IOException;

public class SolrJQuery {
    String urlString = "http://localhost:8983/solr/uss";
    public SolrJQuery(String q) {
        SolrClient solr = new HttpSolrClient.Builder(urlString).build();
        SolrQuery query = new SolrQuery();
        query.setQuery(q);
        //query.setRequestHandler("/spellCheckCompRH");
        try {
            QueryResponse response = solr.query(query);
            SolrDocumentList list = response.getResults();
            System.out.println(list.toString());
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
