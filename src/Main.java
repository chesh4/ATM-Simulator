import service.ATMService;
import ui.ConsoleUI;

public class Main {
	public static void main(String[] args) {
		ATMService service = new ATMService();
		ConsoleUI ui = new ConsoleUI(service);
		ui.start();
	}
}


