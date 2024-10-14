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

  public int index;
  public int score;
}

class Team {
  public Team(int index) {
    this.index = index;
  }

  public int index;
  public ArrayList<Player> player = new ArrayList<Player>();

  public int score() {
    int sum = 0;
    for(Player p : player)
      sum += p.score;

    return sum;
  }
}

class TeamManager {
  ArrayList<Team> team = new ArrayList<Team>();
  ArrayList<Player> player = new ArrayList<Player>();

  public void addTeam() {
    team.add(new Team(team.size() + 1));
  }

  public void addPlayer(int i, int score) {
    Player p = new Player(player.size() + 1, score);
    team.get(i).player.add(p);
    player.add(p);
  }

  public void sortTeams() {
    for (int i = 0; i < team.size() - 1; i++) {
      for (int j = 0; j < team.size() - i - 1; j++) {
        if (team.get(j).score() < team.get(j + 1).score()) {
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
        if (player.get(j).score < player.get(j + 1).score) {
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
