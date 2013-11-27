package com.prjmoose.treeless.views;

import javax.swing.JPanel;

import java.awt.GridBagLayout;

import javax.swing.JButton;

import java.awt.CardLayout;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.AbstractAction;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import com.prjmoose.treeless.Game;
import com.prjmoose.treeless.main.GameLoop;
import com.prjmoose.treeless.main.Main;

public class SplashPanel extends JPanel {
	private final Action start = new SwingAction();

	/**
	 * Create the panel.
	 */
	public SplashPanel() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, 0.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{1.0, 0.0, 1.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		JButton btnStart = new JButton("Start");
		btnStart.setAction(start);
		GridBagConstraints gbc_btnStart = new GridBagConstraints();
		gbc_btnStart.insets = new Insets(0, 0, 5, 5);
		gbc_btnStart.gridx = 1;
		gbc_btnStart.gridy = 1;
		add(btnStart, gbc_btnStart);

	}

	private class SwingAction extends AbstractAction {
		public SwingAction() {
			putValue(NAME, "Start");
			putValue(SHORT_DESCRIPTION, "Start the game");
		}

		public void actionPerformed(ActionEvent e) {
			// Create game
			Game localWorld = new Game();
			
			// Create a game thread and start it
			GameLoop gl = new GameLoop(localWorld);
			Thread glt = gl.start();
			
			// Notify main thread that we now have a live game
			Main.setGameLoop(gl);
			
			// Create a local player
			PlayerInterfacePanel cam = new PlayerInterfacePanel(localWorld);
			Thread camt = cam.start();

			// Switch to game camera
			getParent().add(cam, "Game");
			((CardLayout) getParent().getLayout()).show(getParent(), "Game");

		}
	}
}
