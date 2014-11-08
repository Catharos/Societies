package net.catharos.societies.commands.society.home;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import net.catharos.groups.Group;
import net.catharos.groups.Member;
import net.catharos.groups.setting.Setting;
import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.ExecuteException;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.reflect.*;
import net.catharos.lib.core.command.reflect.instance.Children;
import net.catharos.societies.commands.RuleStep;
import net.catharos.societies.commands.VerifyStep;
import net.catharos.societies.member.SocietyMember;
import net.catharos.societies.teleport.TeleportController;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Set;

/**
 * Represents a AbandonCommand
 */
@Command(identifier = "command.home.home")
@Permission("societies.home.teleport")
@Children({
        HomeCommand.SetHomeCommand.class,
        HomeCommand.RegroupCommand.class,
        HomeCommand.RemoveHomeCommand.class
})
@Meta({@Entry(key = RuleStep.RULE, value = "home.teleport"), @Entry(key = VerifyStep.VERIFY)})
@Sender(value = SocietyMember.class)
public class HomeCommand implements Executor<SocietyMember> {

    private final Setting<Location> homeSetting;

    private final TeleportController teleportController;

    @Inject
    public HomeCommand(@Named("home") Setting<Location> homeSetting, TeleportController teleportController) {
        this.homeSetting = homeSetting;
        this.teleportController = teleportController;
    }

    @Override
    public void execute(CommandContext<SocietyMember> ctx, SocietyMember sender) throws ExecuteException {
        Group group = sender.getGroup();

        if (group == null) {
            sender.send("society.not-found");
            return;
        }

        Location location = group.getBoolean(homeSetting);

        if (location == null) {
            sender.send("home.not-set");
            return;
        }

        teleportController.teleport(sender, location);
        sender.send("home.teleporting");
    }


    @Command(identifier = "command.home.remove", async = true)
    @Permission("societies.home.remove")
    @Meta({@Entry(key = RuleStep.RULE, value = "home.remove"), @Entry(key = VerifyStep.VERIFY)})
    @Sender(value = SocietyMember.class)
    public static class RemoveHomeCommand implements Executor<Member> {

        private final Setting<Location> homeSetting;

        @Inject
        public RemoveHomeCommand(@Named("home") Setting<Location> homeSetting) {
            this.homeSetting = homeSetting;
        }

        @Override
        public void execute(CommandContext<Member> ctx, Member sender) throws ExecuteException {
            Group group = sender.getGroup();

            if (group == null) {
                sender.send("society.not-found");
                return;
            }

            group.remove(homeSetting);
            sender.send("home.removed");
        }
    }

    @Command(identifier = "command.home.set")
    @Permission("societies.home.set")
    @Meta({@Entry(key = RuleStep.RULE, value = "home.set"), @Entry(key = VerifyStep.VERIFY)})
    @Sender(value = SocietyMember.class)
    public static class SetHomeCommand implements Executor<SocietyMember> {

        @Option(name = "argument.location")
        Location location;

        private final Setting<Location> homeSetting;

        @Inject
        public SetHomeCommand(@Named("home") Setting<Location> homeSetting) {
            this.homeSetting = homeSetting;
        }

        @Override
        public void execute(CommandContext<SocietyMember> ctx, SocietyMember sender) throws ExecuteException {
            Group group = sender.getGroup();

            if (group == null) {
                sender.send("society.not-found");
                return;
            }

            if (location == null) {
                Player player = sender.toPlayer();
                assert player != null : "Player not available!";
                location = player.getLocation();
            }

            Location currentHome = group.getBoolean(homeSetting);

            if (currentHome != null) {
                sender.send("home.already-set");
                return;
            }

            group.set(homeSetting, location);
            sender.send("home.set", location.getX(), location.getY(), location.getZ());
        }
    }


    @Command(identifier = "command.home.regroup")
    @Permission("societies.home.regroup")
    @Meta({@Entry(key = RuleStep.RULE, value = "home.regroup"), @Entry(key = VerifyStep.VERIFY)})
    @Sender(value = SocietyMember.class)
    public static class RegroupCommand implements Executor<Member> {

        private final Setting<Location> homeSetting;

        @Inject
        public RegroupCommand(@Named("home") Setting<Location> homeSetting) {
            this.homeSetting = homeSetting;
        }

        @Override
        public void execute(CommandContext<Member> ctx, Member sender) throws ExecuteException {
            Group group = sender.getGroup();

            if (group == null) {
                sender.send("society.not-found");
                return;
            }

            Location location = group.getBoolean(homeSetting);

            if (location == null) {
                sender.send("home.not-set");
                return;
            }

            Set<Member> members = group.getMembers();

            for (Member member : members) {
                Player player = ((SocietyMember) member).toPlayer();

                if (player != null) {
                    player.teleport(location);
                    sender.send("home.regrouped");
                }
            }
        }
    }
}
