package hse.sachkov.learningtrackbackend.api.skill;

import java.util.HashMap;
import java.util.Map;

public enum SkillLevel {

    BEGINNER(1.0), INTERMEDIATE(2.0), EXPERT(3.0);

    private final double level;

    SkillLevel(double level) {
        this.level = level;
    }

    private static final Map<Double, SkillLevel> map;

    static {
        map = new HashMap<>();
        for (SkillLevel v : SkillLevel.values()) {
            map.put(v.level, v);
        }
    }

    public static SkillLevel findByLevel(double i) {
        return map.get(i);
    }
}
