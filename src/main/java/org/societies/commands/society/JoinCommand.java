package org.societies.commands.society;

import com.google.common.util.concurrent.FutureCallback;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import order.CommandContext;
import order.Executor;
import order.reflect.Argument;
import order.reflect.Command;
import order.reflect.Permission;
import order.reflect.Sender;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.shank.config.ConfigSetting;
import org.societies.api.Members;
import org.societies.groups.group.Group;
import org.societies.groups.member.Member;
import org.societies.groups.rank.Rank;
import org.societies.groups.request.*;
import org.societies.groups.request.simple.Choices;
import org.societies.request.ChoiceRequestMessenger;

import javax.annotation.Nullable;
import java.util.Set;

import static com.google.common.util.concurrent.Futures.addCallback;

/**
 * Represents a AbandonCommand
 */
@Command(identifier = "command.join", async = true)
@Permission("societies.join")
@Sender(value = Member.class)
public class JoinCommand implements Executor<Member> {

    @Argument(name = "argument.target.society")
    Group target;

    private final boolean trustDefault;
    private final Rank normalDefaultRank;
    private final Rank superRank;
    private final RequestFactory<Choices> requests;
    private final int maxSize;

    private final Logger logger;

    @Inject
    public JoinCommand(@ConfigSetting("trust.trust-members-by-default") boolean trustDefault,
                       @Named("normal-default-rank") Rank normalDefaultRank,
                       @Named("super-default-rank") Rank superRank,
                       RequestFactory<Choices> requests, @ConfigSetting("society.max-size") int maxSize, Logger logger) {
        this.trustDefault = trustDefault;
        this.normalDefaultRank = normalDefaultRank;
        this.superRank = superRank;
        this.requests = requests;
        this.maxSize = maxSize;
        this.logger = logger;
    }

    @Override
    public void execute(CommandContext<Member> ctx, final Member sender) {
        if (sender.hasGroup()) {
            sender.send("society.already-member");
            return;
        }

        if (maxSize >= 0 && target.size() >= maxSize) {
            sender.send("society.other-reached-max-size", target.getName());
            return;
        }

        //allow to fix empty groups
        if (target.size() == 0) {
            target.addMember(sender);

            sender.addRank(superRank);
            sender.addRank(normalDefaultRank);
            return;
        }

        Set<Member> participants = Members.onlineMembers(target.getMembers("vote.join"));

        if (participants.size() < 1) {
            sender.send("target-participants.not-available");
            return;
        }

        final Request<Choices> request = requests
                .create(sender, new SetInvolved(participants), new JoinRequestMessenger(target));

        if (!request.start()) {
            sender.send("requests.participants-not-ready");
            return;
        }

        addCallback(request.result(), new FutureCallback<DefaultRequestResult<Choices>>() {
            @Override
            public void onSuccess(@Nullable DefaultRequestResult<Choices> result) {
                if (result == null) {
                    return;
                }

                if (result.getChoice().success()) {
                    target.addMember(sender);

                    if (trustDefault) {
                        target.addRank(normalDefaultRank);
                    }

                }
            }

            @Override
            public void onFailure(@NotNull Throwable t) {
                logger.catching(t);
            }
        });
    }

    private static class JoinRequestMessenger extends ChoiceRequestMessenger {

        private final Group group;

        private JoinRequestMessenger(Group group) {
            this.group = group;
        }


        @Override
        public void start(Request<Choices> request) {
            request.getSupplier().send("join.started", group.getTag());
            super.start(request);
        }

        @Override
        public void voted(Request<Choices> request, Choices choice, Participant participant) {
            //do nothing
        }

        @Override
        public void cancelled(Request<Choices> request) {
            //do noting
        }

        @Override
        public void start(Request<Choices> request, Participant participant) {
            participant.send("join.member-requests", participant.getName());
        }

        @Override
        public void end(Request<Choices> request, Choices choice) {
            if (choice.success()) {
                request.getSupplier().send("you-joined", group.getTag());
            } else {
                request.getSupplier().send("you-failed", group.getTag());
            }

            super.end(request, choice);
        }

        @Override
        public void end(Participant participant, Request<Choices> request, Choices choice) {
            if (choice.success()) {
                participant.send("member-joined", participant.getName());
            } else {
                participant.send("member-failed", participant.getName());
            }


        }
    }
}
