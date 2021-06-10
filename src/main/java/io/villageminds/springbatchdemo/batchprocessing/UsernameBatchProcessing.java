package io.villageminds.springbatchdemo.batchprocessing;

import io.villageminds.springbatchdemo.batchprocessing.domain.UserInfo;
import io.villageminds.springbatchdemo.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
public class UsernameBatchProcessing implements ItemProcessor<UserInfo, User> {

    @Override
    public User process(final UserInfo item) throws Exception {

        log.info("UserInfo that needs to be persisted in DB is, {}", item);
        return User.builder()
                .identifier(Long.parseLong(item.getIdentifier()))
                .username(item.getUsername())
                .email(item.getEmail())
                .firstName(item.getFirstName())
                .lastName(item.getLastName())
                .build();
    }
}
