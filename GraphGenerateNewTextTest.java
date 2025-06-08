import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import java.io.*;

public class GraphGenerateNewTextTest {
    static final String TEST_TEXT = "The scientist carefully analyzed the data, wrote a detailed report, and shared the report with the team, but the team requested more data, so the scientist analyzed it again.";

    @Before
    public void setup() throws IOException {
        // 重置静态图对象
        Lab1_GraphProcessor.graph = new Lab1_GraphProcessor.Graph();

        // 创建临时文件构建图
        File tempFile = File.createTempFile("test", ".txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            writer.write(TEST_TEXT);
        }
        Lab1_GraphProcessor.buildGraph(tempFile.getAbsolutePath());
        tempFile.delete();
    }

    @Test
    public void testGenerateNewText_EmptyGraph() throws IOException {
        // 创建空图
        Lab1_GraphProcessor.graph = new Lab1_GraphProcessor.Graph();

        String input = "analyzed the report";
        String result = Lab1_GraphProcessor.generateNewText(input);
        assertEquals("analyzed the report", result.toLowerCase());
    }

    @Test
    public void testGenerateNewText_WithBridgeWords() {
        String input = "shared team";
        String result = Lab1_GraphProcessor.generateNewText(input);
        assertNotNull(result);
        assertTrue(result.matches("shared (\\w+ )?team"));
    }

    @Test
    public void testGenerateNewText_NoBridgeWords() {
        String input = "quick brown fox";
        String result = Lab1_GraphProcessor.generateNewText(input);
        assertEquals("quick brown fox", result.toLowerCase());
    }

    @Test
    public void testGenerateNewText_EmptyInput() {
        String result = Lab1_GraphProcessor.generateNewText("");
        assertEquals("", result);
    }
}