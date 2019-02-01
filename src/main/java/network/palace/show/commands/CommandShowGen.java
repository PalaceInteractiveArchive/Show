package network.palace.show.commands;

import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.Rank;
import network.palace.show.commands.showgen.CommandGenerate;
import network.palace.show.commands.showgen.CommandSetCorner;
import network.palace.show.commands.showgen.CommandSetInitialScene;

@CommandMeta(description = "Generate blocks of show actions with one command", rank = Rank.MOD)
public class CommandShowGen extends CoreCommand {

    public CommandShowGen() {
        super("showgen");
        registerSubCommand(new CommandGenerate());
        registerSubCommand(new CommandSetCorner());
        registerSubCommand(new CommandSetInitialScene());
    }

    @Override
    protected boolean isUsingSubCommandsOnly() {
        return true;
    }
}
