package net.catharos.societies.database.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.google.common.collect.Table;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.migcomponents.migbase64.Base64;
import gnu.trove.set.hash.THashSet;
import net.catharos.groups.Group;
import net.catharos.groups.GroupBuilder;
import net.catharos.groups.rank.Rank;
import net.catharos.groups.rank.RankFactory;
import net.catharos.groups.setting.Setting;
import net.catharos.groups.setting.SettingProvider;
import net.catharos.groups.setting.target.SimpleTarget;
import net.catharos.groups.setting.target.Target;
import net.catharos.lib.core.util.CastSafe;
import org.joda.time.DateTime;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;

/**
 * Represents a GroupMapper
 */
public class GroupMapper extends AbstractMapper {

    private final Provider<GroupBuilder> builders;
    private final RankFactory rankFactory;
    private final SettingProvider settingProvider;

    @Inject
    public GroupMapper(Provider<GroupBuilder> builders, RankFactory rankFactory, SettingProvider settingProvider) {
        this.builders = builders;
        this.rankFactory = rankFactory;
        this.settingProvider = settingProvider;
    }

    public Set<Group> readGroups(JsonParser parser) throws IOException {
        parser.nextToken();
        validateObject(parser);

        THashSet<Group> groups = new THashSet<Group>();

        while (parser.nextToken() != JsonToken.END_ARRAY) {
            groups.add(readGroup(parser));
        }

        return groups;
    }

    static void validateObject(JsonParser parser) throws IOException {
        if (parser.getCurrentToken() != JsonToken.START_OBJECT) {
            throw new IOException("Expected data to start with an Object, but was " + parser.getCurrentToken());
        }
    }

    static void validateArray(JsonParser parser) throws IOException {
        if (parser.getCurrentToken() != JsonToken.START_ARRAY) {
            throw new IOException("Expected data to start with an Array, but was " + parser.getCurrentToken());
        }
    }

    public Group readGroup(JsonParser parser) throws IOException {
        parser.nextToken();
        validateObject(parser);

        GroupBuilder builder = builders.get();

        while (parser.nextToken() != JsonToken.END_OBJECT) {
            String groupField = parser.getCurrentName();

            parser.nextToken();
            if (groupField.equals("uuid")) {
                builder.setUUID(UUID.fromString(parser.getText()));
            } else if (groupField.equals("name")) {
                builder.setName(parser.getText());
            } else if (groupField.equals("tag")) {
                builder.setTag(parser.getText());
            } else if (groupField.equals("created")) {
                builder.setCreated(new DateTime(parser.getLongValue()));
            } else if (groupField.equals("state")) {
                builder.setState(parser.getShortValue());
            } else if (groupField.equals("settings")) {
                validateArray(parser);

                while (parser.nextToken() != JsonToken.END_ARRAY) {
                    validateObject(parser);

                    Target target = null;
                    Setting setting = null;
                    byte[] value = null;

                    while (parser.nextToken() != JsonToken.END_OBJECT) {
                        String settingField = parser.getCurrentName();

                        parser.nextToken();
                        if (settingField.equals("target")) {
                            target = new SimpleTarget(UUID.fromString(parser.getText()));
                        } else if (settingField.equals("setting")) {
                            setting = settingProvider.getSetting(parser.getIntValue());
                        } else if (settingField.equals("value")) {
                            value = parser.getBinaryValue();
                        }
                    }

                    if (target == null || setting == null || value == null) {
                        continue;
                    }

                    builder.put(setting, target, value);
                }
            } else if (groupField.equals("ranks")) {
                validateArray(parser);

                ArrayList<Rank> ranks = new ArrayList<Rank>();
                while (parser.nextToken() != JsonToken.END_ARRAY) {
                    ranks.add(readRank(parser));
                }

                builder.setRanks(ranks);
            }
        }


        return builder.build();
    }

    public void writeGroup(JsonGenerator generator, Group group) throws IOException {
        generator.writeStartObject();

        generator.writeStringField("uuid", group.getUUID().toString());
        generator.writeStringField("name", group.getName());
        generator.writeStringField("tag", group.getTag());
        generator.writeNumberField("created", group.getCreated().getMillis());
        generator.writeNumberField("state", (short) group.getState());


        Collection<Rank> ranks = group.getRanks();
        if (!ranks.isEmpty()) {
            generator.writeArrayFieldStart("ranks");
            for (Rank rank : ranks) {
                writeRank(generator, rank);
            }
            generator.writeEndArray();
        }

        Set<Table.Cell<Setting, Target, Object>> settings = group.getSettings().cellSet();
        if (!settings.isEmpty()) {

            generator.writeArrayFieldStart("settings");
            for (Table.Cell<Setting, Target, Object> cell : settings) {
                generator.writeStartObject();
                Target target = cell.getColumnKey();
                Setting<Object> setting = CastSafe.toGeneric(cell.getRowKey());

                generator.writeStringField("target", target.getUUID().toString());

                generator.writeNumberField("setting", setting.getID());
                byte[] convert = setting.convert(group, target, cell.getValue());
                generator.writeStringField("value", Base64.encodeToString(convert, false));
                generator.writeEndObject();
            }
            generator.writeEndArray();

        }

        generator.writeEndObject();
    }


    public Rank readRank(JsonParser parser) throws IOException {
        validateObject(parser);

        UUID uuid = null;
        String name = null;

        while (parser.nextToken() != JsonToken.END_OBJECT) {
            String fieldName = parser.getCurrentName();

            parser.nextToken();
            if (fieldName.equals("uuid")) {
                uuid = UUID.fromString(parser.getText());
            } else if (fieldName.equals("name")) {
                name = parser.getText();
            }
        }

        return rankFactory.create(uuid, name, Rank.DEFAULT_PRIORITY);
    }

    public void writeRank(JsonGenerator generator, Rank rank) throws IOException {
        generator.writeStartObject();

        generator.writeStringField("uuid", rank.getUUID().toString());
        generator.writeStringField("name", rank.getName());
        generator.writeEndObject();
    }


    public Group readGroup(File file) throws IOException {
        JsonParser parser = createParser(file);
        Group output = readGroup(parser);
        parser.close();
        return output;
    }

    public Group readGroup(String data) throws IOException {
        JsonParser parser = createParser(data);
        Group output = readGroup(parser);
        parser.close();
        return output;
    }

    public Set<Group> readGroups(File file) throws IOException {
        JsonParser parser = createParser(file);
        Set<Group> output = readGroups(parser);
        parser.close();
        return output;
    }

    public void writeGroup(Group group, File file) throws IOException {
        JsonGenerator jg = createGenerator(file);
        writeGroup(jg, group);

        jg.close();
    }

    public String writeGroup(Group group) throws IOException {
        StringWriter stringWriter = new StringWriter();
        JsonGenerator jg = createGenerator(stringWriter);
        writeGroup(jg, group);

        jg.close();

        return stringWriter.toString();
    }
}
