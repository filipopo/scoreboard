package com.filip.scoreboard;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

class Gui extends JFrame {
  public Gui(Synonyms synonyms) {
    // Set the title of the JFrame
    setTitle("Scoreboard");
    setSize(1280, 720);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLayout(new BorderLayout());

    // Label for the title
    JLabel titleLabel = new JLabel("Scoreboard Application", SwingConstants.CENTER);
    titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
    add(titleLabel, BorderLayout.NORTH);

    // Table to display players and their scores
    tableModel.addTableModelListener(e -> {
      int row = e.getFirstRow();
      int col = e.getColumn();

      if (row >= 0 && col >= 0) { // Ignore invalid events
        Player player = manager.getPlayer(playerId.get(row));
        String data = tableModel.getValueAt(row, col).toString();

        Col name = Col.values()[col];
        switch (name) {
          case PLAYER:
            manager.updatePlayerId(player.getId(), data);
            playerId.set(row, data);
            break;
          case TEAM:
            Team t = manager.getTeam(data);
            if (t == null) {
              t = player.getTeam();

              if (t.getPlayer().size() > 1)
                t = manager.addTeam(data);
              else
                manager.updateTeamId(t.getId(), data);
            }

            player.getTeam().removePlayer(player);
            t.addPlayer(player);
            break;
          case SCORE:
            player.setScore(Integer.parseInt(data));
            break;
          default:
        }
      }
    });

    JScrollPane tableScrollPane = new JScrollPane(table);
    add(tableScrollPane, BorderLayout.CENTER);

    // Button to add a player
    addPlayerButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        manager.addPlayer("1", 0);
        Player p = manager.getPlayer(Integer.toString(manager.getPlayer().size()));
        playerId.add(p.getId());

        scores.put(p.getId(), 0);
        tableModel.addRow(new Object[] {p.getId(), 1, 0, 0});
      }
    });

    // Button to sort by players
    sortPlayersButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        clearPlayers();
        manager.sortPlayers();

        for (Player p : manager.getPlayer()) {
          tableModel.addRow(new Object[] {
            p.getId(), p.getTeam().getId(), p.getScore(), scores.get(p.getId())
          });

          playerId.add(p.getId());
        }
      }
    });

    // Button to sort by teams
    sortTeamsButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        clearPlayers();
        manager.sortTeams();

        for (Team t : manager.getTeam()) {
          for (Player p : t.getPlayer()) {
            tableModel.addRow(new Object[] {
              p.getId(), t.getId(), p.getScore(), scores.get(p.getId())
            });

            playerId.add(p.getId());
          }
        }
      }
    });

    // Button to start next round
    nextRoundButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        for (int row = 0; row < tableModel.getRowCount(); row++) {
          Player p = manager.getPlayer(playerId.get(row));
          scores.merge(p.getId(), p.getScore(), Integer::sum);
          p.setScore(0);

          tableModel.setValueAt(0, row, Col.SCORE.getNum());
          tableModel.setValueAt(scores.get(p.getId()), row, Col.TOTAL.getNum());
        }
      }
    });

    // Add button panel to the bottom
    add(createButtonPanel(), BorderLayout.SOUTH);

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
  private Map<String, Integer> scores = new HashMap<>();

  // Table to display players and their scores
  private String[] colNames = {"Player", "Team", "Round score", "Total score"};

  private DefaultTableModel tableModel = new DefaultTableModel(colNames, 0) {
    @Override
    public boolean isCellEditable(int row, int col) {
      return col != Col.TOTAL.getNum();
    }
  };

  private JTable table = new JTable(tableModel);

  private List<String> playerId = new ArrayList<String>();
  private void clearPlayers() {
    playerId.clear();
    tableModel.setRowCount(0);
  }

  private JButton addPlayerButton = new JButton("Add a player");
  private JButton sortPlayersButton = new JButton("Sort by players");
  private JButton sortTeamsButton = new JButton("Sort by teams");
  private JButton nextRoundButton = new JButton("Next round");

  // Panel to hold buttons
  private JPanel createButtonPanel() {
    JPanel playerPanel = new JPanel();
    playerPanel.add(sortPlayersButton);
    playerPanel.add(addPlayerButton);
    playerPanel.add(sortTeamsButton);

    JPanel roundPanel = new JPanel();
    roundPanel.add(nextRoundButton);

    JPanel panel = new JPanel(new BorderLayout());
    panel.add(playerPanel, BorderLayout.CENTER);
    panel.add(roundPanel, BorderLayout.EAST);

    return panel;
  }

  private void showPopupMenu(MouseEvent e) {
    if (!e.isPopupTrigger())
      return;

    int row = table.rowAtPoint(e.getPoint());
    int col = table.columnAtPoint(e.getPoint());

    // Select the row where the click happened
    table.setRowSelectionInterval(row, row);

    JMenuItem editItem = new JMenuItem("Edit cell");
    editItem.addActionListener(event -> {
      table.editCellAt(row, col);
      table.getEditorComponent().requestFocusInWindow();
    });

    JMenuItem deleteItem = new JMenuItem("Delete row");
    deleteItem.addActionListener(event -> {
      manager.removePlayer(playerId.get(row));
      tableModel.removeRow(row);
      playerId.remove(row);
    });

    JPopupMenu popupMenu = new JPopupMenu();
    popupMenu.add(editItem);
    popupMenu.add(deleteItem);

    popupMenu.show(table, e.getX(), e.getY());
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
