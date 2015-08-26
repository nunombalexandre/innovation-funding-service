package com.worth.ifs.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Holds all the fields which do not belong to the defined general costs.
 */
@Entity
public class CostField {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    String title;
    String type;

    @OneToMany(mappedBy="costField")
    private List<CostValue> costValues = new ArrayList<CostValue>();

    public CostField() {
    }

    public CostField(Long id, String title, String type) {
        this.id = id;
        this.title = title;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getType() {
        return type;
    }
}
