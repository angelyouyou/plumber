package com.hebaibai.plumber.component.dao;

import com.hebaibai.plumber.component.entity.DataBaseId;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

public interface DatabaseIdDao extends CrudRepository<DataBaseId, String>, JpaSpecificationExecutor<DataBaseId> {

}
