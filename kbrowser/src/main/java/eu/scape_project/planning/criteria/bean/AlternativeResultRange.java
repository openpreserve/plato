package eu.scape_project.planning.criteria.bean;

public class AlternativeResultRange {

    private String name;

    private double result;

    private double lowerBound;

    private double upperBound;

    public AlternativeResultRange(String name, double result) {
        this.name = name;
        this.result = result;
        this.lowerBound = result;
        this.upperBound = result;
    }

    public AlternativeResultRange(String name, double result, double lowerBound, double upperBound) {
        this.name = name;
        this.result = result;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    // ---------- getter/setter ----------
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getResult() {
        return result;
    }

    public void setResult(double result) {
        this.result = result;
    }

    public double getLowerBound() {
        return lowerBound;
    }

    public void setLowerBound(double lowerBound) {
        this.lowerBound = lowerBound;
    }

    public double getUpperBound() {
        return upperBound;
    }

    public void setUpperBound(double upperBound) {
        this.upperBound = upperBound;
    }

}
