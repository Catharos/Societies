package org.societies.database.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;
import com.google.inject.Inject;
import com.google.inject.Provider;
import gnu.trove.set.hash.THashSet;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.societies.groups.group.Group;
import org.societies.groups.group.GroupBuilder;
import org.societies.groups.rank.Rank;
import org.societies.groups.rank.RankFactory;
import org.societies.groups.rank.memory.MemoryRank;
import org.societies.groups.setting.Setting;
import org.societies.groups.setting.SettingProvider;
import org.societies.groups.setting.target.Target;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Represents a GroupMapper
 */
public class GroupMapper extends AbstractMapper {

    private final Provider<GroupBuilder> builders;
    private final RankFactory rankFactory;

    @Inject
    public GroupMapper(Logger logger, Provider<GroupBuilder> builders, RankFactory rankFactory, SettingProvider settingProvider) {
        super(logger, settingProvider);
        this.builders = builders;
        this.rankFactory = rankFactory;
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

    public Group readGroup(JsonParser parser) throws IOException {
        parser.nextToken();
        validateObject(parser);

        GroupBuilder builder = builders.get();


        List<Rank> rawRanks = Lists.newArrayList();

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
            } else if (groupField.equals("settings")) {
                readSettings(parser, builder.getSettings());
            } else if (groupField.equals("ranks")) {
                validateArray(parser);

                while (parser.nextToken() != JsonToken.END_ARRAY) {
                    rawRanks.add(readRank(parser));
                }
            }
        }


        //beatify
        Group group = builder.build();

        group.unlink();

        for (Rank rank : rawRanks) {
            group.addRank(finalizeRank(group, rank));
        }

        group.link();

        return group;
    }

    public void writeGroup(JsonGenerator generator, Group group) throws IOException {
        generator.writeStartObject();

        generator.writeStringField("uuid", group.getUUID().toString());
        generator.writeStringField("name", group.getName());
        generator.writeStringField("tag", group.getTag());
        generator.writeNumberField("created", group.getCreated().getMillis());
        generator.writeBooleanField("verified", group.isVerified());


        Collection<Rank> ranks = group.getRanks();
        if (!ranks.isEmpty()) {
            generator.writeArrayFieldStart("ranks");
            for (Rank rank : ranks) {
                writeRank(generator, rank);
            }
            generator.writeEndArray();
        }

        writeSettings(group, generator, group.getSettings());

        generator.writeEndObject();
    }

    private Rank finalizeRank(Group group, Rank rank) {
        if (rank instanceof MemoryRank) {
            ((MemoryRank) rank).setGroup(group);
        }

        rank.link();

        return rank;
    }

    private Rank readRank(JsonParser parser) throws IOException {
        validateObject(parser);

        UUID uuid = null;
        String name = null;
        int priority = 0;
        Table<Setting, Target, String> settings = HashBasedTable.create();

        while (parser.nextToken() != JsonToken.END_OBJECT) {
            String fieldName = parser.getCurrentName();

            parser.nextToken();
            if (fieldName.equals("uuid")) {
                uuid = UUID.fromString(parser.getText());
            } else if (fieldName.equals("name")) {
                name = parser.getText();
            } else if (fieldName.equals("priority")) {
                priority = parser.getIntValue();
            } else if (fieldName.equals("settings")) {
                readSettings(parser, settings);
            }
        }

        Rank rank = rankFactory.create(uuid, name, priority, null);

        for (Table.Cell<Setting, Target, String> cell : settings.cellSet()) {
            rank.set(cell.getRowKey(), cell.getColumnKey(), cell.getValue());
        }

        return rank;
    }

    public void writeRank(JsonGenerator generator, Rank rank) throws IOException {
        if (rank.isStatic()) {
            return;
        }

        generator.writeStartObject();

        generator.writeStringField("uuid", rank.getUUID().toString());
        generator.writeStringField("name", rank.getName());
        writeSettings(rank, generator, rank.getSettings());

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
