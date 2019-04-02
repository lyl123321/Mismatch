package solution.MismatchSolution.dtd2xml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


public class Elem 
{   
    private String name = null;
    private String identifier = null;
    private List<Object> objects = null;
    private List<Elem> elems = null;
    private List<Attribute> attributes = null;
    private String minOccurs;
    private String maxOccurs;
    
    public Elem(String name)
    {
        this.name = name;     
        this.identifier = "element";
        objects = new LinkedList<>();
        elems = new ArrayList<>();
        attributes = new ArrayList<>();
        minOccurs = "1";
        maxOccurs = "1";
    } 
    
    public void addObject(Object object)
    {     
        objects.add(object);
    }
    
    public void addElement(Elem element)
    {
        elems.add(element);
    }
    
    public String getName()
    {
        return name;
    }
    
    public List<Object> getObjects()
    {
        return Collections.unmodifiableList(objects);
    }
    
    public List<Elem> getElements()
    {
        return Collections.unmodifiableList(elems);
    }
    
    public void addAttribute(Attribute attribute)
    {        
        attributes.add(attribute);
    }
    
    public List<Attribute> getAttributes()
    {
        return Collections.unmodifiableList(attributes);
    }
    
    public String getIdentifier()
    {
        return identifier;
    }
    
    public String getMaxOccurs()
    {
        return maxOccurs;
    }
    
    public void setMaxOccurs(String maxOccurs)
    {
        this.maxOccurs = maxOccurs;
    }
    
    public String getMinOccurs()
    {
        return minOccurs;
    }
    
    public void setMinOccurs(String minOccurs)
    {
        this.minOccurs = minOccurs;
    }
    
    public String toXsd(Docu document)
    {
        String ret = new String();
        
        if( !objects.isEmpty() || !attributes.isEmpty() )
        {            
            if(name.equals(document.getRootElement().getName()))
            {
                ret += "<xsd:element name=\"" + name + "\">\n";                              
            }
            else
            {
                ret += "<xsd:element name=\"" + name + "\" minOccurs=\"" + minOccurs + "\" maxOccurs=\"" + maxOccurs + "\">\n";
            }
            ret += "<xsd:complexType>\n";
            
            for(Object o:objects)
            {
                if(o.getClass().equals(Elem.class))
                {                    
                    ret += ((Elem)o).toXsd(document);
                }
                else
                {
                    ret += ((Structure)o).toXsd(document);
                }
            }   
            for( Attribute a : attributes )
            {
                ret += a.toXsd();
            }            
            ret += "</xsd:complexType>\n";
            if(name.equals(document.getRootElement().getName()))
            {
                for(Key a:document.getKeys())
                    {
                        ret += "<xsd:key name=\"" + a.getName() + "Key\">\n";
                        ret += "<xsd:selector xpath=\".//" + a.getParent() + "\"/>\n";
                        ret += "<xsd:field xpath=\"@" + a.getName() + "\"/>\n";
                        ret += "</xsd:key>\n";
                    }  
            }
            ret += "</xsd:element>\n";
        }       
        else
        {            
            if(name.equals(document.getRootElement().getName()))
            {
                ret += "<xsd:element name=\"" + name + "\" type=\"xsd:string\"/>\n";
            }
            else
            {
                ret += "<xsd:element name=\"" + name + "\" type=\"xsd:string\" minOccurs=\"" + minOccurs + "\" maxOccurs=\"" + maxOccurs + "\"/>\n";
            }
        }     
        
        return ret;
    }
    
    public String toXml()
    {
        String ret = new String();
        ret += "<" + name + ">";
        
        if( !objects.isEmpty()) {
        	ret += "\n";
            for(Object o:objects) {
                if(o.getClass().equals(Elem.class)) {                 
                    ret += ((Elem)o).toXml();
                } else {
                    ret += ((Structure)o).toXml();
                }
            }
        }
        
        ret += "</" + name + ">\n";
        
        return ret;
    }
}
