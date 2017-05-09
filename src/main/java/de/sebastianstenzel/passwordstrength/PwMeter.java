package de.sebastianstenzel.passwordstrength;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.nulabinc.zxcvbn.Strength;
import com.nulabinc.zxcvbn.Zxcvbn;

public class PwMeter {

	private final List<String> dictionary;
	private final Zxcvbn zxcvbn = new Zxcvbn();

	private PwMeter(List<String> dictionary) {
		this.dictionary = dictionary;
	}

	private Strength measure(String password) {
		return zxcvbn.measure(password, dictionary);
	}

	public static void main(String[] args) throws IOException {
		if (args.length != 2) {
			System.err.println("Usage: java -jar /path/to/pwmeter.jar /path/to/dict.utf8.txt /path/to/passwords.utf8.txt");
			System.exit(1);
		} else if (!Files.isRegularFile(Paths.get(args[0]))) {
			System.err.println("Dict file not found");
			System.exit(1);
		} else if (!Files.isRegularFile(Paths.get(args[1]))) {
			System.err.println("Password file not found");
			System.exit(1);
		}

		try (Stream<String> dictLines = Files.lines(Paths.get(args[0]), StandardCharsets.UTF_8); //
				Stream<String> pwLines = Files.lines(Paths.get(args[1]), StandardCharsets.UTF_8)) {
			PwMeter pwMeter = new PwMeter(dictLines.collect(Collectors.toList()));
			System.out.println("Password\tGuesses");
			pwLines.map(pwMeter::measure).forEach(strength -> {
				String result = String.format("%s\t%.0f", strength.getPassword(), strength.getGuesses());
				System.out.println(result);
			});
		}
	}

}
