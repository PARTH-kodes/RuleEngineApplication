package com.example.RuleEngine.model;

import java.util.*;
import jakarta.persistence.*;
// import com.fasterxml.jackson.databind.JsonNode;

import org.springframework.stereotype.*;


@Component
@Entity
public class RuleNode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    private String astJson;

    private String type; // "operator" or "operand"
    private String value; // Operand value (e.g., "age > 30")


    @OneToOne(cascade = CascadeType.ALL)
    private RuleNode left;

    @OneToOne(cascade = CascadeType.ALL)
    private RuleNode right;

    public RuleNode(){}

    public RuleNode(String type, RuleNode left, RuleNode right) {
        this.type = type;
        this.left = left;
        this.right = right;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAstJson() {
        return astJson;
    }

    public void setAstJson(String astJson) {
        this.astJson = astJson;
    }
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public RuleNode getLeft() {
        return left;
    }

    public void setLeft(RuleNode left) {
        this.left = left;
    }

    public RuleNode getRight() {
        return right;
    }

    public void setRight(RuleNode right) {
        this.right = right;
    }

    // To assign ID in the final formed AST(rule)
    public void assignID(RuleNode root, long ID){
        if(root == null) return;

        assignID(root.left, ID + 1);
        assignID(root.right, ID + 1);

        root.id = ID;
    }

    /**
     * Evaluates the rule node against the provided data.
     * @param data The map containing the data against which the rule is evaluated.
     * @return The result of the evaluation as a boolean.
     * @throws IllegalArgumentException if the node type is invalid or if an operator is invalid.
    */
    public boolean evaluate(Map<String, Object> map) {
        if("AND".equals(type)){
            return left.evaluate(map) && right.evaluate(map);
        } else if("OR".equals(type)){
            return left.evaluate(map) || right.evaluate(map);
        } else if("operand".equals(type)) {
            
            String parts[] = value.split(" ");
            if(parts.length != 3) {
                throw new IllegalArgumentException("Invalid operand: " + value);
            }

            String attribute = parts[0];
            String operator = parts[1];
            String comparisonValue = parts[2].replaceAll("'", "");

            Object actualValue = map.get(attribute);

            if(actualValue == null) { 
                throw new IllegalArgumentException("Invalid argument type for attribute: " + attribute);
            }

            int intValue = (actualValue instanceof Integer) ? (Integer) actualValue : 0;
            String strValue = (actualValue instanceof String) ? (String) actualValue : "";

            switch(operator){
                case ">":
                    return intValue > Integer.parseInt(comparisonValue);
                case "<":
                    return intValue < Integer.parseInt(comparisonValue);
                case ">=":
                    return intValue >= Integer.parseInt(comparisonValue);
                case "<=":
                    return intValue <= Integer.parseInt(comparisonValue);
                    case "!=":
                    return !strValue.equals(comparisonValue);
                case "==":
                case "=":
                    return strValue.equals(comparisonValue);
                default:
                    throw new IllegalArgumentException("Invalid operator: " + operator);
            }
        }

        throw new IllegalArgumentException("Unknown type: " + type);
    }
}
