package demo.test;

import core.entity.analysis.TaskParam;
import utils.CallUtil;
import utils.TaskParamReader;

import java.io.File;
import java.util.List;

public class CallTest {
    public static void main(String[] argss) {
        TaskParam taskParam = TaskParamReader.getTaskParam();
        String userDir = System.getProperty("user.dir");
        String prompt ="write a unit test about network connection";
        String pythonPath = userDir + File.separator + "model" + File.separator;
        String pythonPathTest = "/Users/gaoyi/PycharmProjects/nn_demo/gpt/test_generator.py";
        String outputPath = userDir + File.separator + "output" + File.separator + "Mytest.java";

        String[] args = new String[]{
                pythonPathTest,
                prompt,
                outputPath
        };

        List<String> call = CallUtil.call(args);

        for (String s : call) {
            System.out.println(s);
        }
    }
}
