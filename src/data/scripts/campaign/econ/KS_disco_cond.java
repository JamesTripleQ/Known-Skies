package data.scripts.campaign.econ;

import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.econ.MarketImmigrationModifier;
import com.fs.starfarer.api.impl.campaign.econ.BaseMarketConditionPlugin;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.population.PopulationComposition;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

public class KS_disco_cond extends BaseMarketConditionPlugin implements MarketImmigrationModifier {
    private final float STABILITY_MALUS = -4f;
    private final float ACCESSIBILITY_BONUS = 25f;
    public static float INCOME_BONUS = 25f;

    @Override
    public void apply(String id) {
        market.getStability().modifyFlat(id, STABILITY_MALUS, condition.getName());
        market.addTransientImmigrationModifier(this);
        market.getAccessibilityMod().modifyFlat(id, ACCESSIBILITY_BONUS / 100, condition.getName());
        market.getIncomeMult().modifyPercent(id, INCOME_BONUS, condition.getName());
    }

    @Override
    public void unapply(String id) {
        market.getStability().unmodify(id);
        market.removeTransientImmigrationModifier(this);
        market.getAccessibilityMod().unmodify(id);
        market.getIncomeMult().unmodify(id);
    }

    @Override
    public void modifyIncoming(MarketAPI market, PopulationComposition incoming) {
        incoming.add(Factions.PIRATES, 5f);
        incoming.add(Factions.TRITACHYON, 5f);
        incoming.add(Factions.INDEPENDENT, 15f);
        incoming.add(Factions.PLAYER, 8f);
        incoming.getWeight().modifyFlat(getModId(), getImmigrationBonus(), Misc.ucFirst(condition.getName().toLowerCase()));
    }

    private float getImmigrationBonus() {
        return 10 * market.getSize();
    }

    @Override
    protected void createTooltipAfterDescription(TooltipMakerAPI tooltip, boolean expanded) {
        super.createTooltipAfterDescription(tooltip, expanded);

        tooltip.addPara("%s stability.", 10f, Misc.getHighlightColor(), "" + (int) STABILITY_MALUS);
        tooltip.addPara("%s population growth (based on market size).", 10f, Misc.getHighlightColor(), "+" + (int) getImmigrationBonus());
        tooltip.addPara("%s accessibility.", 10f, Misc.getHighlightColor(), "+" + (int) ACCESSIBILITY_BONUS + "%");
        tooltip.addPara("%s colony income.", 10f, Misc.getHighlightColor(), "+" + (int) INCOME_BONUS + "%");
    }
}
