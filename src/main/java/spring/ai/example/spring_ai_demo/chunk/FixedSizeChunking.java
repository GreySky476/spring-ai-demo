package spring.ai.example.spring_ai_demo.chunk;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * 固定大小分块策略
 *
 */
public class FixedSizeChunking implements ChunkService {


    private int chunkSize = 10;
    private int overlap = 2;

    @Override
    public List<String> chunk(String text) {
        List<String> chunks = Lists.newArrayList();

        if (text.length() <= chunkSize) {
            chunks.add(text);
            return chunks;
        }
        int start = 0;
        int end = chunkSize;
        chunks.add(text.substring(start, end));

        boolean flag = true;
        while (flag) {
            start = end - overlap;
            end = end + chunkSize;

            if (end >= text.length()) {
                end = text.length();
                start = end - chunkSize;
                flag = false;
            }

            chunks.add(text.substring(start, end));
        }
        return chunks;
    }

    public static void main(String[] args) {
        FixedSizeChunking fixedSizeChunking = new FixedSizeChunking();
        List<String> chunks = fixedSizeChunking.chunk("123456789023e2132eqwef42342r23123e234123e23eeorwyuopq3h9ryge9pg19p4rg9o;wht239p4g9tg9q4");
        System.out.println(chunks);
    }

}
