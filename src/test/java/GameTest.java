import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class GameTest {

	private int stage1StartId = 1;
	private int stage1ExitId = 2;

	private Player.Game getStage1Game(Player.GameCommunicator communicator) {
		Map<Integer, Player.Node> nodes = new HashMap<Integer, Player.Node>();

		Player.Node nodeA = new Player.Node(0);
		Player.Node startNode = new Player.Node(stage1StartId);
		Player.Node gateway = new Player.Node(stage1ExitId);

		nodeA.addPeer(startNode.getId());
		startNode.addPeer(nodeA.getId());

		startNode.addPeer(gateway.getId());
		gateway.addPeer(startNode.getId());

		nodes.put(nodeA.getId(), nodeA);
		nodes.put(startNode.getId(), startNode);
		nodes.put(gateway.getId(), gateway);

		return new Player.Game(nodes, new HashSet<Integer>(Arrays.asList(gateway.getId())));
	}

	@Test
	public void testCutLink() {
		InputStream in = new ByteArrayInputStream("".getBytes());
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Player.GameCommunicator communicator = new Player.StreamGameCommunicator(in, new PrintStream(out, true));

		Player.Game game = getStage1Game(communicator);

		Player.Node startNode = game.getNodes().get(stage1StartId);
		Player.Node gateway = game.getNodes().get(stage1ExitId);

		game.cutLink(startNode.getId(), gateway.getId());

		Assert.assertFalse(startNode.getPeers().contains(gateway.getId()));
		Assert.assertFalse(gateway.getPeers().contains(startNode.getId()));

	}

	@Test
	public void testFindLinkTocut_stage1() {
		InputStream in = new ByteArrayInputStream("".getBytes());
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Player.GameCommunicator communicator = new Player.StreamGameCommunicator(in, new PrintStream(out, true));

		Player.Game game = getStage1Game(communicator);

		game.skynetNodeId = stage1StartId;

		Player.Node startNode = game.getNodes().get(stage1StartId);
		Player.Node gateway = game.getNodes().get(stage1ExitId);

		Player.Pair<Integer> linkToCut = game.findLinkToCut(stage1StartId);

		int minId = Math.min(linkToCut.getFirst(), linkToCut.getSecond());
		int maxId = Math.max(linkToCut.getFirst(), linkToCut.getSecond());

		Assert.assertEquals(Math.min(stage1StartId, stage1ExitId), minId);
		Assert.assertEquals(Math.max(stage1StartId, stage1ExitId), maxId);
	}

	@Test
	public void testFindLinkTocut_stage2() {
		InputStream in = new ByteArrayInputStream("4 4 1 0 1 0 2 1 3 2 3 3 0 2".getBytes());
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Player.GameCommunicator communicator = new Player.StreamGameCommunicator(in, new PrintStream(out, true));
		Player.GameBuilder gameBuilder = new Player.GameBuilder();

		Player.Game game = gameBuilder.createGame(communicator);
		int nbLinks = 4;
		Player.Pair<Integer> findLinkToCut = null;
		int skynetNodeId = 0;
		while ((findLinkToCut = game.findLinkToCut(skynetNodeId)) != null) {
			game.cutLink(findLinkToCut.getFirst(), findLinkToCut.getSecond());
			nbLinks--;
			Player.Pair<Integer> nextLink = game.findLinkToCut(skynetNodeId);
			if (nextLink != null) {
				skynetNodeId = nextLink.getSecond();
			}
		}
		Assert.assertEquals(2, nbLinks);
	}
}
