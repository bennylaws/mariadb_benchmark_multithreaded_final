package bench1;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;

/*
 * Diese Klasse hat die Aufgabe branches und tellers mit Daten zu befuellen
 * benutzt batch-updates
 */

public class Fill {

	// Ẑufallszahlen (branchID)
	private static final Random rnd = new Random(System.currentTimeMillis());

	static void fill(Connection conni, int n) throws SQLException {

		conni.setAutoCommit(false);

		// Vorbereitungen und Schleife um die BRANCHES-Relation zu fuellen
		PreparedStatement prepStmt = conni.prepareStatement(Statements.branches);

		prepStmt.setString(2, "nameMit20Buchstaben!");
		prepStmt.setInt(3, 0);
		prepStmt.setString(4, "abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrst");

		for (int i = 0; i < n; i++) {
			prepStmt.setInt(1, i + 1);
			prepStmt.addBatch();
		}

		prepStmt.executeBatch();
		prepStmt.clearBatch();

		// Vorbereitungen und Schleife um die TELLERS-Relation zu fuellen
		prepStmt = conni.prepareStatement(Statements.tellers);

		prepStmt.setString(2, "nameMit20Buchstaben!");
		prepStmt.setInt(3, 0);
		prepStmt.setString(5, "abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnop");

		for (int i = 0; i < n * 10; i++) {
			prepStmt.setInt(1, i + 1);
			prepStmt.setInt(4, rnd.nextInt(n) + 1);	// Ẑufallszahlen (branchID)
			prepStmt.addBatch();
		}

		prepStmt.executeBatch();

		conni.commit();
	}
}
