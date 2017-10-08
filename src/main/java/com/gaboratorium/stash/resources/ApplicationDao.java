package com.gaboratorium.stash.resources;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;

public interface ApplicationDao {

    @SqlQuery("select application_name from applications where id = :applicationId")
    String findById(
        @Bind("applicationId") String applicationId
    );

    @SqlUpdate("insert into applications values (:applicationId, :applicationName, :applicationDescription, :applicationSecret, :masterEmail)")
    void insert(
        @Bind("applicationId") String applicationId,
        @Bind("applicationName") String applicationName,
        @Bind("applicationDescription") String applicationDescription,
        @Bind("applicationSecret") String applicationSecret,
        @Bind("masterEmail") String masterEmail
    );
}
