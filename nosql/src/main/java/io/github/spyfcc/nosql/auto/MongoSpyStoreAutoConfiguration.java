package io.github.spyfcc.nosql.auto;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;

import io.github.spyfcc.core.store.SpyStore;
import io.github.spyfcc.nosql.store.MongoStore;

@AutoConfiguration
@ConditionalOnClass(name = "org.springframework.data.mongodb.core.MongoTemplate")
@ConditionalOnProperty(name = "traffic.spy.storage.type", havingValue = "nosql")
public class MongoSpyStoreAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(SpyStore.class)
    @ConditionalOnProperty(
            name = "traffic.spy.nosql.provider",
            havingValue = "mongo",
            matchIfMissing = true
    )
    public SpyStore mongoSpyStore(ObjectProvider<MongoTemplate> mongoTemplateProvider) {
        MongoTemplate mongoTemplate = mongoTemplateProvider.getIfAvailable();

        if (mongoTemplate == null) {
            throw new IllegalStateException(
                    "traffic.spy.storage.type=nosql and traffic.spy.nosql.provider=mongo selected, " +
                    "but MongoTemplate bean not found. Please add spring-boot-starter-data-mongodb."
            );
        }

        return new MongoStore(mongoTemplate);
    }
}