package net.catharos.societies.database.json;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Table;
import com.migcomponents.migbase64.Base64;
import net.catharos.groups.setting.Setting;
import net.catharos.groups.setting.SettingException;
import net.catharos.groups.setting.SettingProvider;
import net.catharos.groups.setting.subject.Subject;
import net.catharos.groups.setting.target.SimpleTarget;
import net.catharos.groups.setting.target.Target;
import net.catharos.lib.core.util.CastSafe;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.UUID;

/**
 * Represents a AbstractMapper
 */
public class AbstractMapper {

    private final JsonFactory factory = new ObjectMapper().getFactory();
    protected final Logger logger;
    private final SettingProvider settingProvider;

    public AbstractMapper(Logger logger, SettingProvider settingProvider) {
        this.logger = logger;
        this.settingProvider = settingProvider;
    }

    protected JsonParser createParser(String data) throws IOException {
        return factory.createParser(data);
    }

    protected JsonParser createParser(File file) throws IOException {
        return factory.createParser(file);
    }

    protected JsonGenerator createGenerator(File file) throws IOException {
        return factory.createGenerator(file, JsonEncoding.UTF8);
    }

    protected JsonGenerator createGenerator(Writer writer) throws IOException {
        return factory.createGenerator(writer);
    }

    public void readSettings(JsonParser parser, Table<Setting, Target, byte[]> settings) throws IOException {
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

            settings.put(setting, target, value);
        }
    }

    public void writeSettings(Subject subject, JsonGenerator generator, Table<Setting, Target, Object> settings) throws IOException {
        if (settings.isEmpty()) {
            return;
        }

        generator.writeArrayFieldStart("settings");
        for (Table.Cell<Setting, Target, Object> cell : settings.cellSet()) {
            Target target = cell.getColumnKey();
            Setting<Object> setting = CastSafe.toGeneric(cell.getRowKey());
            Object value = cell.getValue();

            byte[] convert;

            try {
                convert = setting.convert(subject, target, value);
            } catch (SettingException e) {
                logger.warn("Failed to convert setting %s! Subject: %s Target: %s Value: %s", setting, subject, target, value);
                continue;
            }

            generator.writeStartObject();
            generator.writeStringField("target", target.getUUID().toString());
            generator.writeNumberField("setting", setting.getID());
            generator.writeStringField("value", Base64.encodeToString(convert, false));
            generator.writeEndObject();
        }
        generator.writeEndArray();
    }

    void validateObject(JsonParser parser) throws IOException {
        if (parser.getCurrentToken() != JsonToken.START_OBJECT) {
            throw new IOException("Expected data to start with an Object, but was " + parser.getCurrentToken());
        }
    }

    void validateArray(JsonParser parser) throws IOException {
        if (parser.getCurrentToken() != JsonToken.START_ARRAY) {
            throw new IOException("Expected data to start with an Array, but was " + parser.getCurrentToken());
        }
    }

}
