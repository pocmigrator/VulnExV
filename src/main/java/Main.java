import core.entity.analysis.*;
import core.processor.*;
import core.prompt.PromptGenerator;
import utils.FileReadUtil;
import utils.GetFoldFileNames;
import utils.TaskParamReader;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import static utils.TaskParamReader.taskParam;

public class Main {
    public static PromptGenerator promptGenerator = new PromptGenerator();

    public static void main(String[] args) {
        TaskParam taskParam = TaskParamReader.getTaskParam();
        Initializer initializer = new Initializer(taskParam);
        initializer.init();

        List<String> prompts = promptGenerator.generatePrompts();
        writePromptToFile(prompts);
    }

    private static void writePromptToFile(List<String> prompts) {
        String promptPath = System.getProperty("user.dir") + File.separator + "prompts" + File.separator;
        int index = 1;
        for (String prompt : prompts) {
            String filepath = promptPath + "prompt-" + index + ".txt";
            writePromptToFile(filepath, prompt);
            index++;
        }
    }

    private static void writePromptToFile(String filepath, String prompt) {
        File file = new File(filepath);
        try {
            FileWriter writer = new FileWriter(file);
            writer.write(prompt);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
