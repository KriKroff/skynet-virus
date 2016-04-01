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
		// 2 nodes, 1 link, 1 gateway
		List<Integer> inputs = Arrays.asList(2, 1, 1, 0, 1, 1);
		Player.GameCommunicator gameCommunicator = new IntegerListGameCommunicator(inputs);

		Player.Game game = gameBuilder.createGame(gameCommunicator);
		Assert.assertNotNull(game);
	}

	@Test
	public void should_have_a_least_2_nodes() {
		List<Integer> inputs = Arrays.asList(1);
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage(Matchers.equalTo("Invalid number of nodes"));
		Player.GameCommunicator gameCommunicator = new IntegerListGameCommunicator(inputs);
		gameBuilder.createGame(gameCommunicator);
	}

	@Test
	public void should_have_a_most_500_nodes() {
		List<Integer> inputs = Arrays.asList(501);
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage(Matchers.equalTo("Invalid number of nodes"));
		Player.GameCommunicator gameCommunicator = new IntegerListGameCommunicator(inputs);
		gameBuilder.createGame(gameCommunicator);
	}

	@Test
	public void should_have_a_least_1_link() {
		List<Integer> inputs = Arrays.asList(2, 0);
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage(Matchers.equalTo("Invalid number of links"));
		Player.GameCommunicator gameCommunicator = new IntegerListGameCommunicator(inputs);
		gameBuilder.createGame(gameCommunicator);
	}

	@Test
	public void should_have_a_most_1000_link() {
		List<Integer> inputs = Arrays.asList(2, 1001);
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage(Matchers.equalTo("Invalid number of links"));
		Player.GameCommunicator gameCommunicator = new IntegerListGameCommunicator(inputs);
		gameBuilder.createGame(gameCommunicator);
	}

	@Test
	public void should_have_a_least_1_gateway() {
		List<Integer> inputs = Arrays.asList(2, 1, 0);
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage(Matchers.equalTo("Invalid number of gateways"));
		Player.GameCommunicator gameCommunicator = new IntegerListGameCommunicator(inputs);
		gameBuilder.createGame(gameCommunicator);
	}

	@Test
	public void should_have_a_most_20_gateways() {
		List<Integer> inputs = Arrays.asList(2, 1, 21);
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage(Matchers.equalTo("Invalid number of gateways"));
		Player.GameCommunicator gameCommunicator = new IntegerListGameCommunicator(inputs);
		gameBuilder.createGame(gameCommunicator);
	}

	@Test
	public void should_have_less_gateway_than_nodes() {
		List<Integer> inputs = Arrays.asList(2, 1, 2);
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage(Matchers.equalTo("Invalid number of gateways"));
		Player.GameCommunicator gameCommunicator = new IntegerListGameCommunicator(inputs);
		gameBuilder.createGame(gameCommunicator);
	}

	/**
	 * Links checks
	 */
	@Test
	public void should_link_two_existing_node() {
		List<Integer> inputs = Arrays.asList(2, 1, 1, -1, 0);
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage(Matchers.equalTo("Invalid link definition"));
		Player.GameCommunicator gameCommunicator = new IntegerListGameCommunicator(inputs);
		gameBuilder.createGame(gameCommunicator);
	}

	/**
	 * Gateway checks
	 */
	@Test
	public void should_have_a_() {
		List<Integer> inputs = Arrays.asList(2, 1, 1, 0, 1, 5);
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage(Matchers.equalTo("Invalid gateway definition"));
		Player.GameCommunicator gameCommunicator = new IntegerListGameCommunicator(inputs);
		gameBuilder.createGame(gameCommunicator);
	}

}
