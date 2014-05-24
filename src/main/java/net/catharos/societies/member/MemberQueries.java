package net.catharos.societies.member;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.catharos.lib.database.DSLProvider;
import net.catharos.lib.database.QueryKey;
import net.catharos.lib.database.QueryProvider;
import net.catharos.societies.database.layout.tables.records.MembersRecord;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.Select;

import static net.catharos.societies.database.layout.Tables.MEMBERS;
import static net.catharos.societies.database.layout.Tables.MEMBERS_RANKS;

/**
 * Represents a SocietiesQueries
 */
@Singleton
public class MemberQueries extends QueryProvider {

    public static final QueryKey<Select<MembersRecord>> SELECT_MEMBER_BY_UUID = QueryKey.create();

    public static final QueryKey<Select<Record1<byte[]>>> SELECT_MEMBER_RANKS = QueryKey.create();

    @Inject
    protected MemberQueries(DSLProvider provider) {
        super(provider);
    }

    @Override
    public void build() {

        builder(SELECT_MEMBER_BY_UUID, new QueryBuilder<Select<MembersRecord>>() {
            @Override
            public Select<MembersRecord> create(DSLContext context) {
                return context.
                        selectFrom(MEMBERS)
                        .where(MEMBERS.UUID.equal(DEFAULT_UUID));
            }
        });

        builder(SELECT_MEMBER_RANKS, new QueryBuilder<Select<Record1<byte[]>>>() {
            @Override
            public Select<Record1<byte[]>> create(DSLContext context) {
                return context.
                        select(MEMBERS_RANKS.RANK).from(MEMBERS)
                        .where(MEMBERS_RANKS.MEMBER.equal(DEFAULT_UUID));
            }
        });
    }
}
