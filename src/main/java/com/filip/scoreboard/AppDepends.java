package com.filip.scoreboard;

import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;
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

  public List<Player> getPlayer() {
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

  public List<Team> getTeam() {
    return team;
  }

  public void addTeam() {
    team.add(new Team(team.size() + 1));
  }

  public List<Player> getPlayer() {
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

  public void setEnter(String[] enter) {
    this.enter = enter;
  }

  public String getPlayer() {
    return random(this.player);
  }

  public void setPlayer(String[] player) {
    this.player = player;
  }

  public String getTeam() {
    return random(this.team);
  }

  public void setTeam(String[] team) {
    this.team = team;
  }

  public String getScore() {
    return random(this.score);
  }

  public void setScore(String[] score) {
    this.score = score;
  }

  public String getTeamVictory() {
    return random(this.teamVictory);
  }

  public void setTeamVictory(String[] teamVictory) {
    this.teamVictory = teamVictory;
  }

  public String getPlayerVictory() {
    return random(this.playerVictory);
  }

  public void setPlayerVictory(String[] playerVictory) {
    this.playerVictory = playerVictory;
  }

  private String enterTeam,
    enterPlayer,
    playerScore,
    victoryMsg,
    playerRank;

  public String getEnterTeam() {
    return this.enterTeam;
  }

  public void setEnterTeam(String enterTeam) {
    this.enterTeam = enterTeam;
  }

  public String getEnterPlayer() {
    return this.enterPlayer;
  }

  public void setEnterPlayer(String enterPlayer) {
    this.enterPlayer = enterPlayer;
  }

  public String getPlayerScore() {
    return this.playerScore;
  }

  public void setPlayerScore(String playerScore) {
    this.playerScore = playerScore;
  }

  public String getVictoryMsg() {
    return this.victoryMsg;
  }

  public void setVictoryMsg(String victoryMsg) {
    this.victoryMsg = victoryMsg;
  }

  public String getPlayerRank() {
    return this.playerRank;
  }

  public void setPlayerRank(String playerRank) {
    this.playerRank = playerRank;
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

    setEnter(messages.enter);
    setPlayer(messages.player);
    setTeam(messages.team);
    setScore(messages.score);
    setTeamVictory(messages.teamVictory);
    setPlayerVictory(messages.playerVictory);
    setEnterTeam(messages.enterTeam);
    setEnterPlayer(messages.enterPlayer);
    setPlayerScore(messages.playerScore);
    setVictoryMsg(messages.victoryMsg);
    setPlayerRank(messages.playerRank);
  }
}
