import javafx.util.Pair;

import java.io.*;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

class KWayMerger
{
	private static final int k = Constants.K;
	
	void mergeAll() throws IOException
	{
		String dir = "Data/output/Page";
		boolean mergedAll = false;
		Long startTime = System.currentTimeMillis();
		System.out.print("\n\t PHASE 2 : INTEGRATION, merging all page level inverted indices........\n");
		
		for(int iter = 0; !mergedAll; iter++)
		{
			if (iter%10 ==0)
				System.out.println();
			System.out.print("\t" + iter);
			
			if(iter != 0)
				dir = "Data/output/Integrated";
			
			File directory = new File(dir);
			File[] fileList = directory.listFiles();
			int noOfFiles = fileList != null ? fileList.length : 0;
			
			for (String fileNames : directory.list() )
				if(noOfFiles == 2 && fileNames.startsWith("."))
				{
					mergedAll = true;
				}
				
			mergedAll |= (noOfFiles<=1);
			if(mergedAll) break;
			
			for (int i = 0; i <= noOfFiles; i += k)
			{
				List<BufferedReader> fileReaders = new LinkedList<>();
				List<String> fileNames = new LinkedList<>();
				
				for (int j = 0; j < k && i + j < noOfFiles; j++)
				{
					String fileName = fileList[i + j].getName();
					if (!fileName.startsWith("."))
					{
						fileReaders.add(new BufferedReader(new FileReader(dir + "/" + fileName)));
						fileNames.add(fileName);
					}
				}
				mergeK(fileNames, fileReaders, String.valueOf(i / k) + ".txt", iter);
				deleteK(dir, fileNames);
			}
		}
		
		Long diff = (System.currentTimeMillis()  - startTime);
		System.out.println(" COMPLETED. took " + diff.toString() + " MILLI-SEC.");
	}

	private void mergeK(List<String> fileNames, List<BufferedReader> fileReaders, String outFileName, int iter) throws IOException
	{
		InvertedIndex invertedIndex = new InvertedIndex();
		FileOps fileOps = FileOps.getInstance();
		List<Boolean> readNextLine = Collections.nCopies(fileNames.size(), true);
		boolean mergeComplete = false, leaveFirstBlankLine = true;
		
		while (!mergeComplete)
		{
			for(int i=0; i<readNextLine.size(); i++)
			{
				if(readNextLine.get(i))
				{
					String line = fileReaders.get(i).readLine();
					if (line != null && !line.isEmpty())
						invertedIndex.insert(i,
							iter==0 ?
							InvertedIndexEntry.fromPgLevelInvertedIndex(fileNames.get(i), line) :
							InvertedIndexEntry.fromInvertedIndexString (line));
				}
			}
			
			if(iter==0 && leaveFirstBlankLine)
			{
				leaveFirstBlankLine = false;
				continue;
			}
			
			Pair <String, List<Boolean>> writeIndexAndReadFlags = invertedIndex.pop(fileNames.size());
			String invertedIndexEntry = writeIndexAndReadFlags.getKey();
			fileOps.save (true,"Integrated", (String.valueOf(iter)+"_"+outFileName) , invertedIndexEntry);
			readNextLine = writeIndexAndReadFlags.getValue();
			
			Boolean flag = false;
			for(Boolean b : readNextLine) flag |= b;
			mergeComplete = !flag;
		}
	}
	
	private void deleteK(String dir, List<String> fileNames)
	{
		fileNames.forEach (f -> new File(dir + "/" + f).delete());
	}
	
}
