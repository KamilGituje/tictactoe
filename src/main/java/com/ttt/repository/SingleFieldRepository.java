package com.ttt.repository;

import com.ttt.model.SingleField;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SingleFieldRepository extends JpaRepository<SingleField, Integer> {
}
