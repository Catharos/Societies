package net.catharos.societies.commands.society;

import com.google.inject.Inject;
import com.typesafe.config.Config;
import net.catharos.groups.Group;
import net.catharos.groups.GroupFactory;
import net.catharos.groups.GroupPublisher;
import net.catharos.groups.validate.NameValidator;
import net.catharos.groups.validate.TagValidator;
import net.catharos.groups.validate.ValidateResult;
import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.reflect.Argument;
import net.catharos.lib.core.command.reflect.Command;
import net.catharos.lib.core.command.sender.Sender;
import net.catharos.societies.member.SocietyMember;

/**
 * Represents a CreateCommand
 */
@Command(identifier = "command.create")
public class CreateCommand implements Executor<Sender> {

    @Argument(name = "argument.society.name")
    String name;

    @Argument(name = "argument.society.tag")
    String tag;

    private final GroupFactory groupFactory;
    private final GroupPublisher publisher;
    private final NameValidator nameValidator;
    private final TagValidator tagValidator;
    private final double price;

    @Inject
    public CreateCommand(GroupFactory groupFactory,
                         GroupPublisher publisher,
                         NameValidator nameValidator, TagValidator tagValidator,
                         Config config) {
        this.groupFactory = groupFactory;
        this.publisher = publisher;
        this.nameValidator = nameValidator;
        this.tagValidator = tagValidator;
        this.price = config.getDouble("economy.creation-price");
    }

    @Override
    public void execute(CommandContext<Sender> ctx, Sender sender) {
        ValidateResult nameResult = nameValidator.validateName(name);


        if (nameResult.isFailed()) {
            sender.send(nameResult.getMessage());
            return;
        }

        ValidateResult tagResult = tagValidator.validateTag(tag);

        if (tagResult.isFailed()) {
            sender.send(tagResult.getMessage());
            return;
        }

        if (!sender.as(new Sender.Executor<SocietyMember, Boolean>() {
            @Override
            public Boolean execute(SocietyMember sender) {return sender.withdraw(price).transactionSuccess(); }

        }, SocietyMember.class)) {
            return;
        }

        Group group = groupFactory.create(name, tag);
        publisher.publish(group);

        if (sender instanceof SocietyMember) {
            group.addMember(((SocietyMember) sender));
        }

        sender.send("society.created", name, tag);
    }
}
