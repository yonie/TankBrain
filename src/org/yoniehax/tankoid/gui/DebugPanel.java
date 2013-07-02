package org.yoniehax.tankoid.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.util.Random;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

import org.yoniehax.helper.QuickLog;
import org.yoniehax.tankoid.HeatMapPlace;
import org.yoniehax.tankoid.Place;
import org.yoniehax.tankoid.Processor;

public class DebugPanel extends JPanel {

	Processor processor;
	double zoomFactor;

	private void doDrawing(Graphics g) {

		Graphics2D g2d = (Graphics2D) g;

		Dimension size = getSize();
		Insets insets = getInsets();

		int w = size.width - insets.left - insets.right;
		int h = size.height - insets.top - insets.bottom;

		Place currentPlace = (processor.getOwnTank() != null ? processor.getOwnTank().getPlace() : null);
		double currentAngle = (processor.getOwnTank() != null ? processor.getOwnTank().getAngle() : 0);
		Place targetPlace = processor.getTargetPlace();
		Double moveThrottle = processor.getMoveThrottle();
		Double rotationThrottle = processor.getRotationThrottle();

		// draw heat map
		if (processor.getHeatMap() != null) {
			Vector<HeatMapPlace> crowdedPlaces = processor.getHeatMap().findCrowdedPlaces(20);

			for (int n = 0; n < crowdedPlaces.size(); n++) {
				HeatMapPlace hmp = (HeatMapPlace) crowdedPlaces.get(n);

				g2d.setColor(Color.RED);

				int x = (int) Math.round(hmp.getX() * zoomFactor);
				int y = (int) Math.round(hmp.getY() * zoomFactor);
				int radius = hmp.getHeat();

				g2d.fillOval(x - radius, y - radius, 2 * radius, 2 * radius);

			}
		}

		// draw current tank
		if (currentPlace != null) {

			g2d.setColor(Color.BLUE);

			int x = (int) Math.round(currentPlace.getX() * zoomFactor);
			int y = (int) Math.round(currentPlace.getY() * zoomFactor);

			int velocityPointerLength = 30;

			g2d.drawOval(x - 4, y - 4, 8, 8);
			g2d.drawOval(x - 5, y - 5, 10, 10);
			g2d.drawLine(x, y, x
					+ (int) (velocityPointerLength * moveThrottle * Math.cos(Math.toRadians(currentAngle))), y
					- (int) (velocityPointerLength * moveThrottle * Math.sin(Math.toRadians(currentAngle))));

			int axisScale = 50;
			int axisX = 250;
			int axisY = 400;

			// heading axis
			int headingAxisX = axisX - (axisScale * 2);
			int headingAxisY = axisY;
			g2d.setColor(Color.BLACK);
			g2d.fillOval(headingAxisX - 2, headingAxisY - 2, 4, 4);
			g2d.drawLine(headingAxisX, headingAxisY,
					headingAxisX + (int) (axisScale * Math.cos(Math.toRadians(currentAngle))), headingAxisY);
			g2d.drawLine(headingAxisX, headingAxisY, headingAxisX,
					headingAxisY - (int) (axisScale * Math.sin(Math.toRadians(currentAngle))));
			g2d.setColor(Color.GRAY);
			g2d.drawOval(headingAxisX - axisScale, headingAxisY - axisScale, axisScale * 2, axisScale * 2);

			// move and rotation throttle
			int moveAxisX = axisX + (axisScale * 2);
			int moveAxisY = axisY;

			g2d.setColor(Color.BLACK);
			g2d.drawLine(moveAxisX, moveAxisY + axisScale, moveAxisX,
					moveAxisY + axisScale - (int) Math.round(2 * axisScale * moveThrottle));
			g2d.drawLine(moveAxisX, moveAxisY, moveAxisX + (int) Math.round(axisScale * rotationThrottle), moveAxisY);
			g2d.setColor(Color.GRAY);
			g2d.drawOval(moveAxisX - axisScale, moveAxisY - axisScale, axisScale * 2, axisScale * 2);
		}

		// draw target
		if (targetPlace != null) {
			g2d.setColor(Color.RED);

			int x = (int) Math.round(targetPlace.getX() * zoomFactor);
			int y = (int) Math.round(targetPlace.getY() * zoomFactor);

			g2d.drawLine(x - 2, y + 2, x + 2, y - 2);
			g2d.drawLine(x - 2, y - 2, x + 2, y + 2);
		}
	}

	@Override
	public void paintComponent(Graphics g) {

		super.paintComponent(g);
		doDrawing(g);
	}

	public DebugPanel(BorderLayout borderLayout, Processor processor, double zoomFactor) {
		super(borderLayout);
		this.zoomFactor = zoomFactor;
		this.processor = processor;
		this.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		this.setBackground(new java.awt.Color(255, 255, 255));
	}
}
