package com.filip.scoreboard;

import java.util.ArrayList;
import java.util.List;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;

class Gui extends JFrame {
  public Gui() {
    // Set the title of the JFrame
    setTitle(s.getGame());
    setSize(1280, 720);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLayout(new BorderLayout());

    // Table to display players and their scores
    String[] colNames = {
      s.getPlayer(),
      s.getTeam(),
      String.format("%s %s", s.getRound(), s.getScore()),
      String.format(s.getTotal(), s.getScore())
    };

    DefaultTableModel tableModel = new DefaultTableModel(colNames, 0) {
      @Override
      public boolean isCellEditable(int row, int col) {
        return col != Col.TOTAL.getNum();
      }
    };

    JTable table = new JTable(tableModel);
    setCustomCells(table);

    // Label for the title
    JLabel titleLabel = new JLabel(title + "1", SwingConstants.CENTER);
    titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
    add(titleLabel, BorderLayout.NORTH);

    // Table to display players and their scores
    tableModel.addTableModelListener(e -> {
      int row = e.getFirstRow();
      int col = e.getColumn();

      if (row >= 0 && col >= 0) { // Ignore invalid events
        Player p = manager.getPlayer(playerId.get(row));
        String data = tableModel.getValueAt(row, col).toString();

        Col name = Col.values()[col];
        switch (name) {
          case PLAYER:
            manager.updatePlayerId(p.getId(), data);
            playerId.set(row, data);
            break;
          case TEAM:
            Team t = manager.getTeam(data);
            if (t == null) {
              t = p.getTeam();

              if (t.getPlayer().size() > 1)
                t = manager.addTeam(data);
              else
                manager.updateTeamId(t.getId(), data);
            }

            p.getTeam().removePlayer(p);
            t.addPlayer(p);
            break;
          case SCORE:
            p.setScore(round, Integer.parseInt(data));
            tableModel.setValueAt(p.getScore(0, round), row, Col.TOTAL.getNum());
            break;
          default:
        }
      }
    });

    JScrollPane tableScrollPane = new JScrollPane(table);
    add(tableScrollPane, BorderLayout.CENTER);

    // Add button panel to the bottom
    add(createButtonPanel(tableModel, titleLabel), BorderLayout.SOUTH);

    // Add right-click context menu
    table.addMouseListener(new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent e) {
        showPopupMenu(e);
      }

      @Override
      public void mouseReleased(MouseEvent e) {
        showPopupMenu(e);
      }
    });

    // Make the frame visible
    setVisible(true);
  }

  private TeamManager manager = new TeamManager();
  private List<String> playerId = new ArrayList<>();
  private Synonyms s = Synonyms.instance();

  private int round = 0;
  private int lastRound = 0;
  private String title = String.format("%s, %s ", s.getGame(), s.getRound());

  // Panel to hold buttons
  private JPanel createButtonPanel(DefaultTableModel tableModel, JLabel titleLabel) {
    // Button to go back to the previous round
    JButton previousRoundButton = new JButton(String.format(s.getPrevious(), s.getRound()));
    previousRoundButton.addActionListener(e -> {
      if (round > 0) {
        titleLabel.setText(title + round);
        round--;
      } else
        return;

      for (int row = 0; row < tableModel.getRowCount(); row++) {
        Player p = manager.getPlayer(playerId.get(row));
        tableModel.setValueAt(p.getScore(round), row, Col.SCORE.getNum());
        tableModel.setValueAt(p.getScore(0, round), row, Col.TOTAL.getNum());
      }
    });

    // Button to sort by players
    JButton sortPlayersButton = new JButton(String.format(s.getSortBy(), s.getPlayer()));
    sortPlayersButton.addActionListener(e -> {
      playerId.clear();
      tableModel.setRowCount(0);
      manager.sortPlayers(0, round);

      for (Player p : manager.getPlayer()) {
        tableModel.addRow(new Object[] {
          p.getId(), p.getTeam().getId(), p.getScore(round), p.getScore(0, round)
        });

        playerId.add(p.getId());
      }
    });

    // Button to add a player
    JButton addPlayerButton = new JButton(String.format(s.getAdd(), s.getPlayer()));
    addPlayerButton.addActionListener(e -> {
      // Create a panel with a grid layout (2 columns: label and field)
      JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));

      // Create and add the fields
      String name = Integer.toString(manager.getPlayer().size() + 1);
      panel.add(new JLabel(String.format("%s:", s.getPlayer())));
      JTextField nameField = new JTextField(name);
      panel.add(nameField);

      panel.add(new JLabel(String.format("%s:", s.getTeam())));
      JTextField teamField = new JTextField("1");
      panel.add(teamField);

      panel.add(new JLabel(String.format("%s:", s.getScore())));
      JTextField scoreField = new JTextField("0");
      panel.add(scoreField);

      // Show a confirm dialog with the custom panel
      int result = JOptionPane.showConfirmDialog(
        this,
        panel,
        String.format("%s %s", s.getEnter(), s.getPlayer()),
        JOptionPane.OK_CANCEL_OPTION,
        JOptionPane.PLAIN_MESSAGE
      );

      if (result != JOptionPane.OK_OPTION)
        return;

      // Retrieve input values
      name = nameField.getText();
      String team = teamField.getText();
      String scoreStr = scoreField.getText();

      // Validate using our utility methods
      if (!Validator.isValidName(name, playerId, this) || !Validator.isValidScore(scoreStr, this))
        return;

      int score = Integer.parseInt(scoreStr);
      manager.addPlayer(name, team).setScore(round, score);

      playerId.add(name);
      tableModel.addRow(new Object[] {name, team, score, score});
    });

    // Button to sort by teams
    JButton sortTeamsButton = new JButton(String.format(s.getSortBy(), s.getTeam()));
    sortTeamsButton.addActionListener(e -> {
      playerId.clear();
      tableModel.setRowCount(0);
      manager.sortTeams(0, round);

      for (Team t : manager.getTeam()) {
        for (Player p : t.getPlayer()) {
          tableModel.addRow(new Object[] {
            p.getId(), t.getId(), p.getScore(round), p.getScore(0, round)
          });

          playerId.add(p.getId());
        }
      }
    });

    // Button to start next round
    JButton nextRoundButton = new JButton(String.format(s.getNext(), s.getRound()));
    nextRoundButton.addActionListener(e -> {
      round++;
      titleLabel.setText(title + (round + 1));

      for (int row = 0; row < tableModel.getRowCount(); row++) {
        Player p = manager.getPlayer(playerId.get(row));
        if (round > lastRound)
          p.setScore(round, 0);

        tableModel.setValueAt(p.getScore(round), row, Col.SCORE.getNum());
        tableModel.setValueAt(p.getScore(0, round), row, Col.TOTAL.getNum());
      }

      if (round > lastRound)
        lastRound = round;
    });

    // Button to go to last round
    JButton lastRoundButton = new JButton(String.format(s.getLast(), s.getRound()));
    lastRoundButton.addActionListener(e -> {
      round = lastRound;
      titleLabel.setText(title + (round + 1));

      for (int row = 0; row < tableModel.getRowCount(); row++) {
        Player p = manager.getPlayer(playerId.get(row));
        tableModel.setValueAt(p.getScore(round), row, Col.SCORE.getNum());
        tableModel.setValueAt(p.getScore(), row, Col.TOTAL.getNum());
      }
    });

    JPanel previousRoundPanel = new JPanel();
    previousRoundPanel.add(previousRoundButton);

    JPanel playerPanel = new JPanel();
    playerPanel.add(sortPlayersButton);
    playerPanel.add(addPlayerButton);
    playerPanel.add(sortTeamsButton);

    JPanel nextRoundPanel = new JPanel();
    nextRoundPanel.add(nextRoundButton);
    nextRoundPanel.add(lastRoundButton);

    JPanel panel = new JPanel(new BorderLayout());
    panel.add(previousRoundPanel, BorderLayout.WEST);
    panel.add(playerPanel, BorderLayout.CENTER);
    panel.add(nextRoundPanel, BorderLayout.EAST);

    return panel;
  }

  private void showPopupMenu(MouseEvent e) {
    if (!e.isPopupTrigger())
      return;

    JTable table = (JTable)e.getComponent();
    int row = table.rowAtPoint(e.getPoint());
    int col = table.columnAtPoint(e.getPoint());

    // Select the row where the click happened
    table.setRowSelectionInterval(row, row);

    JMenuItem editItem = new JMenuItem(s.getEdit());
    editItem.addActionListener(event -> {
      table.editCellAt(row, col);
      table.getEditorComponent().requestFocusInWindow();
    });

    JMenuItem deleteItem = new JMenuItem(s.getDelete());
    deleteItem.addActionListener(event -> {
      manager.removePlayer(playerId.get(row));
      ((DefaultTableModel)table.getModel()).removeRow(row);
      playerId.remove(row);
    });

    JPopupMenu popupMenu = new JPopupMenu();
    popupMenu.add(editItem);
    popupMenu.add(deleteItem);

    popupMenu.show(table, e.getX(), e.getY());
  }

  private void setCustomCells(JTable table) {
    TableColumnModel colModel = table.getColumnModel();

    colModel.getColumn(Col.PLAYER.getNum()).setCellRenderer(new BoldCellRenderer());

    colModel.getColumn(Col.PLAYER.getNum()).setCellEditor(
      new PlayerCellEditor(new JTextField(), playerId)
    );

    colModel.getColumn(Col.SCORE.getNum()).setCellEditor(
      new ScoreCellEditor(new JTextField())
    );
  }
}

