package spring.ai.example.spring_ai_demo.asm;

import org.springframework.asm.ClassReader;
import org.springframework.asm.ClassVisitor;
import org.springframework.asm.ClassWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class Generator {
    public static void main(String[] args) throws Exception {
        genVisitor2();
    }

    public static void genVisitor() throws Exception {
        genClass("spring/ai/example/spring_ai_demo/asm/Base",
                "target/classes/spring/ai/example/spring_ai_demo/asm/Base.class",
                MyClassVisitor.class);
    }

    public static void genVisitor2() throws Exception {
        genClass("spring/ai/example/spring_ai_demo/asm/Base2",
                "target/classes/spring/ai/example/spring_ai_demo/asm/Base2.class",
                MyClassVisitor2.class);
    }

    public static void genClass(String classPath, String outClassPath, Class<? extends ClassVisitor> classVisitorClass) throws IOException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        // 读取
        ClassReader classReader = new ClassReader(classPath);
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        // 处理
        ClassVisitor classVisitor = new MyClassVisitor2(classWriter);
        classReader.accept(classVisitor, ClassReader.SKIP_DEBUG);
        byte[] data = classWriter.toByteArray();
        // 输出
        File file = new File(outClassPath);

        FileOutputStream fos = new FileOutputStream(file);
        fos.write(data);
        fos.close();
        System.out.println("now generator cc success!!!");
    }
}
