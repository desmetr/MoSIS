/**
 * Copyright (c) 2016 committers of YAKINDU and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * 	committers of YAKINDU - initial API and implementation
 * 
 */
package train;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import train.traincontroller.TrainControllerStatemachine;

public class RailwayPanel extends JPanel {

	private BufferedImage train;
	private BufferedImage redLight;
	private BufferedImage greenLight;
	private BufferedImage yellowLight;
	private BufferedImage station;
	private BufferedImage railway;
	private List<Map<String, Object>> items = new LinkedList<Map<String, Object>>();
	
	private double travelled_x = 0.0;
	private int travelled_x_int = 0;
	private double next_light = 0.0;
	private double remainder = 0.0;
	private int next_station = 1000;
	private double accelaration = 0.0;
	private int accumulated_delta = 0;
	private int delta;
	private int counter = 0;
	private TrainControllerStatemachine statemachine;
	
	public void setStatemachine(TrainControllerStatemachine statemachine) {
		this.statemachine = statemachine;
	}

	private static int WIDTH_RAILWAY = 20;
	
	public RailwayPanel() {
		setBackground(Color.WHITE);
		try {
			train = ImageIO.read(new File("imgs/train.gif"));
			redLight = ImageIO.read(new File("imgs/red.gif"));
			greenLight = ImageIO.read(new File("imgs/green.gif"));
			yellowLight = ImageIO.read(new File("imgs/yellow.gif"));
			station = ImageIO.read(new File("imgs/station.gif"));
			railway = ImageIO.read(new File("imgs/rail.gif"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static final long serialVersionUID = 1L;

	@Override
	public void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);
		Graphics2D g2d = (Graphics2D) graphics.create();
		
		this.counter += 1;
		
		int x = 0;
		while (x <= 1000 + WIDTH_RAILWAY) {
			int calculated_x = x + accumulated_delta;
			if (calculated_x < -WIDTH_RAILWAY) {
				accumulated_delta += WIDTH_RAILWAY;
			}
			g2d.drawImage(railway, x + accumulated_delta, 50, this);
			x += WIDTH_RAILWAY;
		}
		
		ListIterator<Map<String, Object>> iter = items.listIterator();
		while (iter.hasNext()) {
			Map<String, Object> item = iter.next();
			item.put("x", ((int) item.get("x")) + delta);
			if ((int) item.get("x") > -200) {
				if (item.get("type") == "STATION") {
					g2d.drawImage(station, (int) item.get("x"), 100, this);
					if ((int) item.get("x") < 30 && !(boolean) item.get("entered")) {
						statemachine.getSCInterface().raiseEnter();
						item.put("entered", true);
					} else if ((int) item.get("x") < -170 && !(boolean) item.get("left")) {
						statemachine.raiseLeave();
						item.put("left", true);
					}
				} else if (item.get("type") == "REDLIGHT") {
					if (counter >= (int) item.get("toYellow")) {
						item.put("type", "YELLOWLIGHT");
						item.put("toGreen", (int) (this.counter + Math.random() * 250));
						g2d.drawImage(yellowLight, (int) item.get("x"), 10, this);
					} else {
						g2d.drawImage(redLight, (int) item.get("x"), 10, this);
						if ((int) item.get("x") < 30 && !(boolean) item.get("marked")) {
							statemachine.getSCInterface().raiseRed_light();
							item.put("marked", true);
						}
					}
				} else if (item.get("type") == "YELLOWLIGHT") {
					if (counter >= (int) item.get("toGreen")) {
						item.put("type", "GREENLIGHT");
						g2d.drawImage(greenLight, (int) item.get("x"), 10, this);
					} else {
						g2d.drawImage(yellowLight, (int) item.get("x"), 10, this);
						if ((int) item.get("x") < 30 && !(boolean) item.get("marked")) {
							statemachine.getSCInterface().raiseYellow_light();
							item.put("marked", true);
						}
					}
				} else {
					g2d.drawImage(greenLight, (int) item.get("x"), 10, this);
					if ((int) item.get("x") < 30 && !(boolean) item.get("marked")) {
						statemachine.getSCInterface().raiseGreen_light();
						item.put("marked", true);
					}
				}
			} else {
				iter.remove();
			}
		}
		
		if (this.next_station < this.travelled_x) {
			Map<String, Object> item = new HashMap<String, Object>();
			item.put("x", 1000);
			item.put("type", "STATION");
			item.put("entered", false);
			item.put("left", false);
			g2d.drawImage(station, (int) item.get("x"), 100, this);
			items.add(item);
			this.next_station += Math.random() * 3000 + 2000;
		}
		
		if (this.next_light < this.travelled_x) {
			Map<String, Object> item = new HashMap<String, Object>();
			item.put("x", 1000);
			item.put("type", "REDLIGHT");
			item.put("toYellow", (int) (this.counter + Math.random() * 250));
			item.put("marked", false);
			g2d.drawImage(redLight, (int) item.get("x"), 10, this);
			items.add(item);
			this.next_light += 500;
		}
		
		g2d.drawImage(train, 0, 40, this);
		g2d.dispose();
	}
	
	public void setDelta(int delta) {
		this.delta = delta;
		accumulated_delta += delta;
	}
	
	public void setTravelledX(double travelled_x) {
		this.travelled_x = travelled_x;
	}

}
