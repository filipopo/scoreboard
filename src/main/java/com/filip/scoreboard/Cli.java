package com.filip.scoreboard;

import java.util.Scanner;

class Cli {
  public static void print(String s, Object... args) {
    System.out.println(String.format(s, args));
  }

  public Cli(Synonyms s) {
    Scanner input = new Scanner(System.in);

    print(s.getEnterTeam(), s.getEnter(), s.getTeam());
    int n = input.nextInt();
    TeamManager manager = new TeamManager();

    for(int i = 0; i < n; i++) {
      manager.addTeam();
      print("\n%s %d:", s.getTeam(), i + 1);

      print(s.getEnterPlayer(), s.getEnter(), s.getPlayer(), s.getTeam());
      int num = input.nextInt();

      for (int j = 1; j <= num; j++) {
        print(s.getPlayerScore(), s.getEnter(), s.getPlayer(), j, s.getScore());
        manager.addPlayer(i, input.nextInt());
      }
    }

    input.close();
    manager.sortTeams();

    System.out.println('\n');
    print(s.getVictoryMsg(), s.getTeamVictory(), s.getTeam());

    n = 1;
    for (Team t : manager.getTeam()) {
      print("#%d - %s %d:", n, s.getTeam(), t.getIndex());

      int i = 1;
      for (Player p : t.getPlayer()) {
        print(s.getPlayerRank(), s.getPlayer(), i, s.getScore(), p.getScore());
        i++;
      }

      print("--\n%d %s\n", t.getScore(), s.getScore());
      n++;
    }

    manager.sortPlayers();
    print(s.getVictoryMsg(), s.getPlayerVictory(), s.getPlayer());

    n = 1;
    for (Player p : manager.getPlayer()) {
      System.out.printf("#%d - ", n);
      print(s.getPlayerRank(), s.getPlayer(), p.getIndex(), s.getScore(), p.getScore());
      n++;
    }
  }
}
