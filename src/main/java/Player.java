import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

/**
 * Auto-generated code below aims at helping you parse the standard input
 * according to the problem statement.
 **/
class Player {

	private static final Logger logger = new Logger();

	private static final int MIN_NODES = 2;
	private static final int MAX_NODES = 500;

	private static final int MIN_LINKS = 1;
	private static final int MAX_LINKS = 1000;

	private static final int MIN_GATEWAYS = 1;
	private static final int MAX_GATEWAYS = 20;

	public static class Logger {
		public void debug(String message) {
			System.err.println(message);
		}
	}

	public static interface GameCommunicator {

		public int nextValue();

		public boolean hasNext();

		public void cutLink(int nodeA, int nodeB);

	}

	public static class StreamGameCommunicator implements GameCommunicator {
		private Scanner in;
		private PrintStream out;

		public StreamGameCommunicator(InputStream in, PrintStream out) {
			this.in = new Scanner(in);
			this.out = out;
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
			logger.debug("Nb Nodes = " + nbNodes);

			int nbLinks = gameCommunicator.nextValue();
			if (nbLinks < MIN_LINKS || nbLinks > MAX_LINKS) {
				throw new IllegalArgumentException("Invalid number of links");
			}
			logger.debug("Nb Links = " + nbLinks);

			int nbGateways = gameCommunicator.nextValue();
			if (nbGateways < MIN_GATEWAYS || nbGateways > MAX_GATEWAYS || nbGateways >= nbNodes) {
				throw new IllegalArgumentException("Invalid number of gateways");
			}

			logger.debug("Nb Gateways = " + nbGateways);

			for (int i = 0; i < nbLinks; i++) {
				int nodeA = gameCommunicator.nextValue();
				int nodeB = gameCommunicator.nextValue();

				if (nodeA < 0 || nodeB < 0 || nodeA >= nbNodes || nodeB >= nbNodes) {
					throw new IllegalArgumentException("Invalid link definition");
				}

				logger.debug("Link " + nodeA + " " + nodeB);

			}

			for (int i = 0; i < nbGateways; i++) {
				int gatewayId = gameCommunicator.nextValue();
				if (gatewayId < 0 || gatewayId >= nbNodes) {
					throw new IllegalArgumentException("Invalid gateway definition");
				}
				logger.debug("Gateway " + gatewayId);
			}

			return new Game(gameCommunicator);
		}

	}

	public static void main(String args[]) {
		GameCommunicator communicator = new StreamGameCommunicator(System.in, System.out);

		Game game = new GameBuilder().createGame(communicator);

		// // game loop
		// while (true) {
		// int SI = in.nextInt(); // The index of the node on which the Skynet
		// // agent is positioned this turn
		//
		// // Write an action using System.out.println()
		// // To debug: System.err.println("Debug messages...");
		//
		// System.out.println("0 1"); // Example: 0 1 are the indices of the
		// // nodes you wish to sever the link
		// // between
		// }
	}
}