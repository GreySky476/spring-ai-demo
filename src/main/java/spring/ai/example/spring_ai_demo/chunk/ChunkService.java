package spring.ai.example.spring_ai_demo.chunk;

import java.util.List;

public interface ChunkService {

    List<String> chunk(String text);
}
