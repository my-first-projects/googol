package data;

import main.Var;
import sql.DatabaseConnector;
import sql.QueryResult;

public class Data {

	public static DatabaseConnector dbCon;

	public static void init() {

		dbCon = new DatabaseConnector("127.0.0.1", 0, "data.db", "user", "password");

	}

	/*
	 * public static int getNumberOfUrls() { QueryResult res =
	 * getResult("SELECT * from urls;"); if(res != null) return res.getRowCount();
	 * return 0; }
	 */

	public static int getNumberOfUrls() {
		QueryResult res = getResult("SELECT COUNT(*) from urls;");
		if (res != null)
			return Integer.parseInt(res.getData()[0][0]);
		return 0;
	}

	/*
	 * public static int getNumberOfFounds() { QueryResult res =
	 * getResult("SELECT * from found;"); if(res != null) return res.getRowCount();
	 * return 0; }
	 */

	public static int getNumberOfFounds() {
		QueryResult res = getResult("SELECT COUNT(*) from found;");
		if (res != null)
			return Integer.parseInt(res.getData()[0][0]);
		return 0;
	}

	public static void updateUrls() {
		int anzahl = getNumberOfUrls();
		Var.gui.numberOfUrls.setText("Urls: " + anzahl);

		anzahl = getNumberOfFounds();
		Var.gui.numberOfFounds.setText("Found: " + anzahl);
		
		anzahl = getNumberOfDomains();
		Var.gui.numberOfDomains.setText("Domains: " + anzahl);
		
		anzahl = getNumberOfWiki();
		Var.gui.numberOfWiki.setText("Wikipedia: " + anzahl);
	}

	private static int getNumberOfWiki() {
		QueryResult res = getResult("SELECT COUNT(*) FROM urls WHERE domain = 'de.wikipedia.org'");
		if (res != null)
			return Integer.parseInt(res.getData()[0][0]);
		return 0;
	}

	private static int getNumberOfDomains() {
		QueryResult res = getResult("SELECT COUNT(*) FROM (SELECT DISTINCT domain FROM urls);");
		if (res != null)
			return Integer.parseInt(res.getData()[0][0]);
		return 0;
	}

	public static void addUrl(String title, String url, int links, int images, String domain) {
		getResult("INSERT INTO urls(title, url, links, images, domain) VALUES('" + title + "','" + url + "'," + links
				+ "," + images + ",'" + domain + "')");
	}

	public static void addFound(String url[], String source, String[] domains) {
		for (int i = 0; i < url.length; i++)
			if (!isNull(url[i], source, domains[i]) && !existUrl(url[i]))
				getResult("INSERT INTO found(url, source, domain) VALUES('" + url[i] + "','" + source + "','" + domains[i] + "')");
	}

	private static boolean isNull(String url, String source, String domain) {
		if (url.equalsIgnoreCase("") || source.equalsIgnoreCase("") || domain.equalsIgnoreCase("")
				|| url.equalsIgnoreCase("null") || source.equalsIgnoreCase("null") || domain.equalsIgnoreCase("null"))
			return true;
		return false;
	}

	private static boolean existUrl(String url) {
		int anzahl = 0;
		QueryResult res = getResult("SELECT url from urls WHERE url = '" + url + "' UNION " + "SELECT url from found WHERE url = '" + url + "'");
		if (res != null)
			anzahl = res.getRowCount();
		return anzahl >= 1;
	}

	public static QueryResult getResult(String cmd) {
		dbCon.executeStatement(cmd);
		return dbCon.getCurrentQueryResult();
	}

	public static String getFoundUrl() {
		String url = "";
		String domain = getRareDomain();
		System.out.println("Domain: " + domain);
		String cmd = "SELECT * FROM found WHERE domain='" + domain + "' LIMIT 5;";
		QueryResult res = getResult(cmd);
		String[] args = res.getData()[(int) (Math.random() * res.getData().length)];
		url = args[0];
		getResult("DELETE FROM found WHERE url = '" + url + "'");
		return url;
	}

	private static String getRareDomain() {
		
		int percentage = 50;
		
		if(Math.random() < percentage/100)
			return "de.wikipedia.org";
		
		String cmd = "SELECT domain FROM found GROUP BY domain ORDER BY COUNT(domain) ASC LIMIT 5";
		QueryResult res = getResult(cmd);

		if (res == null) {
			System.err.println("FEHLER");
			System.exit(-1);
		}

		String[][] data = res.getData();

		if (data.length >= 2) {
			return data[(int)(Math.random()*2)][0];
		} else {
			return data[0][0];
		}
	}

}
