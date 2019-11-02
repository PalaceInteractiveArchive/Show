package network.palace.show.commands;

import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.Rank;
import network.palace.show.commands.showgen.GenerateCommand;
import network.palace.show.commands.showgen.SetCornerCommand;
import network.palace.show.commands.showgen.SetInitialSceneCommand;

@CommandMeta(description = "Generate blocks of show actions with one command", rank = Rank.TRAINEETECH)
public class ShowGenCommand extends CoreCommand {

    public ShowGenCommand() {
        super("showgen");
        registerSubCommand(new GenerateCommand());
        registerSubCommand(new SetCornerCommand());
        registerSubCommand(new SetInitialSceneCommand());
    }

    @Override
    protected boolean isUsingSubCommandsOnly() {
        return true;
    }
}
