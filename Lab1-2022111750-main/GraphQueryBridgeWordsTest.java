import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import java.io.*;

public class GraphQueryBridgeWordsTest {
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
    public void testQueryBridgeWords_HasBridge() {
        String result = Lab1_GraphProcessor.queryBridgeWords("analyzed", "the");
        assertEquals("No bridge words from \"analyzed\" to \"the\"!", result);
    }

    @Test
    public void testQueryBridgeWords_HasBridge2() {
        String result = Lab1_GraphProcessor.queryBridgeWords("shared", "team");
        assertTrue(result.startsWith("The bridge words from \"shared\" to \"team\" is:"));
        assertTrue(result.contains("the"));
    }

    @Test
    public void testQueryBridgeWords_NoBridge() {
        String result = Lab1_GraphProcessor.queryBridgeWords("scientist", "carefully");
        assertEquals("No bridge words from \"scientist\" to \"carefully\"!", result);
    }

    @Test
    public void testQueryBridgeWords_WordNotFound() {
        String result = Lab1_GraphProcessor.queryBridgeWords("unknown", "team");
        assertEquals("No \"unknown\" in the graph!", result);
    }

    @Test
    public void testQueryBridgeWords_SpecialChar() {
        String result = Lab1_GraphProcessor.queryBridgeWords("@data", "#team");
        assertEquals("No \"@data\" in the graph!", result);
    }

    @Test
    public void testQueryBridgeWords_EmptyInput() {
        String result = Lab1_GraphProcessor.queryBridgeWords("", "scientist");
        assertEquals("No \"\" in the graph!", result);
    }
}