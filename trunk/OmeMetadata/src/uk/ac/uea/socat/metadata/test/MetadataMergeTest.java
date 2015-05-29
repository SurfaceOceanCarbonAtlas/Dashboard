package uk.ac.uea.socat.metadata.test;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.jdom2.Document;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import uk.ac.uea.socat.metadata.OmeMetadata.OmeMetadata;

public class MetadataMergeTest {
	
	public MetadataMergeTest() {
		// Make sure the relevant files exist
		boolean ok = checkFilesExist();
		
		// Run the tests
		if (ok) {
			runTests();
		}
	}
	
	public static void main(String[] args) {
		new MetadataMergeTest();
	}
	
	private boolean checkFilesExist() {
		boolean result = true;
		
		File exampleOnePartOneCheck = new File("Test1.xml");
		if (!exampleOnePartOneCheck.exists()) {
			result = false;
			System.out.println("Missing file Test1.xml");
		}
		
		return result;
	}
	
	private void runTests() {
		mergeTest();
		//makeHeaderTest();
		//readHeaderTest();
	}
	
	private void readHeaderTest() {
		try {
			StringBuffer fileData = new StringBuffer();
	        BufferedReader reader = new BufferedReader(new FileReader("header.txt"));
	        char[] buf = new char[1024];
	        int numRead=0;
	        while((numRead=reader.read(buf)) != -1){
	            String readData = String.valueOf(buf, 0, numRead);
	            fileData.append(readData);
	        }
	        reader.close();
	        
	        OmeMetadata inputMD = new OmeMetadata("");
	        inputMD.assignFromHeaderText(fileData.toString());
	        
	        System.out.println(inputMD.getHeaderText());

		} catch (Exception e) {
			System.out.println("Test failed because of exception: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unused")
	private void makeHeaderTest() {
		try {
			File inputFile = new File("Test1.xml");
			Document input = (new SAXBuilder()).build(inputFile);
			OmeMetadata inputMD = new OmeMetadata("");
			inputMD.assignFromOmeXmlDoc(input);
			
			System.out.println(inputMD.getHeaderText());
		} catch (Exception e) {
			System.out.println("Test failed because of exception: " + e.getMessage());
		}
	}
	
	/**
	 * The pathological case where a request is made
	 * to merge one file. It should return the exact same
	 * file.
	 */
	@SuppressWarnings("unused")
	private void mergeTest() {
		try {
			// Load the input metadata
			/*
			File inputFile = new File("Test1.xml");
			Document input = (new SAXBuilder()).build(inputFile);
			OmeMetadata input1 = new OmeMetadata("");
			input1.assignFromOmeXmlDoc(input);

			inputFile = new File("Test2.xml");
			input = (new SAXBuilder()).build(inputFile);
			OmeMetadata input2 = new OmeMetadata("");
			input2.assignFromOmeXmlDoc(input);
			
			inputFile = new File("Test3.xml");
			input = (new SAXBuilder()).build(inputFile);
			OmeMetadata input3 = new OmeMetadata("");
			input3.assignFromOmeXmlDoc(input);
			*/

			File inputFile = new File("conflict.xml");
			Document input = (new SAXBuilder()).build(inputFile);
			OmeMetadata input1 = new OmeMetadata("");
			input1.assignFromOmeXmlDoc(input);

			
			//OmeMetadata merged = OmeMetadata.merge(input1, input2, input3);
			OmeMetadata merged = OmeMetadata.merge(input1);
			
			Document output = merged.createOmeXmlDoc();
			
			(new XMLOutputter(Format.getPrettyFormat())).output(output, System.out);
		}
		catch (Exception e) {
			System.out.println("Test failed because of exception: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
