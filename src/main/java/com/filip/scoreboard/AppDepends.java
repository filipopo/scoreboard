package com.filip.scoreboard;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.ToIntFunction;
import java.util.logging.Logger;
import java.io.File;
import java.io.IOException;
import com.fasterxml.jackson.databind.ObjectMapper;

interface Scorable {
  String getId();
  void setId(String id);
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

  public int getScore() {
    return getScore(0, score.size() - 1);
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

  public int getScore() {
    return player.stream().mapToInt(Player::getScore).sum();
  }

  public int getScore(int start, int end) {
    return player.stream().mapToInt(p -> p.getScore(start, end)).sum();
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
    return addPlayer(
      Integer.toString(player.size() + 1),
      id
    );
  }

  public Player addPlayer(String pId, String tId) {
    Team t = team.get(tId);
    if (t == null)
      t = addTeam(tId);

    Player p = new Player(pId, t);
    player.put(pId, p);
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

  // Sort by scores in descending order and recreate the LinkedHasMap
  private <T extends Scorable> void sortByValue(Map<String, T> linkedHm, ToIntFunction<T> fun) {
    List<T> sortedHm = new ArrayList<>(linkedHm.values());
    sortedHm.sort((a, b) -> Integer.compare(fun.applyAsInt(b), fun.applyAsInt(a)));

    linkedHm.clear();
    for (T e : sortedHm)
      linkedHm.put(e.getId(), e);
  }

  public void sortTeams() {
    sortByValue(team, Team::getScore);
  }

  public void sortTeams(int start, int end) {
    sortByValue(team, t -> t.getScore(start, end));
  }

  public void sortPlayers() {
    sortByValue(player, Player::getScore);
  }

   public void sortPlayers(int start, int end) {
    sortByValue(player, p -> p.getScore(start, end));
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
