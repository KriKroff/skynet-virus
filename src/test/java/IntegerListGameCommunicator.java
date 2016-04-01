import java.util.Iterator;

public class IntegerListGameCommunicator implements Player.GameCommunicator {

	private final Iterator<Integer> inputs;

	public IntegerListGameCommunicator(Iterable<Integer> inputs) {
		this.inputs = inputs.iterator();
	}

	@Override
	public int nextValue() {
		return inputs.next();
	}

	@Override
	public boolean hasNext() {
		return inputs.hasNext();
	}

	@Override
	public void cutLink(int nodeA, int nodeB) {
	}

}