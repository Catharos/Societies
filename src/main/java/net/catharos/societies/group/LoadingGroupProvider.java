package net.catharos.societies.group;

import com.google.inject.Inject;
import com.google.inject.Provider;
import gnu.trove.set.hash.THashSet;
import net.catharos.groups.Group;
import net.catharos.groups.GroupFactory;
import net.catharos.groups.GroupProvider;
import net.catharos.lib.core.util.ByteUtil;
import net.catharos.lib.core.uuid.UUIDGen;
import net.catharos.societies.database.layout.tables.records.SocietiesRecord;
import org.jooq.Result;
import org.jooq.Select;

import java.util.Set;
import java.util.UUID;

import static net.catharos.societies.group.SocietyQueries.*;

/**
 * Represents a LoadingGroupProvider
 */
public class LoadingGroupProvider implements GroupProvider {
    private final SocietyQueries queries;
    private final Provider<Group> groupProvider;
    private final GroupFactory factory;

    @Inject
    public LoadingGroupProvider(SocietyQueries queries, Provider<Group> groupProvider, GroupFactory factory) {
        this.queries = queries;
        this.groupProvider = groupProvider;
        this.factory = factory;
    }

    @Override
    public Group getGroup(UUID uuid) {
        Select<SocietiesRecord> query = queries.getQuery(SELECT_SOCIETY_BY_UUID);
        query.bind(1, ByteUtil.toByteArray(uuid.getMostSignificantBits(), uuid.getLeastSignificantBits()));

        return evaluateSingle(query(query));
    }

    private Group evaluateSingle(Result<SocietiesRecord> result) {

        if (result.isEmpty()) {
            return groupProvider.get();
        } else if (result.size() > 1) {
            throw new SocietyException("There are more groups with the same uuid?!");
        }

        return createGroup(result.get(0));
    }

    private Set<Group> evaluate(Result<SocietiesRecord> result) {

        THashSet<Group> groups = new THashSet<Group>(result.size());

        for (SocietiesRecord record : result) {
            groups.add(createGroup(record));
        }

        return groups;
    }

    private Result<SocietiesRecord> query(Select<SocietiesRecord> query) {
        try {
            return query.fetch();
        } catch (RuntimeException e) {
            throw new SocietyException(e, "Query failed to execute!");
        }
    }

    private Group createGroup(SocietiesRecord record) {
        return factory.create(UUIDGen.toUUID(record.getUuid()), record.getName());
    }

    @Override
    public Set<Group> getGroup(String name) {
        Select<SocietiesRecord> query = queries.getQuery(SELECT_SOCIETY_BY_NAME);
        query.bind(1, name);

        return evaluate(query(query));
    }

    @Override
    public Set<Group> getGroups() {
        return evaluate(query(queries.getQuery(SELECT_SOCIETIES)));
    }
}
