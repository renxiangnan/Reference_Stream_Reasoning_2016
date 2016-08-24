package rsp.performance.eval.csparql;

import eu.larkc.csparql.common.RDFTable;
import eu.larkc.csparql.common.RDFTuple;
import eu.larkc.csparql.common.streams.format.GenericObservable;
import eu.larkc.csparql.core.ResultFormatter;


public class ResultDisplayer extends ResultFormatter {


	public void update(final GenericObservable<RDFTable> observed, final RDFTable q) {
			String result=null;
//			long usedMemory=0;
               
			System.out.println();
			System.out.println("-------"+ q.size() + " results at SystemTime=["+System.nanoTime()+"]--------");
		
			for (final RDFTuple t : q) {
					result=t.toString().replaceAll("http://myexample.org/","").replaceAll("http://myexample.org/","");
					System.out.println(result.replaceAll("http://www.w3.org/2001/XMLSchema#integer",""));

//				usedMemory=Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();     
				
                    }
//                System.out.println("-----------------> Used Memory= "+usedMemory);
//                System.out.println("-----------------> Free Memory= "+(Runtime.getRuntime().freeMemory()));
//                System.out.println("-----------------> Total Memory= "+(Runtime.getRuntime().totalMemory()));
//                System.out.println("Max Memory: " + Runtime.getRuntime().maxMemory() + "\n");
	}
}
