package IoTDBDemo.dao;

import org.apache.iotdb.jdbc.IoTDBSQLException;

import java.sql.*;

public class IoTDBJDBC {

    private static Connection connection;

    static {
        try {
            Class.forName("org.apache.iotdb.jdbc.IoTDBDriver");
            connection =
                    DriverManager.getConnection("jdbc:iotdb://127.0.0.1:6667/", "root", "root");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) throws ClassNotFoundException, SQLException {

        try (Statement statement = connection.createStatement()) {

            // set JDBC fetchSize
            statement.setFetchSize(10000);

            try {
                statement.execute("SET STORAGE GROUP TO root.sg1");
                statement.execute(
                        "CREATE TIMESERIES root.sg1.d1.s1 WITH DATATYPE=INT64, ENCODING=RLE, COMPRESSOR=SNAPPY");
                statement.execute(
                        "CREATE TIMESERIES root.sg1.d1.s2 WITH DATATYPE=INT64, ENCODING=RLE, COMPRESSOR=SNAPPY");
                statement.execute(
                        "CREATE TIMESERIES root.sg1.d1.s3 WITH DATATYPE=INT64, ENCODING=RLE, COMPRESSOR=SNAPPY");
            } catch (IoTDBSQLException e) {
                System.out.println(e.getMessage());
            }

            for (int i = 0; i <= 100; i++) {
                statement.addBatch(prepareInsertStatment(i));
            }
            statement.executeBatch();
            statement.clearBatch();

            ResultSet resultSet = statement.executeQuery("select * from root");
            outputResult(resultSet);
            resultSet = statement.executeQuery("select count(*) from root");
            outputResult(resultSet);
            resultSet =
                    statement.executeQuery(
                            "select count(*) from root where time >= 1 and time <= 100 group by ([0, 100), 20ms, 20ms)");
            outputResult(resultSet);
        } catch (IoTDBSQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void outputResult(ResultSet resultSet) throws SQLException {
        if (resultSet != null) {
            System.out.println("--------------------------");
            final ResultSetMetaData metaData = resultSet.getMetaData();
            final int columnCount = metaData.getColumnCount();
            for (int i = 0; i < columnCount; i++) {
                System.out.print(metaData.getColumnLabel(i + 1) + " ");
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

    private static String prepareInsertStatment(int time) {
        return "insert into root.sg1.d1(timestamp, s1, s2, s3) values("
                + time
                + ","
                + 1
                + ","
                + 1
                + ","
                + 1
                + ")";
    }

}
