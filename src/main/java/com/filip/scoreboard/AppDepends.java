package com.filip.scoreboard;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;
import java.io.File;
import java.io.IOException;
import com.fasterxml.jackson.databind.ObjectMapper;

interface Scorable {
  String getId();
  void setId(String id);
  int getScore();
}

class Player implements Scorable {
  public Player(String id, Team team) {
    this.id = id;
    this.team = team;
  }

  private String id;
  private Team team;
  private List<Integer> score = new ArrayList<>();;

  @Override
  public String getId() {
    return id;
  }

  @Override
  public void setId(String id) {
    this.id = id;
  }

  public Team getTeam() {
    return team;
  }

  public void setTeam(Team team) {
    this.team = team;
  }

  @Override
  public int getScore() {
    return score.stream().reduce(0, (a, b) -> a + b);
  }

  public int getScore(int round) {
    return score.get(round);
  }

  public int getScore(int start, int end) {
    int total = 0;
    for (; start <= end; start++)
      total += score.get(start);

    return total;
  }

  public void addScore(int newScore) {
    score.add(newScore);
  }

  public void setScore(int round, int newScore) {
    while (score.size() <= round)
      score.add(0);

    score.set(round, newScore);
  }
}

class Team implements Scorable {
  public Team(String id) {
    this.id = id;
  }

  private String id;
  private List<Player> player = new ArrayList<>();

  @Override
  public String getId() {
    return id;
  }

  @Override
  public void setId(String id) {
    this.id = id;
  }

  public List<Player> getPlayer() {
    return player;
  }

  public void addPlayer(Player p) {
    player.add(p);
    p.setTeam(this);
  }

  public void removePlayer(Player p) {
    player.remove(p);
    p.setTeam(null);
  }

  @Override
  public int getScore() {
    return player.stream().mapToInt(Player::getScore).sum();
  }
}

class TeamManager {
  private Map<String, Team> team = new LinkedHashMap<>();
  private Map<String, Player> player = new LinkedHashMap<>();

  public List<Team> getTeam() {
    return new ArrayList<>(team.values());
  }

  public Team getTeam(String id) {
    return team.get(id);
  }

  public Team addTeam(String id) {
    Team t = new Team(id);
    team.put(id, t);
    return t;
  }

  public void removeTeam(String id) {
    team.remove(id);
  }

  public List<Player> getPlayer() {
    return new ArrayList<>(player.values());
  }

  public Player getPlayer(String id) {
    return player.get(id);
  }

  public Player addPlayer(String id) {
    Team t = team.get(id);
    if (t == null)
      t = addTeam(id);

    Player p = new Player(Integer.toString(player.size() + 1), t);
    player.put(p.getId(), p);
    t.addPlayer(p);

    return p;
  }

  public void removePlayer(String id) {
    Player p = player.get(id);
    p.getTeam().removePlayer(p);
    player.remove(id);
  }

  private <T extends Scorable> void updateId(Map<String, T> hm, String id, String newId) {
    T entity = hm.remove(id); // Remove the old entry
    entity.setId(newId); // Update the ID
    hm.put(newId, entity); // Reinsert with the new key
  }

  public void updateTeamId(String id, String newId) {
    updateId(team, id, newId);
  }

  public void updatePlayerId(String id, String newId) {
    updateId(player, id, newId);
  }

  private <T extends Scorable> void sortByValue(Map<String, T> linkedHm) {
    List<T> sortedHm = new ArrayList<>(linkedHm.values());
    sortedHm.sort((e1, e2) -> Integer.compare(e2.getScore(), e1.getScore()));

    linkedHm.clear();
    for (T e : sortedHm)
      linkedHm.put(e.getId(), e);
  }

  // Sort by team scores in descending order and recreate the LinkedHasMap
  public void sortTeams() {
    sortByValue(team);
  }

  // Sort by player scores in descending order and recreate the LinkedHasMap
  public void sortPlayers() {
    sortByValue(player);
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
