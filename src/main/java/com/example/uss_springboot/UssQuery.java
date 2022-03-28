package com.example.uss_springboot;

import java.util.List;

public class UssQuery {

    private List<String> Queries;

    public UssQuery() {
    }

    public UssQuery(List<String> queries) {
        Queries = queries;
    }

    public void setQueries(List<String> queries) {
        Queries = queries;
    }

    public List<String> getQueries() {
        return Queries;
    }
}
