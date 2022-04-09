package com.example.uss_springboot;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.SpellCheckResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.SolrParams;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

/**
 * 1. query whole q, if get response, add in, if not continue
 * 2. whole q remove stopwords and query, if get response add in, if not continue
 * 3. removed q, do biwords, query with "", if get response add in, if not continue
 * 4. removed q, do biwords, qery without "", if get response add in, if not continue
 *
 * every add in do duplicate check
 * at the end check list, if empty make empty return, else just return
 *
 *
 * 1. biWord
 *    1. cut sentence to biWord term
 *       ie. what a fun place --> what a, a fun, fun place
 *    2. Loop each biword with single query
 *    3. Append result together
 *
 *    --------------- remove this point -----------------
 * 2. create a list of passive list ie. but, however...
 *    1. ie. the service is good but it is expensive --> +the service is good -it is expensive
 *
 *    fun place but cheap -> +"fun place" -cheap
 *    user wont get any result of "cheap" in this query
 *    ---------------------------------------------------
 * 3. remove stopwords in the query
 *    1. export solr stopwords, might need add more
 *    2. Delete stopwords in the query
 *       ie. the service is good but it is expensive --> service good but expensive
 *    3. redo for step 2
 */


public class SolrJQuery {

//    String[] opposition_Arr = "but,however,on the contrary,despite".split(",");
    String[] stopwords_str = ("a,an,and,are,as,at,be,but,by,for,it,in,into,is,it,into,not,of,on,or,such,that,the," +
            "their,then,there,these,they,this,to,was,will,with,which,where,when,what,how,why,uss").split(",");
    List<String> listOfStopWords = Arrays.asList(stopwords_str);

    String urlString = "http://localhost:8983/solr/uss";
    SolrClient solr = new HttpSolrClient.Builder(urlString).build();
    SolrQuery query = new SolrQuery();


    static final String FIELD_NAME = "CHECK_FIELD";
    static final String FIELD_VALUE = "INVALID";
    static final String FIELD_DOCID = "a_docId";
    static final String FIELD_RATING = "a_rating";
    static final String FIELD_UPVOTES = "a_comment_upvotes";
    static final String FIELD_COUNT = "a_count";
    static final String FIELD_CONTRIBUTION = "a_reviewer_contribution";
    static final String ORDER_ASC = "asc";
    static final String ORDER_DESC = "desc";
    static final String PREFIX_COMMENT = "a_content_comment:";

    SolrDocumentList result_solrDocumentList = new SolrDocumentList();
    String removedQuery = "";
    public String afterSpellCheck = "";


    public SolrDocumentList biQuery(String q, String filter, String sort){
        // split by space
        q = removedQuery;
        System.out.println("q = removedQuery --> q = " + q);
        String arr_splitBySpace[] = q.split(" ");
        System.out.println("check after split arr_splitBySpace: " + Arrays.toString(arr_splitBySpace));
        int noOfWord = arr_splitBySpace.length;
        System.out.println("noOfWord : " + noOfWord);


        ArrayList<String> biWordList = new ArrayList<>();
        SolrDocumentList solrDocumentList = new SolrDocumentList();
        //check size
        if(noOfWord == 1 || noOfWord == 2){
            //one term  || only one pair of biWord --> call SingleTermQuery()
            solrDocumentList = singleTermQuery(q,filter,sort,true);
        }
        else if(noOfWord>2){
            for(int i = 1; i < noOfWord; i++){
                biWordList.add(arr_splitBySpace[i-1] + " " + arr_splitBySpace[i]);
            }
            //loop SingleTermQuery
            for(String eachBiWord : biWordList){

                SolrDocumentList new_solrDocumentList;
                new_solrDocumentList = singleTermQuery(eachBiWord,filter,sort,true);
                solrDocumentList = duplicateCheck(solrDocumentList, new_solrDocumentList);

            }
        }
        else{
            solrDocumentList = setInavlidDocList();
        }

        return solrDocumentList;
    }

    private SolrDocumentList fullQuery(String q, String filter, String sort){

        SolrDocumentList solrDocumentList_fullQuery = new SolrDocumentList();

        query.setQuery(PREFIX_COMMENT + "\"" + q + "\"");
        if(!filter.isEmpty()){
            query.setFilterQueries(filter);
        }
        if(!sort.isEmpty()){
            doSorting(sort);
        }
        //query.setRequestHandler("/spellCheckCompRH");
        try {
            QueryResponse response = solr.query(query);
            solrDocumentList_fullQuery = response.getResults();
        } catch (SolrServerException | IOException e) {
            e.printStackTrace();
        }
        return solrDocumentList_fullQuery;
    }

