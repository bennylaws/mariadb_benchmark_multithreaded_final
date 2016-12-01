package bench1;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Random;
import java.util.Scanner;

public class DBI42Bench1 {
	
	private static final int N = 100_000; // anzahl accounts pro branch

	private static final Random rnd = new Random(System.currentTimeMillis());
	public static int[] randomArr;

	public static void main(String[] args) throws Exception {
		System.out.print("Jetzt n eingeben: ");

		Scanner scanner = new Scanner(System.in);
		int n = scanner.nextInt();// anzahl branches

		int accounts = n * N; // anzahl accounts gesamt

		randomArr = new int[accounts];
		for (int i = 0; i < accounts; i++)
			randomArr[i] = rnd.nextInt(n) + 1;

		// conni =
		// DriverManager.getConnection("jdbc:mysql://192.168.122.46",
		// "dbi", "dbi_pass");

//		int numConnections = 5; // muss N restlos teilen!!!
		System.out.println("Anzahl Threads (muss 'n' RESTLOS teilen!): ");
		int numConnections = scanner.nextInt();
		scanner.close();
		
		Connection[] connections = new Connection[numConnections];
		Connection conni = DriverManager.getConnection("jdbc:mysql://192.168.122.46", "dbi", "dbi_pass");

		System.out.println("Sehr verbunden :)\n");

		Init.init(conni);

		for (int c = 0; c < numConnections; c++) { // c++ :D
			connections[c] = DriverManager.getConnection("jdbc:mysql://192.168.122.46/bank", "dbi", "dbi_pass");
		}

		int numThreads = numConnections; // muss N restlos teilen!!!
		Thread[] threads = new Thread[numThreads];

		for (int i = 0; i < numThreads; i++) {
			threads[i] = new Thread(new FillAccs(connections[i], accounts / numThreads * i,
					accounts / numThreads * (i + 1), "SQL" + i));
		}

		long start = System.nanoTime();

		// fill branch, tellers
		Fill.fill(conni, n);

		// Fill accounts
		for (Thread t : threads) {
			t.start();
		}

		for (Thread t : threads) {
			t.join();
		}

		conni.prepareStatement(Statements.after1).execute();
		conni.prepareStatement(Statements.after2).execute();

		long stop = System.nanoTime();
		long diff = stop - start;
		System.out.printf("Fertig, es hat %.2fs gedauert", (double) diff / 1_000_000_000.0);

		// Verbindung beenden, Programm verlassen
		for (int i = 0; i < numConnections; i++) {

			connections[i].createStatement().executeUpdate(Statements.after1);
			connections[i].createStatement().executeUpdate(Statements.after2);
			connections[i].close();

			conni.close();
		}
	}
}
