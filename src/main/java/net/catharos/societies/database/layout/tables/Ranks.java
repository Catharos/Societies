/**
 * This class is generated by jOOQ
 */
package net.catharos.societies.database.layout.tables;

/**
 * This class is generated by jOOQ.
 */
@javax.annotation.Generated(value    = { "http://www.jooq.org", "3.2.0" },
                            comments = "This class is generated by jOOQ")
@java.lang.SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Ranks extends org.jooq.impl.TableImpl<net.catharos.societies.database.layout.tables.records.RanksRecord> {

	private static final long serialVersionUID = -1535658755;

	/**
	 * The singleton instance of <code>societies.ranks</code>
	 */
	public static final net.catharos.societies.database.layout.tables.Ranks RANKS = new net.catharos.societies.database.layout.tables.Ranks();

	/**
	 * The class holding records for this type
	 */
	@Override
	public java.lang.Class<net.catharos.societies.database.layout.tables.records.RanksRecord> getRecordType() {
		return net.catharos.societies.database.layout.tables.records.RanksRecord.class;
	}

	/**
	 * The column <code>societies.ranks.uuid</code>. 
	 */
	public final org.jooq.TableField<net.catharos.societies.database.layout.tables.records.RanksRecord, byte[]> UUID = createField("uuid", org.jooq.impl.SQLDataType.VARBINARY.length(16).nullable(false), this);

	/**
	 * The column <code>societies.ranks.name</code>. 
	 */
	public final org.jooq.TableField<net.catharos.societies.database.layout.tables.records.RanksRecord, java.lang.String> NAME = createField("name", org.jooq.impl.SQLDataType.VARCHAR.length(45), this);

	/**
	 * Create a <code>societies.ranks</code> table reference
	 */
	public Ranks() {
		super("ranks", net.catharos.societies.database.layout.Societies.SOCIETIES);
	}

	/**
	 * Create an aliased <code>societies.ranks</code> table reference
	 */
	public Ranks(java.lang.String alias) {
		super(alias, net.catharos.societies.database.layout.Societies.SOCIETIES, net.catharos.societies.database.layout.tables.Ranks.RANKS);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.UniqueKey<net.catharos.societies.database.layout.tables.records.RanksRecord> getPrimaryKey() {
		return net.catharos.societies.database.layout.Keys.KEY_RANKS_PRIMARY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.util.List<org.jooq.UniqueKey<net.catharos.societies.database.layout.tables.records.RanksRecord>> getKeys() {
		return java.util.Arrays.<org.jooq.UniqueKey<net.catharos.societies.database.layout.tables.records.RanksRecord>>asList(net.catharos.societies.database.layout.Keys.KEY_RANKS_PRIMARY);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public net.catharos.societies.database.layout.tables.Ranks as(java.lang.String alias) {
		return new net.catharos.societies.database.layout.tables.Ranks(alias);
	}
}