    private SolrDocumentList fullStopWordsQuery(String q, String filter, String sort){

        SolrDocumentList solrDocumentList_fullStopWordsQuery = new SolrDocumentList();

        stopWordsCleaning(q);
        if(removedQuery.isEmpty()){
            return new SolrDocumentList();
        }
        query.setQuery("a_content_comment:\""+removedQuery+"\"");
        if(!filter.isEmpty()){
            query.setFilterQueries(filter);
        }
        if(!sort.isEmpty()){
            doSorting(sort);
        }
        try {
            QueryResponse response = solr.query(query);
            solrDocumentList_fullStopWordsQuery = response.getResults();

        } catch (SolrServerException | IOException e) {
            e.printStackTrace();
        }
        return solrDocumentList_fullStopWordsQuery;
    }

    public SolrDocumentList singleTermQuery(String q, String filter, String sort, boolean isDoubleQuotes) {

        SolrDocumentList solrDocumentList = new SolrDocumentList();

        if(isDoubleQuotes){
            query.setQuery("a_content_comment:\""+q+"\"");
        }
        else{
            query.setQuery(PREFIX_COMMENT + q);
        }

        if(!filter.isEmpty()){
            //set filter here
            query.setFilterQueries(filter);
        }
        if(!sort.isEmpty()){
            //do sort function here
            doSorting(sort);

        }
        //query.setRequestHandler("/spellCheckCompRH");
        try {
            QueryResponse response = solr.query(query);
            solrDocumentList = response.getResults();
        } catch (SolrServerException | IOException e) {
            e.printStackTrace();
        }
        System.out.println("SingleTermQuery SIZE OF SOLR DOCUMENT: " + solrDocumentList.getNumFound());
        return solrDocumentList;
    }

