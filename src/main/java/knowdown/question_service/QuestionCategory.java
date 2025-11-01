package knowdown.question_service;

import java.util.HashMap;
import java.util.Map;

public enum QuestionCategory {
    GENERAL_KNOWLEDGE("General Knowledge"),
    BOOKS("Entertainment: Books"),
    FILM("Entertainment: Film"),
    MUSIC("Entertainment: Music"),
    THEATRES("Entertainment: Musicals &amp; Theatres"),
    TELEVISION("Entertainment: Television"),
    VIDEO_GAMES("Entertainment: Video Games"),
    BOARD_GAMES("Entertainment: Board Games"),
    SCIENCE_NATURE("Science &amp; Nature"),
    COMPUTERS("Science: Computers"),
    MATHEMATICS("Science: Mathematics"),
    MYTHOLOGY("Mythology"),
    SPORTS("Sports"),
    GEOGRAPHY("Geography"),
    HISTORY("History"),
    POLITICS("Politics"),
    ART("Art"),
    CELEBRITIES("Celebrities"),
    ANIMALS("Animals"),
    VEHICLES("Vehicles"),
    COMICS("Entertainment: Comics"),
    GADGETS("Science: Gadgets"),
    ANIME("Entertainment: Japanese Anime &amp; Manga"),
    CARTOONS("Entertainment: Cartoon &amp; Animations");

    private final String name;
    public String getName() {
        return this.name;
    }
    QuestionCategory(String name) {
        this.name = name;
    }

    private static final Map<String, QuestionCategory> map = new HashMap<>();

    static {
        for (QuestionCategory c : values()) {
            map.put(c.getName(), c);
        }
    }

    public static QuestionCategory getFromName(String name) {
        return map.get(name);
    }





}
