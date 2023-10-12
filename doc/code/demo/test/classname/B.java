package demo.test.classname;
import antlr.Java8Lexer;
import
antlr .
        Java8Parser;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import
        org.antlr.v4 .runtime.tree.RuleNode;

import demo.test.classname.dir1.A;

public class B  extends A{

    A a  =new A();

    RuleNode ruleNode1= new Java8Parser.LambdaBodyContext(null, 1);
    @Override
    public void func1(){
        B b = new B();
        b.func1();

        RuleNode ruleNode = new Java8Parser.LambdaBodyContext(null, 1);

        RuleContext ruleContext = ruleNode.getRuleContext();

        a.func1();

        ParseTree child = ruleNode1.getChild(1);

        int identifier = Java8Lexer.Identifier;
    }

}


/**
 ParseTree child = ruleNode1.getChild(1);
 处理方式：

被调用方法名：getChild
参数列表：1
类型：org.antlr.v4 .runtime.tree.RuleNode

是否client方法

// 先不保存
返回值

 */