package io.villageminds.springbatchdemo.batchprocessing.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserInfo {

    private String username;
    private String email;
    private String identifier;
    private String firstName;
    private String lastName;

}
