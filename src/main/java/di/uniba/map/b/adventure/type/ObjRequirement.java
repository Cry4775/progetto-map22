package di.uniba.map.b.adventure.type;

public class ObjRequirement {
    private RequirementType requirementType;
    private Integer requiredItemId;
    private AdvItem requiredItem;

    private boolean goesIntoRequiredItem;

    public RequirementType getRequirementType() {
        return requirementType;
    }

    public void setRequirementType(RequirementType requirementType) {
        this.requirementType = requirementType;
    }

    public Integer getRequiredItemId() {
        return requiredItemId;
    }

    public void setRequiredItemId(Integer requiredItemId) {
        this.requiredItemId = requiredItemId;
    }

    public AdvItem getRequiredItem() {
        return requiredItem;
    }

    public void setRequiredItem(AdvItem requiredItem) {
        this.requiredItem = requiredItem;
    }

    public boolean isGoesIntoRequiredItem() {
        return goesIntoRequiredItem;
    }

    public void setGoesIntoRequiredItem(boolean goesIntoRequiredItem) {
        this.goesIntoRequiredItem = goesIntoRequiredItem;
    }
}
