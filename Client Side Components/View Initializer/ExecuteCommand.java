import java.io.IOException;

/**
 * @author Ujjwal Acharya : Executes each thread with their command line
 *         arguments for the preferred functionalities
 *
 */
@SuppressWarnings(value = "unused")
public class ExecuteCommand implements Runnable {
	private String[] psexecSyntax;
	private String threadID;

	public ExecuteCommand(String[] args, String index) {
		// TODO Auto-generated constructor stub
		this.psexecSyntax = args;
		this.threadID = index;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		synchronized (this) {
			try {
				Process process = new ProcessBuilder(psexecSyntax).start();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public String getThreadName() {
		return threadID;
	}

}
