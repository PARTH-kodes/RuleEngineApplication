package com.example.RuleEngine.controller;

import java.util.*;
import com.example.RuleEngine.model.RuleNode;
import com.example.RuleEngine.service.RuleService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.RuleEngine.model.RuleEntity;
import com.example.RuleEngine.repository.RuleRepository;
import com.example.RuleEngine.parser.RuleParser;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.ResponseEntity;
;

@RestController
@RequestMapping("/api/rules")

public class RuleController {
    
    @Autowired
    private RuleService ruleService;

    @Autowired
    private RuleParser ruleParser;
    
    @Autowired
    private RuleRepository ruleRepository;

    // Endpoint to create a rule
    @PostMapping("/create")
    public ResponseEntity<RuleNode> createRule(@RequestBody String ruleString) {
        //Logic to handle the incoming rule and create an AST
        RuleNode root = ruleService.createRule(ruleString);
        return ResponseEntity.ok(root);
    }

    // Endpoint to combine multiple rules
    @PostMapping("/combine")
    public ResponseEntity<RuleNode> combineRules(@RequestBody Map<String, Object> combiningMap) {

        List<String> ruleStrings = null;

        if(combiningMap.get("rules") instanceof List<?> list){
            if(list.isEmpty()) return ResponseEntity.badRequest().body(null);
            ruleStrings = new ArrayList<>();
            for(Object obj : list){
                if(obj instanceof String str){
                    ruleStrings.add(str);
                } else {
                    // Handle the case where the object is not a String
                    return ResponseEntity.badRequest().body(null);
                }
            }
        }

        String operator = (String) combiningMap.get("operator");

        // Validating the input
        if(ruleStrings == null || ruleStrings.isEmpty()) return ResponseEntity.badRequest().body(null); 
        
        if(operator.isEmpty() || operator == null) operator = "AND";

        RuleNode combinedRoot = ruleService.combineRules(ruleStrings, operator);
        
        return (combinedRoot != null) ? ResponseEntity.ok(combinedRoot) : ResponseEntity.status(500).body(null);
    }

    // Endpoint to evaluate a rule agauinst data
    @PostMapping("/evaluate")
    public ResponseEntity<Boolean> evaluateRule(@RequestBody Map<String, Object> data) {

        RuleNode rootNode = ruleParser.parseRule(data.get("rule").toString());

        boolean result = ruleService.evaluateRule(rootNode, data);

        return ResponseEntity.ok(result);
    }

    // Endpoint to save a rule
    @PostMapping("/save")
    public String saveRules(@RequestBody String ruleString) throws Exception {
        // Parse the rule string into an AST
        RuleNode ast = ruleParser.parseRule(ruleString);

        // Convert AST to JSON string for storage
        String astJson = new ObjectMapper().writeValueAsString(ast);

        // Save the rule to the database
        RuleEntity ruleEntity = new RuleEntity(ruleString, astJson);
        ruleRepository.save(ruleEntity);

        return "Rule saved successfully!";
    }

    // Endpoint to load all rules
    @GetMapping("/load")
    public ResponseEntity<List<RuleEntity>> loadRules() {
        List<RuleEntity> rules = ruleService.getAllRules();
        return ResponseEntity.ok(rules);
    }

    //Endpoint to update a rule by ID
    @PutMapping("/update/{id}")
    public ResponseEntity<RuleEntity> updateRule(@PathVariable Long id, @RequestBody String newRuleString) throws JsonProcessingException{
        RuleEntity updateRule =  ruleService.updateRule(id, newRuleString);
        return updateRule != null ? ResponseEntity.ok(updateRule) : ResponseEntity.notFound().build();
    }

    @GetMapping("/test")
    public String testEndpoint() {
        return "API is working!";
    }
}
