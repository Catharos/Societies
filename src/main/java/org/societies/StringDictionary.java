package org.societies;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import net.catharos.lib.core.i18n.DefaultDictionary;
import org.societies.bridge.ChatColor;

import java.util.Locale;

/**
 * Represents a StringDictionary
 */
public class StringDictionary extends DefaultDictionary<String> {

    private final String defaultColor;

    @Inject
    public StringDictionary(@Named("default-locale") Locale defaultLocale, @Named("default-color") String defaultColor) {
        super(defaultLocale);
        this.defaultColor = defaultColor;
    }

    @Override
    public String getTranslation(String key, String locale, Object... args) {
        translateArguments(args);
        return defaultColor + super.getTranslation(key, locale, args);
    }

    private void translateArguments(Object... args) {
        for (int i = 0, length = args.length; i < length; i++) {
            Object arg = args[i];

            if (arg instanceof String) {
                if (((String) arg).startsWith(":")) {
                    args[i] = getTranslation(((String) arg).substring(1));
                }

                args[i] = ChatColor.RESET.toString() + args[i] + defaultColor;
            }
        }
    }
}
