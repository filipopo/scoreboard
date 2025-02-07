package com.filip.scoreboard;

import java.util.Scanner;

class Cli {
  public Cli(Synonyms s) {
    print(s.getEnterTeam(), s.getEnter(), s.getTeam());
    int n = validInt();
    TeamManager manager = new TeamManager();

    for(int i = 1; i <= n; i++) {
      print("\n%s %d:", s.getTeam(), i);

      print(s.getEnterPlayer(), s.getEnter(), s.getPlayer(), s.getTeam());
      int num = validInt();

      for (int j = 0; j < num; j++) {
        print(s.getPlayerScore(), s.getEnter(), s.getPlayer(), manager.getPlayer().size() + 1, s.getScore());
        manager.addPlayer(Integer.toString(i)).addScore(validInt());
      }
    }

    manager.sortTeams();
    System.out.println('\n');
    print(s.getVictoryMsg(), s.getTeamVictory(), s.getTeam());

    n = 1;
    for (Team t : manager.getTeam()) {
      print("#%d - %s %s:", n, s.getTeam(), t.getId());

      for (Player p : t.getPlayer())
        print(s.getPlayerRank(), s.getPlayer(), p.getId(), s.getScore(), p.getScore());

      print("--\n%d %s\n", t.getScore(), s.getScore());
      n++;
    }

    manager.sortPlayers();
    print(s.getVictoryMsg(), s.getPlayerVictory(), s.getPlayer());

    n = 1;
    for (Player p : manager.getPlayer()) {
      System.out.printf("#%d - ", n);
      print(s.getPlayerRank(), s.getPlayer(), p.getId(), s.getScore(), p.getScore());
      n++;
    }
  }

  private void print(String s, Object... args) {
    System.out.println(String.format(s, args));
  }

  private int validInt() {
    Scanner input = new Scanner(System.in);
    while (!input.hasNextInt()) {
      System.out.println("You need to enter a number");
      input.next();
    }

    return input.nextInt();
  }
}
