package IoTDBDemo.service;

import com.sun.management.OperatingSystemMXBean;
import java.io.File;
import java.lang.management.ManagementFactory;

/**
 * 获取windows系统信息（CPU,内存,文件系统）
 * @author libing
 *
 */

public class WindowsInfoUtil {
    
    public static final String[] DISK_NAME ={"C","D","E"};

    private static OperatingSystemMXBean osmxb = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

    public static void main(String[] args) throws Exception {

        for(int j=0;j<10;j++){
            System.out.println("CPU使用效率："+(int)(getCpuRatioForWindows()*100)+"%");

            System.out.println("内存已使用:"+ (int) (getMemery()*100) +"%");

            System.out.println("磁盘使用情况：");
            double[] diskUsage=getDisk();
            for(int i = 0; i< DISK_NAME.length; i++){
                System.out.println(DISK_NAME[i]+": "+(diskUsage[i]*100)+"%\t");
            }
            System.out.println("===================");
            Thread.sleep(1000);
        }



    }

    //获得cpu使用率
    public static double getCpuRatioForWindows() {
        return osmxb.getSystemCpuLoad();
    }

    //获取内存使用率
    public static double getMemery(){
        // 总的物理内存+虚拟内存
        long totalvirtualMemory = osmxb.getTotalSwapSpaceSize();
        // 剩余的物理内存
        long freePhysicalMemorySize = osmxb.getFreePhysicalMemorySize();
        return 1-freePhysicalMemorySize*1.0/totalvirtualMemory;
    }

    //获取文件系统使用率
    public static double[] getDisk() {
        double[] res=new double[DISK_NAME.length];
        for(int i = 0; i< DISK_NAME.length; i++){
            String dirName= DISK_NAME[i]+":/";
            File win = new File(dirName);
            if(win.exists()){
                long total=win.getTotalSpace();
                long free=win.getFreeSpace();
                double compare=1-free*1.0/total;
                res[i]=compare;
            }
        }
        return res;
    }


}
