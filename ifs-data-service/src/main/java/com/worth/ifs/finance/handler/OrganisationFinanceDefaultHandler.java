package com.worth.ifs.finance.handler;

import com.worth.ifs.application.domain.Question;
import com.worth.ifs.application.transactional.QuestionService;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.finance.domain.ApplicationFinance;
import com.worth.ifs.finance.domain.Cost;
import com.worth.ifs.finance.domain.CostField;
import com.worth.ifs.finance.handler.item.*;
import com.worth.ifs.finance.repository.CostFieldRepository;
import com.worth.ifs.finance.repository.CostRepository;
import com.worth.ifs.finance.resource.category.*;
import com.worth.ifs.finance.resource.cost.CostItem;
import com.worth.ifs.finance.resource.cost.CostType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * OrganisationFinanceDefaultHandler maintains the finances from
 * an organisation's perspective and calculates the totals
 */
@Component
public class OrganisationFinanceDefaultHandler implements OrganisationFinanceHandler {
    private static final Log LOG = LogFactory.getLog(OrganisationFinanceDefaultHandler.class);
    EnumMap<CostType, CostCategory> costCategories = new EnumMap<>(CostType.class);

    @Autowired
    QuestionService questionService;

    @Autowired
    CostRepository costRepository;

    @Autowired
    CostFieldRepository costFieldRepository;

    @Autowired
    AutowireCapableBeanFactory beanFactory;

    @Override
    public Iterable<Cost> initialiseCostType(ApplicationFinance applicationFinance, CostType costType){
    	
    	if(costTypeSupportedByHandler(costType)) {
	        Question question = getQuestionByCostType(costType);
	        try{
	            List<Cost> cost = getCostHandler(costType).initializeCost();
	            cost.forEach(c -> {
	                c.setQuestion(question);
	                c.setApplicationFinance(applicationFinance);
	            });
	            if(!cost.isEmpty()){
	                costRepository.save(cost);
	                return cost;
	            }else{
	                return new ArrayList<>();
	            }
	
	        }catch (IllegalArgumentException e){
	            LOG.error(String.format("No CostHandler for type: %s", costType.getType()), e);
	        }
    	}
        return null;
    }

	// TODO DW - INFUND-1555 - handle rest result
    private Question getQuestionByCostType(CostType costType) {
        return questionService.getQuestionByFormInputType(costType.getType()).getSuccessObjectOrThrowException();
    }

    @Override
    public Map<CostType, CostCategory> getOrganisationFinances(Long applicationFinanceId) {
        List<Cost> costs = costRepository.findByApplicationFinanceId(applicationFinanceId);
        createCostCategories();
        addCostsToCategories(costs);
        calculateTotals();
        return costCategories;
    }

    @Override
    public Map<CostType, CostCategory> getOrganisationFinanceTotals(Long applicationFinanceId, Competition competition) {
        getOrganisationFinances(applicationFinanceId);
        resetCosts();
        return costCategories;
    }

    private void createCostCategories() {
        for(CostType costType : CostType.values()) {
            CostCategory costCategory = createCostCategoryByType(costType);
            costCategories.put(costType, costCategory);
        }
    }

    private void addCostsToCategories(List<Cost> costs) {
        costs.stream().forEach(c -> addCostToCategory(c));
    }

    /**
     * The costs are converted to a representation based on its type that can be used in the view and
     * are added to a specific category (e.g. labour).
     * @param cost Cost to be added
     */
    private void addCostToCategory(Cost cost) {
        CostType costType = CostType.fromString(cost.getQuestion().getFormInputs().get(0).getFormInputType().getTitle());
        CostHandler costHandler = getCostHandler(costType);
        CostItem costItem = costHandler.toCostItem(cost);
        CostCategory costCategory = costCategories.get(costType);
        costCategory.addCost(costItem);
    }

    private void calculateTotals() {
        costCategories.values()
                .forEach(cc -> cc.calculateTotal());
        calculateOverheadTotal();
    }

    private void calculateOverheadTotal() {
        CostCategory labourCostCategory = costCategories.get(CostType.LABOUR);
        OverheadCostCategory overheadCategory = (OverheadCostCategory) costCategories.get(CostType.OVERHEADS);
        overheadCategory.setLabourCostTotal(labourCostCategory.getTotal());
        overheadCategory.calculateTotal();
    }

    private void resetCosts() {
        costCategories.values()
                .forEach(cc -> cc.setCosts(new ArrayList<>()));
    }

    @Override
    public Cost costItemToCost(CostItem costItem) {
        CostHandler costHandler = getCostHandler(costItem.getCostType());
        List<CostField> costFields = costFieldRepository.findAll();
        costHandler.setCostFields(costFields);
        return costHandler.toCost(costItem);
    }

    @Override
    public CostItem costToCostItem(Cost cost) {
        CostType costType = CostType.fromString(cost.getQuestion().getFormInputs().get(0).getFormInputType().getTitle());
        CostHandler costHandler = getCostHandler(costType);
        return costHandler.toCostItem(cost);
    }

    @Override
    public List<CostItem> costToCostItem(List<Cost> costs) {
        List<CostItem> costItems = new ArrayList<>();
        costs.stream()
                .forEach(cost -> costItems.add(costToCostItem(cost)));
        return costItems;
    }

    @Override
    public List<Cost> costItemsToCost(List<CostItem> costItems) {
        List<Cost> costs = new ArrayList<>();
        costItems.stream()
                .forEach(costItem -> costs.add(costItemToCost(costItem)));
        return costs;
    }

    private boolean costTypeSupportedByHandler(CostType costType) {
		return !(CostType.YOUR_FINANCE.equals(costType) || CostType.ACADEMIC.equals(costType));
	}

    @Override
    public CostHandler getCostHandler(CostType costType) {
        CostHandler handler = null;
        switch(costType) {
            case LABOUR:
                handler = new LabourCostHandler();
                break;
            case CAPITAL_USAGE:
                handler = new CapitalUsageHandler();
                break;
            case MATERIALS:
                handler = new MaterialsHandler();
                break;
            case OTHER_COSTS:
                handler = new OtherCostHandler();
                break;
            case OVERHEADS:
                handler = new OverheadsHandler();
                break;
            case SUBCONTRACTING_COSTS:
                handler = new SubContractingCostHandler();
                break;
            case TRAVEL:
                handler = new TravelCostHandler();
                break;
            case FINANCE:
                handler = new GrantClaimHandler();
                break;
            case OTHER_FUNDING:
                handler = new OtherFundingHandler();
                break;
        }
        if(handler != null){
            // some times the handler needs autowired classes
            beanFactory.autowireBean(handler);
            return handler;
        }
        LOG.error("Not a valid FinanceType: " + costType);
        throw new IllegalArgumentException("Not a valid FinanceType: " + costType);
    }

    private CostCategory createCostCategoryByType(CostType costType) {
        switch(costType) {
            case LABOUR:
                return new LabourCostCategory();
            case OTHER_FUNDING:
                return new OtherFundingCostCategory();
            case OVERHEADS:
                return new OverheadCostCategory();
            case FINANCE:
                return new GrantClaimCategory();
            default:
                return new DefaultCostCategory();
        }
    }

}
