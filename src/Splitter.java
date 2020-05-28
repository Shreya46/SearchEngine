import java.io.*;
import java.util.ArrayList;

class Splitter
{
	void splitAndDelete() throws IOException
	{
		Long startTime = System.currentTimeMillis();
		System.out.print("\n\t PHASE 3 : SPLITTING, breaking inverted index into equal sized index...");
		
		String dir = "Data/output/Integrated";
		File[] fileList = new File(dir).listFiles();
		String integratedFileName = fileList[0].getName();
		BufferedReader reader = new BufferedReader(new FileReader(dir + "/" + integratedFileName));
		FileOps fileOps = FileOps.getInstance();
		
		String line = "";
		int outFileCount = 0;
		int currentFileSize = 0;
		String outFile = String.valueOf (outFileCount);
		ArrayList <Long> startWordId = new ArrayList<>();
		startWordId.add(0L);
		
		while (true)
		{
			line = reader.readLine();
			if (line==null || line.isEmpty())
				break;
			
			int lineSize = line.length();
			if (currentFileSize + lineSize > Constants.maxIntegratedFileSize)
			{
				currentFileSize = 0;
				outFileCount++;
				outFile = String.valueOf(outFileCount);
				fileOps.save(true,"Integrated", outFile, line);
				startWordId.add (Long.valueOf(line.split("\t")[0]));
			}
			else
				fileOps.save(true,"Integrated", outFile, "\n"+line);
			currentFileSize += lineSize;
		}
		
		fileList[0].delete();
		StringBuilder wordIdIndexStr = new StringBuilder();
		startWordId.forEach(id -> wordIdIndexStr.append(id).append("\n"));
		fileOps.save(false, "Page", Constants.wordIndex_file, wordIdIndexStr.toString());
		Long diff = (System.currentTimeMillis()  - startTime);
		System.out.println(" COMPLETED. took " + diff.toString() + " MILLI-SEC.");
	}
}
