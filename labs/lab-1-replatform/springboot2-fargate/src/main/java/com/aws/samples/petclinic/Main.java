package com.aws.samples.petclinic;

import org.socialsignin.spring.data.dynamodb.repository.config.EnableDynamoDBRepositories;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import com.aws.samples.petclinic.config.AWSConfig;
import com.aws.samples.petclinic.pet.PetRepository;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
@EnableDynamoDBRepositories(basePackageClasses = PetRepository.class)
@Configuration
@Import({AWSConfig.class})
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, "--spring.main.allow-bean-definition-overriding=true");
    }
}
