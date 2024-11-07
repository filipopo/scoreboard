package com.filip.scoreboard;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.io.File;
import java.io.IOException;
import com.fasterxml.jackson.databind.ObjectMapper;

class Player {
  public Player(int index, int score) {
    this.index = index;
    this.score = score;
  }

  private int index;
  private int score;

  public int getIndex() {
    return index;
  }

  public int getScore() {
    return score;
  }
}

class Team {
  public Team(int index) {
    this.index = index;
  }

  private int index;
  private ArrayList<Player> player = new ArrayList<Player>();

  public int getIndex() {
    return index;
  }

  public ArrayList<Player> getPlayer() {
    return player;
  }

  public int score() {
    int sum = 0;
    for(Player p : player)
      sum += p.getScore();

    return sum;
  }
}

class TeamManager {
  private ArrayList<Team> team = new ArrayList<Team>();
  private ArrayList<Player> player = new ArrayList<Player>();

  public ArrayList<Team> getTeam() {
    return team;
  }

  public void addTeam() {
    team.add(new Team(team.size() + 1));
  }

  public ArrayList<Player> getPlayer() {
    return player;
  }

  public void addPlayer(int i, int score) {
    Player p = new Player(player.size() + 1, score);
    Team t = team.get(i);

    t.getPlayer().add(p);
    player.add(p);
  }

  public void sortTeams() {
    for (int i = 0; i < team.size() - 1; i++) {
      for (int j = 0; j < team.size() - i - 1; j++) {
        int score = team.get(j).score();
        int nextScore = team.get(j + 1).score();

        if (score < nextScore) {
          Team t = team.get(j);
          team.set(j, team.get(j + 1));
          team.set(j + 1, t);
        }
      }
    }
  }

  public void sortPlayers() {
    for (int i = 0; i < player.size() - 1; i++) {
      for (int j = 0; j < player.size() - i - 1; j++) {
        int score = player.get(j).getScore();
        int nextScore = player.get(j + 1).getScore();

        if (score < nextScore) {
          Player p = player.get(j);
          player.set(j, player.get(j + 1));
          player.set(j + 1, p);
        }
      }
    }
  }
}

class Synonyms {
  private Synonyms() {}

  private static Synonyms singleObj;

  public static Synonyms instance() {
    if (singleObj == null)
      singleObj = new Synonyms();

    return singleObj;
  }

  public String[] enter;
  public String[] player;
  public String[] team;
  public String[] score;
  public String[] teamVictory;
  public String[] playerVictory;
  public String enterTeam;
  public String enterPlayer;
  public String playerScore;
  public String victoryMsg;
  public String playerRank;

  public void loadConf(String path) {
    ObjectMapper objectMapper = new ObjectMapper();
    File file = new File(path);
    Synonyms messages = null;

    try {
      messages = objectMapper.readValue(file, Synonyms.class);
    } catch (IOException e) {
      System.out.println("Couldn't read " + file);
      System.exit(1);
    }

    this.enter = messages.enter;
    this.player = messages.player;
    this.team = messages.team;
    this.score = messages.score;
    this.teamVictory = messages.teamVictory;
    this.playerVictory = messages.playerVictory;
    this.enterTeam = messages.enterTeam;
    this.enterPlayer = messages.enterPlayer;
    this.playerScore = messages.playerScore;
    this.victoryMsg = messages.victoryMsg;
    this.playerRank = messages.playerRank;
  }

  public String random(String[] arr) {
    int random = ThreadLocalRandom.current().nextInt(0, arr.length);
    return arr[random];
  }
}
