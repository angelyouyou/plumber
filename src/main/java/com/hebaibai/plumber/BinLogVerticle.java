package com.hebaibai.plumber;

import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.hebaibai.plumber.component.EntityService;
import com.hebaibai.plumber.core.utils.DataBaseComponent;
import io.vertx.core.AbstractVerticle;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class BinLogVerticle extends AbstractVerticle {

    public static final long DELAY = 10 * 1000;
    @Autowired
    private BinaryLogClient binaryLogClient;
    @Autowired
    private EntityService entityService;
    @Autowired
    private DataBaseComponent dataBaseComponent;

    @Override
    public void start() throws Exception {
        // 启动 binaryLogClient
        Thread thread = new Thread(() -> {
            try {
                log.info(Style.style(Style.FontColor.YELLOW).str("binaryLogClient connect ..."));
                binaryLogClient.connect();
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(0);
            }
        });
        thread.setName("sync-data");
        thread.start();
    }

    @Override
    public void stop() throws Exception {
        binaryLogClient.disconnect();
    }

}