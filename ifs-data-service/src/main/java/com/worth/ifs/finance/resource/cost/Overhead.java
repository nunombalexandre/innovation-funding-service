package com.worth.ifs.finance.resource.cost;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.math.BigDecimal;

/**
 * {@code Overhead} implements {@link CostItem}
 */
public class Overhead implements CostItem {
    private Long id;
    @Enumerated(EnumType.STRING)
    private OverheadRateType rateType;
    private Integer rate;
    private CostType costType;

    public Overhead() {
        this.costType = CostType.OVERHEADS;
        this.rateType = OverheadRateType.NONE;
    }

    public Overhead(Long id, OverheadRateType rateType, Integer rate) {
        this();
        this.id = id;
        this.rateType = rateType;
        this.rate = rate;
    }

    public Integer getRate(){
        return rate;
    }


    @Override
    public BigDecimal getTotal() {
        return BigDecimal.ZERO;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public CostType getCostType() {
        return costType;
    }

    public OverheadRateType getRateType() {
        return rateType;
    }

    public void setRateType(OverheadRateType rateType) {
        this.rateType = rateType;
    }
}
