package IoTDBDemo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class Computer {

    Timestamp timestamp;
    double cpu;
    double memory;
    double[] disk;

}
