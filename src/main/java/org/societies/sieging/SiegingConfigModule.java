package org.societies.sieging;

import com.typesafe.config.Config;
import org.joda.time.Duration;
import org.societies.AbstractConfigModule;

import java.util.concurrent.TimeUnit;

/**
 * Represents a ConfigModule
 */
class SiegingConfigModule extends AbstractConfigModule {


    public SiegingConfigModule(Config config) {
        super(config);
    }

    @Override
    protected void configure() {
        bindNamed("sieging.min-distance", "city.sieging.min-distance", double.class);
        bindNamed("sieging.start-duration", Duration.class).toInstance(new Duration(config
                .getDuration("city.sieging.start-duration", TimeUnit.MILLISECONDS)));

        bindNamed("city.min-distance", "city.min-distance", double.class);
        bindNamed("city.start-lands", "city.start-lands", int.class);
    }
}
