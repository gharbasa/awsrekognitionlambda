package com.dt.rts.eregusa;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.cloudsearchv2.AmazonCloudSearch;
import com.amazonaws.services.cloudsearchv2.AmazonCloudSearchClientBuilder;

public class AmazonClient {


    public void initializeAmazon() {
    	System.out.println("Hello World2");
    	AmazonCloudSearchClientBuilder amazonCloudSearchClientBuilder = AmazonCloudSearchClientBuilder.standard();
        EndpointConfiguration endpointConfiguration = new EndpointConfiguration("search-dsq-tnxcm22xrkjudyx5clglenzcli.us-east-1.cloudsearch.amazonaws.com", "us-east-1");
        amazonCloudSearchClientBuilder.setEndpointConfiguration(endpointConfiguration);
        AWSCredentials awsCredentials = new BasicAWSCredentials("ASIAVQCPOJN6PWFVSCWU", "RDR5mLSrMq9kJgpMeoKda2+chHTMLIboeaFRwnEt");
        AmazonCloudSearch amazonCloudSearch = amazonCloudSearchClientBuilder.withCredentials(new AWSStaticCredentialsProvider(awsCredentials)).build();
        //AWSCredentialsProvider aWSCredentialsProvider = amazonCloudSearchClientBuilder.getCredentials();
        
        /**
        ListDomainNamesResult domains = amazonCloudSearch.listDomainNames();
        Map<String, String> domainsMap = domains.getDomainNames();
        domainsMap.forEach((key, value) -> {
        	System.out.println("key="  + key + ", value=" + value);
        });
        */
        /*
        AmazonCloudSearchQuery query = new AmazonCloudSearchQuery();
        query.query = "Dining Tables";
        query.queryParser = "simple";
        query.start = 0;
        query.size = 16;
        query.setDefaultOperator("or");
        query.setFields("sku_no^11", "title^10", "description^9", "features^8", "specification^8", "categories^7");
        query.addExpression("sort_expr", "(0.3*popularity)+(0.7*_score)");
        query.addSort("sort_expr", "desc");

        AmazonCloudSearchResult result = client.search(query);
        */
        //AmazonCloudSearchClient client = new AmazonCloudSearchClient(awsCredentials);
        //amazonCloudSearch.setSearchDomain("search-dsq-tnxcm22xrkjudyx5clglenzcli.us-east-1.cloudsearch.amazonaws.com");
        //client.setDocumentDomain("doc-dsq-tnxcm22xrkjudyx5clglenzcli.us-east-1.cloudsearch.amazonaws.com");
    }
    
    
    public static void main(String[] args) {
    	AmazonClient amazonClient = new AmazonClient();
    	amazonClient.initializeAmazon();
    }

}
