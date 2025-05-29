import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class Lab1_GraphProcessorTest_white {
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
    public void queryBridgeWordsTest1(){
        System.out.println(lab1.queryBridgeWords("am", "to"));
    }

    @Test
    public void queryBridgeWordsTest2(){
        System.out.println(lab1.queryBridgeWords("am", "is"));
    }
    @Test
    public void queryBridgeWordsTest3(){
        System.out.println(lab1.queryBridgeWords("to", "out"));
    }
    @Test
    public void queryBridgeWordsTest4(){
        System.out.println(lab1.queryBridgeWords("to", "seek"));
    }
}