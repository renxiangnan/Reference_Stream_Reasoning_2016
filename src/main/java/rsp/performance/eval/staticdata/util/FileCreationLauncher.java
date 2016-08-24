package rsp.performance.eval.staticdata.util;

public class FileCreationLauncher {

	public static void main(String[] args) {
		int maxTriples = 200;

		StaticDataCreator fileCreator = new StaticDataCreator("http://myexample.org", maxTriples, "sensor_data.rdf");
		fileCreator.staticDataGenerator();
	}
}
