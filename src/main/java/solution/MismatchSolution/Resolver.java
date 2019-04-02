package solution.MismatchSolution;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;

import solution.MismatchSolution.xmlParser.InvertedTable;
import solution.MismatchSolution.xmlParser.ReplaceTable;
import solution.MismatchSolution.xmlParser.SubtreeTable;

public class Resolver {
	private Environment myDbEnvironment;
	private ReplaceTable replaceTable;
	private SubtreeTable subtreeTable;
	private InvertedTable invertedTable;
	private Vector<String> typeList;
	private int[][] maxContain;
	private int[] Ft;
	private double τ;
	private String[] Q;
	private List<Map> R;
	
	public Resolver(String[] Q, List<Map> R, double τ, HashMap info) {
		try {
			EnvironmentConfig envConfig = new EnvironmentConfig();
		    envConfig.setAllowCreate(false);
		    myDbEnvironment = new Environment(new File("data/dbEnv"), envConfig);
		} catch (Exception e) {
			System.err.println("ERROR: database environment can not be opened");
		}
		replaceTable = new ReplaceTable();
		replaceTable.openReplaceTableDB(myDbEnvironment);
		subtreeTable = new SubtreeTable();
		subtreeTable.openSubtreeTableDB(myDbEnvironment);
		invertedTable = new InvertedTable();
		invertedTable.openInvertedTableDB(myDbEnvironment, Q);
		this.typeList = (Vector<String>)info.get("typeList");
		this.Ft = (int [])info.get("ft");
		this.τ = τ;
		this.Q = Q;
		this.R = R;
		setMaxContain("reed");
	}
	
	public List<String[]> resolve() {
		List<String[]> suggestedQueries = new ArrayList<String[]>();
    	
    	//detector
    	for(Map r : R) {
    		String vlca = (String)r.get("vlca");
    		String vlcaType = replaceTable.getIndex(vlca).getType();
    		String[] nodes = (String[])r.get("nodes");
    		String targetType = getTNT(nodes);
    		if(vlcaType.contentEquals(targetType)) {
    			System.out.println("This query don't exist mismatch problem");
    			return null;
    		}
        }
    	
    	for(Map r : R) {
    		String vlca = (String)r.get("vlca");
    		String vlcaType = replaceTable.getIndex(vlca).getType();
    		String[] nodes = (String[])r.get("nodes");
    		String targetType = getTNT(nodes);
    		//Suggester
    		int[] rExLable = constructExlabel(nodes);
    		//Phase 1
    		for(String node : nodes) {
    			String[] keywords = getKeywords(node);
    			if(getDist(node, keywords) > τ) {
    				int vlcaLen = vlca.split("\\.").length;
    				String[] ids = node.split("\\.");
    				int nodeLen = ids.length;
    			}
    		}
        }
    	
    	return suggestedQueries;
    }
	
	//暂时 ok
	private int[] constructExlabel(String[] nodes) {
		int len = nodes.length;
		int[] res = new int[typeList.size()];
		for(int i = 0; i < len; i++) {
			String type = replaceTable.getIndex(nodes[i]).getType();
			res[typeList.indexOf(type)] = 1;
		}
		return res;
	}
	
	private String[] getKeywords(String node) {
		String subtree = subtreeTable.getIndex(node);
		ArrayList<String> keywords = new ArrayList<String>();
		for(int i = 0, len = Q.length; i < len; i++) {
			String keyword = Q[i];
			if(subtree.indexOf(keyword) >= 0) {
				keywords.add(keyword);
			}
		}
		return keywords.toArray(new String[0]);
	}
	
	private double getDist(String node, String[] keywords) {
		InvertedTable iTable = new InvertedTable();
		iTable.openInvertedTableDB(myDbEnvironment, keywords);
		
		String type = replaceTable.getIndex(node).getType();
		double ft = Ft[typeList.indexOf(type)];
		double ftK = invertedTable.getFtK(type, keywords);
		double dist = 1.0 - ftK / ft + 1.0 / ft;
		System.out.println(dist);
		
		iTable.closeInvertedTableDB();
    	
    	return dist;
	}
	
	//ok 
	private String getTNT(String[] nodes) {
		int len = nodes.length;
		//m1 到 mn 中 ti 类型的不同关键字匹配节点的数量
		int[] count = new int [len];
		String[] types = new String[len];
		List<String> typesNoRe = new ArrayList<String>();
		for(int i = 0; i < len; i++) {
			types[i] = replaceTable.getIndex(nodes[i]).getType();
		}
		
		//去重，计数
		Arrays.fill(count, 1);
		Arrays.sort(types);
		typesNoRe.add(0, types[0]);
		for(int i = 1, j = 0; i < len; i++) {
			if(types[i].contentEquals(typesNoRe.get(j))) {
				count[j]++;
			} else {
				typesNoRe.add(++j, types[i]);
			}
		}
		types = typesNoRe.toArray(new String[0]);
		len = types.length;
		
		//获取每个 type 的 tag
		String[][] arrays = new String[len][];
		for(int i = 0; i < len; i++) {
			arrays[i] = types[i].split("/");
		}
		
		//获取 tnt
		String[] commonPref = getCommonPref(arrays);
		int cLen = commonPref.length;
		for(int i = cLen - 1; i >= 0; i--) {
			boolean b = true;
			int anIndex = typeList.indexOf(commonPref[i]);
			for(int k = 0; k < len; k++) {
				int chIndex = typeList.indexOf(types[k]);
				if(maxContain[anIndex][chIndex] < count[k]) {
					b = false;
					break;
				}
			}
			if(b) return commonPref[i];
		}
		
		return "";
	}
	
	//获取公共前缀
	private String[] getCommonPref(String[][] arrays) {
		int len = arrays.length;
		int minLen = arrays[0].length;
		List<String> commonPref = new ArrayList<String>();
		
		for(int i = 1; i < len; i++) {
			if(arrays[i].length < minLen) {
				minLen = arrays[i].length;
			}
		}
		
		for(int i = 1; i < minLen; i++) {
			boolean b = true;
			String temp = arrays[0][i];
			for(int j = 1; j < len; j++) {
				if(temp.contentEquals(arrays[j][i])) continue;
				b = false;
				break;
			}
			if(b) {
				commonPref.add(i - 1, temp);
			} else {
				break;
			}
		}
		
		String[] commonPath = commonPref.toArray(new String[0]);
		int cLen = commonPath.length;
		
		for (int i = cLen - 1; i >= 0; i--) {
			String temp = "";
			for(int j = 0; j <= i; j++) {
				temp += "/" + commonPath[j];
			}
			commonPath[i] = temp;
		}
		
		return commonPath;
	}
	
	private void setMaxContain(String model) {
		int len = typeList.size();
		maxContain = new int[len][];
		switch (model) {
		case "reed":
			maxContain[0] = new int[len];
			Arrays.fill(maxContain[0], 100);
			maxContain[0][0] = 1;
			for (int i = 1; i < len; i++) {
				maxContain[i] = new int[len];
				Arrays.fill(maxContain[i], 1);;
			}
			break;
		case "dblp":
			for (int i = 0; i < len; i++) {
				maxContain[i] = new int[len];
				Arrays.fill(maxContain[i], 100);;
			}
			break;
		default:
			break;
		}
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