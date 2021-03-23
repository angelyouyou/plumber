package com.hebaibai.plumber;

import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.github.shyiko.mysql.binlog.event.deserialization.EventDeserializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hebaibai.plumber.component.EntityService;
import com.hebaibai.plumber.config.DataSourceConfig;
import com.hebaibai.plumber.config.SyncDataConfig;
import com.hebaibai.plumber.config.TableSyncJob;
import com.hebaibai.plumber.core.BinlogEventListener;
import com.hebaibai.plumber.core.utils.DataBaseComponent;
import com.hebaibai.plumber.core.utils.TableMateData;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.vertx.core.Vertx;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.PoolOptions;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.boot.SpringApplication.run;

@Slf4j
@EnableScheduling
@SpringBootApplication
@EnableJpaRepositories(basePackages = {
        "com.hebaibai.plumber.component.dao"
})
@EnableTransactionManagement
public class PlumberMain {

    /**
     * Vertx 实例
     */
    public static final Vertx VERTX = Vertx.vertx();
    /**
     * 配置文件地址
     */
    public static final String CONFIG_FILE_PATH = System.getProperty("user.dir") + File.separator + "plumber.json";
    /**
     * 配置文件
     */
    public static final SyncDataConfig CONFIG = config();

    /**
     * 数据来源连接池
     */
    public static final DataSource DATA_SOURCE = dataSource(CONFIG.getDataSource());

    /**
     * 数据目标连接池
     */
    public static final DataSource DATA_TARGET = dataSource(CONFIG.getDataTarget());

    /**
     * 获取DataSource
     *
     * @param config
     * @return
     */
    public static DataSource dataSource(DataSourceConfig config) {
        String jdbcUrl = "jdbc:mysql://" + config.getHost() + ":" + config.getPort() + "?characterEncoding=utf-8&useSSL=true";
        log.info("dataSource: {}", jdbcUrl);
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName("com.mysql.jdbc.Driver");
        hikariConfig.setJdbcUrl(jdbcUrl);
        hikariConfig.setUsername(config.getUsername());
        hikariConfig.setPassword(config.getPassword());
        hikariConfig.setMaximumPoolSize(Runtime.getRuntime().availableProcessors());
        DataSource dataSource = new HikariDataSource(hikariConfig);
        return dataSource;
    }

    /**
     * 主启动类
     *
     * @param args
     */
    public static void main(String[] args) {
        ApplicationContext applicationContext = run(PlumberMain.class, args);
        BinLogVerticle binLogVerticle = applicationContext.getBean(BinLogVerticle.class);
        VERTX.deployVerticle(binLogVerticle);
    }

    @SneakyThrows
    private static SyncDataConfig config() {
        File file = new File(CONFIG_FILE_PATH);
        FileInputStream inputStream = new FileInputStream(file);
        byte[] bytes = new byte[inputStream.available()];
        inputStream.read(bytes);
        String configJson = new String(bytes, "utf-8");
        Gson gson = new Gson();
        return gson.fromJson(configJson, SyncDataConfig.class);
    }

    @Bean
    public BinaryLogClient binaryLogClient(
            @Autowired BinlogEventListener binlogEventListener,
            @Autowired EntityService entityService
    ) {
        DataSourceConfig dataSource = CONFIG.getDataSource();
        BinaryLogClient client = new BinaryLogClient(
                dataSource.getHost(),
                dataSource.getPort(),
                dataSource.getUsername(),
                dataSource.getPassword()
        );
        EventDeserializer eventDeserializer = new EventDeserializer();
        eventDeserializer.setCompatibilityMode(
                EventDeserializer.CompatibilityMode.CHAR_AND_BINARY_AS_BYTE_ARRAY
        );
        client.setEventDeserializer(eventDeserializer);
        client.registerEventListener(binlogEventListener);
        //binlog 设置
        if (CONFIG.getLogName() != null) {
            client.setBinlogFilename(CONFIG.getLogName());
        }
        if (CONFIG.getPosition() != null) {
            client.setBinlogPosition(CONFIG.getPosition());
        }
        String logName = entityService.getLogName();
        if (logName != null) {
            client.setBinlogFilename(logName);
        }
        Long position = entityService.getPosition();
        if (position != null) {
            client.setBinlogPosition(position);
        }
        return client;
    }

    @Bean
    public MySQLPool mySQLPool() {
        DataSourceConfig dataTarget = CONFIG.getDataTarget();
        MySQLConnectOptions connectOptions = new MySQLConnectOptions()
                .setCharset("utf8mb4")
                .setPort(dataTarget.getPort())
                .setHost(dataTarget.getHost())
                .setUser(dataTarget.getUsername())
                .setPassword(dataTarget.getPassword());
        PoolOptions poolOptions = new PoolOptions().setMaxSize(Runtime.getRuntime().availableProcessors() * 2);
        return MySQLPool.pool(VERTX, connectOptions, poolOptions);
    }

    @Bean("tableMateDataMap")
    public Map<String, TableMateData> tableMateDataMap(
            @Autowired DataBaseComponent dataBaseComponent
    ) throws Exception {
        Map<String, TableMateData> tableMateDataMap = new HashMap<>();
        GsonBuilder builder = new GsonBuilder().setPrettyPrinting();
        for (TableSyncJob tableSyncJob : CONFIG.getTableSyncJob()) {
            String data = tableSyncJob.getSource();
            String[] split = data.split("\\.");
            TableMateData tableMateData = dataBaseComponent.tableMateData(DATA_SOURCE, split[0], split[1]);
            if (tableSyncJob.getMapping() == null || tableSyncJob.getMapping().size() == 0) {
                Map<String, String> map = new HashMap<>();
                for (String column : tableMateData.getColumns()) {
                    map.put(column, column);
                }
                tableSyncJob.setMapping(map);
            }
            if (StringUtils.isBlank(tableSyncJob.getPrimaryKey())) {
                String[] ids = tableMateData.getIds();
                if (ids.length == 0) {
                    log.error(Style.error().str("{} must have a primary key"), data);
                    System.exit(0);
                }
                tableSyncJob.setPrimaryKey(ids[0]);
            }
            tableMateDataMap.put(data, tableMateData);
            log.info(Style.style(Style.FontColor.YELLOW).str(builder.create().toJson(tableSyncJob)));
        }
        return tableMateDataMap;
    }

}
