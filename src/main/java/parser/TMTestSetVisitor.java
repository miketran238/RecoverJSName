package parser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;

import org.mozilla.javascript.ast.*;


public class TMTestSetVisitor implements NodeVisitor{
	HashSet<String> programEntities = new HashSet<>();
	HashSet<String> variableNames = new HashSet<>();
	//HashSet<String> relationships = new HashSet<>();
	HashMap<Record, Integer> recordList = new HashMap<>();
	HashMap<FunctionNode, String> functionSet = new HashMap<>();
	public TMTestSetVisitor()
	{
		//relationships.add("property");
		//relationships.put("AssignVar", 1);
	}
	
	public void print()
	{
		for ( FunctionNode fn: functionSet.keySet() )
		{
			System.out.println(functionSet.get(fn));
		}
	}
	
	public void printToFile(String dest) throws IOException
	{
		for ( FunctionNode fn: functionSet.keySet() )
		{
			if ( functionSet.get(fn).split(" ").length > 10 )
			{
				String dir = dest + "_" + fn.getName();
				File file = new File(dir + "/topicModel.txt");
				FileWriter fw;
				if (!file.exists()) {
					file.createNewFile();
				}
				fw = new FileWriter(file.getAbsoluteFile());
				fw.write(functionSet.get(fn));
				fw.write("\n");
				fw.close();
			}
		}
	}
	
	@Override
	public boolean visit(AstNode node) {
		if ( node.getEnclosingFunction() == null )
			return true;
		if ( node instanceof Name )
		{
			FunctionNode fn = node.getEnclosingFunction();
			if (! functionSet.containsKey(fn) )
			{
				functionSet.put(fn, "");
			}
			String name = ((Name)node).getIdentifier();
			functionSet.put(fn, functionSet.getOrDefault(fn, "") + " " + name);
		}
		return true;
	}
}
