package data;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.JumpPointAPI;
import com.fs.starfarer.api.campaign.JumpPointAPI.JumpDestination;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.PlanetSpecAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.econ.MarketConditionAPI;
import com.fs.starfarer.api.impl.campaign.ids.StarTypes;
import com.fs.starfarer.loading.specs.PlanetSpec;
import data.scripts.KS_DiscoScript;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class KS_utils {
    // ID for both the star and the condition
    public static final String DISCO_ID = "KS_disco";
    // Memory key to check if a star is a disco ball
    public static final String DISCO_MEM_KEY = "$isDiscoBall";
    // List of stars allowed to be converted to disco balls
    public static ArrayList<String> ALLOWED_STARS = new ArrayList<>();

    static {
        ALLOWED_STARS.add(StarTypes.YELLOW);
        ALLOWED_STARS.add(StarTypes.WHITE_DWARF);
        ALLOWED_STARS.add(StarTypes.BLUE_GIANT);
        //ALLOWED_STARS.add(StarTypes.BLUE_SUPERGIANT);
        ALLOWED_STARS.add(StarTypes.ORANGE);
        //ALLOWED_STARS.add(StarTypes.ORANGE_GIANT);
        //ALLOWED_STARS.add(StarTypes.RED_SUPERGIANT);
        ALLOWED_STARS.add(StarTypes.RED_GIANT);
        ALLOWED_STARS.add(StarTypes.RED_DWARF);
        ALLOWED_STARS.add(StarTypes.BROWN_DWARF);
        //ALLOWED_STARS.add(StarTypes.NEUTRON_STAR);
        //ALLOWED_STARS.add(StarTypes.BLACK_HOLE);
    }

    // Converts a star into a disco ball
    public static void convertToDisco(PlanetAPI star) {
        PlanetSpecAPI starSpec = star.getSpec();
        // Stars usually don't have a tilt or pitch, so we set them ourselves along with rotation speed
        float tilt = new Random().nextInt(80) - 40;
        float pitch = new Random().nextInt(60) - 30;
        float rotation = (new Random().nextInt(25) + 15) * (new Random().nextBoolean() ? -1 : 1);

        for (PlanetSpecAPI spec : Global.getSettings().getAllPlanetSpecs()) {
            if (spec.getPlanetType().equals(DISCO_ID)) {
                starSpec.setTilt(tilt);
                starSpec.setPitch(pitch);
                starSpec.setRotation(rotation);
                starSpec.setPlanetColor(spec.getPlanetColor());
                starSpec.setAtmosphereThickness(spec.getAtmosphereThickness());
                starSpec.setAtmosphereThicknessMin(spec.getAtmosphereThicknessMin());
                starSpec.setTexture(spec.getTexture());
                starSpec.setIconColor(spec.getIconColor());
                starSpec.setCoronaTexture(starSpec.getCoronaTexture());
                starSpec.setCoronaColor(spec.getCoronaColor());
                ((PlanetSpec) starSpec).name = spec.getName();
                ((PlanetSpec) starSpec).descriptionId = ((PlanetSpec) spec).descriptionId;
                ((PlanetSpec) starSpec).iconTexture = spec.getIconTexture();
                break;
            }
        }

        ((PlanetSpec) starSpec).planetType = DISCO_ID;
        star.getSpec().setGlowColor(Color.WHITE);
        star.setLightColorOverrideIfStar(Color.WHITE);
        star.getStarSystem().setLightColor(Color.WHITE);
        star.setTypeId(DISCO_ID);

        star.applySpecChanges();
        // Sets mem key to easily check if a star is a disco ball (since we can't actually use the ID)
        star.getMemoryWithoutUpdate().set(DISCO_MEM_KEY, true);

        // Updates the jump point name
        for (SectorEntityToken j : Global.getSector().getHyperspace().getJumpPoints()) {
            JumpPointAPI jump = (JumpPointAPI) j;
            for (JumpDestination destination : jump.getDestinations()) {
                if (destination.getDestination().getId().equals(star.getId())) {
                    jump.setName(star.getName() + ", Disco Ball");
                }
            }
        }

        // Adds the script to the star
        Global.getSector().addScript(new KS_DiscoScript(star));

        // Applies the condition
        for (PlanetAPI planet : star.getStarSystem().getPlanets()) {
            if (planet.getMarket() != null) {
                if (!planet.getMarket().hasCondition(DISCO_ID)) {
                    planet.getMarket().addCondition(DISCO_ID);
                }
            }
        }
    }
}