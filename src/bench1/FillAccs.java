package bench1;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/*
 * Diese Klasse befuellt die Accounts-Relation mittels FileWriter und INFILE
 */

public class FillAccs implements Runnable {

	private int nStart, nStop;

	private PreparedStatement prepStmt;
	private String filename;
	private FileWriter fw;
	private StringBuilder sb = new StringBuilder();

	public FillAccs(Connection conni, int nStart, int nStop, String filename) throws IOException, SQLException {
		this.nStart = nStart;
		this.nStop = nStop;
		prepStmt = conni.prepareStatement(Statements.loadfile);
		this.filename = filename;
		this.fw = new FileWriter(filename);
	}

	// multithreading
	@Override
	public void run() {
		try {
			// Schleife um die ACCOUNTS-Relation zu fuellen
			for (int i = nStart; i < nStop; i++) {
				sb.setLength(0);
				sb.append(i + 1)
					.append("\tnameMit20Buchstaben!\t0\t")
					.append(DBI42Bench1.randomArr[i])
					.append("\tabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnop")
					.append(System.getProperty("line.separator"));
				fw.write(sb.toString());
			}
			fw.flush();

			// lies Datei ein
			prepStmt.setString(1, filename);
			prepStmt.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
