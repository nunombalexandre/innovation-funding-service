package com.worth.ifs.application.finance.view.item;

import com.worth.ifs.application.finance.model.CostFormField;
import com.worth.ifs.finance.domain.Cost;
import com.worth.ifs.finance.resource.cost.CostItem;
import com.worth.ifs.finance.resource.cost.GrantClaim;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Handles the conversion of form fields to a grant claims
 */
public class GrantClaimHandler extends CostHandler {
    @Override
    public CostItem toCostItem(Long id, List<CostFormField> costFormFields) {
        Optional<CostFormField> grantClaimPercentageField = costFormFields.stream().findFirst();
        Integer grantClaimPercentage = 0;
        if (grantClaimPercentageField.isPresent()) {
            grantClaimPercentage = getIntegerValue(grantClaimPercentageField.get().getValue(), 0);
        }

        return new GrantClaim(id, grantClaimPercentage);
    }

}