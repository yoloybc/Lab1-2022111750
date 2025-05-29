import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class Lab1_GraphProcessorTest {

    static Lab1_GraphProcessor lab1;

    @BeforeAll
    public static void init() {
        lab1 = new Lab1_GraphProcessor();
        try {
            lab1.buildGraph("1.txt");
        } catch (Exception e) {
            e.printStackTrace();
            fail("图构建失败");
        }
    }

    @Test
    void test_TC1_emptyInput() {
        String input = "";
        String expected = "";
        System.out.println(lab1.generateNewText(input));
        assertEquals(expected, lab1.generateNewText(input));
    }

    @Test
    void test_TC2_singleWord() {
        String input = "to";
        String expected = "to";
        System.out.println(lab1.generateNewText(input));
        assertEquals(expected, Lab1_GraphProcessor.generateNewText(input));
    }

    @Test
    void test_TC3_withBridgeWords() {
        String input = "to explore new worlds";
        String expected = "to explore strange new worlds";
        System.out.println(lab1.generateNewText(input));
        assertEquals(expected, Lab1_GraphProcessor.generateNewText(input));
    }

    @Test
    void test_TC4_noBridgeWord() {
        String input = "seek banana";
        String expected = "seek banana";
        System.out.println(lab1.generateNewText(input));
        assertEquals(expected, Lab1_GraphProcessor.generateNewText(input));
    }

    @Test
    void test_TC5_illegalCharacters() {
        String input = "To @ explore # strange & new worlds,";
        String expected = "to explore strange new worlds";
        System.out.println(lab1.generateNewText(input));
        assertEquals(expected, Lab1_GraphProcessor.generateNewText(input));
    }
}
