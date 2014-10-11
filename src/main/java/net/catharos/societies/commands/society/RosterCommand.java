package net.catharos.societies.commands.society;

import com.google.inject.Inject;
import net.catharos.groups.Group;
import net.catharos.groups.Member;
import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.format.table.Table;
import net.catharos.lib.core.command.reflect.Command;
import net.catharos.lib.core.command.reflect.Option;
import net.catharos.lib.core.command.reflect.Sender;

import javax.inject.Provider;

/**
 * Represents a SocietyProfile
 */
@Command(identifier = "command.roster")
@Sender(Member.class)
public class RosterCommand implements Executor<Member> {

    @Option(name = "argument.target.society")
    Group target;

    private final Provider<Table> tableProvider;

    @Option(name = "argument.page")
    int page;

    @Inject
    public RosterCommand(Provider<Table> tableProvider) {
        this.tableProvider = tableProvider;
    }

    @Override
    public void execute(CommandContext<Member> ctx, Member sender) {
        Group group = sender.getGroup();

        if (group == null) {
            sender.send("society.not-found");
            return;
        }

        Table table = tableProvider.get();

        table.addRow("Name", "Rank", "Seen");

        for (Member member : target.getMembers()) {
            table.addRow(member.getName(), member.getRanks(), member.getLastActive());
        }

        sender.send(table.render(ctx.getName(), page));
    }
}
