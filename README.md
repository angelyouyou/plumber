# plumber

数据实时同步工具，使用 vert.x 异步框架，效率高

### 原理

将自己伪装成 mysql slave，获取mysql推送的binlog信息，通过字段映射，处理成为新的sql并向目标库执行,从而完成数据实时同步。

### 使用条件

mysql开启binlog记录, 格式为ROW, mysql版本在5.7及以上

### 安装启动说明

1. git clone https://github.com/hjx601496320/plumber.git
2. cd plumber/
3. mvn package
4. cd target/
5. 添加配置文件 plumber.json 到 plumber-1.0-SNAPSHOT.jar同级目录
6. 执行 java -jar plumber-1.0-SNAPSHOT.jar

### 配置文件说明

```json
//配置文件 plumber.json。将文件放在jar对应的user.dir目录下
{
  //数据来源
  "dataSource": {
    "host": "127.0.0.1",
    "port": 3306,
    "username": "root",
    "password": "00000"
  },
  //数据目标
  "dataTarget": {
    "host": "127.0.0.1",
    "port": 3306,
    "username": "root",
    "password": "00000"
  },
  //执行器 执行sql语句
  "executer": [
    "MysqlEventExecuter"
  ],
  //同步任务配置
  "tableSyncJob": [
    {
      //字段转换配置 key:来源表字段名, value:目标表字段名称
      "mapping": {
        "id": "id",
        "log_type": "log_type",
        "content": "content",
        "create_date": "create_date",
        "mark": "mark",
        "param0": "param0",
        "param1": "param1",
        "param2": "param2",
        "param3": "param3"
      },
      //数据来源表 主键/唯一值 字段名称
      "primaryKey": "id",
      //目标库.表
      "target": "db_full.log",
      //来源库.表
      "source": "db_3.log"
    }
  ]
}
```

### 注意

- 在非UTF-8环境下，同步数据中文会乱码。

