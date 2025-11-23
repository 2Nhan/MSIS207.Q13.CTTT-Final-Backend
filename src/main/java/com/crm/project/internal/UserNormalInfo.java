package com.crm.project.internal;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserNormalInfo {
    private String id;

    private String firstName;

    private String lastName;

    private String email;
}
