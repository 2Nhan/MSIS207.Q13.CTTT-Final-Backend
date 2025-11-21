package com.crm.project.redis.redishash;

import org.springframework.data.annotation.Id;
import lombok.*;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@RedisHash("jwt_blacklist")
public class LogoutToken {
    @Id
    private String jit;

    @TimeToLive
    private Long ttl;
}
