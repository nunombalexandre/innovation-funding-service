package com.worth.ifs.application.finance.view.item;

import com.worth.ifs.application.finance.model.FinanceFormField;
import com.worth.ifs.finance.resource.cost.CostItem;
import com.worth.ifs.finance.resource.cost.TravelCost;

import java.math.BigDecimal;
import java.util.List;

public class TravelCostHandler extends CostHandler {

    @Override
    public CostItem toCostItem(Long id, List<FinanceFormField> financeFormFields) {
        BigDecimal costPerItem = null;
        String item = null;
        Integer quantity = null;

        for(FinanceFormField financeFormField : financeFormFields) {
            String fieldValue = financeFormField.getValue();
            if(fieldValue != null) {
                switch (financeFormField.getCostName()) {
                    case "travelPurpose":
                        item = fieldValue;
                        break;
                    case "travelNumTimes":
                        quantity = getIntegerValue(fieldValue, 0);
                        break;
                    case "travelCostEach":
                        costPerItem = getBigDecimalValue(fieldValue, 0d);
                        break;
                    default:
                        log.info("Unused costField: " + financeFormField.getCostName());
                        break;
                }
            }
        }
        return new TravelCost(id, item, costPerItem, quantity);
    }
}