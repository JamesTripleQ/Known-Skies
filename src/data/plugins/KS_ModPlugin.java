package data.plugins;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import data.scripts.KS_DiscoScript;

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
}