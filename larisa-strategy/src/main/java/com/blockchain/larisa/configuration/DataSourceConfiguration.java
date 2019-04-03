package com.blockchain.larisa.configuration;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({RdsConnectionProperties.class})
public class DataSourceConfiguration {

    @Autowired
    private RdsConnectionProperties rdsConnectionProperties;

    @Bean(initMethod = "init")
    public DruidDataSource dataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName(rdsConnectionProperties.getDriverName());
        dataSource.setUrl(rdsConnectionProperties.getUrl());
        dataSource.setUsername(rdsConnectionProperties.getUserName());
        dataSource.setPassword(rdsConnectionProperties.getPassword());
        dataSource.setInitialSize(rdsConnectionProperties.getInitialSize());
        dataSource.setMinIdle(rdsConnectionProperties.getMinIdle());
        dataSource.setMaxActive(rdsConnectionProperties.getMaxActive());

        return dataSource;
    }
}
