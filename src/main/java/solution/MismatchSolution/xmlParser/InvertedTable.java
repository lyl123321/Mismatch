package solution.MismatchSolution.xmlParser;

import java.util.ArrayList;
import java.util.Arrays;

import com.sleepycat.je.Cursor;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;

public class InvertedTable {
	private int myDBNumber;
	private String[] myDBName;
	private String[] keywords;
	private Database[] myDB;
    
    public InvertedTable() {
    	myDBNumber = 0;
		myDBName = null;
		keywords = null;
		myDB = null;
    }
    
	public void buildInvertedTableDB(Environment myDbEnvironment, String[] qList) {
		keywords = qList;
		myDBNumber = keywords.length;
		myDBName = new String[myDBNumber];
		for(int i = 0; i < myDBNumber; i++) {
			myDBName[i] = "invertedTableDB_" + keywords[i];
		}
		myDB = new Database[myDBNumber];
		DatabaseConfig dbConfig = new DatabaseConfig();
	    dbConfig.setAllowCreate(true);
	    dbConfig.setSortedDuplicates(true);
	    for (int i = 0; i < myDBNumber; i++) {
			try {
				myDB[i] = myDbEnvironment.openDatabase(null, myDBName[i], dbConfig);
			} catch (DatabaseException dbe) {
				System.err.println("ERROR: inverted table database can not be built");
			}
	    }
	}
	
	public void openInvertedTableDB(Environment myDbEnvironment, String[] qList) {
		keywords = qList;
		myDBNumber = keywords.length;
		myDBName = new String[myDBNumber];
		for(int i = 0; i < myDBNumber; i++) {
			myDBName[i] = "invertedTableDB_" + keywords[i];
		}
		myDB = new Database[myDBNumber];
		DatabaseConfig dbConfig = new DatabaseConfig();
	    dbConfig.setAllowCreate(false);
	    dbConfig.setSortedDuplicates(true);
		try {
			for (int i = 0; i < myDBNumber; i++) {
				myDB[i] = myDbEnvironment.openDatabase(null, myDBName[i], dbConfig);
			}
		} catch (DatabaseException dbe) {
			System.err.println("ERROR: inverted table database can not be opened");
		}
	}
	
	public void setIndex(String type, String deweyID, String subtree) {
		for(int i = 0; i < myDBNumber; i++) {
			if(subtree.indexOf(keywords[i]) >= 0) {
				//System.out.println("type:" + type + ", deweyID: " + deweyID + ", keyword:" + keywords[i]);
				try {
					DatabaseEntry theKey = new DatabaseEntry(type.getBytes("UTF-8"));
					DatabaseEntry theData = new DatabaseEntry(deweyID.getBytes("UTF-8"));
					myDB[i].put(null, theKey, theData);
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		}
		
	}
	
	/*public String getIndex(String type) {
		String deweyID = "";
		try {
			DatabaseEntry theKey = new DatabaseEntry(type.getBytes("UTF-8"));
			DatabaseEntry theData = new DatabaseEntry();
			if (myDatabase.get(null, theKey, theData, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
				deweyID = new String(theData.getData(), "UTF-8");
			} else {
				System.out.println("No record found for key '" + theKey + "'.");
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return deweyID;
	}*/
	
	//求 FtK， 首先取出K中每个关键字关于类型 t的 deweyID数组，然后找到长度最短的数组，遍历这个数组，依次判断其中每个deweyID是否在剩余的数组中也存在，count++,最后求得fkt
	public int getFtK(String type, String[] K) {
		int len = K.length;
		ArrayList<ArrayList<String>> idArray = new ArrayList<ArrayList<String>>();
		
		for(int i = 0; i < len; i++) {
			ArrayList<String> names = new ArrayList<String>(Arrays.asList(myDBName));
			int index = names.indexOf("invertedTableDB_" + K[i]);
			Database database = myDB[index];
			Cursor cursor = database.openCursor(null, null);
			ArrayList<String> deweyIDs = new ArrayList<String>();
			try {
				DatabaseEntry theKey = new DatabaseEntry(type.getBytes("UTF-8"));
				DatabaseEntry theData = new DatabaseEntry();
				while (cursor.getNext(theKey, theData, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
			        String deweyID = new String(theData.getData(), "UTF-8");
			        deweyIDs.add(deweyID);
				}
			} catch (Exception e) {
				// TODO: handle exception
			} finally {
				//System.out.println(deweyIDs);
				idArray.add(deweyIDs);
				cursor.close();
			}
		}
		
		int len2 = idArray.size();
		for(int i = 1; i < len2; i++) {
			ArrayList<String> temp = idArray.get(i);
			temp.retainAll(idArray.get(i - 1));
			idArray.set(i, temp);
		}
		
		return idArray.get(len2 - 1).size();
	}
	
	
	public void closeInvertedTableDB() {
		for (int i = 0; i < myDBNumber; i++) {
			if (myDB[i] != null) {
				myDB[i].close();
			}
		}
	}
}