package com.kh.youtube.domain;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Category {

    @Id
    @Column(name="category_Code")
    @GeneratedValue(strategy= GenerationType.IDENTITY, generator = "categorySequence")
    @SequenceGenerator(name="categorySequence", sequenceName = "SEQ_CATEGORY", allocationSize = 1)
    private int categoryCode;

    @Column(name="category_Name")
    private String categoryName;
}
