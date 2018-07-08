package parser;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.mozilla.javascript.CompilerEnvirons;
import org.mozilla.javascript.IRFactory;
import org.mozilla.javascript.ast.AstRoot;

/**
 * @author Mike
 * Main program. 
 * Generate file lists from data into training and testing.
 * Parse each file to build corpus.
 */
public class MainParser {
	static String all = "../Data/";
//	static String all = "F:\\Study\\Research\\RecoverJsName\\Data\\0xsky\\xblog\\xblogroot\\admin\\js\\admin.js";
//	static String all = ".\\resources\\_test";
//	static String output = "../Data/_Output";
//	static String output = ".\\resources/parsedData";
//	static String testSetDir = ".\\resources/parsedData/testSet";
	
	static String trainSetDir = "../TrainSet";
	static String testSetDir = "../TestSet";
	static String bakerDir = "../BakerData";
	static String trainTMDir = "../TrainTM";
	static String fileList = "../FileList";
//	ArrayList<File> testSet = new ArrayList<>();
//	ArrayList<File> trainSet = new ArrayList<>();
	
	public static void main(String[] args) throws Exception {
		MainParser demo = new MainParser();
		//demo.generateFileList(all);
		demo.parseTrainSetForest();
		//demo.parseBaker();
//		demo.parseTestSet();
//		demo.parseTrainSetTM();
//		demo.parseTestSetTM();
	}

	public void generateFileList (String filePath) throws Exception
	{
		File trainFileList = new File(fileList + "/trainFileList.txt");
		FileWriter fwTrainList = new FileWriter(trainFileList);
	    PrintWriter pwTrainList = new PrintWriter(fwTrainList);
	    
		File testFileList = new File(fileList + "/testFileList.txt");
		FileWriter fwTestList = new FileWriter(testFileList);
	    PrintWriter pwTestList = new PrintWriter(fwTestList);
	    
		File dir = new File(filePath);
		ArrayList<File> files = new ArrayList<>();
		searchDir(dir, files);
		//File[] files = dir.listFiles();
		int i = 1;

		for ( File file : files )
		{
			if ( i < 10 ) {
				//trainSet.add(file);
				pwTrainList.println(file.getCanonicalPath());
				i++;
			}
			else {
				i = 1;
				//testSet.add(file);
				pwTestList.println(file.getCanonicalPath());
			}
		}
		//myVisitor.print();
		//myVisitor.printToFile(output);
	    pwTrainList.close();
	    pwTestList.close();
	}

	public void parseBaker() throws Exception
	{
		File trainFileList = new File(fileList + "/trainFileList.txt");
//		File trainFileList = new File(fileList + "/test.txt");
		CompilerEnvirons env = new CompilerEnvirons();
		env.setRecoverFromErrors(true);
		BakerVisitor myVisitor = new BakerVisitor();
		int count = 0;
		if ( trainFileList.exists() )
		{
			List<String> lines = FileUtils.readLines(trainFileList, "UTF-8");
			for ( String str: lines)
			{
				count++;
				if ( count > 100 ) break;
				System.out.println(str);
				try
				{
					FileReader strReader = new FileReader(str);
					IRFactory factory = new IRFactory(env, new JSErrorReporter());
					AstRoot rootNode = factory.parse(strReader, null, 0);
					rootNode.visit(myVisitor);
				}
				catch (Exception e)
				{
					System.out.println(e.getMessage());
					continue;
				}
			}
			myVisitor.printToFile(bakerDir);
			//myVisitor.printToFile(trainSetDir);
		}
	}
	
	public void parseTrainSetForest() throws IOException {
		File trainFileList = new File(fileList + "/test.txt");
//		File trainFileList = new File(fileList + "/trainFileList.txt");
		if ( trainFileList.exists() )
		{
			int count = 0;
			List<String> lines = FileUtils.readLines(trainFileList, "UTF-8");
			for ( String str: lines)
			{
				count++;
				if ( count > 10 ) break;
				try
				{
					CompilerEnvirons env = new CompilerEnvirons();
					env.setRecoverFromErrors(true);
			    	FileReader strReader = new FileReader(str);
					IRFactory factory = new IRFactory(env, new JSErrorReporter());
					//String path = str.substring(str.indexOf("Data") + 4, str.lastIndexOf(".js"));
					String path = str.substring(str.indexOf("Data") + 5, str.lastIndexOf(".js"));
					String projectName = path.substring(0, path.indexOf("\\"));
					String fileName = path.substring(path.lastIndexOf("\\")+1);
					path = "/" + projectName + "_" + fileName; 
//					path = trainSetDir + path;
					path = "../TestRun" + path;
					System.out.println(path);
					ForestVisitor myVisitor = new ForestVisitor(path);
					AstRoot rootNode = factory.parse(strReader, null, 0);
					rootNode.visit(myVisitor);
				}
				
				catch (Exception e)
				{
					System.out.println("Exception :" + e.getMessage());
					e.printStackTrace();
					continue;
				}
			}
		}
	
	}
	
