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
		List<Integer> inputs = Arrays.asList(2);
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

}
