/**
 * This class is generated by jOOQ
 */
package org.societies.database.sql.layout;

/**
 * This class is generated by jOOQ.
 */
@javax.annotation.Generated(value    = { "http://www.jooq.org", "3.4.4" },
                            comments = "This class is generated by jOOQ")
@java.lang.SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Societies extends org.jooq.impl.SchemaImpl {

	private static final long serialVersionUID = -1181059847;

	/**
	 * The singleton instance of <code>societies</code>
	 */
	public static final Societies SOCIETIES = new Societies();

	/**
	 * No further instances allowed
	 */
	private Societies() {
		super("societies");
	}

	@Override
	public final java.util.List<org.jooq.Table<?>> getTables() {
		java.util.List result = new java.util.ArrayList();
		result.addAll(getTables0());
		return result;
	}

	private final java.util.List<org.jooq.Table<?>> getTables0() {
		return java.util.Arrays.<org.jooq.Table<?>>asList(
			org.societies.database.sql.layout.tables.Cities.CITIES,
			org.societies.database.sql.layout.tables.Lands.LANDS,
			org.societies.database.sql.layout.tables.Members.MEMBERS,
			org.societies.database.sql.layout.tables.MembersRanks.MEMBERS_RANKS,
			org.societies.database.sql.layout.tables.MemberSettings.MEMBER_SETTINGS,
			org.societies.database.sql.layout.tables.Ranks.RANKS,
			org.societies.database.sql.layout.tables.RanksSettings.RANKS_SETTINGS,
			org.societies.database.sql.layout.tables.Relations.RELATIONS,
			org.societies.database.sql.layout.tables.Sieges.SIEGES,
			org.societies.database.sql.layout.tables.Societies.SOCIETIES,
			org.societies.database.sql.layout.tables.SocietiesLocks.SOCIETIES_LOCKS,
			org.societies.database.sql.layout.tables.SocietiesRanks.SOCIETIES_RANKS,
			org.societies.database.sql.layout.tables.SocietiesSettings.SOCIETIES_SETTINGS);
	}
}
