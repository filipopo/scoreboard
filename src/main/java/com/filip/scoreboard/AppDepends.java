package com.filip.scoreboard;

import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.concurrent.ThreadLocalRandom;
import java.io.File;
import java.io.IOException;
import com.fasterxml.jackson.databind.ObjectMapper;

class Player {
  public Player(int index, int team, int score) {
    this.index = index;
    this.team = team;
    this.score = score;
  }

  private int index;
  private int team;
  private int score;

  public int getIndex() {
    return index;
  }

  public int getTeam() {
    return team;
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

  public List<Player> getPlayer() {
    return player;
  }

  public Player getPlayer(int i) {
    return player.get(i);
  }

  public void addPlayer(Player p) {
    player.add(p);
  }

  public int getScore() {
    int sum = 0;
    for(Player p : player)
      sum += p.getScore();

    return sum;
  }

  public int getScore(int p) {
    return player.get(p).getScore();
  }
}

class TeamManager {
  private ArrayList<Team> team = new ArrayList<Team>();
  private ArrayList<Player> player = new ArrayList<Player>();

  public List<Team> getTeam() {
    return team;
  }

  public Team getTeam(int i) {
    return team.get(i);
  }

  public void addTeam() {
    team.add(new Team(team.size() + 1));
  }

  public List<Player> getPlayer() {
    return player;
  }

  public Player getPlayer(int i) {
    return player.get(i);
  }

  public void addPlayer(int i, int score) {
    Player p = new Player(player.size() + 1, i + 1, score);
    team.get(i).addPlayer(p);
    player.add(p);
  }

  public void sortTeams() {
    for (int i = 0; i < team.size() - 1; i++) {
      for (int j = 0; j < team.size() - i - 1; j++) {
        int score = team.get(j).getScore();
        int nextScore = team.get(j + 1).getScore();

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

  private String random(String[] arr) {
    int random = ThreadLocalRandom.current().nextInt(0, arr.length);
    return arr[random];
  }

  private String[] enter,
    player,
    team,
    score,
    teamVictory,
    playerVictory;
  
  public String getEnter() {
    return random(this.enter);
  }

  public String getPlayer() {
    return random(this.player);
  }

  public String getTeam() {
    return random(this.team);
  }

  public String getScore() {
    return random(this.score);
  }

  public String getTeamVictory() {
    return random(this.teamVictory);
  }

  public String getPlayerVictory() {
    return random(this.playerVictory);
  }

  private String enterTeam,
    enterPlayer,
    playerScore,
    victoryMsg,
    playerRank;

  public String getEnterTeam() {
    return this.enterTeam;
  }

  public String getEnterPlayer() {
    return this.enterPlayer;
  }

  public String getPlayerScore() {
    return this.playerScore;
  }

  public String getVictoryMsg() {
    return this.victoryMsg;
  }

  public String getPlayerRank() {
    return this.playerRank;
  }

  public void loadConf(String path) {
    ObjectMapper objectMapper = new ObjectMapper();
    File file = new File(path);
    Synonyms messages = null;

    try {
      messages = objectMapper.readValue(file, Synonyms.class);
    } catch (IOException e) {
      Logger logger = Logger.getLogger(getClass().getName());
      logger.severe("Couldn't read " + file);
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
}
