package data.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.SoundAPI;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.WeightedRandomPicker;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

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

    private static final ArrayList<String> TRACK_LIST = new ArrayList<>();

    static {
        TRACK_LIST.add("KS_Dance");
        TRACK_LIST.add("KS_Disco");
        TRACK_LIST.add("KS_Friday");
        TRACK_LIST.add("KS_Roll");
        TRACK_LIST.add("KS_Specialist");
    }

    public PlanetAPI star;
    private final WeightedRandomPicker<Color> picker = new WeightedRandomPicker<>();
    private transient SoundAPI caramelDansen = null;
    public boolean done = false;
    private boolean isMuted = false;
    private boolean isMusicPlayerRunning = true;

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
        if (!isMusicPlayerRunning) {
            Global.getSoundPlayer().setSuspendDefaultMusicPlayback(false);
            isMusicPlayerRunning = true;
        }
    }

    public void setMuted(boolean muted) {
        isMuted = muted;
    }

    public void setVolumeZero() {
        if (caramelDansen != null) caramelDansen.setVolume(0f);
        if (!isMusicPlayerRunning) {
            Global.getSoundPlayer().setSuspendDefaultMusicPlayback(false);
            isMusicPlayerRunning = true;
        }
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

        float distance = Misc.getDistance(playerFleet, star);
        float falloff = 10000f + playerFleet.getMaxSensorRangeToDetect(star) + star.getRadius() + playerFleet.getRadius();
        float vol = (1f - (distance / falloff)) * 5;

        if (caramelDansen == null || total > 224f) {
            total = 0;
            caramelDansen = Global.getSoundPlayer().playSound(TRACK_LIST.get(new Random().nextInt(TRACK_LIST.size())), 1f, vol, playerFleet.getLocation(), Misc.ZERO);
        }

        if (vol > 0 && caramelDansen != null && isMusicPlayerRunning) {
            Global.getSoundPlayer().setSuspendDefaultMusicPlayback(true);
            Global.getSoundPlayer().pauseMusic();
            isMusicPlayerRunning = false;
        } else if ((vol <= 0 || caramelDansen == null) && !isMusicPlayerRunning) {
            Global.getSoundPlayer().setSuspendDefaultMusicPlayback(false);
            isMusicPlayerRunning = true;
        }

        caramelDansen.setVolume(vol);
        caramelDansen.setLocation(playerFleet.getLocation().x, playerFleet.getLocation().y);

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