package project;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class FullAssembler implements Assembler {
	private boolean readingCode = true;
	
	@Override
	public int assemble(String inputFileName, String outputFileName, StringBuilder error) {
		
		if(error == null)
			throw new IllegalArgumentException("Error cannot be null");
		
		ArrayList<String> txtFile = new ArrayList<String>();
		boolean errorInSyntax = false;
		  try {
	            	
			  	

			  	File file = new File(inputFileName);
	            
	           Scanner input = new Scanner(file);
	           

	            while (input.hasNextLine()) {
	            	
	                txtFile.add(input.nextLine());
	                
	                
	                
	            }
	            input.close();

	        } catch (Exception ex) {
	            ex.printStackTrace();
	        }

		  int latestErrorLine = 0;
			Boolean blankLineFound = false;
		  	int firstBlankLine = 0;
		  	int currentLine = 0;
		  
		  	//First Loop For Data And Blank Lines
		  for(String line : txtFile) {
			  currentLine++;
			
              //Checking for blank line errors
              if(line.trim().length() == 0 && blankLineFound != true) {
            	   
              	blankLineFound = true;
              	firstBlankLine = currentLine;
              	latestErrorLine = firstBlankLine;
              	
              }
              
              if(line.trim().length() != 0 && blankLineFound == true) {
            	 
              if(error.lastIndexOf("Illegal blank line in the source file") == -1)
            	  error.append("\nIllegal blank line in the source file at line: " + firstBlankLine);
              	
              	latestErrorLine = currentLine;
              	errorInSyntax = true;
              }
              
              //Checking for white space at beginning of a non-blank line
             if(blankLineFound != true) {
              if(line.charAt(0) == ' ' || line.charAt(0) == '\t') {
            	 
            	 error.append("\nLine starts with illegal white space at line: " + currentLine);
              	latestErrorLine = currentLine;
              	errorInSyntax = true;
              	
              }
             }
              if(line.trim().toUpperCase().equals("DATA") && readingCode == false) {
              	error.append("\nThere is a second seperator at line: " + currentLine);
              	latestErrorLine = currentLine;	
              	errorInSyntax = true;
              }
              
              if(line.trim().toUpperCase().equals("DATA") && readingCode == true) {
                	if(!line.trim().equals("DATA")) {
                		error.append("\nLine does not have DATA in upper case at line: " + currentLine);
                		latestErrorLine = currentLine;
                		errorInSyntax = true;
                	}
                	if(line.trim().equals("DATA")) {
                		readingCode = false;

                	}              	
                }
		  }
		  readingCode = true;
		  currentLine = 0;
		  //Second Loop For Code (only looks at code if syntax is correct
		  // from fist loop
		 blankLineFound = false;
		if(errorInSyntax == false) {
		  for(String line : txtFile) {
			  currentLine++;
			 
			  if(line.trim().length() == 0 && blankLineFound != true) {
           	   
	              	blankLineFound = true;
	              	
	              }
		if(blankLineFound != true) {
			  String[] parts = line.split("\\s+");
			//If DATA pops up code is no longer going to be looked at
			  if(readingCode == true && line.trim().equals("DATA"))
				  readingCode = false;
			  
			if(readingCode == true) {
			  //Checking for illegal mnemonic
				
			  if(!(InstrMap.toCode.keySet().contains(parts[0].toUpperCase()))) {
				  error.append("\nError on line " + (currentLine) + ": illegal mnemonic");
				  latestErrorLine = currentLine;
			  }
			  
			  //Checking for errors within legal mnemonic
			  if(InstrMap.toCode.keySet().contains(parts[0].toUpperCase())) {
				  //Checking for upper case
				 if(!InstrMap.toCode.keySet().contains(parts[0])){
					 error.append("\nError on line " + (currentLine) + ": mnemonic must be upper case");
					 latestErrorLine = currentLine;
				 }
				 
				 //Capitalized 
				 if(InstrMap.toCode.keySet().contains(parts[0])){
					 
					 //Does not need an argument
					 if(noArgument.contains(parts[0]) && parts.length != 1) {
						 error.append("\nError on line " + (currentLine) + ": this mnemonic cannot take arguments");
						 latestErrorLine = currentLine;
					 }
					 
					 //Needs an argument
					 if(!noArgument.contains(parts[0])) {
						
						 //Too many args
						 if(parts.length > 2) {
							 error.append("\nError on line " + (currentLine) + ": this mnemonic has too many arguments");
							 latestErrorLine = currentLine;
						 }
						 
						 //Not enough args
						 if(parts.length < 2) {
							 error.append("\nError on line " + (currentLine) + ": this mnemonic is missing an argument");
							 latestErrorLine = currentLine;
						 }
						 
						 //Correct number of args for further testing
						 if(parts.length == 2) {
							 try{
								
									int arg = Integer.parseInt(parts[1],16);
								
								} catch(NumberFormatException e) {
									error.append("\nError on line " + (currentLine) + 
											": argument is not a hex number");
									latestErrorLine = currentLine;				
								} 
							 
							 
						 }
						 
						 
					 }
					 
					 
				 }
				 
				 
			  }
			  
			}
			if(readingCode == false && !line.trim().equals("DATA")){
				 try{
					
						int arg = Integer.parseInt(parts[0],16);
					
					} catch(NumberFormatException e) {
						error.append("\nError on line " + (currentLine) + 
								": data has non-numeric memory address");
						latestErrorLine = currentLine;				
					}
				 
				 try{
						
						int arg = Integer.parseInt(parts[1],16);
					
					} catch(NumberFormatException e) {
						error.append("\nError on line " + (currentLine) + 
								": data has non-numeric memory value");
						latestErrorLine = currentLine;						
						}
				
			}
			  
			
			  
			  

		  }
		  }
		} 
		  
		
		  
		  
		  
		if(error.length() == 0) {
			SimpleAssembler s = new SimpleAssembler();
			return s.assemble(inputFileName, outputFileName, error);
		}
			return latestErrorLine;
	}

}
