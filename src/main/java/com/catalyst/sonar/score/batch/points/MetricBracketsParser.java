/**
 * 
 */
package com.catalyst.sonar.score.batch.points;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The MetricBracketsParser class contains a String metricBracketsString,
 * and a methods to parse the metricBracketsString into a two-dimensional double array
 * representing an array of metricBrackets, which in turn can be encapsulated
 * by an instance of {@link com.catalyst.sonar.score.batch.points.MetricBrackets}.
 *
 */
public class MetricBracketsParser {
	
	public static final int DIVISOR = MetricBrackets.EXPECTED_LENGTH;
	public static final Pattern DECIMAL = Pattern.compile("[0-9]\\d*(\\.\\d+)?");
	
	private final String metricBracketsString;
	private Matcher matcher;
	
	/**
	 * Constructs a MetricBracketsParser, setting the metricBracketsString
	 * to equal the String argument, and instantiates the matcher
	 * from the Pattern DECIMAL with the metricBracketsString.
	 * @param metricBracketsString
	 */
	public MetricBracketsParser(String metricBracketsString) {
		this.metricBracketsString = metricBracketsString;
		matcher = DECIMAL.matcher(this.metricBracketsString);
	}
	
	/**
	 * Resets the matcher.
	 * @return
	 */
	protected Matcher resetMatcher() {
		return matcher.reset();
	}
	
	/**
	 * Using the matcher, numberOfDoubles() calculates the numberOfDoubles
	 * that can be parsed out of the metricBracketsString, resetting the
	 * matcher before and after to ensure continual data integrity.
	 * @return
	 */
	protected int numberOfDoubles() {
		resetMatcher();
		int numberOfDoubles = 0;
		while(matcher.find()) {
			numberOfDoubles++;
		}
		resetMatcher();
		return numberOfDoubles;
	}
	
	/**
	 * Takes the single-dimension array returned from parseDoubles(),
	 * and condenses it into a two-dimensional Array, which in turn can be encapsulated
	 * by an instance of {@link com.catalyst.sonar.score.batch.points.MetricBrackets}. 
	 * @return
	 */
	public double[][] parseMetricBrackets() {
		double[] doubles = parseDoubles();
		double[][] metricBrackets = new double[doubles.length / DIVISOR][DIVISOR];
		int doublesIndex = 0;
		for(int index = 0; index < metricBrackets.length; index ++) {
			for(int secondIndex = 0; secondIndex < DIVISOR; secondIndex++) {
				metricBrackets[index][secondIndex] = doubles[doublesIndex];
				doublesIndex++;
			}
		}
		return metricBrackets;
	}
	
	/**
	 * Parses the metricBraketsString into a single-dimension double array.
	 * @return
	 * @throws IllegalArgumentException
	 */
	public double[] parseDoubles() throws InvalidNumberOfDoublesException {
		int numberOfDoubles = numberOfDoubles();
		if(numberOfDoubles % DIVISOR != 0) {
			throw new InvalidNumberOfDoublesException(metricBracketsString, numberOfDoubles);
		}
		double[] doubles = new double[numberOfDoubles];
		int index = 0;
		while(matcher.find()) {
			String match = metricBracketsString.substring(matcher.start(), matcher.end());
			doubles[index] = Double.parseDouble(match);
			index++;
		}
		return doubles;
	}

}
