package rsp.performance.eval.cqels;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.impl.ResourceImpl;
import org.deri.cqels.engine.ExecContext;
import org.deri.cqels.engine.RDFStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StreamGenerator extends RDFStream implements Runnable {

	public static long timestampFirstInput = 0L;
	public static long tripleCreationTime = 0L;
	public static long tripleCounter = 0L;
	
	private long tripleNumber = 30000L;
	private boolean warmup = true;
	private boolean keepRunning = true;

	protected final Logger logger = LoggerFactory.getLogger(StreamGenerator.class);


	public StreamGenerator(ExecContext context, String uri) {
		super(context, uri);
	}
	

	public static void getFirstEncode() {
		timestampFirstInput = ((tripleCounter == 1) ? System.nanoTime() : timestampFirstInput);
	}

	
	public void run() {
		if(warmup == true) warmUp(); 
		generate(this.tripleNumber);	
	}

	
	public void generate(long tripleNumber) {
		String predicate = "/observeFlow";
		long tStart, tEnd;
		long t;
		long c = 1L;

		Resource hasTag = new ResourceImpl("http://myexample.org/hasTag");

		while (keepRunning) {

			tStart = System.nanoTime();

			predicate = (((c % 50 == 0)) ? "/observeChlorine" : "/observeFlow");

			Resource observe = new ResourceImpl("http://myexample.org" + predicate);

			Resource message = new ResourceImpl("http://myexample.org/message_" + c);
			Resource observation = new ResourceImpl("http://myexample.org/observation_" + c);

			Resource observationTag1 = new ResourceImpl("http://myexample.org/tag_" + c + 1);
			Resource observationTag2 = new ResourceImpl("http://myexample.org/tag_" + c + 2);
			Resource observationTag3 = new ResourceImpl("http://myexample.org/tag_" + c + 3);
			
			tEnd = System.nanoTime();
			
			tripleCreationTime = tripleCreationTime + (tEnd - tStart);

			tripleCounter = tripleCounter + 1L;
			stream(observation.asNode(), hasTag.asNode(), observationTag1.asNode());
			tripleCounter = tripleCounter + 1L;
			stream(observation.asNode(), hasTag.asNode(), observationTag2.asNode());
			tripleCounter = tripleCounter + 1L;
			stream(observation.asNode(), hasTag.asNode(), observationTag3.asNode());
			tripleCounter = tripleCounter + 1L;
			stream(message.asNode(), observe.asNode(), observation.asNode());

			c++;

			if (tripleCounter == tripleNumber) {
//				System.gc();	
				stop();
				
				t = System.nanoTime() - timestampFirstInput ;
				System.out.println();
				
				if((ResultDisplayer.timestampLastOutput - timestampFirstInput) < 0)
					System.out.println("--------- No answer found ! ---------");
				else System.out.println("---------" + 
					tripleNumber + " triples have been processed !" );	
			}
		}
	}


	public void warmUp() {
		int warmupTripleCounter = 0;
		int warmupTripleNumber = 50000;
		int warmupDuration = 15000;
		
		boolean warmUpKeepRunning = true;
		long warmUpStart = 0L, warmUpEnd = 0L;
		long t1 = 0L, t2 = 0L;

		System.out.println("----------------------------------- ");
		System.out.println("---------Warm up started !--------- ");
		System.out.println("----------------------------------- ");

		warmUpStart = t1 = System.nanoTime();

		while (warmUpKeepRunning) {

			warmUpEnd = System.nanoTime();

			Resource subject = new ResourceImpl("http://myexample.org/observation_X");
			Resource predicate = new ResourceImpl("http://myexample.org/hasTag");
			Resource object = new ResourceImpl("http://myexample.org/tag_X");

			stream(subject.asNode(), predicate.asNode(), object.asNode());
			warmupTripleCounter++;

			if (warmupTripleCounter == warmupTripleNumber) {
				t2 = System.nanoTime();
				
				System.out.println(
						"---------" + warmupTripleCounter + 
						" triples have been completely updated, time spent: " + (t2 - t1)/1e6 + " ms");
				
				warmupTripleCounter = 0;
				t1 = System.nanoTime();
			}
			warmUpKeepRunning = (((warmUpEnd - warmUpStart)/1e6 >= warmupDuration) ? false : true);
		}

		try {
			Thread.sleep(5000);
			System.gc();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		System.out.println("------------------------------------");
		System.out.println("---------Warm up finished !---------");
		System.out.println("------------------------------------");
	}
	

	@Override
	public void stop() {
		keepRunning = false;
	}
	
}
