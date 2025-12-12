package knowdown.question_service;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.stream.Stream;


public enum QuestionCategory {
    GENERAL_KNOWLEDGE("General Knowledge"),
    BOOKS("Entertainment: Books"),
    MOVIES("Entertainment: Film"),
    MUSIC("Entertainment: Music"),
    THEATRES("Entertainment: Musicals & Theatres"),
    TELEVISION("Entertainment: Television"),
    VIDEO_GAMES("Entertainment: Video Games"),
    BOARD_GAMES("Entertainment: Board Games"),
    SCIENCE_NATURE("Science & Nature"),
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
    ANIME("Entertainment: Japanese Anime & Manga"),
    CARTOONS("Entertainment: Cartoon & Animations");

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

@JsonValue
    public String getDisplayName() {
        return name;
    }
    
private static final Map<String, QuestionCategory> BY_DISPLAY_NAME = 
        Stream.of(values())
              .collect(Collectors.toUnmodifiableMap(
                  QuestionCategory::getDisplayName,
                  c -> c
              ));

    @JsonCreator
    public static QuestionCategory fromDisplayName(String displayName) {
        if (displayName == null) {
            return null;
        }
        QuestionCategory category = BY_DISPLAY_NAME.get(displayName);
        if (category == null) {
            throw new IllegalArgumentException("Unknown category: " + displayName);
        }
        return category;
    }

}
