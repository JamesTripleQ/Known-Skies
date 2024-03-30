package data.plugins;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.impl.campaign.procgen.StarSystemGenerator;
import data.scripts.KS_DiscoScript;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.fs.starfarer.api.impl.campaign.ids.Tags.*;
import static data.KS_utils.ALLOWED_STARS;
import static data.KS_utils.convertToDisco;

@SuppressWarnings("unused")
public class KS_ModPlugin extends BaseModPlugin {
    @Override
    public void beforeGameSave() {
        for (EveryFrameScript script : Global.getSector().getScripts()) {
            if (script instanceof KS_DiscoScript) {
                ((KS_DiscoScript) script).setMuted(true);
            }
        }
        super.beforeGameSave();
    }

    @Override
    public void afterGameSave() {
        for (EveryFrameScript script : Global.getSector().getScripts()) {
            if (script instanceof KS_DiscoScript) {
                ((KS_DiscoScript) script).setMuted(false);
            }
        }
        super.afterGameSave();
    }

    @Override
    public void onGameLoad(boolean newGame) {
        for (EveryFrameScript script : Global.getSector().getScripts()) {
            if (script instanceof KS_DiscoScript) {
                ((KS_DiscoScript) script).setMuted(false);
            }
        }
        super.onGameLoad(newGame);
    }

    @Override
    public void onNewGameAfterProcGen() {
        List<PlanetAPI> discoCandidates = new ArrayList<>();

        for (StarSystemAPI system : Global.getSector().getStarSystems()) {
            if (system != null && system.isProcgen() && system.getType().equals(StarSystemGenerator.StarSystemType.SINGLE)) {
                if (system.hasTag(THEME_RUINS) || system.hasTag(THEME_DERELICT)) {
                    if (ALLOWED_STARS.contains(system.getStar().getTypeId())){
                        discoCandidates.add(system.getStar());
                    }
                }
            }
        }

        if (!discoCandidates.isEmpty()) {
            PlanetAPI star = discoCandidates.get(new Random().nextInt(discoCandidates.size()));
            convertToDisco(star);
        }
    }
}