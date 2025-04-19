package com.filip.scoreboard;

public class App {
  public static void main(String[] args) {
    String path = "en.json";
    boolean cli = false;

    for (String arg : args) {
      if (arg.equalsIgnoreCase("--cli")) {
        cli = true;
      } else {
        path = arg;
      }
    }

    Synonyms s = Synonyms.instance();
    s.loadConf(path);

    if (cli)
      new Cli();
    else
      new Gui();
  }
}
