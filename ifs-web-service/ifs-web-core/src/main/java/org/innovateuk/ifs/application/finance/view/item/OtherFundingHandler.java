package org.innovateuk.ifs.application.finance.view.item;

import org.innovateuk.ifs.application.finance.model.FinanceFormField;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.OtherFunding;
import org.innovateuk.ifs.util.NumberUtils;

import java.math.BigDecimal;
import java.util.List;

import static org.innovateuk.ifs.finance.resource.category.OtherFundingCostCategory.OTHER_FUNDING;
import static org.innovateuk.ifs.util.NullCheckFunctions.allNull;

/**
 * Handles the conversion of form fields to other funding item
 */
public class OtherFundingHandler extends FinanceRowHandler {

    @Override
    public FinanceRowItem toFinanceRowItem(Long id, List<FinanceFormField> financeFormFields) {
        String otherPublicFunding = null;
        String fundingSource = null;
        String securedDate = null;
        BigDecimal fundingAmount = null;

        for (FinanceFormField financeFormField : financeFormFields) {
            String fieldValue = financeFormField.getValue();
            if (fieldValue != null) {
                switch (financeFormField.getCostName()) {
                    case "otherPublicFunding":
                        fundingSource = OTHER_FUNDING;
                        otherPublicFunding = fieldValue;
                        break;
                    case "fundingAmount":
                        fundingAmount = NumberUtils.getBigDecimalValue(fieldValue, 0d);
                        break;
                    case "fundingSource":
                        fundingSource = fieldValue;
                        break;
                    case "securedDate":
                        securedDate = fieldValue;
                        break;
                    default:
                        LOG.info("Unused costField: " + financeFormField.getCostName());
                        break;
                }
            }
        }

        if(allNull(id, otherPublicFunding, fundingSource, securedDate, fundingAmount)) {
        	return null;
        }
        
        if((id == null || Long.valueOf(0L).equals(id)) && (fundingAmount == null)) {
        	fundingAmount = BigDecimal.ZERO;
        }

        return new OtherFunding(id, otherPublicFunding, fundingSource, securedDate, fundingAmount);
    }
}
