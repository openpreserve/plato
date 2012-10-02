package eu.scape_project.planning.model;

public enum PlanType {
    FULL("Full"), 
    FTE("FTE");
    
    private String name;
    
    private PlanType(final String name){
        this.name = name;
    }
    
    
    public boolean isFull(){
        return (this == FULL);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    
}
