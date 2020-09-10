package easteregg.chess.move;

import java.util.Collections;

public class QueenMove {

    public static FullMove[] fullMove = {
            new FullMove(
                    Collections.singletonList(Move.TOP_LEFT),
                    true,
                    true,
                    false,
                    false),
            new FullMove(
                    Collections.singletonList(Move.TOP_RIGHT),
                    false,
                    true,
                    false,
                    false),
            new FullMove(
                    Collections.singletonList(Move.BOTTOM_LEFT),
                    false,
                    true,
                    false,
                    false),
            new FullMove(
                    Collections.singletonList(Move.BOTTOM_RIGHT),
                    false,
                    true,
                    false,
                    false),
            new FullMove(
                    Collections.singletonList(Move.LEFT),
                    true,
                    true,
                    false,
                    false),
            new FullMove(
                    Collections.singletonList(Move.TOP),
                    false,
                    true,
                    false,
                    false),
            new FullMove(
                    Collections.singletonList(Move.RIGHT),
                    false,
                    true,
                    false,
                    false),
            new FullMove(
                    Collections.singletonList(Move.BOTTOM),
                    false,
                    true,
                    false,
                    false),
    };

}
