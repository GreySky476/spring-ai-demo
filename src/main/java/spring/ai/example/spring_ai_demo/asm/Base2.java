package spring.ai.example.spring_ai_demo.asm;

/**
 * 构建各种测试方法
 */
public class Base2 {

    public void process() {
        System.out.println("process");
    }

    public void process(String task) {
        System.out.println("process: " + task);
    }

    public int execute() {
        System.out.println("execute");
        return 0;
    }

    public int execute(String task) {
        System.out.println("execute: " + task);
        return 1;
    }

    public int execute0(String task) throws InterruptedException {
        Thread.sleep(1000L);
        System.out.println("execute0: " + task);
        return 2;
    }


}
