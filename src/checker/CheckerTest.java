package checker;

import junit.framework.TestCase;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class CheckerTest extends TestCase {

	public void testInput() {
		try {
			String inputFilePath = "test" + File.separatorChar + "test1.input";

			Checker.main(new String[]{inputFilePath});
			List<String> result = Checker.getResults();

			String outputFilePath = "test" + File.separatorChar + "test1.output";
			List<String> output = Files.readAllLines(Paths.get(outputFilePath), StandardCharsets.UTF_8);
			assertEquals(output, result);
		} catch (IOException ioEx) {
			System.err.println("Can't read testing files");
		}
	}
}
