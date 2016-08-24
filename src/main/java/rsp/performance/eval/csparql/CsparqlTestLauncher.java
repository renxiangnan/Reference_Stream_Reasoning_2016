package rsp.performance.eval.csparql;

public class CsparqlTestLauncher {

	public static void main(String[] args) {
		
	CsparqlExecutor executor = new CsparqlExecutor();
	try {
		executor.execute(5, 0);	
	} catch (InterruptedException e) {
		e.printStackTrace();
		}
	}
}
