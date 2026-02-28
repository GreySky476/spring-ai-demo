package com.shopdora.crawler.execute.mcpserver.provider;

import io.modelcontextprotocol.server.McpSyncServerExchange;
import io.modelcontextprotocol.spec.McpSchema;
import javafx.application.Preloader;
import org.springaicommunity.mcp.annotation.McpProgressToken;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class WeatherService {

    public record WeatherResponse(Current current) {
        /**
         *
         * @param time              时间
         * @param interval          间隔
         * @param temperature_2m    温度，离地两米，2米高度接近人体呼吸区域，能直接反馈实际体感温度
         */
        public record Current(LocalDateTime time, int interval, int temperature_2m) {}
    }

    @McpTool(description = "Get the temperature (in celsius) for a specific location")
    public String getTemperature(
            // 提供对服务器-客户端通信功能的访问权限。它允许服务器向客户端发送通知和请求。
            McpSyncServerExchange exchange,
            @McpToolParam(description = "The location latitude") double latitude,
            @McpToolParam(description = "The location longitude") double longitude,
            // 参数启用进度跟踪。客户端提供此令牌，服务器使用该令牌发送进度更新。
            @McpProgressToken String progressToken) {


        // 向客户端发送结构化的日志消息，用于调试和监控目的。
        exchange.loggingNotification(McpSchema.LoggingMessageNotification.builder()
            .level(McpSchema.LoggingLevel.DEBUG)
            .data("Call getTemperature Tool with latitude: " + latitude + " and longitude: " + longitude)
            .meta(Map.of())
            .build());

        WeatherResponse weatherResponse = RestClient.create()
                .get()
                .uri("https://api.open-meteo.com/v1/forecast?latitude={latitude}&longitude={longitude}&current=temperature_2m",
                        latitude, longitude)
                .retrieve()
                .body(WeatherResponse.class);

        String epicPoem = "MCP Client doesn't provide sampling capability.";

        if (exchange.getClientCapabilities().sampling() != null) {
            // 50% progress 向客户报告操作进度（本例中为 50%），并附有描述性消息。
            exchange.progressNotification(new McpSchema.ProgressNotification(progressToken, 0.5, 1.0, "Start sampling"));

            String sampling = """
                    For a weather forecast (temperature is in Celsius): %s.
                    At location with latitude: %s and longitude: %s.
                    Please write an epic poem about this forecast using a Shakespearean style.
                    """.formatted(weatherResponse.current().temperature_2m(), latitude, longitude);

            // 采样能力，服务器可以请求客户端的 LLM 生成内容
            McpSchema.CreateMessageResult samplingResponse = exchange.createMessage(McpSchema.CreateMessageRequest.builder()
                    .systemPrompt("You are a poet!")
                    .messages(List.of(new McpSchema.SamplingMessage(McpSchema.Role.USER, new McpSchema.TextContent(sampling))))
                    .build());

            epicPoem = ((McpSchema.TextContent) samplingResponse.content()).text();
        }

        // 100% progress
        exchange.progressNotification(new McpSchema.ProgressNotification(progressToken, 1.0, 1.0, "Start sampling"));

        return """
                Weather Poem: %s			
                about the weather: %s°C at location: (%s, %s)
                """.formatted(epicPoem, weatherResponse.current().temperature_2m(), latitude, longitude);
    }


}
