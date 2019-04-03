package solution.MismatchSolution.dtd2xml;


public class Attribute {
    private String name;    
    private String option;
    private String fixed;
    
    public Attribute(String name, String option, String fixed)
    {
        this.name = name;      
        this.option = option;
        this.fixed = fixed;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public String getOption()
    {
        return option;
    }
    
    public void setOption(String option)
    {
        this.option = option;
    }
    
    public String toXsd() 
    {
        return "<xsd:attribute name=\"" + name + "\" type=\"xsd:string\" " + 
               "use=\"" + option + "\"" + ((!fixed.isEmpty()) ? " fixed=" + fixed : "") + "/>\n";         
    }
    
    public String toXml() 
    {
        return " " + name + "=" + fixed;         
    }
}
