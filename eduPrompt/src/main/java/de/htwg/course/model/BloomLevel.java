package de.htwg.course.model;




public enum BloomLevel {
    NONE(0),
    REMEMBERING(1),
    UNDERSTANDING(2),
    APPLYING(3),
    ANALYZING(4),
    EVALUATING(5),
    CREATING(6);

    private final int level;

    BloomLevel(int level) {
        this.level = level;
    }

    public int getLevel() {
        return this.level;
    }

    public static BloomLevel fromLevel(int level) {
        for (BloomLevel bloomLevel : BloomLevel.values()) {
            if (bloomLevel.level == level) {
                return bloomLevel;
            }
        }
        // Fallback to NONE if an invalid level is passed
        return NONE;
    }
}