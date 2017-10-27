package com.gaboratorium.stash;

import com.gaboratorium.stash.modules.appAuthenticator.appAuthenticationRequired.AppAuthenticationRequiredFilter;
import com.gaboratorium.stash.modules.appAuthenticator.AppTokenStore;
import com.gaboratorium.stash.resources.apps.dao.AppDao;
import com.gaboratorium.stash.resources.apps.AppResource;
import io.dropwizard.Application;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.db.ManagedDataSource;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import liquibase.Liquibase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.skife.jdbi.v2.DBI;

import java.sql.Connection;
import java.sql.SQLException;

public class StashApplication extends Application<StashConfiguration> {
    public static void main(String[] args) throws Exception {
        new StashApplication().run(args);
    }

    @Override
    public void initialize(Bootstrap<StashConfiguration> bootstrap) {
        // Bootstrap
    }

    @Override
    public void run(StashConfiguration configuration, Environment environment) throws Exception {

        // Database
        final DataSourceFactory dataSourceFactory = configuration.getDatabase();
        final DBI dbi = getDBI(environment, dataSourceFactory);

        // Modules
        final AppTokenStore appTokenStore = new AppTokenStore();

        // Dao
        final AppDao appDao = dbi.onDemand(AppDao.class);

        // Resource
        final AppResource appResource = new AppResource(
            appDao,
            appTokenStore
        );

        // Run Migrations
        runDatabaseMigrations(environment, dataSourceFactory);

        // Module registrations
        environment.jersey().register(AppAuthenticationRequiredFilter.class);
        environment.jersey().register(appResource);
    }

    private void runDatabaseMigrations(Environment environment, DataSourceFactory database) throws LiquibaseException, SQLException {
        final ManagedDataSource ds = database.build(environment.metrics(), "migration");
        final Connection connection = ds.getConnection();
        final Liquibase migrator = new Liquibase(
            "migrations.xml",
            new ClassLoaderResourceAccessor(),
            new JdbcConnection(connection)
        );

        migrator.update("Custom contexts");
    }

    private DBI getDBI (Environment environment, DataSourceFactory database) {
        return new DBIFactory().build(environment, database, "postgresql");
    }
}
