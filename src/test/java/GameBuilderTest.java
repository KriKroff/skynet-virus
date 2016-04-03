import java.util.Arrays;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class GameBuilderTest {

	private Player.GameBuilder gameBuilder = new Player.GameBuilder();

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Test
	public void should_return_game() {
		int nbNodes = 2;
		int nbLinks = 1;
		int nbGateways = 1;
		int gatewayId = 1;

		int nodeA = 0;
		int nodeB = 1;

		List<Integer> inputs = Arrays.asList(nbNodes, nbLinks, nbGateways, nodeA, nodeB, gatewayId);

		Player.GameCommunicator gameCommunicator = new IntegerListGameCommunicator(inputs);

		Player.Game game = gameBuilder.createGame(gameCommunicator);
		Assert.assertNotNull(game);
		Assert.assertNotNull(game.getNodes());
		Assert.assertNotNull(game.getGatewayIds());
		Assert.assertEquals(nbNodes, game.getNodes().size());
		Assert.assertEquals(nbGateways, game.getGatewayIds().size());
	}

	/*
	 * Number of Nodes
	 */

	@Test
	public void testReadNbNodes() {
		int nbNodes = 2;
		List<Integer> inputs = Arrays.asList(nbNodes);

		Player.GameCommunicator gameCommunicator = new IntegerListGameCommunicator(inputs);

		Assert.assertEquals(nbNodes, gameBuilder.readNbNodes(gameCommunicator));
	}

	@Test
	public void testReadNbNodes_fail_at_least_2_nodes() {
		List<Integer> inputs = Arrays.asList(1);

		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage(Matchers.equalTo("Invalid number of nodes"));

		Player.GameCommunicator gameCommunicator = new IntegerListGameCommunicator(inputs);

		gameBuilder.readNbNodes(gameCommunicator);
	}

	@Test
	public void testReadNbNodes_fail_at_most_500_nodes() {
		List<Integer> inputs = Arrays.asList(501);

		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage(Matchers.equalTo("Invalid number of nodes"));

		Player.GameCommunicator gameCommunicator = new IntegerListGameCommunicator(inputs);

		gameBuilder.readNbNodes(gameCommunicator);
	}

	/*
	 * Number of Links
	 */

	@Test
	public void testReadNbLinks() {
		int nbLinks = 2;
		List<Integer> inputs = Arrays.asList(nbLinks);

		Player.GameCommunicator gameCommunicator = new IntegerListGameCommunicator(inputs);

		Assert.assertEquals(nbLinks, gameBuilder.readNbLinks(gameCommunicator));
	}

	@Test
	public void testReadNbLinks_fail_at_least_1_link() {
		List<Integer> inputs = Arrays.asList(0);

		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage(Matchers.equalTo("Invalid number of links"));

		Player.GameCommunicator gameCommunicator = new IntegerListGameCommunicator(inputs);

		gameBuilder.readNbLinks(gameCommunicator);
	}

	@Test
	public void testReadNbLinks_fail_at_most_1000_link() {
		List<Integer> inputs = Arrays.asList(1001);

		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage(Matchers.equalTo("Invalid number of links"));

		Player.GameCommunicator gameCommunicator = new IntegerListGameCommunicator(inputs);

		gameBuilder.readNbLinks(gameCommunicator);
	}

	/*
	 * Number of Gateways
	 */

	@Test
	public void testReadNbGateways() {
		int nbNodes = 2;
		int nbGateways = 1;
		List<Integer> inputs = Arrays.asList(nbGateways);

		Player.GameCommunicator gameCommunicator = new IntegerListGameCommunicator(inputs);

		Assert.assertEquals(nbGateways, gameBuilder.readNbGateways(gameCommunicator, nbNodes));
	}

	@Test
	public void testReadNbGateways_fail_at_least_1_gateway() {
		int nbNodes = 2;
		int nbGateways = 0;
		List<Integer> inputs = Arrays.asList(nbGateways);

		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage(Matchers.equalTo("Invalid number of gateways"));

		Player.GameCommunicator gameCommunicator = new IntegerListGameCommunicator(inputs);

		gameBuilder.readNbGateways(gameCommunicator, nbNodes);
	}

	@Test
	public void testReadNbGateways_fail_at_most_20_gateways() {
		int nbNodes = 2;
		int nbGateways = 21;
		List<Integer> inputs = Arrays.asList(nbGateways);

		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage(Matchers.equalTo("Invalid number of gateways"));

		Player.GameCommunicator gameCommunicator = new IntegerListGameCommunicator(inputs);

		gameBuilder.readNbGateways(gameCommunicator, nbNodes);
	}

	@Test
	public void testReadNbGateways_fail_less_gateway_than_nodes() {
		int nbNodes = 2;
		int nbGateways = 21;

		List<Integer> inputs = Arrays.asList(nbGateways);

		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage(Matchers.equalTo("Invalid number of gateways"));

		Player.GameCommunicator gameCommunicator = new IntegerListGameCommunicator(inputs);

		gameBuilder.readNbGateways(gameCommunicator, nbNodes);
	}

	/**
	 * Links checks
	 */
	@Test
	public void testReadGraph_fail_link_two_existing_node() {
		int nbNodes = 2;
		int nbLinks = 1;

		List<Integer> inputs = Arrays.asList(-1, 0);

		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage(Matchers.equalTo("Invalid node index"));

		Player.GameCommunicator gameCommunicator = new IntegerListGameCommunicator(inputs);

		gameBuilder.readGraph(gameCommunicator, nbNodes, nbLinks);
	}

	/**
	 * Gateway checks
	 */
	@Test
	public void testReadGateways_fail_incorrect_gateway_index() {
		// gateway index is > nbNodes
		List<Integer> inputs = Arrays.asList(5);
		int nbNodes = 2;
		int nbGateways = 1;

		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage(Matchers.equalTo("Invalid node index"));

		Player.GameCommunicator gameCommunicator = new IntegerListGameCommunicator(inputs);

		gameBuilder.readGateways(gameCommunicator, nbNodes, nbGateways);
	}

}
