package spring.ai.example.spring_ai_demo.asm;

import org.springframework.asm.ClassReader;
import org.springframework.asm.ClassVisitor;
import org.springframework.asm.ClassWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Generator {
    public static void main(String[] args) throws IOException {
        // 读取
        ClassReader classReader = new ClassReader("spring/ai/example/spring_ai_demo/asm/Base");
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        // 处理
        ClassVisitor classVisitor = new MyClassVisitor(classWriter);
        classReader.accept(classVisitor, ClassReader.SKIP_DEBUG);
        byte[] data = classWriter.toByteArray();
        // 输出
        File file = new File("target/classes/spring/ai/example/spring_ai_demo/asm/Base.class");

        FileOutputStream fos = new FileOutputStream(file);
        fos.write(data);
        fos.close();
        System.out.println("now generator cc success!!!");
    }
}
