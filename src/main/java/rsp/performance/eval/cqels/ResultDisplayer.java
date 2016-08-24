package rsp.performance.eval.cqels;

import com.hp.hpl.jena.sparql.core.Var;
import org.deri.cqels.data.Mapping;
import org.deri.cqels.engine.ContinuousListener;

import java.util.Iterator;



public class ResultDisplayer implements ContinuousListener  {

	public static long timestampLastOutput = 0L;

	public void update(Mapping mapping) {
		String result = "";

		timestampLastOutput = System.nanoTime();
		
		for (Iterator<Var> vars = mapping.vars(); vars.hasNext();) {
			result += "   " + CqelsExecutor.context.engine().decode(mapping.get(vars.next()));
		}
		
		System.out.println(result + 
				"     timestamp of decoding moment: " + timestampLastOutput + 
				"     Execution time is: " + 
				(timestampLastOutput - StreamGenerator.timestampFirstInput - StreamGenerator.tripleCreationTime)/1e6 +
				" ms");
	}	
}
