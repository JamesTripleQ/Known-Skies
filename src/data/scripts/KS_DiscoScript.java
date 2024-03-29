package data.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.SoundAPI;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.WeightedRandomPicker;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;

import static data.KS_utils.DISCO_ID;

public class KS_DiscoScript extends BaseCampaignEventListenerAndScript {
    public static final List<Color> COLOR_LIST = new LinkedList<Color>() {{
        add(new Color(100, 255, 237, 255));
        add(new Color(255, 145, 253, 255));
        add(new Color(255, 50, 50, 255));
        add(new Color(210, 230, 255, 255));
        add(new Color(183, 50, 255, 255));
        add(new Color(255, 225, 125, 255));
        add(new Color(62, 150, 62, 255));
        add(new Color(46, 60, 255, 255));
        add(new Color(245, 250, 255, 255));
        add(new Color(100, 255, 100, 255));
        add(new Color(255, 90, 50, 255));
        add(new Color(255, 175, 94, 255));
        add(new Color(244, 255, 94, 255));
    }};

    public PlanetAPI star;
    private final WeightedRandomPicker<Color> picker = new WeightedRandomPicker<>();
    private transient SoundAPI caramelDansen = null;
    public boolean done = false;
    private boolean isMuted = false;

    float count = 0;
    float total = 0;

    public KS_DiscoScript(PlanetAPI star) {
        super();
        this.star = star;
    }

    @Override
    public boolean isDone() {
        return done || star == null || star.getStarSystem() == null || star.getContainingLocation() == null;
    }

    public void stopDansen() {
        if (caramelDansen != null) caramelDansen.stop();
        caramelDansen = null;
    }

    public void setMuted(boolean muted) {
        isMuted = muted;
    }

    public void setVolumeZero() {
        if (caramelDansen != null) caramelDansen.setVolume(0f);
    }

    @Override
    public boolean runWhilePaused() {
        return true;
    }

    @Override
    public void advance(float amount) {
        if (isDone()) {
            stopDansen();
            return;
        }

        playDansen(amount);
    }

    public void setDone(boolean done) {
        this.done = done;

        if (done) {
            stopDansen();
            Global.getSector().removeListener(this);
        }
    }

    public void playDansen(float amount) {
        CampaignFleetAPI playerFleet = Global.getSector().getPlayerFleet();
        SectorAPI sector = Global.getSector();

        if (sector.isPaused() ||
                isMuted ||
                star.getContainingLocation() != playerFleet.getContainingLocation() ||
                sector.getCampaignUI().isShowingDialog() ||
                sector.getCampaignUI().isShowingMenu() ||
                sector.getCampaignUI().getCurrentInteractionDialog() != null) {

            setVolumeZero();
            total += getAdjustedAmt(amount);
            return;
        }

        if (star == null) {
            setDone(true);
            return;
        }

        // TODO find a way to raise the volume
        // TODO Occasionally doesn't play... might be worth keeping like that since it makes it easier to add song variety
        float vol = Math.min(1f, 2500f / Misc.getDistance(playerFleet, star));
        vol *= 20;

        if (caramelDansen == null || total > 178f) {
            total = 0;
            caramelDansen = Global.getSoundPlayer().playSound(DISCO_ID, 1f, vol, star.getLocation(), Misc.ZERO);
        }

        caramelDansen.setVolume(vol);
        caramelDansen.setLocation(star.getLocation().x, star.getLocation().y);

        float add = getAdjustedAmt(amount);
        count += add;
        total += add;

        if (count > 0.365f) {
            if (picker.isEmpty()) picker.addAll(COLOR_LIST);

            Color nextColor = picker.pickAndRemove();

            star.setLightColorOverrideIfStar(nextColor);
            star.getSpec().setPlanetColor(nextColor);
            star.getSpec().setAtmosphereColor(nextColor);
            star.getSpec().setCloudColor(nextColor);
            star.getSpec().setCoronaColor(nextColor);
            star.getSpec().setGlowColor(nextColor);
            star.getSpec().setShieldColor(nextColor);
            star.getSpec().setShieldColor2(nextColor);
            star.applySpecChanges();

            star.getStarSystem().setLightColor(nextColor);
            count = 0;
        }
    }

    @Override
    public void reportFleetJumped(CampaignFleetAPI fleet, SectorEntityToken from, JumpPointAPI.JumpDestination to) {
        super.reportFleetJumped(fleet, from, to);

        if (fleet.isPlayerFleet()) {
            if (to != null && to.getDestination() != null && star.getContainingLocation() == to.getDestination().getContainingLocation())
                playDansen(0);
            else setVolumeZero();
        }
    }

    private float getAdjustedAmt(float amount) {
        boolean fast = Global.getSector().getCampaignUI().isFastForward();
        float mult = Global.getSettings().getFloat("campaignSpeedupMult");
        return fast ? amount / mult : amount;
    }
}