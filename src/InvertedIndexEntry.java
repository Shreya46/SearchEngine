import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class InvertedIndexEntry
{
	private Long wordId;
	private Map <Long, Integer[]> countList;
	
	private InvertedIndexEntry(Long wordId, Map<Long, Integer[]> countList)
	{
		this.wordId = wordId;
		this.countList = countList;
	}
	
	public Long getWordId() {	return wordId;	}
	private Map<Long, Integer[]> getCountList() {return countList;}
	
	static InvertedIndexEntry fromPgLevelInvertedIndex(String pgNo, String line)
	{
		String[] word_vals = line.split("\t:\t");
		Long wordId = Long.valueOf(word_vals[0]);
		
		String[] valStrArr = word_vals[1].split(" ");
		int noOfVals = valStrArr.length;
		
		Integer[] valArr = new Integer[noOfVals];
		for(int i=0; i<noOfVals; i++)
			valArr[i] = Integer.valueOf (valStrArr[i]);
		
		Map <Long, Integer[]> countList = new HashMap<>();
		countList.put(Long.valueOf(pgNo), valArr);
		
		return new InvertedIndexEntry(wordId, countList);
	}
	
	static InvertedIndexEntry fromInvertedIndexString(String line)
	{
		//101	0:0,1,0	7:0,1,0	9:0,1,0	11:0,1,0
		String[] word_Pgvals = line.split("\t");
		Long wordId = Long.valueOf(word_Pgvals[0]);             //101
		Map <Long, Integer[]> countList = new HashMap<>();
		
		for(int i=1; i<word_Pgvals.length; i++)
		{
			String[] pg_Vals = word_Pgvals[i].split(":");
	 		Long pgNo = Long.valueOf(pg_Vals[0]);               //0
			
			String[] valStrArr = pg_Vals[1].split(",");
			int noOfVals = valStrArr.length;                    //0,1,0
			
			Integer[] valArr = new Integer[noOfVals];
			for(int j=0; j<noOfVals; j++)
				valArr[j] = Integer.valueOf (valStrArr[j]);     //[0,1,0]
			
			countList.put(pgNo, valArr);
		}
		
		return new InvertedIndexEntry (wordId, countList);
	}
	
	public static Set<OutputElement> setOfIndices(String line)
	{
		InvertedIndexEntry indexEntry = fromInvertedIndexString(line);
		return OutputElement.createSet(indexEntry.getWordId(), indexEntry.getCountList());
	}
	
	void merge(InvertedIndexEntry newInvertedIndexEntry)
	{
		if(!newInvertedIndexEntry.getWordId().equals(this.wordId))
			System.out.println("err while merging, trying to merge " +
					newInvertedIndexEntry.getWordId() + " with " + wordId);
		
		newInvertedIndexEntry.getCountList().forEach((key, value) -> this.countList.put(key, value));
	}
	
	@Override
	public boolean equals(Object obj)
	{
		InvertedIndexEntry that = (InvertedIndexEntry) obj;
		return wordId.equals(that.getWordId());
	}
	
	@Override
	public String toString()
	{
		StringBuilder out = new StringBuilder(wordId.toString());
		for(Long pg : countList.keySet())
		{
			out.append("\t").append(pg.toString());
			out.append(":");
			Integer[] vals = countList.get(pg);
			for (int i=0; i<vals.length; i++)    //Todo: optimise when val=0 dont write, then add label like 2H,45B,12R
			{
				if (i!=0)
					out.append(",");
				out.append(vals[i]);
			}
		}
		out.append("\n");
		return out.toString();
	}
}