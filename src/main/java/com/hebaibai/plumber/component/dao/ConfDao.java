package com.hebaibai.plumber.component.dao;

import com.hebaibai.plumber.component.entity.Conf;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

public interface ConfDao extends CrudRepository<Conf, String>, JpaSpecificationExecutor<Conf> {

    Conf findByKey(String key);

}
