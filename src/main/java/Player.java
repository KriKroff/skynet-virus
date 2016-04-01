import java.io.PrintStream;
import java.util.Scanner;

/**
 * Auto-generated code below aims at helping you parse the standard input
 * according to the problem statement.
 **/
public class Player {

	private static final int MIN_NODES = 2;
	private static final int MAX_NODES = 500;

	public static interface GameCommunicator {

		public int nextValue();

		public boolean hasNext();

		public void cutLink(int nodeA, int nodeB);

	}

	public static class SystemGameCommunicator implements GameCommunicator {
		private Scanner in;
		private PrintStream out;

		public SystemGameCommunicator() {
			this.in = new Scanner(System.in);
			this.out = System.out;
		}

		public int nextValue() {
			return in.nextInt();
		}

		public boolean hasNext() {
			return in.hasNext();
		}

		public void cutLink(int nodeA, int nodeB) {
			out.println(String.format("%1d %1d", nodeA, nodeB));
		}
	}

	public static class Node {

	}

	public static class Game {

		private GameCommunicator communicator;

		public Game(GameCommunicator communicator) {
			this.communicator = communicator;
		}
	}

	public static class GameBuilder {

		public Game createGame(GameCommunicator gameCommunicator) {
			int nbNodes = gameCommunicator.nextValue();

			if (nbNodes < MIN_NODES || nbNodes > MAX_NODES) {
				throw new IllegalArgumentException("Invalid number of nodes");
			}
			return null;
		}

	}

	public static void main(String args[]) {
		Scanner in = new Scanner(System.in);
		int N = in.nextInt(); // the total number of nodes in the level,
								// including the gateways
		int L = in.nextInt(); // the number of links
		int E = in.nextInt(); // the number of exit gateways
		for (int i = 0; i < L; i++) {
			int N1 = in.nextInt(); // N1 and N2 defines a link between these
									// nodes
			int N2 = in.nextInt();
		}
		for (int i = 0; i < E; i++) {
			int EI = in.nextInt(); // the index of a gateway node
		}

		// game loop
		while (true) {
			int SI = in.nextInt(); // The index of the node on which the Skynet
									// agent is positioned this turn

			// Write an action using System.out.println()
			// To debug: System.err.println("Debug messages...");

			System.out.println("0 1"); // Example: 0 1 are the indices of the
										// nodes you wish to sever the link
										// between
		}
	}
}