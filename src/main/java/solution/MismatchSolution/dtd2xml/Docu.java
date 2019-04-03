package solution.MismatchSolution.dtd2xml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


public class Docu 
{
    private String version;
    private String encoding;
    private Elem rootElement = null;
    private List<Key> keys = null;
    private List<Elem> allElements = null;
    
    public Docu()
    {
        keys = new ArrayList<>();
        allElements = new ArrayList<>();
        version = "1.0";
        encoding = "UTF-8";
    }
    public void addElement(String parentName, Elem element)
    {
        if(parentName == null)
        {
            rootElement = element;            
        }
        else
        {
            findElement(parentName).addElement(element);            
        }      
        allElements.add(element);
    }
    
    public void addObject(String parentName, Structure structure)
    {
        findElement(parentName).addObject(structure);      
    }
    
    public Elem findElement(String parentName)
    {
        for(Elem i:allElements)
        {
            if(i.getName().equals(parentName))
            {
                return i;
            }
        }
        return null;
    }
    
    public String getVersion()
    {
        return version;
    }
    
    public void setVersion(String version)
    {
        this.version = version;
    }
    
    public String getEncoding()
    {
        return encoding;
    }
    
    public void setEncoding(String encoding)
    {
        this.encoding = encoding;
    }
    
    public Elem getRootElement()
    {
        return rootElement;
    }
    
    public List<Elem> getElements(String parentName)
    {
        Elem pom = findElement(parentName);
        return pom.getElements();
    }
      
    public void addAttribute(String parentName, Attribute attribute)
    {        
            findElement(parentName).addAttribute(attribute); 
    }
    
    public List<Attribute> getAttributes(String parentName)
    {
        Elem pom = findElement(parentName);
        return pom.getAttributes();
    }
    
    public void addKey(String parentName, String name, String fixedValue)
    {
        if(keys == null)
        {
            keys = new LinkedList<>();
        }
        
        keys.add(new Key(parentName, name, "required", fixedValue));   
    }
    
    public List<Key> getKeys()
    {
        return Collections.unmodifiableList(keys);
    }
    
    public String toXsd() 
    {
        String ret = new String();
        ret += String.format( "<?xml version=\"%s\" encoding=\"%s\"?>\n", getVersion(), getEncoding() );  
        ret += "<xsd:schema xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" elementFormDefault=\"qualified\">\n";
        ret += rootElement.toXsd(this);
        ret += "</xsd:schema>";
        return ret;
    }
    
    public String toXml(String dtdUrl) {
    	String ret = new String();
    	String[] names = dtdUrl.split("[\\./]");
        ret += String.format( "<?xml version=\"%s\" encoding=\"%s\"?>\n", getVersion(), getEncoding() );  
        ret += "<!DOCTYPE "+ names[names.length - 2] + " SYSTEM \"" + dtdUrl + "\">\n";
        ret += rootElement.toXml();
        return ret;
    }
}
