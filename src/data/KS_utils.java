package data;

import com.fs.starfarer.E.J;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.JumpPointAPI;
import com.fs.starfarer.api.campaign.JumpPointAPI.JumpDestination;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.PlanetSpecAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.impl.campaign.ids.StarTypes;
import com.fs.starfarer.api.impl.campaign.terrain.StarCoronaTerrainPlugin;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.loading.specs.PlanetSpec;
import data.scripts.KS_DiscoScript;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class KS_utils {
    // ID for both the star and the condition
    public static final String DISCO_ID = "KS_disco";
    // List of stars allowed to be converted to disco balls
    public static final ArrayList<String> ALLOWED_STARS = new ArrayList<>();

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
        float tilt = new Random().nextInt(80) - 40;
        float pitch = new Random().nextInt(60) - 30;

        for (final PlanetSpecAPI spec : Global.getSettings().getAllPlanetSpecs()) {
            if (spec.getPlanetType().equals(DISCO_ID)) {
                starSpec.setTilt(tilt);
                starSpec.setPitch(pitch);
                // TODO pick a rotation speed
                // starSpec.setRotation();
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

        /* TODO find a way to rename the jump point, this doesn't work
        for (SectorEntityToken j : star.getStarSystem().getJumpPoints()){
            //JumpPointAPI jump = (JumpPointAPI) j;
            j.setName(star.getName() + ", Disco Ball");
        }
         */
        /*
        for(SectorEntityToken j : Global.getSector().getHyperspace().getJumpPoints()) {
            JumpPointAPI jump = (JumpPointAPI) j;
            for (JumpDestination destination : jump.getDestinations()) {
                if (destination.getDestination().getId().equals(star.getId())) {
                    jump.removeDestination(star);
                    jump.addDestination(new JumpDestination(star, star.getName()));
                }
            }
        }*/

        Global.getSector().addScript(new KS_DiscoScript(star));
    }
}