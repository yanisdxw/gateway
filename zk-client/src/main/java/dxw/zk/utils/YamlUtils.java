package dxw.zk.utils;

import org.yaml.snakeyaml.Yaml;
import java.io.*;
import java.util.Map;

public class YamlUtils {

    /**
     * 获取yml文件中的指定字段,返回一个map
     *
     * @param sourcename
     * @return
     */
    public static Map<String, Object> getResMap(String sourcename) {
        System.out.println("开始获取source");
        return (Map<String, Object>) YmlInit.ymlMap.get(sourcename);
    }

    // 配置文件仅需要读取一次,读取配置文件的同时把数据保存到map中,map定义为final,仅可以被赋值一次
    private static class YmlInit {
        //初始化文件得到的map
        private static final Map<String, Object> ymlMap = getYml();

        // 读取配置文件,并初始化ymlMap
        private static Map<String, Object> getYml() {
            Yaml yml = new Yaml();
            String path = Object.class.getResource("/").getPath().substring(1) + "application.yml";
            System.out.println("path:"+path);
            Reader reader = null;
            try {
                reader = new FileReader(new File(path));
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }
            return yml.loadAs(reader, Map.class);
        }

    }
}
