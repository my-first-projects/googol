package data;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import main.Var;

public class Crawler extends Thread {

	public Crawler() {
		super();
	}

	public void run() {
		while (true) {
		
			//if(Var.gui != null)
			//Var.gui.htmlResult.setBackground(new Color((int)(Math.random()*255), (int)(Math.random()*255), (int)(Math.random()*255)));
			
			
			try {
				if (Var.crawling) {
					// System.out.println("getting url");
					String url = Data.getFoundUrl();

					String domain = getDomain(url);
					// System.out.println("getting domain");

					Var.gui.currentURL.setText("Url: " + url);
					// System.out.println("setting url");

					// System.out.println("getting html");
					String html = getHTML(url);

					if (html == null)
						continue;

					// System.out.println("getting title");
					String title = getTitle(html);

					// test
					// Var.gui.htmlResult.setText(html);

					// System.out.println("getting links");
					String[] links = getLinks(html, url);

					// System.out.println("getting images");
					int images = getImages(html);

					// System.out.println("adding url");
					Data.addUrl(title, url, links.length, images, domain);

					// System.out.println("adding links");
					Data.addFound(links, url, getDomains(links));

					Data.updateUrls();

				} else {
					System.out.println("idle");
					if (Var.gui != null)
						Var.gui.currentURL.setText("idle...");
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				System.out.println("FEHLER");
				e.printStackTrace();
			}
		}
	}

	private String getDomain(String url) {
		try {
			URI uri = new URI(url);
			String domain = uri.getHost();
			return domain.startsWith("www.") ? domain.substring(4) : domain;
		} catch (Exception e) {
			// System.out.println("can not find domain of " + url);
			return "";
		}
	}

	private String[] getDomains(String[] urls) {
		String[] ret = new String[urls.length];

		for (int i = 0; i < urls.length; i++) {
			try {
				URI uri = new URI(urls[i]);
				String domain = uri.getHost();
				ret[i] = domain.startsWith("www.") ? domain.substring(4) : domain;
			} catch (Exception e) {
				ret[i] = "";
				// System.out.println("can not find domain of " + urls[i]);
			}
		}

		return ret;
	}

	private int getImages(String html) {
		int images = html.split("<img").length;
		return images;
	}

	private String[] getLinks(String html, String url) {

		String[] temp = html.split("href=\"");
		List<String> links = new ArrayList<String>();
		for (int i = 1; i < temp.length; i++) {
			try {
				String link = temp[i].split("\"")[0];
				if (link.charAt(0) == '/')
					if (getDomain(url).equals("de.wikipedia.org")) {
						link = "https://de.wikipedia.org" + link;
					} else {
						link = url + link;
					}
				if (!invalidLink(link))
					links.add(link);
			} catch (Exception e) {
			}
		}

		// cast to array
		String[] l = new String[links.size()];
		for (int i = 0; i < l.length; i++)
			l[i] = links.get(i);
		return l;
	}

	private boolean invalidLink(String link) {

		if (!link.contains(".de") && !link.contains("de.")) {
			// System.out.println("invalid4: " + link);
			return true;
		}

		String[] invalidStart = { "#", "javascript", "android-app", "mw-data", "mailto" };
		for (int i = 0; i < invalidStart.length; i++) {
			try {
				if (link.substring(0, invalidStart[i].length()).equalsIgnoreCase(invalidStart[i])) {
					// System.out.println("invalid1: " + link);
					return true;
				}
			} catch (Exception e) {
			}
		}

		
		  String[] invalidDomains = { "youtube.", "youtu.be", "instagram.", "twitter.",
				  "facebook.", "amazon.", "ebay.", "ebay-kleinanzeigen.", "doubleclick.",
				  "wordpress.", "google.", "goo.gl", "archive.", "fh.desy.de",
		  	"instandhaltung.de", "brinkhoffs.de", "cleverboy.de", "bayernlb.de", "carstenkarl.de", "arbeitsagentur.de", "d-s-u.de" };
		  for (int i = 0; i < invalidDomains.length; i++) 
			  if (link.contains(invalidDomains[i]))
				  return true;
		  
			  //System.out.println("invalid2: " +	  link); return true; } }
		 

		if (link.endsWith(".pdf") || link.endsWith(".zip") || link.endsWith(".exe") || link.endsWith(".css")
				|| link.endsWith(".png") || link.endsWith(".jpg") || link.endsWith(".ico") || link.endsWith(".htm")) {
			// System.out.println("invalid3: " + link);
			return true;
		}

		if (link.contains("///")) {
			// System.out.println("invalid5: " + link);
			return true;
		}

		if (getDomain(link).equalsIgnoreCase("de.wikipedia.org")
				&& (link.contains(".php") || link.split("//").length >= 3)
				|| link.contains("Benutzer") || link.contains("Diskussion")|| link.contains("Spezial")|| link.contains("Vorlage") || link.contains("MediaWiki") || link.contains("Datei") || link.contains("Kategorie")) {
			// System.out.println("invalid6: " + link);
			return true;
		}

		return false;
	}

	private String getTitle(String html) {
		try {
			String[] temp = html.split("<title>");
			String title = temp[1].split("</title>")[0];
			return title;
		} catch (Exception e) {
			return "none";
		}

	}

	private String getHTML(String url) {
		String ret = "";
		try {
			URL c = new URL(url);
			BufferedReader in = new BufferedReader(new InputStreamReader(c.openStream()));
			String inputLine;
			while ((inputLine = in.readLine()) != null)
				ret += inputLine;
			in.close();
		} catch (Exception e) {
			return null;
		}
		return ret;
	}
}
