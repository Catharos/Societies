package org.societies.commands.society;

import com.google.inject.Inject;
import order.CommandContext;
import order.Executor;
import order.format.table.RowFactory;
import order.format.table.Table;
import order.reflect.*;
import org.bukkit.entity.Player;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.societies.commands.RuleStep;
import org.societies.groups.group.Group;
import org.societies.groups.member.Member;
import org.societies.groups.rank.Rank;

import javax.inject.Provider;

/**
 * Represents a SocietyProfile
 */
@Command(identifier = "command.roster")
@Permission("societies.roster")
@Meta(@Entry(key = RuleStep.RULE, value = "roster"))
@Sender(Member.class)
public class RosterCommand implements Executor<Member> {

    private final Provider<Table> tableProvider;
    private final RowFactory rowFactory;
    private final PeriodFormatter periodFormatter;

    @Option(name = "argument.page")
    int page;

    @Inject
    public RosterCommand(Provider<Table> tableProvider, RowFactory rowFactory, PeriodFormatter periodFormatter) {
        this.tableProvider = tableProvider;
        this.rowFactory = rowFactory;
        this.periodFormatter = periodFormatter;
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

        for (Member member : group.getMembers()) {
            Rank rank = member.getRank();
            Period inactive = new Interval(member.getLastActive(), DateTime.now()).toPeriod();

            boolean available = member.get(Player.class).isOnline();

            table.addForwardingRow(rowFactory.translated(true, member.getName(),
                    rank == null ? "None" : rank.getName(),
                    available ? "lookup.online" : periodFormatter.print(inactive)));
        }

        sender.send(table.render(ctx.getName(), page));
    }
}
