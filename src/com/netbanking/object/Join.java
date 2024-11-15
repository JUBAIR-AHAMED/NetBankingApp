package com.netbanking.object;

public class Join {
    private String leftTable;
    private String leftColumn;
    private String rightTable;
    private String rightColumn;
    private String operator;
    private String logicalOperator;

    public Join() {}

    public Join(String leftTable, String leftColumn, 
                         String rightTable, String rightColumn, 
                         String operator, String logicalOperator) {
        this.leftTable = leftTable;
        this.leftColumn = leftColumn;
        this.rightTable = rightTable;
        this.rightColumn = rightColumn;
        this.operator = operator;
        this.logicalOperator = logicalOperator;
    }

    public Join(String leftTable, String leftColumn, 
                         String rightTable, String rightColumn, 
                         String operator) {
        this(leftTable, leftColumn, rightTable, rightColumn, operator, null);
    }

    // Getters
    public String getLeftTable() {
        return leftTable;
    }

    public String getLeftColumn() {
        return leftColumn;
    }

    public String getRightTable() {
        return rightTable;
    }

    public String getRightColumn() {
        return rightColumn;
    }

    public String getOperator() {
        return operator;
    }

    public String getLogicalOperator() {
        return logicalOperator;
    }

    // Setters
    public void setLeftTable(String leftTable) {
        this.leftTable = leftTable;
    }

    public void setLeftColumn(String leftColumn) {
        this.leftColumn = leftColumn;
    }

    public void setRightTable(String rightTable) {
        this.rightTable = rightTable;
    }

    public void setRightColumn(String rightColumn) {
        this.rightColumn = rightColumn;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public void setLogicalOperator(String logicalOperator) {
        this.logicalOperator = logicalOperator;
    }
}
