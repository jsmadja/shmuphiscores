package formatters;

import models.Difficulty;
import models.Mode;

public class KeyFormatter {

    public static String unique(Difficulty difficulty, Mode mode) {
        String difficultyId = "";
        if (difficulty != null) {
            difficultyId = difficulty.id.toString();
        }
        String modeId = "";
        if (mode != null) {
            modeId = mode.id.toString();
        }
        return difficultyId + "_" + modeId;
    }

}
