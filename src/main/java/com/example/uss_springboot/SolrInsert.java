package com.example.uss_springboot;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SolrInsert {
    private String urlString = "http://localhost:8983/solr/uss";
    private SolrClient solr = new HttpSolrClient.Builder(urlString).build();

    public static void main(String[] args) throws Exception {
        SolrInsert insert = new SolrInsert();

        // Enter json file
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter file path:");

        String path = scanner.nextLine();

        if (!insert.TryInsertData(path))
        {
            throw new Exception("Failed to execute insert");
        }
    }

    public SolrInsert() {

    }

    public Boolean TryInsertData(String path) throws Exception {
        if (path == null) {
            return false;
        }

        JSONParser parser = new JSONParser();
        List<UssDocument> documents = new ArrayList<UssDocument>();

        try (FileReader reader = new FileReader(path)) {
            Object obj = parser.parse(reader);

            JSONArray dataArr = (JSONArray) obj;

            for (int i = 0; i < dataArr.size(); i++) {
                JSONObject document = (JSONObject) dataArr.get(i);

                documents.add(parseUssObject(document));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }

        try {
            solr.addBeans(documents);
            solr.commit();
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private static UssDocument parseUssObject(JSONObject uss){
        String docId = (String) uss.get("a_docId");
        String user = (String) uss.get("a_reviewer_name");
        String rating = (String) uss.get("a_rating");
        String country = (String) uss.get("a_reviewer_location");
        String date = (String) uss.get("a_comment_date");
        String contributions = (String) uss.get("a_reviewer_contribution");
        String commentLike = (String) uss.get("a_comment_upvotes");
        String titleComment = (String) uss.get("a_title_comment");
        String contentComment = (String) uss.get("a_content_comment");
        String url = (String) uss.get("a_url");

        return new UssDocument(docId, user, rating, country, date, contributions, commentLike, titleComment, contentComment, url);
    }
}
