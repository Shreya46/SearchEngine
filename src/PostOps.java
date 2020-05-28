import javafx.util.Pair;

import java.io.*;
import java.util.*;

public class PostOps
{
	public PostOps(List<Long> wordIds)
	{
		Pair bothOutput = getOutputEntrySet(wordIds);
		List <Integer> docCounts = (List<Integer>) bothOutput.getKey();
		Set<OutputElement> outputEntries = (Set<OutputElement>) bothOutput.getValue();
		
		if (docCounts.size() != 0)
		{
			Map<Long, Integer> idf = calcIDF(docCounts, wordIds);
			HashMap<Long, Integer> fullList = applyTfIdf(outputEntries, idf);
			List<Long> sortedPgList = SortList(fullList);
			printTop(sortedPgList);
		}
		else
		{
			System.out.println("\t Sorry.. No relevant data present in Wikipedia datafile for given search item");
		}
	}
	
	private List<Long> SortList(HashMap<Long,Integer> fullList)
	{
		List<Map.Entry<Long, Integer>> arr = new LinkedList<>();
		List<Long> pgIds = new ArrayList<>();
		
		for(Map.Entry<Long, Integer> e: fullList.entrySet())
			arr.add(e);
		
		Comparator<Map.Entry<Long, Integer>> valueComparator = (e1, e2) -> {
			Integer v1 = e1.getValue();
			Integer v2 = e2.getValue();
			return v1.compareTo(v2)*(-1);
		};
		
		Collections.sort(arr, valueComparator);
		arr.forEach (entry -> pgIds.add(entry.getKey()));
		return pgIds;
	}
	
	private Map<Long, Integer> calcIDF(List<Integer> docCounts, List<Long> wordIds)
	{
		int numerator = 1 + OnlineSearch.totalNoOfPages;
		Map<Long, Integer> idf = new HashMap<>();
		for(int i=0; i<wordIds.size(); i++)
		{
			Integer count = docCounts.get(i);
			if (count != 0)
				idf.put(wordIds.get(i), (int) Math.log ( numerator/ (1+count)));
		}
		return idf;
	}
	
	private static HashMap<Long, Integer> applyTfIdf(Set<OutputElement> outputEntries, Map<Long, Integer> idf)
	{
		HashMap <Long, Integer>  pgWeightage = new HashMap<>();
		
		for(OutputElement entry : outputEntries)
		{
			Long pgId = entry.getPgId();
			Integer oldWeight = pgWeightage.getOrDefault(pgId, 0);
			Integer newWeight = entry.getFreq() * idf.get(entry.getWordId());
			pgWeightage.put (pgId, (oldWeight + newWeight));
		}
		
		return pgWeightage;
	}
	
	private static Pair getOutputEntrySet(List<Long> wordIds)
	{
		Set<OutputElement> outputElements = new HashSet<>();
		List<Integer> docsCounts = new ArrayList<>();
		
		for(Long id : wordIds){
			int index = Collections.binarySearch(OnlineSearch.indexer, id);
			if (index < 0)
				index = (-1)*index - 2;
			
			String invertedIndexStr = readLine (index, id);
			Set<OutputElement> currentSet;
			
			if (invertedIndexStr != null && !invertedIndexStr.isEmpty())
			{
				currentSet = InvertedIndexEntry.setOfIndices(invertedIndexStr);
				docsCounts.add(currentSet.size());
				
				if (outputElements.isEmpty())
					outputElements = currentSet;
				else
					outputElements = retainAllBasedOnPgId(outputElements, currentSet);
			}
		}
		Pair result = new Pair(docsCounts, outputElements);
		return result;
	}
	
	private static String readLine(int index, Long id)
	{
		if (id == -1)
			return null;
		
		String line = "";
		String lineToReturn = "";
		String fileName = Constants.integrated_dir + String.valueOf (index);
		try
		{
			
			BufferedReader in = new BufferedReader(new FileReader(fileName));
			while (line != null )
			{
				line = in.readLine();
				if (line.isEmpty())
					continue;
				
				if (line == null)
					return null;
				
				Long wordId = Long.valueOf(line.split("\t")[0]);
				if (id.equals(wordId))
					return line;
			}
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		return lineToReturn;
	}
	
	private static Set<OutputElement> retainAllBasedOnPgId(Set<OutputElement> pgSet, Set<OutputElement> currentSet)
	{
		HashSet<Long> set1 = new HashSet<>();
		HashSet<Long> set2 = new HashSet<>();
		pgSet.parallelStream().forEach(entry -> set1.add(entry.getPgId()));
		currentSet.parallelStream().forEach(entry -> set2.add(entry.getPgId()));
		
		set1.retainAll(set2);
		Set<OutputElement> newSet = new HashSet<>();
		
		for (OutputElement pg : pgSet)
		
			if(set1.contains(pg.getPgId()))
				newSet.add(pg);
		
		for (OutputElement pg : currentSet)
			if(set1.contains(pg.getPgId()))
				newSet.add(pg);
		
		return newSet;
	}
	
	private static void printTop(List<Long> pgList)
	{
		pgList.stream().limit (Constants.topResults).forEach(pgId -> {
			String pointer_title = FileOps.getInstance().get (pgId);
			System.out.println (pointer_title.split ("_")[1]);
		});
		
		System.out.print ("\t  : ");
		Scanner sc = new Scanner(System.in);
		int input = sc.nextInt();
		if (input < Constants.topResults && input >= 0)
			printWikiPage(pgList.get(input));
		
	}
	
	
	private static void printWikiPage(Long pgId)
	{
		String pointer_title = FileOps.getInstance().get (pgId);
		Long pointer = Long.valueOf(pointer_title.split ("_")[0]);
		byte[] bytes = new byte[0];
		
		try
		{
			FileInputStream fileInputStream = new FileInputStream (Constants.input_file3);
			fileInputStream.getChannel().position(pointer);
			bytes = new byte[4096];
			fileInputStream.read(bytes);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		
		System.out.println(new String(bytes));
	}
	
}