import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Page implements Runnable
{
	private long pageNo;
	private String title;
	private long filePointer;
	private Map<Long, int[]> pageLevel_invertedIndex;
	
	Page(long pageNo, long filePointer)
	{
		this.pageNo = pageNo;
		this.filePointer = filePointer;
		pageLevel_invertedIndex = new ConcurrentHashMap<>();
	}
	
	@Override
	public void run()
	{
		FileOps fileOps = FileOps.getInstance();
		fileOps.put(pageNo, filePointer, title);
		fileOps.save(false,"Page", String.valueOf(pageNo), this.toString());
	}
	
	void put(Long word, Tags tag)
	{
		int[] inv_index = pageLevel_invertedIndex.getOrDefault(word, new int[]{0, 0, 0});
		inv_index [tag.ordinal()]++;
		pageLevel_invertedIndex.put(word, inv_index);
	}
	
	@Override
	public String toString()
	{
		StringBuilder strMap = new StringBuilder();
		List<Long> keySet = new ArrayList<>(pageLevel_invertedIndex.keySet());
		Collections.sort(keySet);
		for(Long key : keySet)
		{
			strMap.append("\n" + String.valueOf(key) + "\t:\t");
			int[] arr = pageLevel_invertedIndex.get(key);
			for (Integer anArr : arr) strMap.append(anArr + " ");
		}
		return strMap.toString();
	}
	
	public void setTitle(String readText)
	{
		this.title = readText;
	}
}