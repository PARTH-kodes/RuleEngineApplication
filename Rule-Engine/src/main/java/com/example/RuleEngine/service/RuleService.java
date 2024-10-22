package com.example.RuleEngine.service;

import java.util.*;
import com.example.RuleEngine.model.RuleEntity;
import com.example.RuleEngine.model.RuleNode;
import com.example.RuleEngine.parser.RuleParser;
import com.example.RuleEngine.repository.RuleRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RuleService {

    @Autowired
    private RuleParser ruleParser;

    @Autowired
    private RuleRepository ruleRepository;

    // Create an AST from the provided rule string
    public RuleNode createRule(String ruleString) {
        return ruleParser.parseRule(ruleString);
    }

    // Combining rules 
    public RuleNode combineRules(List<String> ruleStrings, String operator) {
        List<RuleNode> ruleNodes = new ArrayList<>();

        // Parse each rule string and collect the resulting ASTs
        for (String ruleString : ruleStrings) {
            RuleNode node = createRule(ruleString);
            ruleNodes.add(node);
        }

        // Now we need to combine the rules efficiently
        return combineRuleNodes(ruleNodes, operator);
    }

    // This method combines RuleNodes based on operator
    private RuleNode combineRuleNodes(List<RuleNode> ruleNodes, String operator) {
        if (ruleNodes.isEmpty()) {
            return null; // No rules to combine
        }

        // Use a stack to manage the rule nodes for combining
        Stack<RuleNode> stack = new Stack<>();

        for (RuleNode node : ruleNodes) {
            
            // Combine with the provided operator
            if (!stack.isEmpty()) {
                RuleNode leftNode = stack.pop();
                RuleNode combinedNode = ruleParser.combineRules(leftNode, node, operator);
                stack.push(combinedNode);
            } else {
                stack.push(node); // Push the first node onto the stack
            }
        }

        return stack.pop(); // Return the final combined root node
    }

    // Evaluate the rule against the provided data
    public boolean evaluateRule(RuleNode root, Map<String, Object> data) {
        return root.evaluate(data); // Use the existing evaluate method
    }

    // Save a new rule to the database
    public RuleEntity saveRule(String ruleString) throws JsonProcessingException{

        // Parse the rule string into an AST
        RuleNode ast = ruleParser.parseRule(ruleString);

        // Convert AST to JSON string for storage
        String astJson = new ObjectMapper().writeValueAsString(ast);
        
        // Convert and Save
        RuleEntity ruleEntity = new RuleEntity(ruleString, astJson);
        return ruleRepository.save(ruleEntity);
    }

    //Load all rules from the database
    public List<RuleEntity> getAllRules(){
        return ruleRepository.findAll();
    }

    public RuleEntity updateRule(Long id, String newRuleString) throws JsonProcessingException{
        Optional<RuleEntity> optionalRule = ruleRepository.findById(id);
        if(optionalRule.isPresent()){
            RuleEntity ruleEntity = optionalRule.get();
            
            // Parse new rule to AST and convert to JSON
            RuleNode newAst = ruleParser.parseRule(newRuleString);
            String newAstJson = new ObjectMapper().writeValueAsString(newAst);

            // Set the new rule
            ruleEntity.setRuleString(newRuleString);
            ruleEntity.setAstJson(newAstJson);

            return ruleRepository.save(ruleEntity);
        }
        return null; // If not found
    }
}