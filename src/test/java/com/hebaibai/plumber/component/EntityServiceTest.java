package com.hebaibai.plumber.component;

import com.hebaibai.plumber.PlumberMain;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = PlumberMain.class)
public class EntityServiceTest {

    @Autowired
    private EntityService entityService;

    @Test
    public void name() {
        entityService.seveTableName(12312L, "何嘉旋2");
        String tableName = entityService.getTableName(12312L);
        System.out.println(tableName);
    }
}