package com.filip.scoreboard;

import java.util.Scanner;

class Cli {
  public static void print(String s, Object... args) {
    System.out.println(String.format(s, args));
  }

  public Cli(Synonyms s) {
    Scanner input = new Scanner(System.in);

    print(s.enterTeam, s.random(s.enter), s.random(s.team));
    int n = input.nextInt();
    TeamManager manager = new TeamManager();

    for(int i = 0; i < n; i++) {
      manager.addTeam();
      print("\n%s %d:", s.random(s.team), i + 1);

      print(s.enterPlayer, s.random(s.enter), s.random(s.player), s.random(s.team));
      int num = input.nextInt();

      for (int j = 1; j <= num; j++) {
        print(s.playerScore, s.random(s.enter), s.random(s.player), j, s.random(s.score));
        manager.addPlayer(i, input.nextInt());
      }
    }

    input.close();
    manager.sortTeams();

    System.out.println('\n');
    print(s.victoryMsg, s.random(s.teamVictory), s.random(s.team));

    n = 1;
    for (Team team : manager.getTeam()) {
      print("#%d - %s %d:", n, s.random(s.team), team.getIndex());

      int i = 1;
      for (Player p : team.getPlayer()) {
        print(s.playerRank, s.random(s.player), i, s.random(s.score), p.getScore());
        i++;
      }

      print("--\n%d %s\n", team.score(), s.random(s.score));
      n++;
    }

    manager.sortPlayers();
    print(s.victoryMsg, s.random(s.playerVictory), s.random(s.player));

    n = 1;
    for (Player p : manager.getPlayer()) {
      System.out.printf("#%d - ", n);
      print(s.playerRank, s.random(s.player), p.getIndex(), s.random(s.score), p.getScore());
      n++;
    }
  }
}
