//package com.example.uss_springboot;
//
//
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//@EnableSolrRepositories
//class ApplicationConfig {
//
//    @Bean
//    public SolrClient solrClient() {
//        EmbeddedSolrServerFactory factory = new EmbeddedSolrServerFactory("classpath:com/acme/solr");
//        return factory.getSolrServer();
//    }
//
//    @Bean
//    public SolrOperations solrTemplate() {
//        return new SolrTemplate(solrClient());
//    }
//}