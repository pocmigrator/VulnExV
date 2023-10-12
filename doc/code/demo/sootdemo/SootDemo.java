package demo.sootdemo;

import org.junit.Before;
import org.junit.Test;
import soot.PackManager;
import soot.Scene;
import soot.options.Options;

import java.util.Arrays;

/**
 * 创建 call graph
 *
 * 创建 DFG
 * 分析 DFG
 *
 *
 */
public class SootDemo {


    @Before
    public void init(){
        soot.G.reset();//re-initializes all of soot
        Options.v().set_src_prec(Options.src_prec_class);//设置处理文件的类型,当然默认也是class文件
        Options.v().set_process_dir(Arrays.asList("target/classes/org/example"));//处理路径
        Options.v().set_whole_program(true);//开启全局模式
        Options.v().set_prepend_classpath(true);//对应命令行的 -pp
        Options.v().set_output_format(Options.output_format_jimple);//输出jimple文件
        try{
            Scene.v().loadNecessaryClasses();//加载所有需要的类
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Test
    public void test(){
        PackManager.v().runPacks();//运行(要有，不然下面没有输出...坑了好久，加上后运行好慢)
        PackManager.v().writeOutput();//输出jimple到sootOutput目录中
    }

}
