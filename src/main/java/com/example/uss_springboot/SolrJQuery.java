package com.example.uss_springboot;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.core.annotation.Order;

import javax.print.DocFlavor;
import java.io.IOException;
import java.util.*;

/**
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


//    @Autowired
//    @Value("${Opposition.test}")//TODO cant get from properties, will check ltR
//    String opposition_Str;
    String[] opposition_Arr = "but,however,on the contrary,despite".split(",");
    List<String> listOfOpposition = Arrays.asList(opposition_Arr);
    //private List<String> listOfOpposition;

//    @Value("#{'${StopWords}'.split(',')}") //TODO cant get from properties, will check ltr
    String[] stopwords_str = ("A,an,and,are,as,at,be,but,by,for,it,in,into,is,it,into,not,of,on,or,such,that,the," +
            "their,then,there,these,they,this,to,was,will,with,which,where,when,what,how,why,uss").split(",");
    List<String> listOfStopWords = Arrays.asList(stopwords_str);

    String urlString = "http://localhost:8983/solr/uss";
    SolrClient solr = new HttpSolrClient.Builder(urlString).build();
    SolrQuery query = new SolrQuery();
    final static String FIELD_NAME = "CHECK_FIELD";
    final static String FIELD_VALUE = "INVALID";
    final static String FIELD_DOCID = "a_docId";
    final static String FIELD_RATING = "a_rating";
    final static String FIELD_UPVOTES = "a_comment_upvotes";
    final static String FIELD_COUNT = "a_count";
    final static String FIELD_CONTRIBUTION = "a_reviewer_contribution";
    final static String ORDER_ASC = "asc";
    final static String ORDER_DESC = "desc";



    public SolrDocumentList BiQuery(String q, String filter, String sort){
        // split by space
        String[] arr_splitBySpace = q.split(" ");
        int noOfWord = arr_splitBySpace.length;
        ArrayList<String> biWordList = new ArrayList<>();
        SolrDocumentList solrDocumentList = new SolrDocumentList();
        //check size
        if(noOfWord == 1 || noOfWord == 2){
            //one term  || only one pair of biWord --> call SingleTermQuery()
            solrDocumentList = SingleTermQuery(q,filter,sort,true);
        }
        else if(noOfWord>2){
            for(int i = 1; i < noOfWord; i++){
                biWordList.add(arr_splitBySpace[i-1] + " " + arr_splitBySpace[i]);
            }
            //loop SingleTermQuery
            for(String eachBiWord : biWordList){

                SolrDocumentList new_solrDocumentList = new SolrDocumentList();
                new_solrDocumentList = SingleTermQuery(eachBiWord,filter,sort,true);
                solrDocumentList = DuplicateCheck(solrDocumentList, new_solrDocumentList);

            }
        }
        else{
            solrDocumentList = SetInavlidDocList();
        }


        return solrDocumentList;
    }



    public SolrDocumentList SingleTermQuery(String q, String filter, String sort, boolean isDoubleQuotes) {
        // TODO Ryan: i added a_content_comment pls check

        //TODO TESTING HERE example get filter string and sort string here, or pass all value in MixQuery

        LinkedHashMap orderedSortHashMap = new LinkedHashMap<String, String>();

        String filter_afterCheck = SetUpFilterAndSortQuery(filter);
        String sort_afterCheck = SetUpFilterAndSortQuery(sort);
        if(isDoubleQuotes){
            query.setQuery("a_content_comment:\""+q+"\"");
        }
        else{
            query.setQuery("a_content_comment:" + q);
        }

        if(!filter_afterCheck.isEmpty()){
            //set filter here
            query.setFilterQueries(filter_afterCheck);
        }
        if(!sort_afterCheck.isEmpty()){
            //do sort function here
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
        SolrDocumentList solrDocumentList = new SolrDocumentList();
        //query.setRequestHandler("/spellCheckCompRH");
        try {
            QueryResponse response = solr.query(query);
            solrDocumentList = response.getResults();
            System.out.println(solrDocumentList.toString());
        } catch (SolrServerException | IOException e) {
            e.printStackTrace();
        }
        System.out.println("SIZE OF SOLR DOCUMENT: " + solrDocumentList.size());
        return solrDocumentList;
    }


    public SolrDocumentList StopWordsQuery(String q, String filter, String sort){
        String cleaned_q = StopWordsCleaning(q);
        return SingleTermQuery(cleaned_q, filter, sort, false);
    }

    public SolrDocumentList MixQuery(String q, String filter, String sort){ //
        //TODO add filter part here
        query.setRows(15000);
        SolrDocumentList solrDocumentList_bi = new SolrDocumentList();
        solrDocumentList_bi = BiQuery(q,filter,sort);
        SolrDocumentList solrDocumentList_sw = new SolrDocumentList();
        solrDocumentList_sw = StopWordsQuery(q,filter,sort);
        System.out.println("LENGTH MIXQUERY BIWORD: " + solrDocumentList_bi.size() + "LENGTH MIXQUERY STOPWORD " + solrDocumentList_sw.size());
        System.out.println("FILTER: " + filter + "SORT " + sort);
        return DuplicateCheck(solrDocumentList_bi,solrDocumentList_sw);

    }

    private String SetUpFilterAndSortQuery(String value_passed) {//value_filter = a_rating:[1 TO *]
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
            System.out.println("VALUE PASSED: " + value_passed);
            return str_filterQuery;
        }
        else{

            return value_passed;
        }
    }

    private SolrDocumentList SetInavlidDocList() {
        //invalid search
        SolrDocumentList invSolrDocumentList = new SolrDocumentList();
        SolrDocument invalidSolrDocument = new SolrDocument();
        invalidSolrDocument.setField(FIELD_NAME, FIELD_VALUE);
        invSolrDocumentList.set(0,invalidSolrDocument);
        return invSolrDocumentList;
    }

//todo filer, sort, duplicate

    private SolrDocumentList DuplicateCheck(SolrDocumentList solrDocumentList, SolrDocumentList new_solrDocumentList) {
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
            for(SolrDocument d : new_solrDocumentList){
                //check here, in case solr return duplicate doc_id
                d_id = ((ArrayList<Long>)d.getFieldValue(FIELD_DOCID)).get(0).toString();
                if(!docIdList.contains(d_id)){
                    docIdList.add(d_id);
                }
            }

            for(SolrDocument d : solrDocumentList){
                d_id = ((ArrayList<Long>)d.getFieldValue(FIELD_DOCID)).get(0).toString();
                if(!docIdList.contains(d_id)){
                    new_solrDocumentList.add(d);
                }
            }
            System.out.println("LENGTH IS: " + new_solrDocumentList.size());
            return new_solrDocumentList;
        }

    }

    private String StopWordsCleaning(String q) {
        String[] q_arr = q.split(" ");
        String q_afterStopWords = "";
        ArrayList<String> listOfQuery = new ArrayList<>(Arrays.asList(q_arr));
        ArrayList<String> listOfQuery_copy = new ArrayList<>(listOfQuery);

        //['which', 'place','is','most','fun']
        for(int i = 0; i< listOfQuery.size(); i++) {
            for (String str : listOfStopWords) {
                if (listOfQuery.get(i).equalsIgnoreCase(str)) {
                    //listOfQuery.remove(i);
                    listOfQuery_copy.remove(i);
                    break;
                }
            }
        }
        for(String s : listOfQuery_copy){
            q_afterStopWords = q_afterStopWords + " " + s;
        }
        System.out.println("Q_AFTER STOPWORDS: "+ q_afterStopWords);
        return q_afterStopWords;
    }


}
