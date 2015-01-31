package org.societies.commands.society;

import com.google.inject.Inject;
import net.catharos.lib.core.command.format.table.RowFactory;
import net.catharos.lib.core.command.format.table.Table;
import net.catharos.lib.core.command.reflect.Command;
import net.catharos.lib.core.command.reflect.Permission;
import org.societies.groups.Relation;
import org.societies.groups.group.GroupProvider;

import javax.inject.Provider;

/**
 * Represents a AlliancesCommand
 */
@Command(identifier = "command.rivalries", async = true)
@Permission("societies.rivalries")
public class RivalriesCommand extends AbstractRelationsCommand {

    @Inject
    public RivalriesCommand(RowFactory rowFactory, GroupProvider groupProvider, Provider<Table> tableProvider) {
        super(groupProvider, tableProvider, rowFactory);
    }

    @Override
    protected Relation.Type getType() {
        return Relation.Type.ALLIED;
    }
}
