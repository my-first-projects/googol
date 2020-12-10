package main;

import data.Crawler;
import data.Data;
import gui.GUI;

public class Var {
	
	public static GUI gui;
	public static int width = 800, height = 600;
	
	public static boolean crawling = false;
	public static Crawler[] crawlers;
	public static int numberOfThreads = 1;
	
	public static void init() {
		Data.init();
		crawlers = new Crawler[numberOfThreads];
		for(int i = 0; i < Var.crawlers.length; i++) {
			crawlers[i] = new Crawler();
			crawlers[i].start();
		}
		
		gui =  new GUI();
		Data.updateUrls();
		
	}
	
}
