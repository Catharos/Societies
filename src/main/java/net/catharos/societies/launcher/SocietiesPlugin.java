package net.catharos.societies.launcher;


import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import net.catharos.groups.Group;
import net.catharos.groups.MemberProvider;
import net.catharos.lib.core.command.Commands;
import net.catharos.lib.core.command.sender.Sender;
import net.catharos.lib.database.Database;
import net.catharos.lib.shank.logging.LoggingModule;
import net.catharos.lib.shank.service.ServiceController;
import net.catharos.lib.shank.service.ServiceModule;
import net.catharos.lib.shank.service.lifecycle.Lifecycle;
import net.catharos.societies.SocietiesModule;
import net.catharos.societies.bukkit.BukkitModule;
import net.catharos.societies.economy.DummyEconomy;
import net.catharos.societies.group.OnlineGroupCache;
import net.catharos.societies.member.OnlineMemberCache;
import net.catharos.societies.member.SocietyMember;
import net.milkbowl.vault.economy.Economy;
import org.apache.logging.log4j.LogManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nullable;
import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static com.google.common.util.concurrent.Futures.addCallback;


/**
 * Represents a Launcher
 */
public class SocietiesPlugin extends JavaPlugin implements Listener, ReloadAction {

    private Injector injector;

    private Commands<Sender> commands;
    private MemberProvider<SocietyMember> memberProvider;
    private ServiceController serviceController;
    private OnlineMemberCache<SocietyMember> memberCache;
    private OnlineGroupCache groupCache;

    @Override
    public void onLoad() {
        //getServer() -> null
    }

    @Override
    public void onEnable() {
        Economy economy;

        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager()
                .getRegistration(Economy.class);

        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        } else {
            economy = new DummyEconomy();
        }

        File dir = getDataFolder();

        injector = Guice.createInjector(
                new ServiceModule(),
                new LoggingModule(dir, LogManager.getContext()),
                new SocietiesModule(dir),
                new BukkitModule(getServer(), this, economy)
        );


        serviceController = injector.getInstance(ServiceController.class);

        serviceController.invoke(Lifecycle.INITIALISING);


        getServer().getPluginManager().registerEvents(this, this);
        commands = injector.getInstance(Key.get(new TypeLiteral<Commands<Sender>>() {}));
        memberProvider = injector.getInstance(Key.get(new TypeLiteral<MemberProvider<SocietyMember>>() {}));
        memberCache = injector.getInstance(Key.get(new TypeLiteral<OnlineMemberCache<SocietyMember>>() {}));
        groupCache = injector.getInstance(OnlineGroupCache.class);

        serviceController.invoke(Lifecycle.STARTING);
    }

    @Override
    public void onDisable() {
        serviceController.invoke(Lifecycle.STOPPING);
        ListeningExecutorService service = injector.getInstance(ListeningExecutorService.class);

        try {
            service.awaitTermination(1000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            // Nobody fucking cares!
            e.printStackTrace();
        }

        service.shutdown();

        Database database = injector.getInstance(Database.class);
        database.close();
    }


    @Override
    public boolean onCommand(CommandSender sender, final Command command, String label, final String[] args) {

        if (injector == null) {
            sender.sendMessage("Societies failed to start somehow, sorry :/");
            return true;
        }

        if (sender instanceof Player) {

            ListenableFuture<SocietyMember> future = memberProvider.getMember(((Player) sender).getUniqueId());

            try {
                future.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            addCallback(future, new FutureCallback<SocietyMember>() {
                @Override
                public void onSuccess(@Nullable SocietyMember result) {
                    commands.execute(result, command.getName(), args);
                }

                @Override
                public void onFailure(Throwable t) {
                    t.printStackTrace();
                }
            });


        } else {
            commands.execute(injector.getInstance(Key.get(Sender.class, Names.named("system-sender"))), command
                    .getName(), args);
        }


        return true;
    }

    @EventHandler
    public void onPlayerJoin(PlayerLoginEvent event) {
        MemberProvider<SocietyMember> provider = injector
                .getInstance(Key.get(new TypeLiteral<MemberProvider<SocietyMember>>() {}));


        provider.getMember(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        SocietyMember member = memberCache.clear(event.getPlayer().getUniqueId());

        if (member != null) {
            Group group = member.getGroup();
            if (group != null) {
                groupCache.clear(member, group);
            }
        }
    }

    @Override
    public void reload() {
        onDisable();
        onEnable();
    }
}
