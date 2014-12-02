/**
 * This class is generated by jOOQ
 */
package org.societies.database.sql.layout.tables;

/**
 * This class is generated by jOOQ.
 */
@javax.annotation.Generated(value = {"http://www.jooq.org", "3.4.4"},
        comments = "This class is generated by jOOQ")
@java.lang.SuppressWarnings({"all", "unchecked", "rawtypes"})
public class RanksSettings extends org.jooq.impl.TableImpl<org.societies.database.sql.layout.tables.records.RanksSettingsRecord> {

    private static final long serialVersionUID = 670895963;

    /**
     * The singleton instance of <code>societies.ranks_settings</code>
     */
    public static final org.societies.database.sql.layout.tables.RanksSettings RANKS_SETTINGS = new org.societies.database.sql.layout.tables.RanksSettings();

    /**
     * The class holding records for this type
     */
    @Override
    public java.lang.Class<org.societies.database.sql.layout.tables.records.RanksSettingsRecord> getRecordType() {
        return org.societies.database.sql.layout.tables.records.RanksSettingsRecord.class;
    }

    /**
     * The column <code>societies.ranks_settings.subject_uuid</code>.
     */
    public final org.jooq.TableField<org.societies.database.sql.layout.tables.records.RanksSettingsRecord, byte[]> SUBJECT_UUID = createField("subject_uuid", org.jooq.impl.SQLDataType.VARBINARY
            .length(16).nullable(false), this, "");

    /**
     * The column <code>societies.ranks_settings.target_uuid</code>.
     */
    public final org.jooq.TableField<org.societies.database.sql.layout.tables.records.RanksSettingsRecord, byte[]> TARGET_UUID = createField("target_uuid", org.jooq.impl.SQLDataType.VARBINARY
            .length(16).nullable(false), this, "");

    /**
     * The column <code>societies.ranks_settings.setting</code>.
     */
    public final org.jooq.TableField<org.societies.database.sql.layout.tables.records.RanksSettingsRecord, org.jooq.types.UShort> SETTING = createField("setting", org.jooq.impl.SQLDataType.SMALLINTUNSIGNED
            .nullable(false), this, "");

    /**
     * The column <code>societies.ranks_settings.value</code>.
     */
    public final org.jooq.TableField<org.societies.database.sql.layout.tables.records.RanksSettingsRecord, byte[]> VALUE = createField("value", org.jooq.impl.SQLDataType.VARBINARY
            .length(64), this, "");

    /**
     * Create a <code>societies.ranks_settings</code> table reference
     */
    public RanksSettings() {
        this("ranks_settings", null);
    }

    /**
     * Create an aliased <code>societies.ranks_settings</code> table reference
     */
    public RanksSettings(java.lang.String alias) {
        this(alias, org.societies.database.sql.layout.tables.RanksSettings.RANKS_SETTINGS);
    }

    private RanksSettings(java.lang.String alias, org.jooq.Table<org.societies.database.sql.layout.tables.records.RanksSettingsRecord> aliased) {
        this(alias, aliased, null);
    }

    private RanksSettings(java.lang.String alias, org.jooq.Table<org.societies.database.sql.layout.tables.records.RanksSettingsRecord> aliased, org.jooq.Field<?>[] parameters) {
        super(alias, org.societies.database.sql.layout.Societies.SOCIETIES, aliased, parameters, "");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public org.jooq.UniqueKey<org.societies.database.sql.layout.tables.records.RanksSettingsRecord> getPrimaryKey() {
        return org.societies.database.sql.layout.Keys.KEY_RANKS_SETTINGS_PRIMARY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public java.util.List<org.jooq.UniqueKey<org.societies.database.sql.layout.tables.records.RanksSettingsRecord>> getKeys() {
        return java.util.Arrays
                .<org.jooq.UniqueKey<org.societies.database.sql.layout.tables.records.RanksSettingsRecord>>asList(org.societies.database.sql.layout.Keys.KEY_RANKS_SETTINGS_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public java.util.List<org.jooq.ForeignKey<org.societies.database.sql.layout.tables.records.RanksSettingsRecord, ?>> getReferences() {
        return java.util.Arrays
                .<org.jooq.ForeignKey<org.societies.database.sql.layout.tables.records.RanksSettingsRecord, ?>>asList(org.societies.database.sql.layout.Keys.FK_RANKS_SETTINGS_RANKS1, org.societies.database.sql.layout.Keys.FK_RANKS_SETTINGS_RANKS2);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public org.societies.database.sql.layout.tables.RanksSettings as(java.lang.String alias) {
        return new org.societies.database.sql.layout.tables.RanksSettings(alias, this);
    }

    /**
     * Rename this table
     */
    public org.societies.database.sql.layout.tables.RanksSettings rename(java.lang.String name) {
        return new org.societies.database.sql.layout.tables.RanksSettings(name, null);
    }
}
