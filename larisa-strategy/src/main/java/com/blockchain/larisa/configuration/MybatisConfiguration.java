package com.blockchain.larisa.configuration;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;


@Configuration
@AutoConfigureAfter(DataSourceConfiguration.class)
public class MybatisConfiguration {

    @Bean
    public SqlSessionFactory sqlSessionFactory(DruidDataSource dataSource) {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(dataSource);
        try {
            ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resource = resolver.getResources("classpath:mapper/*.xml");
            factoryBean.setMapperLocations(resource);

            return factoryBean.getObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
