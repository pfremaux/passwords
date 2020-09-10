package easteregg.chess.move;

import java.util.Arrays;

public class KnightMove {

    public static FullMove[] fullMove = {
            new FullMove(
                    Arrays.asList(Move.LEFT, Move.LEFT, Move.LEFT, Move.TOP),
                    true,
                    false,
                    true,
                    false),
            new FullMove(
                    Arrays.asList(Move.LEFT, Move.LEFT, Move.LEFT, Move.BOTTOM),
                    false,
                    false,
                    true,
                    false),
            new FullMove(
                    Arrays.asList(Move.RIGHT, Move.RIGHT, Move.RIGHT, Move.TOP),
                    true,
                    false,
                    true,
                    false),
            new FullMove(
                    Arrays.asList(Move.RIGHT, Move.RIGHT, Move.RIGHT, Move.BOTTOM),
                    false,
                    false,
                    true,
                    false),
            new FullMove(
                    Arrays.asList(Move.BOTTOM, Move.BOTTOM, Move.BOTTOM, Move.RIGHT),
                    false,
                    false,
                    true,
                    false
            ),
            new FullMove(
                    Arrays.asList(Move.BOTTOM, Move.BOTTOM, Move.BOTTOM, Move.LEFT),
                    false,
                    false,
                    true,
                    false
            ),
            new FullMove(
                    Arrays.asList(Move.TOP, Move.TOP, Move.TOP, Move.RIGHT),
                    false,
                    false,
                    true,
                    false
            ),
            new FullMove(
                    Arrays.asList(Move.TOP, Move.TOP, Move.TOP, Move.LEFT),
                    false,
                    false,
                    true,
                    false
            )
    };

}
