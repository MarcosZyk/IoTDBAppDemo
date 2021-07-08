package IoTDBDemo.service;

import IoTDBDemo.model.Computer;
import IoTDBDemo.service.linechart.LineChartUtil;
import IoTDBDemo.service.linechart.Serie;
import org.apache.iotdb.jdbc.IoTDBSQLException;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;

@Service
public class MonitorService {

    private Connection connection;

    public static void main(String[] args) throws Exception{
        MonitorService.startMonitor();
        MonitorService.checkQuery();
    }

    public static void startMonitor() throws Exception{
        MonitorService monitorService=new MonitorService();
        Thread thread=new Thread(() -> {
            try {
                monitorService.monit();
            }catch (Exception e){
                e.printStackTrace();
            }
        });
        thread.start();
    }

    public static void checkQuery() throws Exception{
        MonitorService monitorService=new MonitorService();
        Thread thread1=new Thread(()->{
            try {
                long start=0, end=System.currentTimeMillis();
                List<String> categories = new ArrayList<>();
                categories.add("0");
                List<Serie> series = new Vector<>();
                Vector<Object> data=new Vector<>();
                data.add(0.0);
                series.add(new Serie("CPU",data));
                LineChartUtil util=new LineChartUtil(categories,series);

                while (true){
                    start=end;
                    end=System.currentTimeMillis();
                    List<Computer> res=monitorService.query(start,end);
                    for(Computer computer:res){
                        System.out.println(computer);
                    }
                    categories.addAll(res.stream().map(c->c.getTimestamp().toString()).collect(Collectors.toList()));
                    series.get(0).getData().addAll(res.stream().map(Computer::getCpu).collect(Collectors.toList()));
                    util.update(categories,series);
                    Thread.sleep(2000);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        });
        thread1.start();
    }

    public MonitorService() throws Exception {
        Class.forName("org.apache.iotdb.jdbc.IoTDBDriver");
        connection =
                DriverManager.getConnection("jdbc:iotdb://127.0.0.1:6667/", "root", "root");

    }

    public void monit() throws Exception {

        try {
            Statement statement;
            statement = connection.createStatement();
            // set JDBC fetchSize
            statement.setFetchSize(10000);
            try {
                statement.execute("SET STORAGE GROUP TO root.sg1");
                statement.execute(
                        "CREATE TIMESERIES root.sg1.d1.cpu WITH DATATYPE=DOUBLE, ENCODING=RLE, COMPRESSOR=SNAPPY");
                statement.execute(
                        "CREATE TIMESERIES root.sg1.d1.memory WITH DATATYPE=DOUBLE, ENCODING=RLE, COMPRESSOR=SNAPPY");
                for (String diskName : WindowsInfoUtil.DISK_NAME) {
                    statement.execute("CREATE TIMESERIES root.sg1.d1.disk." + diskName + " WITH DATATYPE=DOUBLE, ENCODING=RLE, COMPRESSOR=SNAPPY");
                }
            } catch (IoTDBSQLException e) {
                System.out.println(e.getMessage());
            }


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
        } catch (IoTDBSQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public List<Computer> query(long start, long end) throws Exception {

        List<Computer> res=new ArrayList<>();

        Timestamp timestamp;
        double cpu;
        double memory;
        double[] disk;

        Statement statement;
        statement = connection.createStatement();
        // set JDBC fetchSize
        statement.setFetchSize(10000);
        ResultSet resultSet = statement.executeQuery("select * from root where " +
                "time >= "+start+"  and time <= "+end);
//        outputResult(resultSet);
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
        statement.close();
        return res;
    }

    private static void outputResult(ResultSet resultSet) throws SQLException {
        if (resultSet != null) {
            System.out.println("--------------------------");
            final ResultSetMetaData metaData = resultSet.getMetaData();
            final int columnCount = metaData.getColumnCount();
            for (int i = 0; i < columnCount; i++) {
                System.out.print(metaData.getColumnLabel(i + 1) + " | ");
            }
            System.out.println();
            while (resultSet.next()) {
                for (int i = 1; ; i++) {
                    System.out.print(resultSet.getString(i));
                    if (i < columnCount) {
                        System.out.print(", ");
                    } else {
                        System.out.println();
                        break;
                    }
                }
            }
            System.out.println("--------------------------\n");
        }
    }

}
