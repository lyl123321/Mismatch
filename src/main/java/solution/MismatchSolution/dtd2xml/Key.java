package solution.MismatchSolution.dtd2xml;


public class Key extends Attribute {
    private String parent;

    public Key(String parentName, String name, String option, String fixed) {
        super(name, option, fixed);
        this.parent = parentName;
    }
    
    public String getParent()
    {
        return parent;
    }
    
    public void setParent(String parent)
    {
        this.parent = parent;
    }
}
