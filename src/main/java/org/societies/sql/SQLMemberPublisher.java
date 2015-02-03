package org.societies.sql;

import com.google.inject.Inject;
import org.jooq.Insert;
import org.jooq.Query;
import org.societies.database.sql.layout.tables.records.MembersRecord;
import org.societies.groups.cache.MemberCache;
import org.societies.groups.member.Member;
import org.societies.groups.member.MemberPublisher;

/**
 * Represents a SQLMemberPublisher
 */
class SQLMemberPublisher implements MemberPublisher {

    private final Queries queries;
    private final MemberCache memberCache;

    @Inject
    public SQLMemberPublisher(Queries queries, MemberCache memberCache) {
        this.queries = queries;
        this.memberCache = memberCache;
    }

    @Override
    public Member publish(final Member member) {
        Insert<MembersRecord> query = queries.getQuery(Queries.INSERT_MEMBER);

        query.bind(1, member.getUUID());

        query.execute();

        member.activate();
        return member;
    }

    @Override
    public Member destruct(final Member member) {
        Query query = queries.getQuery(Queries.DROP_MEMBER_BY_UUID);

        query.bind(1, member.getUUID());

        query.execute();

        memberCache.clear(member);

        return member;
    }
}
