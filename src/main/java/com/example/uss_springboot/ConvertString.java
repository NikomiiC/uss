package com.example.uss_springboot;

import java.util.Locale;

public class ConvertString {

    // Convert field
    public static String convertField(String filter) {
        String newFilter = "";
        filter = filter.toLowerCase();

        // Fill up the appropriate filters
        // Rating
        if (filter.startsWith("rating")){
            switch (filter) {
                case "rating>=1": newFilter += "a_rating:[1 TO *]";
                    break;
                case "rating>=2": newFilter += "a_rating:[2 TO *]";
                    break;
                case "rating>=3": newFilter += "a_rating:[3 TO *]";
                    break;
                case "rating>=4": newFilter += "a_rating:[4 TO *]";
                    break;
                case "rating>=5": newFilter += "a_rating:[5 TO *]";
                    break;
                default: newFilter += "a_rating:[select all]";
                    break;
            }
        }

        return newFilter;
    }

    // Convert the sorting string
    public static String convertSort(String sort) {
        String newSort = "a_count desc";

        sort = sort.toLowerCase();

        if (sort.startsWith("contributions")) {
            newSort += ",a_reviewer_contribution" + upDown(sort);
        } else if (sort.startsWith("upvotes")) {
            newSort += ",a_comment_upvotes" + upDown(sort);
        }

        return newSort;
    }

    // Convert the asc and desc
    private static String upDown(String sort){
        if (sort.endsWith("asc")){
            return " asc";
        } else {
            return " desc";
        }
    }

}
