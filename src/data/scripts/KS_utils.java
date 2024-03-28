package data.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.PlanetSpecAPI;
import com.fs.starfarer.api.impl.campaign.ids.StarTypes;
import com.fs.starfarer.loading.specs.PlanetSpec;

import java.util.ArrayList;

public class KS_utils {
    public static final String DISCO_ID = "KS_disco";
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

    public static void convertToDisco(PlanetAPI star) {
        PlanetSpecAPI starSpec = star.getSpec();

        for (final PlanetSpecAPI spec : Global.getSettings().getAllPlanetSpecs()) {
            if (spec.getPlanetType().equals(DISCO_ID)) {
                starSpec.setPlanetColor(spec.getPlanetColor());
                starSpec.setAtmosphereThickness(spec.getAtmosphereThickness());
                starSpec.setAtmosphereThicknessMin(spec.getAtmosphereThicknessMin());
                starSpec.setTexture(spec.getTexture());
                starSpec.setIconColor(spec.getIconColor());
                starSpec.setCoronaTexture(starSpec.getCoronaTexture());
                starSpec.setCoronaColor(spec.getCoronaColor());
                ((PlanetSpec) starSpec).planetType = DISCO_ID;
                ((PlanetSpec) starSpec).name = spec.getName();
                ((PlanetSpec) starSpec).descriptionId = ((PlanetSpec) spec).descriptionId;
                ((PlanetSpec) starSpec).iconTexture = spec.getIconTexture();
                break;
            }
        }
        star.setTypeId(DISCO_ID);
        star.applySpecChanges();
    }
}