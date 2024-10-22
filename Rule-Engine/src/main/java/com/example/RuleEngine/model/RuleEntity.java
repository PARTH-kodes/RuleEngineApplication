package com.example.RuleEngine.model;

import jakarta.persistence.*;

@Entity
public class RuleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String ruleString;

    @Column(nullable = false)
    private String astJson; // Seerialized verion of the AST if needed

    //Constructors, Getters and Setters
    public RuleEntity() {}

    public RuleEntity(String ruleString, String astJson){
        this.ruleString = ruleString;
        this.astJson = astJson;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }    

    public String getRuleString() {
        return ruleString;
    }

    public void setRuleString(String ruleString) {
        this.ruleString = ruleString;
    }    

    public String getAstJson() {
        return astJson;
    }

    public void setAstJson(String astJson) {
        this.astJson = astJson;
    }
}
