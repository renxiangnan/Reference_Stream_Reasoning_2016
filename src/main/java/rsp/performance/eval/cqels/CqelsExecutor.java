package rsp.performance.eval.cqels;

import org.deri.cqels.engine.ContinuousSelect;
import org.deri.cqels.engine.ExecContext;

public class CqelsExecutor {
	
	final String fileLoadPath = "/Users/xiangnanren/IDEAWorkspace/rsp-performance-evaluation/data-src";
	final String streamUri = "http://myexample.org";

		public static ExecContext context;

	public CqelsExecutor() {
		super();
		context = new ExecContext(fileLoadPath, false);
	}

	public void execute(int queryId, long testDelay) throws InterruptedException {


		final int QUERY_0 = 0;
		final int QUERY_1 = 1;
		final int QUERY_2 = 2;
		final int QUERY_3 = 3;
		final int QUERY_6 = 6;

		String query = "";
		StreamGenerator generator = null;
		
		switch (queryId) {
		
		case QUERY_0:

			query = " PREFIX ex: <http://myexample.org/> "
					+" SELECT DISTINCT ?observation "
					+" WHERE {"
					+" STREAM <http://myexample.org>[RANGE 1s] "
					+" { ?message ex:observeChlorine ?observation . } "
					+" } ";
			generator = new StreamGenerator(context, streamUri);

			break;
			
		case QUERY_1:
			query = " PREFIX ex: <http://myexample.org/> "
					+" SELECT DISTINCT ?observation "
					+" WHERE {"
					+" STREAM <http://myexample.org>[RANGE 1s] "
					+" { ?message ex:observeChlorine ?observation ."
					+"   ?observation ex:hasTag ?tag .  "
					+ "  } } ";
					
			generator = new StreamGenerator(context, streamUri);
			
			break;
			
		case QUERY_2:

			query = " PREFIX ex: <http://myexample.org/> "
					+" SELECT DISTINCT ?observation (COUNT(?tag) AS ?numberOfTags)  "
					+" WHERE {"
					+" STREAM <http://myexample.org>[RANGE 1s] "
					+" { ?message ex:observeChlorine ?observation . "
					+ "  ?observation ex:hasTag ?tag .  } "
					+" } "
					+" GROUP BY ?observation "
					+" ORDER BY ASC(?observation)";
			
			generator = new StreamGenerator(context, streamUri);

			break;
			
		case QUERY_3:

			query ="PREFIX ex: <http://myexample.org/> "
					  +" SELECT ?observation "
					  +" WHERE { "
					  +" STREAM <http://myexample.org> [RANGE 1s] "
					  +" { ?message ex:observeChlorine ?observation . "
					  +  " ?observation ex:hasTag ?tag . } "
					  +"  FILTER( REGEX(STR(?observation), \"00$\", \"i\")  "
					  +" || ( REGEX(STR(?observation), \"50$\", \"i\"))) "
					  +" } "
					  + "GROUP BY ?observation";
			
			generator = new StreamGenerator(context, streamUri);

			break;
			
		case QUERY_6:
	
			query = " PREFIX ex: <http://myexample.org/> "
					+"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
					+"SELECT ?sector ?manufacture_ID ?label "
	                +" FROM NAMED <C:/Users/A612504/Desktop/workspace/cqels-master/cqels-master/sensor_data.rdf> "
					+" WHERE { "
					+" STREAM <http://myexample.org> [RANGE 1s] "
					+" { ?message ex:observeChlorine ?observation . } "
					+" GRAPH <C:/Users/A612504/Desktop/workspace/cqels-master/cqels-master/sensor_data.rdf> "
					+" {  "
					+"   ?observation ex:isProducedBy ?sensorId ."
					+" 	 ?sensorId ex:belongsTo ?sector ; "
					+"             ex:isCreatedBy ?manufacture_ID ; "
					+"             rdfs:label ?label . } "
					+" }  ";
			
			generator = new StreamGenerator(context, streamUri);

			break;
			
		default:
			System.exit(0);
			break;
		}

		
		ContinuousSelect selQuery = context.registerSelect(query);
		System.out.println("Query Registration Time: " + System.currentTimeMillis());
		System.out.println("\n---------------------------------\n");
		
		
		if (testDelay > 0)
			Thread.sleep(testDelay);
		
		new Thread(generator).start();
	
		selQuery.register(new ResultDisplayer());
	}

}

