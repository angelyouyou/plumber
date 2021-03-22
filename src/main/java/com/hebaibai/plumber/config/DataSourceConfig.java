package com.hebaibai.plumber.config;

import lombok.Data;

@Data
public class DataSourceConfig {

    private String host;

    private int port;

    private String username;

    private String password;

}