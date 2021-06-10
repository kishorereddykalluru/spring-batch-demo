package io.villageminds.springbatchdemo.config;

import io.villageminds.springbatchdemo.batchprocessing.UsernameBatchProcessing;
import io.villageminds.springbatchdemo.batchprocessing.domain.UserInfo;
import io.villageminds.springbatchdemo.entity.User;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    public static final String[] FIELDNAMES = {"username", "email", "identifier", "firstName", "lastName"};
    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    /**
     * Need to perform three steps here
     * 1. reader
     * 2. processor
     * 3. writer
     */
    @Bean
    public FlatFileItemReader<UserInfo> reader(){
        return new FlatFileItemReaderBuilder<UserInfo>()
                .name("userItemReader")
                .resource(new ClassPathResource("username-email.csv"))
                .delimited()
                .names(FIELDNAMES)
                .fieldSetMapper(new BeanWrapperFieldSetMapper<UserInfo>(){{
                    setTargetType(UserInfo.class);
                }})
                .build();
    }

    @Bean
    public UsernameBatchProcessing processor(){
        return new UsernameBatchProcessing();
    }


    @Bean
    public JdbcBatchItemWriter<User> writer(DataSource dataSource){
        return new JdbcBatchItemWriterBuilder<User>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO user(identifier, username, email, first_name, last_name)" +
                        "VALUES (:identifier, :username, :email, :firstName, :lastName)")
                .dataSource(dataSource)
                .build();
    }

    @Bean
    public Job importUserJob(JobCompletionNotificationListener listener, Step step1) {
        return jobBuilderFactory.get("importUserJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(step1)
                .end()
                .build();
    }

    @Bean
    public Step step1(JdbcBatchItemWriter<User> writer) {
        return stepBuilderFactory.get("step1")
                .<UserInfo, User> chunk(10)
                .reader(reader())
                .processor(processor())
                .writer(writer)
                .build();
    }


}
