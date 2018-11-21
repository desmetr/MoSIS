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

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * 
 * @author Tesch
 *
 */

public class InfoPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JLabel infoLabel;
	private JLabel doorsLabel;

	public InfoPanel() {
		createContents();
	}

	private void createContents() {
		setLayout(new BorderLayout());
		infoLabel = new JLabel(" ", SwingConstants.CENTER);
		add(BorderLayout.NORTH, infoLabel);
		doorsLabel = new JLabel("Doors are CLOSED", SwingConstants.CENTER);
		add(BorderLayout.SOUTH, doorsLabel);
	}
	
	public void setDoorsMessage(String msg) {
		doorsLabel.setText(msg);
	}
	
	public void setVelocityMessage(double velocity) {
		infoLabel.setText(String.format("Velocity: %.2f", velocity));
	}
}
