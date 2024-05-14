package com.ttt.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "single_fields")
@Getter
@Setter
public class SingleField {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "single_field_id")
    private int singleFieldId;
    private SingleFieldStatus status = SingleFieldStatus.blank;
    private int position;
    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "play_table_id")
    private PlayTable playTable;
}
