package gui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;

import data.Data;
import main.Var;
import sql.QueryResult;

@SuppressWarnings("serial")
public class GUI extends JFrame implements ActionListener {

	public JLabel numberOfUrls, numberOfFounds, currentURL, numberOfDomains, numberOfWiki;
	public JTextPane htmlResult;
	JTextField searchBar;
	JButton searchBTN, switchCrawler;
	JButton[] links;

	private static long startTime = 0;

	public GUI() {
		super("Googol");

		setSize(Var.width, Var.height);
		setLocationRelativeTo(null);
		setLayout(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
		setBackground(Color.WHITE);
		getContentPane().setBackground(Color.WHITE);

		// numberOfUrls
		numberOfUrls = new JLabel();
		numberOfUrls.setBounds(10, 10, 100, 30);
		numberOfUrls.setText("Urls: 0");
		add(numberOfUrls);

		// numberOfFounds
		numberOfFounds = new JLabel();
		numberOfFounds.setBounds(10, 30, 100, 30);
		numberOfFounds.setText("Found: 0");
		add(numberOfFounds);

		// numberOfDomains
		numberOfDomains = new JLabel();
		numberOfDomains.setBounds(10, 50, 100, 30);
		numberOfDomains.setText("Domains: " + 0);
		add(numberOfDomains);

		// numberOfWiki
		numberOfWiki = new JLabel();
		numberOfWiki.setBounds(10, 70, 100, 30);
		numberOfWiki.setText("Wikipedia: 0");
		add(numberOfWiki);

		// currentURL
		currentURL = new JLabel("Urls: 0");
		currentURL.setBounds(10, 110, 1000, 30);
		currentURL.setText("Url: " + "");
		add(currentURL);

		// searchBar
		searchBar = new JTextField("Suchbegriff");
		searchBar.setBounds(Var.width / 2 - 200, 30, 400, 40);
		//searchBar.setBorder(BorderFactory.createEmptyBorder());
		//searchBar.setBackground(Color.BLACK);
		//searchBar.setForeground(Color.white);
		searchBar.setFont(new Font("Arial", Font.PLAIN, 20));
		add(searchBar);

		// searchBTN
		searchBTN = new JButton("Suchen");
		searchBTN.setBounds(Var.width / 2 - 200, 80, 200, 40);
		searchBTN.setBorder(BorderFactory.createEmptyBorder());
		searchBTN.setFont(new Font("Arial", Font.PLAIN, 20));
		searchBTN.addActionListener(this);
		//searchBTN.setBackground(Color.yellow);
		//searchBTN.setForeground(new Color(150, 0, 200));
		add(searchBTN);

		// switchCrawler
		switchCrawler = new JButton("URL-Suche starten");
		switchCrawler.setBounds(Var.width - 350, 80, 200, 40);
		switchCrawler.setBorder(BorderFactory.createEmptyBorder());
		switchCrawler.setFont(new Font("Arial", Font.PLAIN, 20));
		switchCrawler.addActionListener(this);
		//switchCrawler.setBackground(Color.PINK);
		//switchCrawler.setForeground(Color.GREEN);
		add(switchCrawler);

		// test
		htmlResult = new JTextPane();
		htmlResult.setEditable(false);
		htmlResult.setContentType("text/html");
		htmlResult.setBounds(20, 140, java.awt.Toolkit.getDefaultToolkit().getScreenSize().width - 100,
				java.awt.Toolkit.getDefaultToolkit().getScreenSize().height - 180);
		add(htmlResult);

		// links
		links = new JButton[23];
		for (int i = 0; i < links.length; i++) {
			links[i] = new JButton("");
			links[i].setBounds(5, 165 + i * 38, 15, 30);
			links[i].setVisible(false);
			links[i].addActionListener(this);
			//links[i].setBackground(Color.CYAN);
			add(links[i]);
		}

		// googol logo
		JPanel panel = new JPanel();
		panel.setBounds(680, -10, 611, 155);
		ImageIcon icon = new ImageIcon("googol.jpg");
		JLabel label = new JLabel();
		label.setIcon(icon);
		panel.add(label);
		add(panel);

		setVisible(true);
		repaint();

	}

	public static void setResult(String[][] data) { // title, url, links, images, domain
		String html = "<html>";

		for (int i = 0; i < Var.gui.links.length; i++)
			Var.gui.links[i].setVisible(false);

		long time = System.currentTimeMillis() - startTime;
		double seconds = (double)((double)time / (double)1000);

		if (data.length >= 1)
			html += "<a>" + data.length + " Ergebnisse in " + seconds + " Sekunden</a><br>";
		else
			html += "<a>Keine Ergebnisse in " + seconds + " Sekunden</a><br>";

		for (int i = 0; i < data.length && i < Var.gui.links.length; i++) {

			Var.gui.links[i].setVisible(true);
			Var.gui.links[i].setText(data[i][1]);

			html += "<a href=\"" + data[i][1] + "\">" + data[i][0] + "</a><br>";
			html += "<a style=\" color: green \">" + data[i][1] + "</a><br>";
		}

		html += "</html>";
		Var.gui.htmlResult.setText(html);
		//Var.gui.htmlResult.setBackground(new Color((int)(Math.random()*255), (int)(Math.random()*255), (int)(Math.random()*255)));
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();
		if (src == searchBTN) {
			String keywords = searchBar.getText();
			googol(keywords);
		} else if (src == switchCrawler) {
			if (Var.crawling) {
				switchCrawler.setText("URL-Suche starten");
				Var.crawling = false;
			} else {
				switchCrawler.setText("URL-Suche stoppen");
				Var.crawling = true;
			}
		} else {

			for (int i = 0; i < links.length; i++) {
				if (src == links[i]) {
					openUrl(links[i].getText());
				}
			}

		}
	}

	private void openUrl(String url) {
		try {
			final URI uri = new URI(url);
			Desktop.getDesktop().browse(uri);
		} catch (Exception e) {
			System.err.println("Fehler beim Oeffnen der URL");
		}
	}

	private void googol(String keywords) {

		startTime = System.currentTimeMillis();

		String[][] data = null;
		QueryResult res = null;
		String cmd = "";

		/*
		 * cmd = "SELECT * FROM urls WHERE domain='de.wikipedia.org' AND title LIKE '%"
		 * + keywords + "%'" + " UNION " + "SELECT * FROM urls WHERE title LIKE '%" +
		 * keywords + "%' AND domain != 'de.wikipedia.org';";
		 */
		
		String keywordsTitle = "";
		String[] args = keywords.split(" ");
		for(int i = 0; i < args.length - 1; i++)
			keywordsTitle += "title LIKE '%" + args[i] + "%' AND ";
		keywordsTitle += "title LIKE '%" + args[args.length-1] + "%' ";
		String keywordsUrl = "";
		args = keywords.split(" ");
		for(int i = 0; i < args.length - 1; i++)
			keywordsUrl += "url LIKE '%" + args[i] + "%' AND ";
		keywordsUrl += "url LIKE '%" + args[args.length-1] + "%' ";
		cmd = "SELECT  DISTINCT * FROM ( " + 
				" SELECT * FROM urls WHERE domain = 'de.wikipedia.org' AND title LIKE '%"+ keywords + "%' " 
				+ "UNION" + 
				" SELECT * FROM urls WHERE domain LIKE '%" + keywords + "%' " 
				+ "UNION" +
				" SELECT * FROM urls WHERE " + keywordsTitle + " AND " + keywordsUrl
				+ "UNION" +
				" SELECT * FROM urls WHERE " + keywordsTitle + " OR " + keywordsUrl
				 + " ) ;";

		res = Data.getResult(cmd);
		if (res != null) {
			data = res.getData();
			setResult(data);
		} else {
			System.exit(-1);
		}

	}

}
