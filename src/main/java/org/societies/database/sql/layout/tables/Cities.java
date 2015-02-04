/**
 * This class is generated by jOOQ
 */
package org.societies.database.sql.layout.tables;

/**
 * This class is generated by jOOQ.
 */
@javax.annotation.Generated(
	value = {
		"http://www.jooq.org",
		"jOOQ version:3.5.1"
	},
	comments = "This class is generated by jOOQ"
)
@java.lang.SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Cities extends org.jooq.impl.TableImpl<org.societies.database.sql.layout.tables.records.CitiesRecord> {

	private static final long serialVersionUID = -420244762;

	/**
	 * The reference instance of <code>societies.cities</code>
	 */
	public static final org.societies.database.sql.layout.tables.Cities CITIES = new org.societies.database.sql.layout.tables.Cities();

	/**
	 * The class holding records for this type
	 */
	@Override
	public java.lang.Class<org.societies.database.sql.layout.tables.records.CitiesRecord> getRecordType() {
		return org.societies.database.sql.layout.tables.records.CitiesRecord.class;
	}

	/**
	 * The column <code>societies.cities.uuid</code>.
	 */
	public final org.jooq.TableField<org.societies.database.sql.layout.tables.records.CitiesRecord, java.util.UUID> UUID = createField("uuid", org.jooq.impl.SQLDataType.VARBINARY.length(16).nullable(false), this, "", new org.societies.database.sql.UUIDConverter());

	/**
	 * The column <code>societies.cities.society</code>.
	 */
	public final org.jooq.TableField<org.societies.database.sql.layout.tables.records.CitiesRecord, java.util.UUID> SOCIETY = createField("society", org.jooq.impl.SQLDataType.VARBINARY.length(16).nullable(false), this, "", new org.societies.database.sql.UUIDConverter());

	/**
	 * The column <code>societies.cities.name</code>.
	 */
	public final org.jooq.TableField<org.societies.database.sql.layout.tables.records.CitiesRecord, java.lang.String> NAME = createField("name", org.jooq.impl.SQLDataType.VARCHAR.length(45), this, "");

	/**
	 * The column <code>societies.cities.x</code>.
	 */
	public final org.jooq.TableField<org.societies.database.sql.layout.tables.records.CitiesRecord, java.lang.Short> X = createField("x", org.jooq.impl.SQLDataType.SMALLINT, this, "");

	/**
	 * The column <code>societies.cities.y</code>.
	 */
	public final org.jooq.TableField<org.societies.database.sql.layout.tables.records.CitiesRecord, java.lang.Short> Y = createField("y", org.jooq.impl.SQLDataType.SMALLINT, this, "");

	/**
	 * The column <code>societies.cities.z</code>.
	 */
	public final org.jooq.TableField<org.societies.database.sql.layout.tables.records.CitiesRecord, java.lang.Short> Z = createField("z", org.jooq.impl.SQLDataType.SMALLINT, this, "");

	/**
	 * Create a <code>societies.cities</code> table reference
	 */
	public Cities() {
		this("cities", null);
	}

	/**
	 * Create an aliased <code>societies.cities</code> table reference
	 */
	public Cities(java.lang.String alias) {
		this(alias, org.societies.database.sql.layout.tables.Cities.CITIES);
	}

	private Cities(java.lang.String alias, org.jooq.Table<org.societies.database.sql.layout.tables.records.CitiesRecord> aliased) {
		this(alias, aliased, null);
	}

	private Cities(java.lang.String alias, org.jooq.Table<org.societies.database.sql.layout.tables.records.CitiesRecord> aliased, org.jooq.Field<?>[] parameters) {
		super(alias, org.societies.database.sql.layout.Societies.SOCIETIES, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.UniqueKey<org.societies.database.sql.layout.tables.records.CitiesRecord> getPrimaryKey() {
		return org.societies.database.sql.layout.Keys.KEY_CITIES_PRIMARY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.util.List<org.jooq.UniqueKey<org.societies.database.sql.layout.tables.records.CitiesRecord>> getKeys() {
		return java.util.Arrays.<org.jooq.UniqueKey<org.societies.database.sql.layout.tables.records.CitiesRecord>>asList(org.societies.database.sql.layout.Keys.KEY_CITIES_PRIMARY, org.societies.database.sql.layout.Keys.KEY_CITIES_IDCITIES_UNIQUE);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.util.List<org.jooq.ForeignKey<org.societies.database.sql.layout.tables.records.CitiesRecord, ?>> getReferences() {
		return java.util.Arrays.<org.jooq.ForeignKey<org.societies.database.sql.layout.tables.records.CitiesRecord, ?>>asList(org.societies.database.sql.layout.Keys.FK_CITIES_1);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.societies.database.sql.layout.tables.Cities as(java.lang.String alias) {
		return new org.societies.database.sql.layout.tables.Cities(alias, this);
	}

	/**
	 * Rename this table
	 */
	public org.societies.database.sql.layout.tables.Cities rename(java.lang.String name) {
		return new org.societies.database.sql.layout.tables.Cities(name, null);
	}
}
