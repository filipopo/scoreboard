package com.filip.scoreboard;

public class App {
  public static void main(String args[]) {
    String path;
    boolean cli = false;

    if (args.length > 0) {
      if (args.length > 1)
        cli = args[1].toLowerCase().equals("--cli");

      path = args[0];
    } else
      path = "en.json";

    Synonyms s = Synonyms.instance();
    s.loadConf(path);

    if (cli)
      new Cli(s);
    else
      new Gui(s);
  }
}
