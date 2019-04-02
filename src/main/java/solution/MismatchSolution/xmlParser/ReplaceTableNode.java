package solution.MismatchSolution.xmlParser;

import java.io.Serializable;

public class ReplaceTableNode implements Serializable {
	private static final long serialVersionUID = 8331189051473234391L;
	
	private String type;
    private String path;
    private String text;
    private int[] exLabel;
    
    public ReplaceTableNode(String type, String path, String text, int[] exLabel){
    	this.type = type;
    	this.path = path;
    	this.text = text;
    	this.exLabel = exLabel;
    }
    
    public void setType(String type) {
    	this.type = type;
    }
    
    public void setPath(String path) {
    	this.path = path;
    }
    
    public void setText(String text) {
    	this.text = text;
    }
    
    public void setExLabel(int[] exLabel) {
    	this.exLabel = exLabel;
    }
    
    public String getType() {
        return type;
    }

    public String getPath() {
        return path;
    }
    
    public String getText() {
        return text;
    }
    
    public int[] getExLabel() {
        return exLabel;
    }
}