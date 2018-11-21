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
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Dictionary;
import java.util.Hashtable;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;

/**
 * 
 * @author Tesch
 *
 */

public class ButtonPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private JLabel infoLabel;
	private JButton continuebtn;
	private JButton pause;
	private JButton open;
	private JButton close;
	private JButton poll;
	private JSlider speed;

	public ButtonPanel() {
		setLayout(new GridBagLayout());
		setMinimumSize(new Dimension(1000, 50));
		createContents();
	}

	private void createContents() {
		GridBagConstraints c = new GridBagConstraints();
		
		infoLabel = new JLabel(" ", SwingConstants.CENTER);
		infoLabel.setBackground(Color.LIGHT_GRAY);
		infoLabel.setOpaque(true);
		c.fill = GridBagConstraints.HORIZONTAL;
	    c.gridx = 0;
	    c.gridy = 0;
	    c.gridwidth = 2;
	    c.anchor = GridBagConstraints.CENTER;
	    c.ipadx = 420;
		add(infoLabel, c);
		
		speed = new JSlider(JSlider.HORIZONTAL, -100, 100, 0);
		Dictionary<Integer, JLabel> dict = new Hashtable<Integer, JLabel>();
		for (int i = -100; i <= 100; i += 10) {  
			dict.put(i, new JLabel(Double.toString((double) i / 100)));
		}
		speed.setLabelTable(dict);
		speed.setMajorTickSpacing(10);
		speed.setMinorTickSpacing(1);
		speed.setPaintTrack(false);
		speed.setPaintTicks(true);
		speed.setPaintLabels(true);
		c.fill = GridBagConstraints.HORIZONTAL;
	    c.gridx = 0;
	    c.gridy = 1;
	    c.gridwidth = 2;
	    c.anchor = GridBagConstraints.CENTER;
	    add(speed, c);
		
		continuebtn = new JButton();
		continuebtn.setText("continue");
		c.fill = GridBagConstraints.HORIZONTAL;
	    c.gridx = 0;
	    c.gridy = 2;
	    c.gridwidth = 1;
	    c.anchor = GridBagConstraints.PAGE_START;
		add(continuebtn, c);
		
		pause = new JButton();
		pause.setText("pause");
		c.fill = GridBagConstraints.HORIZONTAL;
	    c.gridx = 1;
	    c.gridy = 2;
	    c.gridwidth = 1;
	    c.anchor = GridBagConstraints.PAGE_END;
		add(pause, c);
		
		open = new JButton();
		open.setText("open");
		c.fill = GridBagConstraints.HORIZONTAL;
	    c.gridx = 0;
	    c.gridy = 3;
	    c.gridwidth = 1;
	    c.anchor = GridBagConstraints.PAGE_START;
		add(open, c);
		
		close = new JButton();
		close.setText("close");
		c.fill = GridBagConstraints.HORIZONTAL;
	    c.gridx = 1;
	    c.gridy = 3;
	    c.gridwidth = 1;
	    c.anchor = GridBagConstraints.PAGE_END;
		add(close, c);
		
		poll = new JButton();
		poll.setText("poll");
		c.fill = GridBagConstraints.HORIZONTAL;
	    c.gridx = 0;
	    c.gridy = 4;
	    c.gridwidth = 2;
	    c.anchor = GridBagConstraints.CENTER;
		add(poll, c);
	}

	public void setInfoMessage(String msg, String severity) {
		infoLabel.setText(msg);
		if (severity == "WARNING") {
			infoLabel.setBackground(Color.YELLOW);
		} else if (severity == "ERROR") {
			infoLabel.setBackground(Color.RED);
		}
		repaint();
	}
	
	public void clearInfoMessage() {
		infoLabel.setText(" ");
		infoLabel.setBackground(Color.LIGHT_GRAY);
	}
	
	public JButton getContinuebtn() {
		return continuebtn;
	}

	public JButton getPause() {
		return pause;
	}

	public JButton getOpen() {
		return open;
	}

	public JButton getClose() {
		return close;
	}

	public JButton getPoll() {
		return poll;
	}

	public JSlider getSpeed() {
		return speed;
	}
}
