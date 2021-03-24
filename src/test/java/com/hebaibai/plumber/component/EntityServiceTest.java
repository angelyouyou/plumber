package com.hebaibai.plumber.component;

import com.hebaibai.plumber.PlumberMain;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.alter.Alter;
import net.sf.jsqlparser.statement.alter.AlterExpression;
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

    @Test
    public void name1() throws JSQLParserException {

        Statement statement = CCJSqlParserUtil.parse("/* dfsdf sdfsdf */ ALTER TABLE account_statement MODIFY COLUMN agreement_name varchar(225) CHARACTER SET utf8 COLLATE utf8_bin NULL COMMENT '1';");
        if (statement instanceof Alter) {
            Alter alter = (Alter) statement;
            Table table = alter.getTable();
            table.setSchemaName("sads");
            table.setName("asdasdasdasd");
            System.out.println(alter.toString());

            for (AlterExpression alterExpression : alter.getAlterExpressions()) {
                System.out.println(alterExpression);
            }
        }

    }
}