# IoTDB应用Demo

### 概述

本项目为使用IoTDB开发的简易应用Demo。

主要功能为检测Windows电脑的CPU、内存以及磁盘的使用效率。

主要功能代码在src/main/java/iotdbdemo/service目录下。

运行MonitorService类中的main方法可进行数据插入和监控。

运行后，命令行将输出查询的数据，同时会展示一个Java Swing界面展示CPU最新数据的动态折现图。

### 核心代码

监控的数据通过WindowsInfoUtil类中的相应方法进行采集，使用了JDK自带的OperatingSystemMXBean相关的API。

MonitorService启动了一个数据插入线程和一个数据查询线程，均使用JDBC与IoTDB进行交互。UI展示使用 jfreechart 进行实现。

数据插入线程通过以下代码进行存储组设置和测点创建

```java
statement.execute("SET STORAGE GROUP TO root.sg1");
statement.execute("CREATE TIMESERIES root.sg1.d1.cpu "
                  + "WITH DATATYPE=DOUBLE, ENCODING=RLE,COMPRESSOR=SNAPPY");
statement.execute("CREATE TIMESERIES root.sg1.d1.memory "
                  + "WITH DATATYPE=DOUBLE, ENCODING=RLE, COMPRESSOR=SNAPPY");
for (String diskName : WindowsInfoUtil.DISK_NAME) {
    statement.execute("CREATE TIMESERIES root.sg1.d1.disk." + diskName 
                      + " WITH DATATYPE=DOUBLE, ENCODING=RLE, COMPRESSOR=SNAPPY");
}
```

数据插入线程通过以下代码进行数据插入

```java
while (true) {
    long timestamp = System.currentTimeMillis();
    statement.addBatch("insert into root.sg1.d1(timestamp, cpu, memory) values("
                        + timestamp + ","
                        + WindowsInfoUtil.getCpuRatioForWindows() + ","
                        + WindowsInfoUtil.getMemery()
                        + ")");
    double[] disk = WindowsInfoUtil.getDisk();
    statement.addBatch("insert into root.sg1.d1.disk(timestamp, C, D, E) values("
                        + timestamp + "," + disk[0] + "," + disk[1] + "," + disk[2] + ")");
    statement.executeBatch();
    statement.clearBatch();
    Thread.sleep(1000);
}
```

数据查询线程通过以下进行最新监控数据的查询和获取

```java
ResultSet resultSet = statement.executeQuery("select * from root where " +
                "time >= "+start+"  and time <= "+end);
if (resultSet != null) {
    while (resultSet.next()) {
        timestamp=resultSet.getTimestamp("Time");
        cpu=resultSet.getDouble("root.sg1.d1.cpu");
        memory=resultSet.getDouble("root.sg1.d1.memory");
        disk=new double[3];
        disk[0]=resultSet.getDouble("root.sg1.d1.disk.C");
        disk[1]=resultSet.getDouble("root.sg1.d1.disk.D");
        disk[2]=resultSet.getDouble("root.sg1.d1.disk.E");
        res.add(new Computer(timestamp,cpu,memory,disk));
    }
}
```

