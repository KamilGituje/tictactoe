package com.ttt.repository;

import com.ttt.model.PlayTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayTableRepository extends JpaRepository<PlayTable, Integer> {
}
