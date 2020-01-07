package project;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SimpleAssembler implements Assembler{
	private boolean readingCode = true;
	/**
	 * Method to assemble a file to its executable representation. 
	 * If the input has errors one or more of the errors will be reported 
	 * the StringBulder. The errors may not be the first error in 
	 * the code and will depend on the order in which instructions 
	 * are checked. There is no attempt to report all the errors.
	 * The line number of the last error that is reported 
	 * is returned as the value of the method. 
	 * A return value of 0 indicates that the code had no errors 
	 * and an output file was produced and saved. If the input or 
	 * output cannot be opened, the return value is -1.
	 * The unchecked exception IllegalArgumentException is thrown 
	 * if the error parameter is null, since it would not be 
	 * possible to provide error information about the source code.
	 * @param inputFileName the source assembly language file name
	 * @param outputFileName the file name of the executable version  
	 * of the program if the source program is correctly formatted
	 * @param error the StringBuilder to store the description 
	 * of the error or errors reported. It will be empty (length 
	 * zero) if no error is found.
	 * @return 0 if the source code is correct and the executable 
	 * is saved, -1 if the input or output files cannot be opened, 
	 * otherwise the line number of a reported error.
	 */
	
	private String makeOutputCode(String[] parts) {
		if(parts.length == 1) {
			return InstrMap.toCode.get(parts[0]) + "\n" + 0;
		}
		
		if(parts.length == 2) {
			return InstrMap.toCode.get(parts[0]) + "\n" + Integer.parseInt(parts[1],16);
		}
		
		return null;
		
	}
	
	private String makeOutputData(String[] parts) {
		if(parts.length == 2) {
			return Integer.parseInt(parts[0],16) + "\n" + Integer.parseInt(parts[1],16);
		}
		
		return null;
	}

	
	@Override
	public int assemble(String inputFileName, String outputFileName, StringBuilder error) {
		if(error == null)
			throw new IllegalArgumentException("Error cannot be null");
		Map<Boolean, List<String>> lists = null;
		try (Stream<String> lines = Files.lines(Paths.get(inputFileName))) {
			lists = lines
				.filter(line -> line.trim().length() > 0) 
				.map(line -> line.trim())
				.peek(line -> {if(line.toUpperCase().equals("DATA")) readingCode = false;})
				.map(line -> line.trim())
				.collect(Collectors.partitioningBy(line -> readingCode));
				System.out.println("true List " + lists.get(true)); // these lines can be uncommented 
				System.out.println("false List " + lists.get(false)); // for checking the code
		} catch (IOException e) {
			e.printStackTrace();
		}
		lists.get(false).remove("DATA");
		List<String> outputCode = lists.get(true).stream()
				.map(line -> line.split("\\s+"))
				.map(this::makeOutputCode) // note how we use an instance method in the same class
				.collect(Collectors.toList());
		
		List<String> outputData = lists.get(false).stream()
				.map(line -> line.split("\\s+"))
				.map(this::makeOutputData) // note how we use an instance method in the same class
				.collect(Collectors.toList());
		
		try (PrintWriter output = new PrintWriter(outputFileName)){
			for(String s : outputCode) output.println(s);
			output.println(-1); // signal for the "DATA" separating code and data
			output.println(0); // filler for the 2-line pattern
			for(String s : outputData) output.println(s);
		}catch (FileNotFoundException e) {
			error.append("\nError: Unable to write the assembled program to the output file");
			return -1;
		} catch (IOException e) {
			error.append("\nUnexplained IO Exception");
			return -1;
		}
		
		return 0;
	}
	
	
	
	
}
