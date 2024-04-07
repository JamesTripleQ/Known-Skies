package data.console;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;

import org.lazywizard.console.BaseCommand;
import org.lazywizard.console.CommonStrings;
import org.lazywizard.console.Console;

import static data.KS_utils.*;

@SuppressWarnings("unused")
public class KS_ConvertDisco implements BaseCommand {
    @Override
    public CommandResult runCommand(String args, CommandContext context) {
        boolean force = false;

        // Checks if used in campaign
        if (!context.isInCampaign()) {
            Console.showMessage(CommonStrings.ERROR_CAMPAIGN_ONLY);
            return CommandResult.WRONG_CONTEXT;
        }

        LocationAPI currentLoc = Global.getSector().getPlayerFleet().getContainingLocation();

        // Checks if used in hyperspace
        if (currentLoc.isHyperspace() || !(currentLoc instanceof StarSystemAPI)) {
            Console.showMessage("Error: This command cannot be used in Hyperspace.");
            return CommandResult.WRONG_CONTEXT;
        }

        StarSystemAPI system = (StarSystemAPI) currentLoc;
        PlanetAPI star = system.getStar();

        if (star == null || system.isNebula()) {
            Console.showMessage("Error: star not found.");
            return CommandResult.WRONG_CONTEXT;
        }

        // Force star swap regardless of type
        if (args.equalsIgnoreCase("force")) {
            force = true;
        }

        // Checks if the star is valid
        if (!ALLOWED_STARS.contains(star.getTypeId()) && !force) {
            Console.showMessage("Error: star not allowed for conversion (type \"force\" after the command to convert anyway regardless of star type).");
            return CommandResult.WRONG_CONTEXT;
        }

        // Checks if star is already a disco ball
        if (star.getMemoryWithoutUpdate().contains(DISCO_MEM_KEY)) {
            Console.showMessage("Error: star is already a disco ball.");
            return CommandResult.WRONG_CONTEXT;
        }

        convertToDisco(star);

        Console.showMessage(star.getName() + " has been converted into a disco ball.");
        return CommandResult.SUCCESS;
    }
}