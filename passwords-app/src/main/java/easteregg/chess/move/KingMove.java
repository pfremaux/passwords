package easteregg.chess.move;

import java.util.Collections;

public class KingMove {

    public static FullMove[] fullMove = {
            new FullMove(
                    Collections.singletonList(Move.LEFT),
                    true,
                    false,
                    false,
                    false),
            new FullMove(
                    Collections.singletonList(Move.TOP),
                    false,
                    false,
                    false,
                    false),
            new FullMove(
                    Collections.singletonList(Move.RIGHT),
                    false,
                    false,
                    false,
                    false),
            new FullMove(
                    Collections.singletonList(Move.BOTTOM),
                    false,
                    false,
                    false,
                    false),
    };

}
