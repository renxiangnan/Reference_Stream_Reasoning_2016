package rsp.performance.eval.csparql;

import java.text.ParseException;

import org.apache.log4j.BasicConfigurator;

import eu.larkc.csparql.cep.api.RdfStream;
import eu.larkc.csparql.engine.CsparqlEngine;
import eu.larkc.csparql.engine.CsparqlEngineImpl;
import eu.larkc.csparql.engine.CsparqlQueryResultProxy;


public class CsparqlExecutor {

	public void execute(int queryId, long testDelay) throws InterruptedException {

		final int TEST_QUERY_1 = 1;
		final int TEST_QUERY_2 = 2;
		final int TEST_QUERY_3 = 3;
		final int TEST_QUERY_4 = 4;
		final int TEST_QUERY_5 = 5;
		final int TEST_QUERY_6 = 6;
		
		String streamURI = "http://myexample.org/stream";
		String query = null;
		RdfStream generator = null;
	
		BasicConfigurator.configure();
		
		switch (queryId) {
			
		case TEST_QUERY_1: 
			
			query = "REGISTER QUERY TestQuery1 AS "
		    + " PREFIX ex: <http://myexample.org/> "
                    + " SELECT DISTINCT ?observation "
		    + " FROM STREAM <http://myexample.org/stream> [RANGE 1s STEP 1s] " 
		    + " WHERE { "
                    + " ?message ex:observeChlorine ?observation .  "
                    + " ?observation ex:hasTag ?tag . "
                    + "} ";
			
			generator = new StreamGenerator(streamURI);

		break;
		
		case TEST_QUERY_2:
			query = "REGISTER QUERY TestQuery2 AS "
		    + " PREFIX ex: <http://myexample.org/> "
                    + " SELECT DISTINCT ?observation (COUNT(?tag) AS ?numberOfTags) "
		    + " FROM STREAM <http://myexample.org/stream> [RANGE 1s STEP 1s] " 
		    + " WHERE { "
                    + " ?message ex:observeChlorine ?observation .  "
                    + " ?observation ex:hasTag ?tag . } "
                    + " GROUP BY ?observation "
                    + " ORDER BY ASC(?observation) ";
			
			generator = new StreamGenerator(streamURI);

		break;
			
		
		case TEST_QUERY_3:
	
			query = "REGISTER QUERY TestQuery3 COMPUTED EVERY 1s AS "
		    + " PREFIX ex: <http://myexample.org/> "
                    + " SELECT ?observation "
		    + " FROM STREAM <http://myexample.org/stream> [RANGE 1s STEP 1s] " 
		    + " WHERE { "
                    + " ?message ex:observeChlorine ?observation .  "
                    + " ?observation ex:hasTag ?tag . "
                    + " FILTER ( regex(str(?observation), '00$', 'i') "
                    + " || ( regex(str(?observation), '50$', 'i'))) "
                    + "} "
                    + " GROUP BY ?observation ";
			
			generator = new StreamGenerator(streamURI);

		break;
		
		case TEST_QUERY_4:	
			
			query = " REGISTER QUERY TestQuery4 AS "
			+" PREFIX ex: <http://myexample.org/> "
			+ "PREFIX f: <http://larkc.eu/csparql/sparql/jena/ext#> "
			+" SELECT ?observation (COUNT(?tag) AS ?numberOfTags) "
			+" FROM STREAM <http://myexample.org/stream> [RANGE 1s STEP 1s] "
			+" WHERE { " 
			+" ?message ex:observeChlorine ?observation . "  
			+" ?observation ex:hasTag ?tag . "
			+" FILTER ( regex(str(?tag), '1$', \"i\") "
			+" || regex(str(?tag), '2$', \"i\")  "
			+" || regex(str(?tag), '3$', \"i\")) "
			+" FILTER (f:timestamp(?observation, ex:hasTag, ?tag) "
			+" >= f:timestamp(?message, ex:observeChlorine, ?observation)) "
			+" } "
			+" GROUP BY ?observation ?numberOfTags";
			
			generator = new StreamGenerator(streamURI);

		break;
		
		case TEST_QUERY_5:	
			query = "REGISTER QUERY TestQuery5 AS "
		    + " PREFIX ex: <http://myexample.org/> "
		    + " PREFIX f: <http://larkc.eu/csparql/sparql/jena/ext#> "
                    + " SELECT ?observation (COUNT(?tag) AS ?numberOfTags) "
		    + " FROM STREAM <http://myexample.org/stream> [RANGE 1s STEP 1s] " 
	            + " WHERE { "
                    + " { "
                    + "  ?message ex:observeChlorine ?observation .  "
                    + "  ?observation ex:hasTag ?tag . "
                    + "  FILTER ( regex(str(?observation), '00$', 'i') )"
		    + "  FILTER (f:timestamp(?observation, ex:hasTag, ?tag) "
                    + "  >= f:timestamp(?message, ex:observeChlorine, ?observation)) "
                    + " } "
                    + " UNION "
                    + " { "
                    + " ?message1 ex:observeFlow ?observation. "
                    + " ?observation ex:hasTag ?tag .	"
                    + "  FILTER ( regex(str(?observation), '10$', 'i') )"
                    + " } "
                    + " } "
                    + " GROUP BY ?observation "
                    + " HAVING (COUNT(?tag) = 3) " 
                    + " ORDER BY ASC(?observation) ";
			
			generator = new StreamGenerator(streamURI);
			
		break;
		
		
		case TEST_QUERY_6: 
			query = " REGISTER QUERY TestQuery6 AS "
					+" PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
					+" PREFIX ex: <http://myexample.org/> "
					+" SELECT ?sector ?timestamp ?label "
					+" FROM NAMED STREAM <http://myexample.org> [RANGE 1s STEP 1s] "
					+" FROM <http:...> "
					+ " WHERE { "
					+ " ?message ex:observeChlorine ?observation ."
					+ " ?observation ex:isProducedBy ?sensorId ."
					+ " ?sensorId ex:belongsTo ?sector ;"
					+ " 	      ex:isCreatedBy ?manufacture_ID ; "
					+ "	      rdfs:label ?label . "
					+ " } ";
			
			generator = new StreamGenerator(streamURI);
			
		break;
		
		default:
			System.exit(0);
			break;
		}

		CsparqlEngine engine = new CsparqlEngineImpl();	
		engine.initialize(true);
		engine.registerStream(generator);
	
		final Thread t = new Thread((Runnable) generator);
		t.start();

		CsparqlQueryResultProxy c = null;


		try {
			c = engine.registerQuery(query);
			System.out.println("Query: " + query);
			System.out.println("Query Start Time : " + System.currentTimeMillis());
			} catch (final ParseException ex) {
				System.out.println("errore di parsing: " + ex.getMessage());
			}

		if (c != null) {	
			c.addObserver(new ResultDisplayer());
			}
		
		try {
			Thread.sleep(3600000);
		} catch (InterruptedException e) {
			e.printStackTrace();
			}
		
		engine.unregisterQuery(c.getId());
		((StreamGenerator) generator).pleaseStop();
	
		System.exit(0);	

	}
}
