import app.TestClient;

public class MainTest {
    public static void main(String[] args) {
        TestClient client = new TestClient();
        for(int i = 0; i < 10; i++) {
            client.sendMessage("FORNAX TEST: " + i);
        }
    }
}
