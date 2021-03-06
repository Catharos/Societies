package org.societies.commands.society;

import com.google.common.base.Function;
import order.CommandContext;
import order.ExecuteException;
import order.Executor;
import order.reflect.*;
import org.societies.IterableUtils;
import org.societies.commands.RuleStep;
import org.societies.groups.group.Group;
import org.societies.groups.member.Member;
import org.societies.groups.rank.Rank;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Set;

/**
 * Represents a SocietyProfile
 */
@Command(identifier = "command.kick", async = true)
@Permission("societies.kick")
@Meta(@Entry(key = RuleStep.RULE, value = "kick"))
@Sender(Member.class)
public class KickCommand implements Executor<Member> {

    @Argument(name = "argument.target.member")
    Member target;

    @Override
    public void execute(CommandContext<Member> ctx, Member sender) throws ExecuteException {
        Group group = target.getGroup();

        if (group == null || !target.getGroup().equals(sender.getGroup())) {
            sender.send("target-member.not-same-group", target.getName());
            return;
        }

        if (isCritical(sender, target, group)) {
            return;
        }

        sender.send("you.kicked-member", target.getName(), group.getTag());
        target.send("member.kicked", group.getName());
        group.removeMember(target);
    }

    public static boolean isCritical(Member sender, Member target, Group group) {
        Set<Member> leaders = group.getMembers("leader");

        if (leaders.contains(target)) {
            if (leaders.size() <= 1 && group.size() > 1) {
                Collection<Rank> leaderRanks = group.getRanks("leader");
                String leaderRanksString = IterableUtils.toString(leaderRanks, new Function<Rank, String>() {
                    @Nullable
                    @Override
                    public String apply(Rank input) {
                        return input.getName();
                    }
                });
                sender.send("you.assign-first", leaderRanksString);
                return true;
            }

            return true;
        }

        return false;
    }
}
