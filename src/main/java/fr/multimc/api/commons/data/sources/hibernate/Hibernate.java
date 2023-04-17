package fr.multimc.api.commons.data.sources.hibernate;

import fr.multimc.api.commons.data.sources.DataSourceType;
import fr.multimc.api.commons.data.sources.IDataSource;
import org.hibernate.SessionFactory;

public class Hibernate implements IDataSource {

    private final SessionFactory sessionFactory;

    public Hibernate(final SessionFactory sessionFactory){
        this.sessionFactory = sessionFactory;
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    @Override
    public DataSourceType getDataSourceType() {
        return DataSourceType.DATABASE;
    }
}
