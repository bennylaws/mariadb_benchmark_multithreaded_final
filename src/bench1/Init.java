package bench1;

import java.sql.Connection;

public class Init {

	static void init(Connection conni) {

		// in Schleife alle Einzel-Strings aus der Klasse Statements ausfuehren
		// ergo: DB droppen (falls vorhanden), neu aufbauen,...
		try {
			for (String str : Statements.createDb) {
				System.out.println(str);
				conni.createStatement().executeUpdate(str);
			}
		} catch (Exception e) {
			System.out.println("Error...");
			System.out.println(e);
		}
	}

}