	public void parseTestSet() throws Exception
	{
		//File to record test set
		File testFileList = new File(testSetDir + "/fileList.txt");
//		File testFileList = new File(testSetDir + "/test.txt");
		if ( testFileList.exists() )
		{
			List<String> lines = FileUtils.readLines(testFileList, "UTF-8");
			for ( String str: lines)
			{
				try
				{
					CompilerEnvirons env = new CompilerEnvirons();
					env.setRecoverFromErrors(true);
			    	FileReader strReader = new FileReader(str);
					IRFactory factory = new IRFactory(env, new JSErrorReporter());
					//String path = str.substring(str.indexOf("Data") + 4, str.lastIndexOf(".js"));
					String path = str.substring(str.indexOf("Data") + 5, str.lastIndexOf(".js"));
					String projectName = path.substring(0, path.indexOf("\\"));
					String fileName = path.substring(path.lastIndexOf("\\")+1);
					path = "/" + projectName + "_" + fileName; 
					path = testSetDir + path;
					System.out.println(path);
					TestSetVisitor myVisitor = new TestSetVisitor(path);
					AstRoot rootNode = factory.parse(strReader, null, 0);
					rootNode.visit(myVisitor);
				}
				catch (Exception e)
				{
					System.out.println("Exception at " + e);
					continue;
				}
			}
		}
	}
	
	//For Topic Model training module
	public void parseTrainSetTM() throws Exception
	{
//		File trainFileList = new File(trainSetDir + "/test.txt");
		File trainFileList = new File(trainSetDir + "/fileList.txt");
		if ( trainFileList.exists() )
		{
			CompilerEnvirons env = new CompilerEnvirons();
			env.setRecoverFromErrors(true);

			List<String> lines = FileUtils.readLines(trainFileList, "UTF-8");
			for ( String str: lines)
			{
				System.out.println(str);
				try
				{
					TMTrainSetVisitor myVisitor = new TMTrainSetVisitor();
					FileReader strReader = new FileReader(str);
					IRFactory factory = new IRFactory(env, new JSErrorReporter());
					AstRoot rootNode = factory.parse(strReader, null, 0);
					//Should separate into visitor for each function
					rootNode.visit(myVisitor);
//					myVisitor.print();
					myVisitor.printToFile(trainTMDir);
				}
				catch (Exception e)
				{
					System.out.println("Exception at " + e);
					continue;
				}
			}
			//myVisitor.printToFile(trainSetDir);
		}
	}
	
	public void parseTestSetTM() throws IOException {

		//File to record test set
		File testFileList = new File(testSetDir + "/fileList.txt");
//		File testFileList = new File(testSetDir + "/test.txt");
		if ( testFileList.exists() )
		{
			List<String> lines = FileUtils.readLines(testFileList, "UTF-8");
			for ( String str: lines)
			{
				try
				{
					CompilerEnvirons env = new CompilerEnvirons();
					env.setRecoverFromErrors(true);
			    	FileReader strReader = new FileReader(str);
					IRFactory factory = new IRFactory(env, new JSErrorReporter());
					//String path = str.substring(str.indexOf("Data") + 4, str.lastIndexOf(".js"));
					String path = str.substring(str.indexOf("Data") + 5, str.lastIndexOf(".js"));
					String projectName = path.substring(0, path.indexOf("\\"));
					String fileName = path.substring(path.lastIndexOf("\\")+1);
					path = "/" + projectName + "_" + fileName; 
					path = testSetDir + path;
					System.out.println(path);
					TMTestSetVisitor myVisitor = new TMTestSetVisitor();
					AstRoot rootNode = factory.parse(strReader, null, 0);
					rootNode.visit(myVisitor);
					myVisitor.printToFile(path);
				}
				catch (Exception e)
				{
					System.out.println("Exception at " + e);
					continue;
				}
			}
		}
	
		
	}
	
	public void searchDir(File dir, ArrayList<File> files) {
		if ( dir.isFile() )
		{
			files.add(dir);
			return;
		}
		for ( File file : dir.listFiles() )
		{
			if ( file.isDirectory() )
			{
				searchDir(file,files);
			}
			else if ( file.getName().contains(".js") )
			{
				if ( !file.getName().startsWith("._"))
				{
					files.add(file);
				}
			}
		}
	}	
}
