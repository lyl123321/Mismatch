package solution.MismatchSolution.xmlParser;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;

import org.dom4j.Element;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

public class XmlParser {
	private Environment myDbEnvironment;
	private ReplaceTable replaceTable;
	private SubtreeTable subtreeTable;
	private InvertedTable invertedTable;
	
	public XmlParser(String[] Q) {
		try {
			EnvironmentConfig envConfig = new EnvironmentConfig();
		    envConfig.setAllowCreate(true);
		    myDbEnvironment = new Environment(new File("data/dbEnv"), envConfig);
		} catch (Exception e) {
			System.err.println("ERROR: database environment can not be opened");
		}
		replaceTable = new ReplaceTable();
		replaceTable.buildReplaceTableDB(myDbEnvironment);
		subtreeTable = new SubtreeTable();
		subtreeTable.buildSubtreeTableDB(myDbEnvironment);
		invertedTable = new InvertedTable();
		invertedTable.buildInvertedTableDB(myDbEnvironment, Q);
	}
	
	/*public static String path2Type(String path) {
    	Matcher input = Pattern.compile("\\[.*\\]").matcher(path);
    	String type = input.replaceAll("");
    	return type;
    }*/
    
    public HashMap process1(Element element, String s, int n, Vector<String> typeList){
    	int len = typeList.size();
    	int[] ft = new int[len];
    	Arrays.fill(ft, 0);
    	
    	int[] exLabel = new int[len];
    	process1Rec(element, s, n, typeList, ft, exLabel, exLabel);
    	
    	HashMap info = new HashMap();
    	info.put("typeList", typeList);
    	info.put("ft", ft);
    	//System.out.println(Arrays.toString(ft));
    	
    	return info;
    }
    
    private void process1Rec(Element element, String s, int n, Vector<String> typeList, int[] ft, int[] faExLabel, int[] grExLabel){
    	Iterator<Element> iterator = element.elementIterator();
        String deweyID = s + (new Integer(n)).toString();
        String type = element.getPath();
        String path = element.getUniquePath(); 
        String text = element.getStringValue();
        int m = 0, len1 = typeList.size(), index1 = typeList.indexOf(type);
        ft[index1]++;
        
        int[] exLabel = new int[len1];
        for(int i = 0; i < len1; i++) {
        	exLabel[i] = 0;
        }
        exLabel[index1] = faExLabel[index1] = grExLabel[index1] = 1;
        
        while (iterator.hasNext()) { 
        	Element child = iterator.next();
        	int index2 = typeList.indexOf(element.getPath());
        	exLabel[index2] = faExLabel[index2] = grExLabel[index2] = 1;
        	process1Rec(child, s + (new Integer(n)).toString() + ".", m, typeList, ft, exLabel, faExLabel);
            m++;
        }
        //性能不好
        if(type.split("/").length == 2) {
        	for(int i = 0; i < len1; i++) {
            	exLabel[i] = 1;
            }
        }
        replaceTable.setIndex(deweyID, type, path, text, exLabel);
    }

    public void process2(org.jsoup.nodes.Element element, String s, int n){
    	Iterator<org.jsoup.nodes.Element> iterator = element.children().iterator();
        String deweyID = s + (new Integer(n)).toString();
        String subtree = element.outerHtml();
        int m = 0;
        while (iterator.hasNext()) { 
        	org.jsoup.nodes.Element child = iterator.next();
            process2(child, s + (new Integer(n)).toString() + ".", m);
            m++;
        }
        subtreeTable.setIndex(deweyID, subtree);
    }
    
    public void process3(Element element, String s, int n){
    	Iterator<Element> iterator = element.elementIterator();
        String type = element.getPath();
        String deweyID = s + (new Integer(n)).toString(); 
        String subtree = subtreeTable.getIndex(deweyID);
        int m = 0;
        while (iterator.hasNext()) {
        	Element child = iterator.next();
            process3(child, s + (new Integer(n)).toString() + ".", m);
            m++;
        }
        invertedTable.setIndex(type, deweyID, subtree);
    }
    
    public void close() {
    	replaceTable.closeReplaceTableDB();
    	subtreeTable.closeSubtreeTableDB();
    	invertedTable.closeInvertedTableDB();
		try {
		    if (myDbEnvironment != null) {
		    	myDbEnvironment.cleanLog();
		    	myDbEnvironment.close();
	        }
		} catch (DatabaseException dbe) {
			System.err.println("ERROR: database environment can not be closed");
		}
	}
}