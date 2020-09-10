package easteregg.chess.move;

import java.util.Arrays;
import java.util.Collections;

public class PionMove {
    public static FullMove[] fullMove = {
            new FullMove(
                    Arrays.asList(Move.TOP_FIRST_MOVE, Move.TOP_FIRST_MOVE),
                    true,
                    false,
                    false,
                    true),
            new FullMove(
                    Collections.singletonList(Move.TOP),
                    true,
                    false,
                    false,
                    false),
    };
}
