package org.yoniehax.tankoid.gui;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.yoniehax.tankoid.Processor;

public class DebugGUI extends JFrame {

	DebugPanel panel;
	Processor processor;
	final double zoomFactor = 0.5;

	public DebugGUI(Processor processor) {
		this.processor = processor;
		panel = new DebugPanel(new BorderLayout(), processor, zoomFactor);
		this.setContentPane(panel);
		int mapSize = (int) Math.round(processor.getMapSize() * zoomFactor);
		setSize(mapSize, mapSize);
		setTitle("Tankoid debug");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setVisible(true);
	}

}
