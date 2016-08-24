package rsp.performance.eval.staticdata.util;

public class FileCreatorLauncher {

	public static void main(String[] args) {
		int maxTriples = 200;

		StaticDataCreator fileCreator = new StaticDataCreator("http://myexample.org", maxTriples, "sensor_data.rdf");		
		fileCreator.staticDataGenerator();
	}
}
