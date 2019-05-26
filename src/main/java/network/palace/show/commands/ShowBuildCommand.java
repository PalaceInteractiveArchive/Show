package network.palace.show.commands;

import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.Rank;
import network.palace.show.commands.showbuild.LoadCommand;
import network.palace.show.commands.showbuild.SaveCommand;

@CommandMeta(description = "Save builds for use in Build sequences", rank = Rank.MOD)
public class ShowBuildCommand extends CoreCommand {

    public ShowBuildCommand() {
        super("showbuild");
        registerSubCommand(new LoadCommand());
        registerSubCommand(new SaveCommand());
    }

    @Override
    protected boolean isUsingSubCommandsOnly() {
        return true;
    }
}
