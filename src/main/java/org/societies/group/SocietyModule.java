package org.societies.group;

import com.google.inject.*;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import com.typesafe.config.Config;
import gnu.trove.set.hash.THashSet;
import org.shank.AbstractModule;
import org.societies.groups.DefaultRelation;
import org.societies.groups.Relation;
import org.societies.groups.RelationFactory;
import org.societies.groups.rank.Rank;
import org.societies.groups.rank.RankFactory;
import org.societies.groups.rank.StaticRank;
import org.societies.groups.rank.memory.MemoryRank;
import org.societies.groups.validate.NameValidator;
import org.societies.groups.validate.TagValidator;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Represents a SocietyModule
 */
public class SocietyModule extends AbstractModule {

    private final Config config;

    public SocietyModule(Config config) {this.config = config;}

    @Override
    protected void configure() {
        install(new FactoryModuleBuilder()
                .implement(Rank.class, MemoryRank.class)
                .implement(Rank.class, Names.named("static"), StaticRank.class)
                .build(RankFactory.class));

        install(new FactoryModuleBuilder()
                .implement(Relation.class, DefaultRelation.class)
                .build(RelationFactory.class));

        bind(NameValidator.class).to(SimpleNameValidator.class);
        bind(TagValidator.class).to(SimpleTagValidator.class);


        install(new DefaultRankModule("default-rank", config.getString("ranks.default")));
        install(new DefaultRankModule("normal-default-rank", config.getString("ranks.normal-default")));
        install(new DefaultRankModule("super-default-rank", config.getString("ranks.super-default")));

        install(new RuleModule());
    }

    @Provides
    @Singleton
    @Named("predefined-ranks")
    public Set<Rank> provideDefaultRanks(RankFactory rankFactory) {
        THashSet<Rank> ranks = new THashSet<Rank>();

        List<? extends Config> objectList = config.getConfigList("ranks.predefined");

        for (Config object : objectList) {
            String name = object.getString("name");
            int priority = object.getInt("priority");
            UUID uuid = UUID.fromString(object.getString("uuid"));
            List<String> rules = object.getStringList("rules");

            Rank rank = rankFactory.createStatic(uuid, name, priority);

            for (String rule : rules) {
                rank.addRule(rule);
            }

            ranks.add(rank);
        }

        return Collections.unmodifiableSet(ranks);
    }

    private static interface RankSelector {

        String getName();

        boolean is(Rank rank);
    }

    private static class DefaultRankProvider implements Provider<Rank> {

        private final RankSelector selector;
        private final Set<Rank> ranks;

        @Inject
        private DefaultRankProvider(RankSelector selector, @Named("predefined-ranks") Set<Rank> ranks) {
            this.selector = selector;
            this.ranks = ranks;
        }

        @Override
        public Rank get() {
            for (Rank rank : ranks) {
                if (selector.is(rank)) {
                    return rank;
                }
            }

            throw new RuntimeException("Failed to find rank " + selector.getName() + "!");
        }
    }

    private class DefaultRankModule extends PrivateModule {

        private final String keyName;
        private final String defaultRank;

        private DefaultRankModule(String keyName, String defaultRank) {
            this.keyName = keyName;
            this.defaultRank = defaultRank;
        }

        @Override
        protected void configure() {
            Key<Rank> key = Key.get(Rank.class, Names.named(keyName));
            bind(RankSelector.class).toInstance(new RankSelector() {
                @Override
                public String getName() {
                    return defaultRank;
                }

                @Override
                public boolean is(Rank rank) {
                    return rank.getName().equals(defaultRank);
                }
            });
            bind(key).toProvider(DefaultRankProvider.class).in(Singleton.class);
            expose(key);
        }
    }
}
