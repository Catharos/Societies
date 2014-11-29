package net.catharos.societies.database.sql;

import net.catharos.groups.AbstractMemberHeart;
import net.catharos.groups.Group;
import net.catharos.groups.MemberHeart;
import net.catharos.groups.rank.Rank;
import org.jetbrains.annotations.Nullable;
import org.joda.time.DateTime;

import java.util.Set;

/**
 * Represents a SQLMember
 */
public class SQLMemberHearth extends AbstractMemberHeart implements MemberHeart {

    @Override
    public Set<Rank> getRanks() {
        return null;
    }

    @Override
    public void addRank(Rank rank) {

    }

    @Override
    public boolean removeRank(Rank rank) {
        return false;
    }

    @Override
    public DateTime getLastActive() {
        return null;
    }

    @Override
    public void setLastActive(DateTime lastActive) {

    }

    @Override
    public DateTime getCreated() {
        return null;
    }

    @Override
    public void setCreated(DateTime created) {

    }

    @Nullable
    @Override
    public Group getGroup() {
        return null;
    }

    @Override
    public boolean hasGroup() {
        return false;
    }

    @Override
    public void setGroup(@Nullable Group group) {

    }

    @Override
    public boolean isGroup(Group group) {
        return false;
    }
}
