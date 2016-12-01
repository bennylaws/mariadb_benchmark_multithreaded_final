package bench1;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Random;
import java.util.Scanner;

/*
 * Datenbank-Programm zur Ermittlung von Zeitmessungen für das DBI-Benchmark.
 */

/**
 * 
 * @author Ann-Kathrin Hillig, Benjamin Laws, Tristan Simon
 * @version 1.0 (November/Dezember 2016)
 * 
 */
public class DBI42Bench1 {
	
	private static final int N = 100_000; // Anzahl accounts pro branch

	private static final Random rnd = new Random(System.currentTimeMillis());
	public static int[] randomArr;

	public static void main(String[] args) throws Exception {
		System.out.print("Jetzt n eingeben: ");

		Scanner scanner = new Scanner(System.in);
		int n = scanner.nextInt();// Anzahl branches

		int accounts = n * N; // Anzahl accounts gesamt

		randomArr = new int[accounts];
		for (int i = 0; i < accounts; i++)
			randomArr[i] = rnd.nextInt(n) + 1;
		
		System.out.println("Anzahl Threads (muss 'n' RESTLOS teilen!): ");
		int numConnections = scanner.nextInt();
		scanner.close();
		
		// Connections-Array erzeugen
		Connection[] connections = new Connection[numConnections];
		// conni = init-Verbindung
		Connection conni = DriverManager.getConnection("jdbc:mysql://192.168.122.46", "dbi", "dbi_pass");

		System.out.println("Sehr verbunden :)\n");
		
		// DB initialisieren (drop if exists, create, use,...)
		Init.init(conni);
		
		// Connections erstellen
		for (int c = 0; c < numConnections; c++) { // c++ :D
			connections[c] = DriverManager.getConnection("jdbc:mysql://192.168.122.46/bank", "dbi", "dbi_pass");
		}

		// threads erzeugen
		int numThreads = numConnections; // muss N restlos teilen!!!
		Thread[] threads = new Thread[numThreads];

		for (int i = 0; i < numThreads; i++) {
			threads[i] = new Thread(new FillAccs(connections[i], accounts / numThreads * i,
					accounts / numThreads * (i + 1), "SQL" + i));
		}

		// Beginn der Zeitmessung
		long start = System.nanoTime();

		// fuelle branches, tellers
		Fill.fill(conni, n);

		// fuelle accounts
		for (Thread t : threads) {
			t.start();
		}

		for (Thread t : threads) {
			t.join();
		}

		// key checks wieder einschalten
		conni.prepareStatement(Statements.after1).execute();
		conni.prepareStatement(Statements.after2).execute();

		// Ende der Zeitmessung, Berechnung, Ausgabe
		long stop = System.nanoTime();
		long diff = stop - start;
		System.out.printf("Fertig, es hat %.2fs gedauert", (double) diff / 1_000_000_000.0);

		// Verbindungen beenden, Programm verlassen
		for (int i = 0; i < numConnections; i++) {

			// key checks wieder einschalten
			connections[i].createStatement().executeUpdate(Statements.after1);
			connections[i].createStatement().executeUpdate(Statements.after2);
			connections[i].close();	
		}
		conni.close();
	}
}
