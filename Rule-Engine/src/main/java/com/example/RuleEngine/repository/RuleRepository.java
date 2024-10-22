package com.example.RuleEngine.repository;

import java.util.*;
import com.example.RuleEngine.model.RuleEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RuleRepository extends JpaRepository<RuleEntity, Long> {
    List<RuleEntity> findByRuleString(String ruleString);
}
