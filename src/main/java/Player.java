import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

		public void cutLink(int nodeA, int nodeB) {
			out.println(String.format("%1d %1d", nodeA, nodeB));
		}
	}

	public static class Node {
		private final int id;
		private Set<Integer> peers = new HashSet<Integer>();
		private int nbLinksCut = 0;

		public Node(int id) {
			this.id = id;
		}

		public int getId() {
			return id;
		}

		public Set<Integer> getPeers() {
			return peers;
		}

		public void addPeer(int id) {
			peers.add(id);
		}

		public void removePeer(int id) {
			if (peers.remove(id)) {
				nbLinksCut++;
			}
		}

		public int getDegree() {
			return peers.size();
		}

		public int getNbLinksCut() {
			return nbLinksCut;
		}
	}

	public static class Pair<T> {
		private final T first;
		private final T second;

		public Pair(T a, T b) {
			first = a;
			second = b;
		}

		public T getFirst() {
			return first;
		}

		public T getSecond() {
			return second;
		}
	}

	public static class Path {

		private Node node;

		private Path parent;

		public Path(Node node) {
			this.node = node;
		}

		public Path(Node node, Path subPath) {
			this.node = node;
			this.parent = subPath;
		}

		public Node getNode() {
			return node;
		}

		public int getLength() {
			return 1 + (parent != null ? parent.getLength() : 0);
		}

		public int getVisitedCoefficient() {
			return node.getNbLinksCut() + (parent != null ? parent.getVisitedCoefficient() : 0);
		}

		public Path getParent() {
			return parent;
		}

		public Deque<Node> asDeque() {
			Deque<Node> queue = new ArrayDeque<Node>();
			queue.add(node);
			Path path = parent;
			while (path != null) {
				queue.addFirst(path.getNode());
				path = path.getParent();
			}
			return queue;
		}
	}

	public static class Game {

		private GameCommunicator communicator;
		private Map<Integer, Node> nodes;
		private Set<Integer> gatewayIds;

		int skynetNodeId = -2;

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

		void cutLink(int nodeA, int nodeB) {
			getNode(nodeA).removePeer(nodeB);
			getNode(nodeB).removePeer(nodeA);
			communicator.cutLink(nodeA, nodeB);
		}

		Pair<Integer> findLinkToCut() {
			Optional<Path> shortestPathOpt = findShortestPathsToGateway().stream()
					.min(Comparator.comparing(Path::getLength).thenComparing(Path::getVisitedCoefficient));
			if (shortestPathOpt.isPresent()) {
				Deque<Node> shortestPathDeque = shortestPathOpt.get().asDeque();
				return new Pair<Integer>(shortestPathDeque.pollFirst().getId(), shortestPathDeque.pollFirst().getId());
			}

			return null;
		}

		private boolean isGateway(int id) {
			return gatewayIds.contains(id);
		}

		private Node getNode(int nodeId) {
			return nodes.get(nodeId);
		}

		List<Path> findShortestPathsToGateway() {
			Node currentPosition = getNode(skynetNodeId);
			Deque<Path> toVisit = new ArrayDeque<Path>();

			Set<Integer> visitedNodes = new HashSet<Integer>(nodes.size());
			toVisit.add(new Path(currentPosition));

			int shortestPath = Integer.MAX_VALUE;
			List<Path> shortestPaths = new ArrayList<Path>();

			while (!toVisit.isEmpty()) {
				Path currentPath = toVisit.poll();
				if (currentPath.getLength() > shortestPath) {
					break;
				}

				visitedNodes.add(currentPath.getNode().getId());
				Node node = currentPath.getNode();

				for (int peerId : node.getPeers()) {
					Node peer = getNode(peerId);
					if (isGateway(peerId)) {
						Path pathFound = new Path(peer, currentPath);
						shortestPaths.add(pathFound);
						shortestPath = pathFound.getLength();
						break;
					} else {
						if (!visitedNodes.contains(peerId) && !toVisit.contains(peerId)) {
							toVisit.addLast(new Path(peer, currentPath));
						}
					}
				}
			}
			return shortestPaths;
		}

		public void start() {
			while (true && skynetNodeId != -1) {
				skynetNodeId = communicator.nextValue();
				if (skynetNodeId == -1) { // exit Game
					break;
				}

				Pair<Integer> link = findLinkToCut();
				if (link != null) {
					cutLink(link.getFirst(), link.getSecond());
				} else {
					logger.debug("No link to cut");
					break;
				}
			}
		}

	}

	public static class GameBuilder {

		public Game createGame(GameCommunicator gameCommunicator) {
			int nbNodes = readNbNodes(gameCommunicator);

			int nbLinks = readNbLinks(gameCommunicator);

			int nbGateways = readNbGateways(gameCommunicator, nbNodes);

			Map<Integer, Node> nodes = readGraph(gameCommunicator, nbNodes, nbLinks);

			Set<Integer> gatewayIds = readGateways(gameCommunicator, nodes.size(), nbGateways);

			return new Game(gameCommunicator, nodes, gatewayIds);
		}

		Map<Integer, Node> readGraph(GameCommunicator gameCommunicator, int nbNodes, int nbLinks) {
			Map<Integer, Node> nodes = new HashMap<>();

			// Node map initialization
			for (int i = 0; i < nbNodes; i++) {
				nodes.put(i, new Node(i));
			}

			for (int i = 0; i < nbLinks; i++) {
				int nodeAId = readNodeIndex(gameCommunicator, nodes.size());
				int nodeBId = readNodeIndex(gameCommunicator, nodes.size());
				logger.debug("Link " + nodeAId + " " + nodeBId);

				Node nodeA = nodes.get(nodeAId);
				Node nodeB = nodes.get(nodeBId);

				nodeA.addPeer(nodeBId);
				nodeB.addPeer(nodeAId);
			}

			return nodes;
		}

		int readNbNodes(GameCommunicator gameCommunicator) {
			int nbNodes = gameCommunicator.nextValue();
			if (nbNodes < MIN_NODES || nbNodes > MAX_NODES) {
				throw new IllegalArgumentException("Invalid number of nodes");
			}
			logger.debug("Nb Nodes = " + nbNodes);

			return nbNodes;
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

		Set<Integer> readGateways(GameCommunicator gameCommunicator, int nbNodes, int nbGateways) {
			Set<Integer> gatewayIds = new HashSet<Integer>();
			for (int i = 0; i < nbGateways; i++) {
				int gatewayId = readNodeIndex(gameCommunicator, nbNodes);
				logger.debug("Gateway " + gatewayId);

				gatewayIds.add(gatewayId);
			}
			return gatewayIds;
		}

		int readNodeIndex(GameCommunicator gameCommunicator, int nbNodes) {
			int nodeId = gameCommunicator.nextValue();
			if (nodeId < 0 || nodeId >= nbNodes) {
				throw new IllegalArgumentException("Invalid node index");
			}
			return nodeId;
		}

	}

	public static void main(String args[]) {
		GameCommunicator communicator = new StreamGameCommunicator(System.in, System.out);

		Game game = new GameBuilder().createGame(communicator);

		game.start();
	}
}