package rsp.performance.eval;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.larkc.csparql.cep.api.RdfQuadruple;
import eu.larkc.csparql.cep.api.RdfStream;

public class StreamGenerator extends RdfStream implements Runnable {
	private long c = 1L;
	private int streamRate = 1000; 
	private boolean warmup = false;
	private boolean keepRunning = true;

	protected final Logger logger = LoggerFactory.getLogger(StreamGenerator.class);

	public StreamGenerator(final String iri) {
		super(iri);
	}

	public void pleaseStop() {
		keepRunning = false;
	}

	public void run() {
		if(warmup == true)
			warmup();
		
		generate();
	}


	public void generate() {
		boolean keepRunning = true;
		String predicate = "/observeFlow";
		long tripleCounter = 0;
		long tStart, tEnd;

		this.c = 1L;

		tStart = System.currentTimeMillis();
		
		while (keepRunning && (tripleCounter < streamRate)) {

			predicate = (((c % 50 == 0)) ? "/observeChlorine" : "/observeFlow");

			final RdfQuadruple q = new RdfQuadruple(super.getIRI() + "/message_" + this.c,
					"http://myexample.org" + predicate, super.getIRI() + "/observation_" + this.c, System.currentTimeMillis());
			this.put(q);
			final RdfQuadruple q1 = new RdfQuadruple(super.getIRI() + "/observation_" + this.c,
					"http://myexample.org/hasTag", "http://myexample.org/tag_" + this.c + 1, System.currentTimeMillis());
			this.put(q1);
			final RdfQuadruple q2 = new RdfQuadruple(super.getIRI() + "/observation_" + this.c,
					"http://myexample.org/hasTag", "http://myexample.org/tag_" + this.c + 2, System.currentTimeMillis());
			this.put(q2);
			final RdfQuadruple q3 = new RdfQuadruple(super.getIRI() + "/observation_" + this.c,
					"http://myexample.org/hasTag", "http://myexample.org/tag_" + this.c + 3, System.currentTimeMillis());
			this.put(q3);
			c++;
			
			tripleCounter = tripleCounter + 4;
			if (tripleCounter == streamRate) {
				try {
					tEnd = System.currentTimeMillis();
					Thread.sleep((1000 - (tEnd - tStart)));
					
					System.out.println(tripleCounter + 
							" triples have been completely updated, time spent: " + (tEnd - tStart));
					
					tStart = System.currentTimeMillis();
					tripleCounter = 0;					
				} catch (InterruptedException e) {
					e.printStackTrace();
					}
				}
			}	
		}
	
	
	public void warmup() {
		String predicate = "/observeFlow";
		long tripleCounter = 0;
		long tStart, tEnd;
		long warmupStart, warmupEnd;
		long warmupDuration = 10000L;
		
		System.out.println("----------------------------------- ");
		System.out.println("---------Warm up started !--------- ");
		System.out.println("----------------------------------- ");

		warmupStart = tStart = System.currentTimeMillis();
		
		while (keepRunning && (tripleCounter < streamRate)) {

			predicate = (((c % 50 == 0)) ? "/observeChlorine" : "/observeFlow");

			final RdfQuadruple q = new RdfQuadruple(super.getIRI() + "/message_X",
					"http://myexample.org" + predicate, super.getIRI() + "/observation_X", System.currentTimeMillis());
			this.put(q);
			final RdfQuadruple q1 = new RdfQuadruple(super.getIRI() + "/observation" + this.c,
					"http://myexample.org/hasTag", "http://myexample.org/tag_X", System.currentTimeMillis());
			this.put(q1);
			final RdfQuadruple q2 = new RdfQuadruple(super.getIRI() + "/observation_X",
					"http://myexample.org/hasTag", "http://myexample.org/tag_X", System.currentTimeMillis());
			this.put(q2);
			final RdfQuadruple q3 = new RdfQuadruple(super.getIRI() + "/observation_X",
					"http://myexample.org/hasTag", "http://myexample.org/tag_X", System.currentTimeMillis());
			this.put(q3);
			
			tripleCounter = tripleCounter + 4;
			warmupEnd = System.currentTimeMillis();
	
			if (tripleCounter == streamRate) {
				try {
					tEnd = System.currentTimeMillis();
					Thread.sleep((1000 - (tEnd - tStart)));
					System.out.println(tripleCounter + 
							" triples have been completely updated, time spent: " + (tEnd - tStart));
					tStart = System.currentTimeMillis();
					tripleCounter = 0;					
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			if (warmupEnd - warmupStart > warmupDuration) 
				keepRunning = false; 
		}
		
		System.gc();
		System.out.println("------------------------------------");
		System.out.println("---------Warm up finished !---------");
		System.out.println("------------------------------------");
	}
}
