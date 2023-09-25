package core.processor;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

import java.text.ParseException;

public class ErrorListenerWithException extends BaseErrorListener {
    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
        // 在这里抛出自定义异常，以中止解析
        throw new RuntimeException("Syntax error in input: " + msg + " at Line " + line + ":" + charPositionInLine,e);
    }
}