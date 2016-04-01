import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class StreamGameCommunicatorTest {

	Player.StreamGameCommunicator gameCommunicator;
	ByteArrayOutputStream output;

	@Before
	public void setUp() {
		String input = "20 1 0 0";
		InputStream in = new ByteArrayInputStream(input.getBytes());
		output = new ByteArrayOutputStream();
		gameCommunicator = new Player.StreamGameCommunicator(in, new PrintStream(output, true));
	}

	@Test
	public void testNextValue() {
		int[] expected = { 20, 1, 0, 0 };
		int[] actual = new int[4];

		for (int i = 0; i < 4; i++) {
			actual[i] = gameCommunicator.nextValue();
		}

		Assert.assertArrayEquals(expected, actual);
	}

	@Test
	public void testCutLink() {
		gameCommunicator.cutLink(1, 2);
		Assert.assertEquals("1 2", output.toString().trim());
	}

}
