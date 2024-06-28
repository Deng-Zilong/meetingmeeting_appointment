package com.jfzt.meeting.config;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * 事务配置
 * @author zilong.deng
 * @since 2024-06-28 15:39:26
 */
@Configuration
public class TransactionConfig {

    /**
     * 配置事务管理器
     */
    @Bean
    public PlatformTransactionManager transactionManager (EntityManagerFactory emf) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(emf);
        return transactionManager;
    }

    /**
     * 配置事务模板
     **/
    @Bean
    public TransactionTemplate transactionTemplate (PlatformTransactionManager transactionManager) {
        return new TransactionTemplate(transactionManager);
    }
}
