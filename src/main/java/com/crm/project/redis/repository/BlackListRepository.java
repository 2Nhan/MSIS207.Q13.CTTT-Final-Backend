package com.crm.project.redis.repository;

import com.crm.project.redis.redishash.LogoutToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlackListRepository extends CrudRepository<LogoutToken, String> {
}
