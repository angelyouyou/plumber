package com.hebaibai.plumber.component;

import com.hebaibai.plumber.component.dao.ConfDao;
import com.hebaibai.plumber.component.dao.DatabaseIdDao;
import com.hebaibai.plumber.component.dao.TableIdDao;
import com.hebaibai.plumber.component.entity.Conf;
import com.hebaibai.plumber.component.entity.DataBaseId;
import com.hebaibai.plumber.component.entity.TableId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EntityService {

    @Autowired
    private TableIdDao tableIdDao;

    @Autowired
    private DatabaseIdDao databaseIdDao;

    @Autowired
    private ConfDao confDao;

    private static final String LOG_NAME = "LOG_NAME";
    private static final String POSITION = "POSITION";

    public String getLogName() {
        Conf conf = confDao.findByKey(LOG_NAME);
        if (conf == null) {
            return null;
        }
        return conf.getVal();
    }

    public Long getPosition() {
        Conf conf = confDao.findByKey(POSITION);
        if (conf == null) {
            return null;
        }
        return Long.valueOf(conf.getVal());
    }

    public void savePosition(long position) {
        Conf get = confDao.findById(POSITION).orElseGet(() -> {
            Conf conf = new Conf();
            conf.setId(POSITION);
            conf.setKey(POSITION);
            confDao.save(conf);
            return conf;
        });
        get.setVal(position + "");
        confDao.save(get);
    }

    public void saveLogName(String name) {
        Conf get = confDao.findById(LOG_NAME).orElseGet(() -> {
            Conf conf = new Conf();
            conf.setId(LOG_NAME);
            conf.setKey(LOG_NAME);
            confDao.save(conf);
            return conf;
        });
        get.setVal(name);
        confDao.save(get);
    }

    public void seveTableName(long id, String tableName) {
        TableId get = tableIdDao.findById(id + "").orElseGet(() -> {
            TableId tableId = new TableId();
            tableId.setId(id + "");
            tableIdDao.save(tableId);
            return tableId;
        });
        get.setTableName(tableName);
        tableIdDao.save(get);
    }

    public void seveDatabaseName(long id, String databaseName) {
        DataBaseId get = databaseIdDao.findById(id + "").orElseGet(() -> {
            DataBaseId dataBaseId = new DataBaseId();
            dataBaseId.setId(id + "");
            databaseIdDao.save(dataBaseId);
            return dataBaseId;
        });
        get.setDatabaseName(databaseName);
        databaseIdDao.save(get);
    }

    public String getTableName(Long id) {
        if (id == null) {
            return null;
        }
        return tableIdDao.findById(id + "")
                .flatMap(tableId1 -> Optional.of(tableId1.getTableName()))
                .orElse(null);
    }

    public String getDatabaseName(Long id) {
        if (id == null) {
            return null;
        }
        return databaseIdDao.findById(id + "")
                .flatMap(dataBaseId -> Optional.of(dataBaseId.getDatabaseName()))
                .orElse(null);
    }
}
