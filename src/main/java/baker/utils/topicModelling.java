package baker.utils;

import dataCenter.utils.FileIO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;
/**
 * @author Harry Tran on 5/23/18.
 * @project RecoverJSName
 * @email trunghieu.tran@utdallas.edu
 * @organization UTDallas
 */
public class topicModelling {
	private static final int numOfTopic = 5;
	private static final String resourceDir = "./src/main/python/topicModelling/data/";
	private static final String dictionaryFile = resourceDir + "snapShot/dictionary";
	private static final String topicDetailDir = resourceDir + "snapShot/topicDetails/";

	private final static Logger LOGGER = Logger.getLogger(topicModelling.class.getName());

	private static HashMap<Integer, String> idToNameMap = new HashMap<>();
	private static HashMap<String, Integer> nameToIdMap = new HashMap<>();
	private static ArrayList<HashMap<String, Double> > topicDetailMap = new ArrayList<>();

	public static void loadDictionaryFromFile(String filename) {
		String data = FileIO.readStringFromFile(filename);
		String[] parts = data.split("\\n");
		for (int i = 1; i < parts.length; ++i) {
			String[] tmp = parts[i].split("\\t");
			int idx = -1;
			try {
				idx = Integer.parseInt(tmp[0]);
			} catch (Exception e) {
				LOGGER.info(e.getMessage());
			}
			if (idx != -1 && tmp.length > 1) {
				idToNameMap.put(idx, tmp[1]);
				nameToIdMap.put(tmp[1], idx);
			}
		}
		LOGGER.info("Successfully imported dictionary");
	}

	public static HashMap<String, Double> loadTopicDetailsFromFile(String filename) {
		HashMap<String, Double> res = new HashMap<>();
		String data = FileIO.readStringFromFile(filename);
		String[] parts = data.split("\\n");
		for (int i = 0; i < parts.length; ++i) {
			String[] tmp = parts[i].split(" ");
			if (tmp.length > 1) {
				double prob = -1;
				try {
					prob = Double.parseDouble(tmp[1]);
				} catch (Exception e) {
					LOGGER.info(e.getMessage());
				}
				if (prob != -1) {
					res.put(tmp[0], prob);
				}
			}
		}
		return res;
	}

	public static void loadAllTopicDetailsFromDirectory(String dir) {
		for (int i = 0; i < numOfTopic; ++i) {
			String fname = dir + Integer.toString(i);
			topicDetailMap.add(loadTopicDetailsFromFile(fname));
		}
	}

	public static void main(String[] args) {
		System.out.println("=== Started ...");
		topicModelling.loadDictionaryFromFile(dictionaryFile);
		topicModelling.loadAllTopicDetailsFromDirectory(topicDetailDir);
		System.out.println("... Finished ===");
	}

}
