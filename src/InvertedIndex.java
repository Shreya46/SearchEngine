import javafx.util.Pair;

import java.util.*;

class InvertedIndex
{
	private PriorityQueue <InvertedIndexEntry> invertedIndex;
	private static Map<Long, List<Integer>> wordId_fileReaderId;
	
	InvertedIndex()
	{
		InvIndComparator invIndComparator = new InvIndComparator();
		invertedIndex = new PriorityQueue<>(invIndComparator);
		wordId_fileReaderId = new HashMap<>();
	}
	
	void insert(int fileReaderId, InvertedIndexEntry newInvertedIndexEntry)
	{
		boolean inserted = false;
		for(InvertedIndexEntry e : invertedIndex)
			if (e.equals(newInvertedIndexEntry))
			{
				e.merge(newInvertedIndexEntry);
				inserted = true;
				break;
			}
			
		if (!inserted)	invertedIndex.add(newInvertedIndexEntry);
		insertInFileReaderMapping (newInvertedIndexEntry.getWordId(), fileReaderId);
	}
	
	private void insertInFileReaderMapping(Long wordId, int fileReaderId)
	{
		List<Integer> fileReaderIds = wordId_fileReaderId.getOrDefault(wordId, new LinkedList<>());
		fileReaderIds.add(fileReaderId);
		wordId_fileReaderId.put(wordId, fileReaderIds);
	}
	
	Pair<String,List<Boolean>> pop(int size)
	{
		List<Boolean> readNextLine = new LinkedList<>();
		for (int i=0; i<size; i++)	readNextLine.add(false);
		
		InvertedIndexEntry topElement = invertedIndex.poll();
		if (topElement == null)
		{
			return new Pair<>("", readNextLine);
		}
		
		Long smallestWordId = topElement.getWordId();
		List<Integer> fileReaders = wordId_fileReaderId.get (smallestWordId);
		fileReaders.forEach(index -> readNextLine.set(index, true));
		
		wordId_fileReaderId.remove(smallestWordId);
		return new Pair<>(topElement.toString(), readNextLine);
	}
}
