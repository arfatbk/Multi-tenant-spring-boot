package com.arfat.Repo;

import com.arfat.TenantConfiguration.DataSourceResolver;
import com.arfat.TenantConfiguration.TenantContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ControllerEndPoint {

    @Autowired
    private TestRepo repo;
    @Autowired
    DataSourceResolver dataSourceResolver;

    @GetMapping("/{tenant}")
    private List<Test> get1(@PathVariable("tenant") String tenant) {

        dataSourceResolver.setDataSource(tenant);

        System.out.println(" current tenant = " + TenantContext.getCurrentTenant());

        List<Test> all = repo.findAll();
        System.out.println(all);
        return all;
    }
}
