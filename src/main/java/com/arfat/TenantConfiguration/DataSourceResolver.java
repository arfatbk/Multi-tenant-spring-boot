package com.arfat.TenantConfiguration;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class DataSourceResolver {

    @Autowired
    private Environment env;
    @Autowired
    TenantRepo tenantRepo;

    AbstractRoutingDataSource dataSource = new TenantAwareRoutingDataSource();
    Map<Object, Object> tenants = new HashMap<>();

    public void setDataSource(String tenant) {
        if (!tenants.containsKey(tenant)) {
            tenants.put(tenant, getDataSource(tenant));
            dataSource.setTargetDataSources(tenants);
            dataSource.afterPropertiesSet();
        }
    }

    @Bean
    public DataSource dataSource() {

        tenants.put(null, initializeTenants());
        dataSource.setTargetDataSources(tenants);
        dataSource.afterPropertiesSet();

        return dataSource;
    }

    private DataSource initializeTenants() {
        HikariDataSource dataSource = new HikariDataSource();

        try {

            dataSource.setInitializationFailTimeout(0);
            dataSource.setMaximumPoolSize(5);
            dataSource.setDataSourceClassName(env.getProperty("tenants.datasource.classname"));
            dataSource.addDataSourceProperty("url", env.getProperty("tenants.datasource.url"));
            dataSource.addDataSourceProperty("user", env.getProperty("tenants.datasource.username"));
            dataSource.addDataSourceProperty("password", env.getProperty("tenants.datasource.password"));

        } catch (Exception e) {
            throw new RuntimeException("Tenants Database configuration not found in properties\nConsider defining:\n\n" +
                    "'tenants.datasource.classname'\n" +
                    "'tenants.datasource.url'\n" +
                    "'tenants.datasource.user'\n" +
                    "'tenants.datasource.password'");
        }
        return dataSource;
    }

    private DataSource getDataSource(String tenant) {
        HikariDataSource dataSource = new HikariDataSource();
        Optional<Tenant> t = tenantRepo.findByName(tenant);
        if (t.isPresent()) {

            try {
                Tenant t1 = t.get();
                dataSource.setInitializationFailTimeout(0);
                dataSource.setMaximumPoolSize(5);
                dataSource.setDataSourceClassName(t1.getClassName());
                dataSource.addDataSourceProperty("url", t1.getUrl());
                dataSource.addDataSourceProperty("user", t1.getUserName());
                dataSource.addDataSourceProperty("password", t1.getPassword());

            } catch (Exception e) {
                throw new RuntimeException("Database configuration not found");
            }
        } else {
            throw new RuntimeException("Database configuration not found");
        }
        TenantContext.setCurrentTenant(tenant);
        return dataSource;
    }
}