    private void doSorting(String sort_afterCheck) {
        String[] pairArr = sort_afterCheck.split(",");
        try{
            for(String eachPair : pairArr){

                //check asc or desc
                String order = eachPair.split(" ")[1];
                String field = eachPair.split(" ")[0];

                if(order.equalsIgnoreCase(ORDER_ASC)){
                    query.setSort(field, SolrQuery.ORDER.asc);
                }
                else if(order.equalsIgnoreCase(ORDER_DESC)){
                    query.setSort(field, SolrQuery.ORDER.desc);
                }
                //else is wrong order, dont do anything

            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


    public SolrDocumentList stopWordsQuery(String q, String filter, String sort){
        //String cleaned_q = StopWordsCleaning(q);
        return singleTermQuery(removedQuery, filter, sort, false);
    }

    public SolrDocumentList mixQuery(String q, String filter, String sort){ //

        afterSpellCheck = "";

        if(q == null || q.isEmpty()){
            return setInavlidDocList();
        }


        query.setRows(15000);

        String filter_afterCheck = setUpFilterAndSortQuery(filter);
        String sort_afterCheck = setUpFilterAndSortQuery(sort);

        SolrDocumentList tmpList;

        SolrDocumentList solrDocumentList_fullQuery = fullQuery(q, filter_afterCheck, sort_afterCheck);
        SolrDocumentList solrDocumentList_fullStopWordsQuery = fullStopWordsQuery(q, filter_afterCheck,
                sort_afterCheck);
        SolrDocumentList solrDocumentList_bi = biQuery(q,filter_afterCheck,sort_afterCheck);
        SolrDocumentList solrDocumentList_sw = stopWordsQuery(q,filter_afterCheck,sort_afterCheck);

        tmpList = duplicateCheck(solrDocumentList_fullQuery,solrDocumentList_fullStopWordsQuery);
        tmpList = duplicateCheck(tmpList,solrDocumentList_bi);
        result_solrDocumentList = duplicateCheck(tmpList,solrDocumentList_sw);

        System.out.println("LENGTH MIXQUERY fullQuery: " + solrDocumentList_fullQuery.getNumFound());
        System.out.println("LENGTH MIXQUERY fullStopWordsQuery: " + solrDocumentList_fullStopWordsQuery.getNumFound());
        System.out.println("LENGTH MIXQUERY bi: " + solrDocumentList_bi.getNumFound());
        System.out.println("LENGTH MIXQUERY sw: " + solrDocumentList_sw.getNumFound());

        System.out.println("FILTER: " + filter + "SORT " + sort);

        if(result_solrDocumentList.getNumFound() == (long) 0){
            //do spell check here
            String arrOfQ[] = q.split(" ");
            ArrayList<String> listOfQuery = new ArrayList<>(Arrays.asList(arrOfQ));
            query.setRequestHandler("/spell");
            for(String s : listOfQuery){
                //query.setQuery("a_content_comment:\""+q+"\"");
                    query.setQuery("a_content_comment:\""+s+"\"");
                try {
                    QueryResponse response = solr.query(query);

                    if(response.getResults().getNumFound() == 0){
                        // do single term spell check

                        SpellCheckResponse spellCheckResponse = response.getSpellCheckResponse();
                        //System.out.println("getSpellCheckResponse : " + (response.getSpellCheckResponse() == null));
                        List<SpellCheckResponse.Suggestion> suggestionList = spellCheckResponse.getSuggestions();
                        for(SpellCheckResponse.Suggestion suggestion : suggestionList){
                            int numOfSugesstion = suggestion.getNumFound();
                            System.out.println("get suggested no: " + numOfSugesstion);
                            //always get first result
                            afterSpellCheck = afterSpellCheck + " " +suggestion.getAlternatives().get(0);
                            System.out.println("afterSpellCheck: " + afterSpellCheck);
                            break;
                        }
                    }
                    else{
                        afterSpellCheck = afterSpellCheck + " " + s;
                        System.out.println("see else : " + afterSpellCheck);
                    }

                } catch (SolrServerException | IOException e) {
                    e.printStackTrace();
                }
            }

            System.out.println("do you mean : " + afterSpellCheck.trim());

        }
        return result_solrDocumentList;

    }

    private String setUpFilterAndSortQuery(String value_passed) {//value_filter = a_rating:[1 TO *]
        /**
         * a drop down menu to filter some fields
         *
         * select all
         * 1 and up
         * 2 and up
         * 3 and up
         * 4 and up
         * 5 only
         *
         * fq=RATING:[1 TO *]&fq=section:0
         *
         * sort=field(name,max) !!!!
         *
         * check if pass correct format for fieldqQuery
         */
        String str_filterQuery = "";

        if(value_passed == null || value_passed.isEmpty()){
            //System.out.println("VALUE PASSED: " + value_passed);
            return str_filterQuery;
        }
        else{

            return value_passed;
        }
    }

    private SolrDocumentList setInavlidDocList() {
        //invalid search
        SolrDocumentList invSolrDocumentList = new SolrDocumentList();
        SolrDocument invalidSolrDocument = new SolrDocument();
        invalidSolrDocument.setField(FIELD_NAME, FIELD_VALUE);
        invSolrDocumentList.set(0,invalidSolrDocument);
        return invSolrDocumentList;
    }

    private SolrDocumentList duplicateCheck(SolrDocumentList solrDocumentList, SolrDocumentList new_solrDocumentList) {
        /**
         * check solrDocumentList and new_solrDocumentList get how many result
         *
         */
        ArrayList<String> docIdList = new ArrayList<>();
        String d_id = "";

        if(solrDocumentList.getNumFound()==0){
            return new_solrDocumentList;
        }
        else if(new_solrDocumentList.getNumFound() ==0){
            return solrDocumentList;
        }
        else{
            for(SolrDocument d : solrDocumentList){//new_solrDocumentList
                //check here, in case solr return duplicate doc_id
                d_id = ((ArrayList<Long>)d.getFieldValue(FIELD_DOCID)).get(0).toString();
                if(!docIdList.contains(d_id)){
                    docIdList.add(d_id);
                }
            }

            for(SolrDocument d : new_solrDocumentList){
                d_id = ((ArrayList<Long>)d.getFieldValue(FIELD_DOCID)).get(0).toString();
                if(!docIdList.contains(d_id)){
                    solrDocumentList.add(d);
                }
            }
            System.out.println("after duplicate check size : " + solrDocumentList.getNumFound());
            return solrDocumentList;
        }

    }

    private String stopWordsCleaning(String q) {


        String lowerQ = q.toLowerCase();

        //System.out.println("before stopwords query: " + lowerQ);
        String[] q_arr = lowerQ.split(" ");
        String q_afterStopWords = "";
        ArrayList<String> listOfQuery = new ArrayList<>(Arrays.asList(q_arr));
        ArrayList<String> listOfQuery_copy = new ArrayList<>(listOfQuery);


        for (String str : listOfStopWords){
            if(Pattern.compile(Pattern.quote(str),Pattern.CASE_INSENSITIVE).matcher(lowerQ).find()){
                //System.out.println("stop words found : " + str);
                listOfQuery_copy.remove(str);
                //System.out.println("after remove: " + listOfQuery_copy.toString());
            }

        }

        for(String s : listOfQuery_copy){
            q_afterStopWords = (q_afterStopWords + " " + s.trim()).trim();
        }

        removedQuery = q_afterStopWords;
        return q_afterStopWords;
    }


}
