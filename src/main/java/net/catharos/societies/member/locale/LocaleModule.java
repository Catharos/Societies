package net.catharos.societies.member.locale;

import net.catharos.lib.shank.AbstractModule;

import java.util.Locale;

/**
 * Represents a LocaleModule
 */
public class LocaleModule extends AbstractModule {


    private final Locale defaultLocale;

    public LocaleModule(Locale defaultLocale) {this.defaultLocale = defaultLocale;}

    @Override
    protected void configure() {
        bind(LocaleProvider.class).to(DynamicLocaleProvider.class);
        bindNamed("fallback-provider", LocaleProvider.class).to(StaticLocaleProvider.class);

        bindNamedInstance("default-locale", Locale.class, defaultLocale);
    }
}
