package org.societies.database.sql;

import com.google.inject.Inject;
import com.typesafe.config.Config;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.jooq.Query;
import org.shank.logging.InjectLogger;
import org.shank.service.AbstractService;
import org.shank.service.lifecycle.LifecycleContext;
import org.societies.database.Database;

import java.util.concurrent.TimeUnit;

/**
 * Represents a CleanupPublisher
 */
class CleanupService extends AbstractService {

    private final long memberMillis;
    private final CleanupQueries queries;
    private final Database database;

    @InjectLogger
    private Logger logger;

    @Inject
    public CleanupService(CleanupQueries queries, Database database, Config config) {
        this.queries = queries;
        this.database = database;
        this.memberMillis = config.getDuration("purge.inactive-members", TimeUnit.MILLISECONDS);
    }

    @Override
    public void init(LifecycleContext context) throws Exception {
        long current = System.currentTimeMillis();
        Query query;

        query = queries.getQuery(CleanupQueries.DROP_INACTIVE_MEMBERS);
        query.bind(1, new DateTime(current - memberMillis));
        int members = query.execute();
        logger.info("Dropped {0} members because of inactivity.", members);


        query = queries.getQuery(CleanupQueries.DROP_ORPHAN_SOCIETIES);
        int societies = query.execute();
        logger.info("Dropped {0} societies because of inactivity.", societies);

        // Delete rank orphans
        query = queries.getQuery(CleanupQueries.DROP_RANK_ORPHANS);
        int ranks = query.execute();
        logger.info("Dropped {0} ranks because of inactivity.", ranks);
    }

    @Override
    public void stop(LifecycleContext context) throws Exception {
        database.close();
    }
}
