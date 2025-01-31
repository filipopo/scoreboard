package com.filip.scoreboard;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class Gui extends JFrame {
  public Gui(Synonyms synonyms) {
    // TeamManager instance
    TeamManager manager = new TeamManager();
    manager.addTeam();

    // Set the title of the JFrame
    setTitle("Scoreboard");
    setSize(1280, 720);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLayout(new BorderLayout());

    // Label for the title
    JLabel titleLabel = new JLabel("Scoreboard Application", javax.swing.SwingConstants.CENTER);
    titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
    add(titleLabel, BorderLayout.NORTH);

    // Table to display players and their scores
    String[] columnNames = {"Player", "Team", "Score"};
    DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);

    JTable table = new JTable(tableModel);
    JScrollPane tableScrollPane = new JScrollPane(table);
    add(tableScrollPane, BorderLayout.CENTER);

    // Button to add a player
    JButton addPlayerButton = new JButton("Add a player");
    addPlayerButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        manager.addPlayer("1", 1);
        Player p = manager.getPlayer(Integer.toString(manager.getPlayer().size()));
        tableModel.addRow(new Object[]{p.getId(), 1, p.getScore()});
      }
    });

    // Panel to hold buttons
    JPanel buttonPanel = new JPanel();
    buttonPanel.add(addPlayerButton);

    // Add panel to the bottom
    add(buttonPanel, BorderLayout.SOUTH);

    // Make the frame visible
    setVisible(true);
  }
}
