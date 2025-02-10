package com.filip.scoreboard;

import java.util.Scanner;
import java.util.function.Supplier;

class Cli {
  public Cli() {
    System.out.println(s.getEnterTeam());
    int n = validInt(s::getTeam);
    TeamManager manager = new TeamManager();

    for(int i = 1; i <= n; i++) {
      print("\n%s %d:", s.getTeam(), i);

      System.out.println(s.getEnterPlayer());
      int num = validInt(s::getPlayer);

      for (int j = 0; j < num; j++) {
        print(s.getPlayerScore(), manager.getPlayer().size() + 1);
        Player p = manager.addPlayer(Integer.toString(i));
        p.addScore(validInt(s::getScore));
      }
    }

    manager.sortTeams();
    System.out.println('\n');
    print(s.getVictoryMsg(), s.getTeamVictory(), s.getTeam());

    n = 1;
    for (Team t : manager.getTeam()) {
      print("#%d - %s %s:", n, s.getTeam(), t.getId());

      for (Player p : t.getPlayer())
        print(s.getPlayerRank(), p.getId(), p.getScore());

      print("--\n%d %s\n", t.getScore(), s.getScore());
      n++;
    }

    manager.sortPlayers();
    print(s.getVictoryMsg(), s.getPlayerVictory(), s.getPlayer());

    n = 1;
    for (Player p : manager.getPlayer()) {
      System.out.printf("#%d - ", n);
      print(s.getPlayerRank(), p.getId(), p.getScore());
      n++;
    }
  }

  private Synonyms s = Synonyms.instance();

  private void print(String s, Object... args) {
    System.out.println(String.format(s, args));
  }

  private int validInt(Supplier<String> fun) {
    Scanner input = new Scanner(System.in);
    while (!input.hasNextInt()) {
      print(s.getValidInt(), fun.get());
      input.next();
    }

    return input.nextInt();
  }
}
