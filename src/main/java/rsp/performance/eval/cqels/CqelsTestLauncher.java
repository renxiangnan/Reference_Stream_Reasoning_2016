package rsp.performance.eval.cqels;

public class CqelsTestLauncher {

	public static void main(String[] args) {

		CqelsExecutor executor = new CqelsExecutor();

		try {
			executor.execute(1, 0);	
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
