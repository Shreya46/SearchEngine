import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class OutputElement
{
	private Long pgId;
	private Long wordId;
	private int freq;
	
	OutputElement(Long wordId, Long pgId, Integer[] counts)
	{
		this.wordId = wordId;
		this.pgId = pgId;
		this.freq = 0;
		int rank = -1;
		
		for(int i=0; i<counts.length; i++)
			this.freq += counts[i];
	}
	
	public static Set<OutputElement> createSet(Long wordId, Map<Long,Integer[]> countList)
	{
		Set <OutputElement> set = new HashSet<>();
		countList.keySet().forEach(pgId -> set.add(new OutputElement(wordId, pgId, countList.get(pgId))));
		return set;
	}
	
	public Long getPgId()
	{
		return this.pgId;
	}
	
	public int getFreq()
	{
		return this.freq;
	}
	
	public Long getWordId()
	{
		return this.wordId;
	}
}
