package com.example.RuleEngine.parser;

import java.util.*;
import com.example.RuleEngine.model.RuleNode;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.*;

@Component
public class RuleParser {

    /**
     * Parses a rule string into an Abstract Syntax Tree (AST) represented by RuleNode.
     *
     * @param ruleString The string representation of the rule.
     * @return The root node of the constructed AST.
     */

    @Autowired
    private RuleNode ruleNode;
    public RuleNode parseRule(String ruleString){
        Stack<RuleNode> operands = new Stack<>();
        Stack<String> missedOperators = new Stack<>();

        String tokens[] = ruleString.split("(?<=\\()|(?=\\()|(?<=\\))|(?=\\))|(?<=\\s)|(?=\\s)");

        StringBuilder operandBuilder = new StringBuilder();
        boolean buildingOperand = false;

        for (String token : tokens) {
            token = token.trim(); // Trim any extra spaces
            if (token.isEmpty()) {
                continue; // Skip empty tokens
            }

            if(buildingOperand && (token.equals("AND") || token.equals("OR") || token.equals(")"))){
                RuleNode operandNode = new RuleNode("operand", null, null);
                operandNode.setValue(operandBuilder.toString().trim());
                operands.push(operandNode);
                operandBuilder.setLength(0);
                buildingOperand = false;
            }

            switch (token.toUpperCase()) {
                case "AND":
                case "OR":
                    // Ensure there are enough nodes to pop
                    if (missedOperators.size() > 0 || operands.size() < 2) {
                        missedOperators.push(token);
                        continue;
                    }

                    RuleNode rightNode = operands.pop();
                    RuleNode leftNode = operands.pop();
                    operands.push(new RuleNode(token.toUpperCase(), leftNode, rightNode));
                    break;

                case "(":
                    // Do nothing for opening parenthesis
                    break;

                case ")":
                    // Check if we are building an operand, push it to the operands before proceeding

                    if(operands.size() >= 2){
                        rightNode = operands.pop();
                        leftNode = operands.pop();
                        operands.push(new RuleNode(missedOperators.pop(), leftNode, rightNode));
                    }


                    break;

                default:
                    // Build the operand as a whole if it contains multiple parts like "age > 30"
                    buildingOperand = true;
                    operandBuilder.append(token).append(" ");
                    break;
            }
        }

        // Final check: if there's an unprocessed operand, add it to the operands
        if (buildingOperand) {
            RuleNode operandNode = new RuleNode("operand", null, null);
            operandNode.setValue(operandBuilder.toString().trim());
            operands.push(operandNode);
        }

        while(!missedOperators.isEmpty()){
            RuleNode rightNode = operands.pop();
            RuleNode leftNode = operands.pop();
            operands.push(new RuleNode(missedOperators.pop(), leftNode, rightNode));
        }

        if (operands.size() > 1) {
            throw new IllegalArgumentException("Invalid rule format: too many operands");
        }

        if (operands.isEmpty()) {
            throw new IllegalArgumentException("Invalid rule format: no valid expression found");
        }

        ruleNode.assignID(operands.peek(), 1);
        return operands.pop();
    }

    /**
     * Combines two rule nodes using a specified operator.
     *
     * @param root1   The first rule node.
     * @param root2   The second rule node.
     * @param operator The operator to combine the two rule nodes.
     * @return A new RuleNode representing the combination.
     */

    public RuleNode combineRules(RuleNode root1, RuleNode root2, String operator){
        return new RuleNode(operator, root1, root2);
    }
}
