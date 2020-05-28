import java.util.Comparator;

public class InvIndComparator implements Comparator<InvertedIndexEntry>
{
	@Override
	public int compare(InvertedIndexEntry ii1, InvertedIndexEntry ii2)
	{
		if (ii1.getWordId() == ii2.getWordId())
			return 0;
		else
			return (ii1.getWordId() < ii2.getWordId()) ? -1 : 1;
	}
}