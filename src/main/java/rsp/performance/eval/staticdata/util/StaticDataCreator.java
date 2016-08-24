package rsp.performance.eval.staticdata.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.impl.PropertyImpl;
import com.hp.hpl.jena.rdf.model.impl.ResourceImpl;

public class StaticDataCreator {

	private String uri;
	private long c;
	private long maxTriples;
	private String fileName;

	public StaticDataCreator(String uri, long maxTriples, String fileName) {
		this.uri = uri;
		this.maxTriples = maxTriples;
		this.fileName = fileName;
	}

	public String getUri() {
		return uri;
	}

	public long getC() {
		return c;
	}

	public long getMaxTriples() {
		return maxTriples;
	}

	public String getFileName() {
		return fileName;
	}


	public void staticDataGenerator() {
		int sensorCounter = 1;
		HashMap<String, String> sensorMap = new HashMap<String, String>();

		sensorMap = sensorTableCreator();
		sensorMap = sensorTableLoader();

		Model model = ModelFactory.createDefaultModel();

		while (this.c <= this.maxTriples) {
			Resource observation = new ResourceImpl(this.uri + "/observation_" + this.c);
			Property producedBy = new PropertyImpl(this.uri + "/isProducedBy");
			Resource sensorId = new ResourceImpl(this.uri + "/sensor_" + sensorCounter);

			Property belongsTo = new PropertyImpl(this.uri + "/belongsTo");
			Resource sector = new ResourceImpl(this.uri + "/sector_" + sensorCounter);

			Property createdBy = new PropertyImpl(this.uri + "/isCreatedBy");
			Resource manufacture = new ResourceImpl(this.uri + "/manufacture_" +
			
			sensorMap.get(String.valueOf(sensorCounter)));

			Property label = new PropertyImpl("http://www.w3.org/2000/01/rdf-schema#label");
			Resource labelId = new ResourceImpl(this.uri + "/label_" + sensorCounter);

			sensorCounter = ((this.c % 50 == 0) ? 0 : sensorCounter);

			model.add(observation, producedBy, sensorId);
			model.add(sensorId, belongsTo, sector);
			model.add(sensorId, createdBy, manufacture);
			model.add(sensorId, label, labelId);

			sensorCounter++;
			this.c++;
		}
//		 model.write(System.out, "N-TRIPLES");
		try {
			File file1 = new File(this.fileName);
			model.write(new FileOutputStream(file1), "RDF/XML");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public HashMap<String, String> sensorTableCreator() {
		int index = 0, i = 0;
		String sensor;
		String[] sensorTab = { "flowSensor_A", "flowSensor_B", "flowSensor_C", "flowSensor_D" };

		HashMap<String, String> sensorMap = new HashMap<String, String>();
		Properties properties = new Properties();

		for (i = 1; i <= 49; i++) {
			index = (int) (Math.random() * 4);
			sensor = sensorTab[index];
			sensorMap.put(String.valueOf(i), sensor);
		}
		sensorMap.put(String.valueOf(50), "chlorineSensor");

		for (Map.Entry<String, String> entry : sensorMap.entrySet()) {
			properties.put(entry.getKey(), entry.getValue());
		}
		try {
			properties.store(new FileOutputStream("sensor_table.properties"), null);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return sensorMap;
	}

	public HashMap<String, String> sensorTableLoader() {
		HashMap<String, String> sensorMap = new HashMap<String, String>();
		Properties properties = new Properties();

		try {
			properties.load(new FileInputStream("sensor_table.properties"));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		for (String key : properties.stringPropertyNames()) {
			sensorMap.put(key, (String) properties.get(key));
		}
		System.out.println(sensorMap);

		return sensorMap;
	}
}
