package edu.albany.cs.solr;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.SolrParams;

import edu.albany.cs.util.ConfigManager;

public class SolrHandler {
	public static void main1(String[] args) throws SolrServerException {

		String url = "http://http://topicmodel.cloudapp.net:8090/solr/";
		SolrServer server = new HttpSolrServer(url);
		SolrQuery query = new SolrQuery();
		query.setQuery("*:*");

		// query.addSortField("price", SolrQuery.ORDER.asc);

		QueryResponse rsp = server.query(query);
		SolrDocumentList docs = rsp.getResults();

		System.out.println(docs.get(0).getFirstValue("viewid"));

	}

	
	public static SolrDocumentList sendQuery(String url, String port, String collection, String viewId,
			String term, String solrVersion) throws SolrServerException {

		// http://localhost:8080/solr-itm/select/?q=viewid:170%20AND%20(notecontent:battery%20OR%20notetitle:Metal)

		//url = "http://" + url + ":8080/solr-itm/";
		//<alp> bit more parameterized to work with different ports also moved to solr4
	
		if (solrVersion.equals("3")) {
			url = "http://" + url + ":"+port+"/solr-itm";
		}else {
			url = "http://" + url + ":"+port+"/solr/"+collection+"";	
		}

		SolrServer server = new HttpSolrServer(url);
		SolrQuery query = new SolrQuery();
		String queryText = "viewid:" + viewId + " AND (notecontent:" + term
				+ " OR notetitle:" + term + ")";

		query.set("rows", ConfigManager.getInstance().getSolrHitCount());

		query.setQuery(queryText);

		QueryResponse rsp = server.query(query);
		SolrDocumentList docs = rsp.getResults();
		
		System.out.println("Solr Query: "+query.toString());		
		System.out.println("DOC COUNT : "+docs.size());
		return docs;
	}

	public static void main(String[] args) throws SolrServerException {
		SolrDocumentList documentList = sendQuery("topicmodel.cloudapp.net","8090","collection1", "170", "battery","3");
		System.out.println(documentList.get(0).getFirstValue("viewid"));

	}

}
