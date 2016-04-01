import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

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
		private final int id;
		private Set<Integer> peers = new HashSet<Integer>();

		public Node(int id) {
			this.id = id;
		}

		public int getId() {
			return id;
		}

		public void addPeer(int id) {
			peers.add(id);
		}

		public void removePeer(int id) {
			peers.remove(id);
		}

		public int getDegree() {
			return peers.size();
		}

	}

	public static class Game {

		private GameCommunicator communicator;
		private Map<Integer, Node> nodes;
		private Set<Integer> gatewayIds;

		public Game(GameCommunicator communicator, Map<Integer, Node> nodes, Set<Integer> gatewayIds) {
			this.communicator = communicator;
			this.nodes = nodes;
			this.gatewayIds = gatewayIds;
		}

		public Map<Integer, Node> getNodes() {
			return nodes;
		}

		public Set<Integer> getGatewayIds() {
			return gatewayIds;
		}

	}

	public static class GameBuilder {

		public Game createGame(GameCommunicator gameCommunicator) {
			Map<Integer, Node> nodes = readNodes(gameCommunicator);

			int nbLinks = readNbLinks(gameCommunicator);

			int nbGateways = readNbGateways(gameCommunicator, nodes.size());

			for (int i = 0; i < nbLinks; i++) {
				int nodeAId = gameCommunicator.nextValue();
				int nodeBId = gameCommunicator.nextValue();
				if (nodeAId < 0 || nodeBId < 0 || nodeAId >= nodes.size() || nodeBId >= nodes.size()) {
					throw new IllegalArgumentException("Invalid link definition");
				}
				logger.debug("Link " + nodeAId + " " + nodeBId);

				Node nodeA = nodes.get(nodeAId);
				Node nodeB = nodes.get(nodeBId);

				nodeA.addPeer(nodeAId);
				nodeA.addPeer(nodeBId);
			}

			Set<Integer> gatewayIds = new HashSet<Integer>();
			for (int i = 0; i < nbGateways; i++) {
				int gatewayId = gameCommunicator.nextValue();
				if (gatewayId < 0 || gatewayId >= nodes.size()) {
					throw new IllegalArgumentException("Invalid gateway definition");
				}
				logger.debug("Gateway " + gatewayId);

				gatewayIds.add(gatewayId);
			}

			return new Game(gameCommunicator, nodes, gatewayIds);
		}

		Map<Integer, Node> readNodes(GameCommunicator gameCommunicator) {
			Map<Integer, Node> nodes = new HashMap<>();

			int nbNodes = gameCommunicator.nextValue();
			if (nbNodes < MIN_NODES || nbNodes > MAX_NODES) {
				throw new IllegalArgumentException("Invalid number of nodes");
			}
			logger.debug("Nb Nodes = " + nbNodes);

			// populate node Map
			for (int i = 0; i < nbNodes; i++) {
				nodes.put(i, new Node(i));
			}
			return nodes;
		}

		int readNbLinks(GameCommunicator gameCommunicator) {
			int nbLinks = gameCommunicator.nextValue();
			if (nbLinks < MIN_LINKS || nbLinks > MAX_LINKS) {
				throw new IllegalArgumentException("Invalid number of links");
			}
			logger.debug("Nb Links = " + nbLinks);
			return nbLinks;
		}

		int readNbGateways(GameCommunicator gameCommunicator, int nbNodes) {
			int nbGateways = gameCommunicator.nextValue();
			if (nbGateways < MIN_GATEWAYS || nbGateways > MAX_GATEWAYS || nbGateways >= nbNodes) {
				throw new IllegalArgumentException("Invalid number of gateways");
			}
			logger.debug("Nb Gateways = " + nbGateways);

			return nbGateways;
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