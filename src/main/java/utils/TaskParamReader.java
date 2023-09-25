package utils;

import core.entity.analysis.TaskParam;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class TaskParamReader {

    public static TaskParam taskParam;

    public static TaskParam getTaskParam() {
        if (taskParam != null) {
            return taskParam;
        }
        taskParam = buildTaskParam();
        return taskParam;
    }

    private static TaskParam buildTaskParam() {
        InputStream in = ClassLoader.getSystemResourceAsStream("task.properties");
        Properties properties = new Properties();
        try {
            properties.load(in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String vulKey = properties.getProperty("vulKey").trim();
        TaskParam.VulCode vulCode = new TaskParam.VulCode();

        if (vulKey.contains(",")) {
            String[] split = vulKey.split(",");
            vulCode.setClassName(split[0]);
            vulCode.setMethodName(split[1]);
        } else {
            vulCode.setMethodName(vulKey);
        }

        return TaskParam.builder()
                .projectFilepath(properties.getProperty("projectFilepath"))
                .vulKey(vulKey)
                .vulInput(properties.getProperty("vulInput"))
                .vulDescription(properties.getProperty("vulDescription"))
                .vulFullyQualifiedClassName(properties.getProperty("vulFullyQualifiedClassName"))
                .vulCode(vulCode)
                .needPt(Boolean.parseBoolean(properties.getProperty("needPt")))
                .build();

    }

}
