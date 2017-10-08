package project;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

/**
 * Used to read various files used by the Warehouse and to write files for end of day reports.
 */
public class ReadAndWrite {

  /**
   * Read a file line by line. Each line is a <code>String[]</code> in an <code>ArrayList</code>,
   * the line is split up according to the given separator.
   * 
   * @param location the file path of the file to be read.
   * @param separator determines how each line is split.
   * @param skipHeader if true will not include the first line in the output.
   * @return a list of String arrays, each one corresponding to a line in the file.
   * @throws IOException file is removed or otherwise ceases to exist during reading.
   * @throws FileNotFoundException if a file cannot be found prints an error message that it could
   *         not be found.
   */
  protected static ArrayList<String[]> readFile(Path location, String separator, boolean skipHeader)
      throws IOException, FileNotFoundException {
    BufferedReader br = null;
    String line = "";
    ArrayList<String[]> fileContents = new ArrayList<>();
    try {
      br = new BufferedReader(Files.newBufferedReader(location));
      if (skipHeader) {
        br.readLine(); // Skip the Header
      }
      while ((line = br.readLine()) != null) {
        String[] lineItems;
        lineItems = line.split(separator);
        fileContents.add(lineItems);
      }
    } catch (FileNotFoundException fileNotFound) {
      System.err.println("File " + location + " not found, please enter a valid file path.");
    } catch (IOException error) {
      // TODO Auto-generated catch block
      error.printStackTrace();
    } finally {
      if (br != null) {
        try {
          br.close();
        } catch (IOException error) {
          error.printStackTrace();
        }
      }
    }
    return fileContents;
  }

  /**
   * Given a list of <code>String[]</code> write a comma separated value file. Each
   * <code>String[]</code> in the list corresponds to one line in the file. If the file already
   * exists, overwrite it, else create a new file.
   * 
   * @param fileDirectory the file directory in which the file is to be stored in.
   * @param name of the file to be written.
   * @param toBeWritten Array List of String arrays to be written into the file
   * @throws IOException input file disappears during writing, or is open when writing.
   */
  protected static void writeFile(Path fileDirectory, String name, ArrayList<String[]> toBeWritten)
      throws IOException {
    try {
      Path fileName = fileDirectory.resolve(name);
      String csvFile = fileName.toString();
      FileWriter writer = new FileWriter(csvFile);
      int size = toBeWritten.size();
      StringBuilder oneLine = new StringBuilder();
      for (int index = 0; index < size; index++) {
        String[] stringToBeWritten = toBeWritten.get(index);
        for (int j = 0; j < stringToBeWritten.length; j++) {
          oneLine.append(stringToBeWritten[j] + ",");
        }
        oneLine.append("\n");
      }
      writer.write(oneLine.toString());
      writer.close();
    } catch (FileNotFoundException error) {
      System.err.print(error.getMessage());
    }
  }
}
