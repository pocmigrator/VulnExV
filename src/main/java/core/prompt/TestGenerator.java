package core.prompt;

import java.util.List;

public class TestGenerator {
    private PromptGenerator promptGenerator = new PromptGenerator();

    public List<String> test() {
        List<String> prompts = promptGenerator.generatePrompts();
        for (String prompt: prompts) {

            //invoke openai api
            String output = ""+prompt;
        }
        return null;
    }

}
