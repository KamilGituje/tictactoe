package com.ttt.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "play_tables")
@Getter
@Setter
public class PlayTable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "play_table_id")
    private int playTableId;
    private PlaySide cpuSide;
    private int winningRowSize;
    @OneToMany(mappedBy = "playTable", cascade = CascadeType.ALL)
    @JsonManagedReference
    @OrderBy("position")
    private List<SingleField> singleFields;
}