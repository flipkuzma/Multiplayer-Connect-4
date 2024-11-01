import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.DisplayName;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;

class MyTest {



	@Test
	void test1() {
		ArrayList<Short> moves = new ArrayList<>();

		moves.add((short) 41);
		moves.add((short) 40);
		moves.add((short) 39);
		moves.add((short) 38);

		assertNotNull(GameBoard.checkWinner(moves, 5, 6));
		assertNotNull(GameBoard.checkWinner(moves, 5, 4));
		assertNotNull(GameBoard.checkWinner(moves, 5, 5));
		assertNull(GameBoard.checkWinner(moves,4,3));
	}

	@Test
	void test2(){
		ArrayList<Short> moves = new ArrayList<>();

		moves.add((short) 41);
		moves.add((short) 33);
		moves.add((short) 25);
		moves.add((short) 17);

		assertNotNull(GameBoard.checkWinner(moves, 5, 6));
		assertNotNull(GameBoard.checkWinner(moves, 4, 5));
		assertNotNull(GameBoard.checkWinner(moves, 3, 4));
		assertNotNull(GameBoard.checkWinner(moves, 2, 3));
		assertNull(GameBoard.checkWinner(moves, 0,2));
	}

	@Test
	void test3(){
		ArrayList<Short> moves = new ArrayList<>();

		moves.add((short) 41);
		moves.add((short) 33);
		moves.add((short) 25);
		moves.add((short) 17);

		assertNotNull(GameBoard.checkWinner(moves, 5,6));
		assertNotNull(GameBoard.checkWinner(moves, 4,5));
		assertNotNull(GameBoard.checkWinner(moves, 3,4));
		assertNotNull(GameBoard.checkWinner(moves, 2,3));
		assertNull(GameBoard.checkWinner(moves, 3, 3));
	}

	@Test
	void test4(){
		ArrayList<Short> moves = new ArrayList<>();

		moves.add((short) 35);
		moves.add((short) 29);
		moves.add((short) 23);
		moves.add((short) 17);

		assertNotNull(GameBoard.checkWinner(moves, 5, 0));
		assertNotNull(GameBoard.checkWinner(moves, 4, 1));
		assertNotNull(GameBoard.checkWinner(moves, 3, 2));
		assertNotNull(GameBoard.checkWinner(moves, 2, 3));
		assertNull(GameBoard.checkWinner(moves, 2, 5));
	}



}