class BoldCellRenderer extends DefaultTableCellRenderer {
  @Override
  public Component getTableCellRendererComponent(
    JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column
  ) {
    Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    c.setFont(new Font("Arial", Font.BOLD, 14));
    return c;
  }
}

class PlayerCellEditor extends DefaultCellEditor {
  public PlayerCellEditor(JTextField textField, List<String> playerId) {
    super(textField);
    this.playerId = playerId;
  }

  private List<String> playerId;

  @Override
  public boolean stopCellEditing() {
    if (!Validator.isValidName((String)getCellEditorValue(), playerId, getComponent())) {
      cancelCellEditing();
      return false;
    }

    return super.stopCellEditing();
  }
}

class ScoreCellEditor extends DefaultCellEditor {
  public ScoreCellEditor(JTextField textField) {
    super(textField);
  }

  @Override
  public boolean stopCellEditing() {
    if (!Validator.isValidScore((String)getCellEditorValue(), getComponent())) {
      cancelCellEditing();
      return false;
    }

    return super.stopCellEditing();
  }
}

class Validator {
  private static Synonyms s = Synonyms.instance();

  // Validate that the player name isn't already in use.
  public static boolean isValidName(String name, List<String> existingNames, Component c) {
    if (existingNames.contains(name)) {
      JOptionPane.showMessageDialog(c, String.format(s.getExists(), s.getPlayer()));
      return false;
    }

    return true;
  }

  // Validate that the score string can be parsed into an integer.
  public static boolean isValidScore(String scoreStr, Component c) {
    try {
      Integer.parseInt(scoreStr);
    } catch (NumberFormatException e) {
      JOptionPane.showMessageDialog(c, String.format(s.getValidInt(), s.getScore()));
      return false;
    }

    return true;
  }
}

enum Col {
  PLAYER(0), TEAM(1), SCORE(2), TOTAL(3);

  Col(int num) {
    this.num = num;
  }

  private int num;

  public int getNum() {
    return num;
  }
}
