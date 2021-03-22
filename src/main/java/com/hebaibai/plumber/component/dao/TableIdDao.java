package com.hebaibai.plumber.component.dao;

import com.hebaibai.plumber.component.entity.TableId;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

public interface TableIdDao extends CrudRepository<TableId, String>, JpaSpecificationExecutor<TableId> {

}
