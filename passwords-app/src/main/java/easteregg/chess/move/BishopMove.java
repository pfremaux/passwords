package easteregg.chess.move;

import java.util.Collections;

public class BishopMove {

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
    };



}
