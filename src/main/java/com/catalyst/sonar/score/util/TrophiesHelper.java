package com.catalyst.sonar.score.util;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.sonar.api.config.Settings;
import org.sonar.api.database.DatabaseSession;
import org.sonar.api.measures.Metric;
import org.sonar.api.resources.Resource;
import com.catalyst.sonar.score.ScorePlugin;
import com.catalyst.sonar.score.metrics.MetricsHelper;

public class TrophiesHelper {
	
	private static final int LESS_THAN = -1;
	private static final int EQUAL_TO = 0;
	private static final int GREATER_THAN = 1;
	private static final int NO_MEANING_MEASURE_VALUE = 0;
	private static final int SMALLER_MEASURE_VALUE = -1;
	private static final int BIGGER_MEASURE_VALUE = 1;
	private Settings settings;
	private DateUtility dateUtility;
	private MetricsHelper metricsHelper;
	
	/**
	 * TrophiesHelper constructor
	 * @param settings
	 */
	public TrophiesHelper (Settings settings){
	this.settings = settings;	
	
	}


	/**
	 * This method makes sure that trophies are not awarded twice.	  
	 * @param trophyName
	 * @returns true if the trophy is new for the given project(resource).
	 */
	public boolean newTrophyForThisProject(String trophyName) {
		boolean newTrophy = true;

		Map<String, String> allProperties = new HashMap<String, String>();
		allProperties = settings.getProperties();

		for (Map.Entry<String, String> entry : allProperties.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			/*
			 * if the trophy name is found, this is not a new trophy for this
			 * project (resource)
			 */
			
			if (key.equals(ScorePlugin.PROJECT_TROPHY)) {
				if (parsePropertyValue(value, trophyName)) {
					newTrophy = false;
				}
			}
		}

		return newTrophy;
	}


	/**
	 * Parses the trophy names.  In the database, they are separated
	 * by a semi-colon.
	 * @param value
	 * @param trophyName
	 * @returns true if the trophy name was found
	 */
	private boolean parsePropertyValue(String value, String trophyName) {
		boolean trophyNameFound = false;
		String[] newValue = value.split(";");

		for (String names : newValue) {
			if (names.contains(trophyName)) {
				trophyNameFound = true;
			}
		}
		return trophyNameFound;
	}


	/**
	 * This method takes in a list of build dates and measure values for a given
	 * project and metric along with criteria requirements: a metric name,
	 * a requirement amount and the number of days the requirement must be held 
	 * in order to earn the trophy.
	 * @param entries: a list of snapshot build dates and measure values
	 * @param reqAmt: required amount for metric from criteria list
	 * @param days: number of days to hold a given metric from the criteria list
	 * @returns true if the list of criteria is met for a given metric and project,
	 * false otherwise
	 */
	public boolean criteriaMet(List<SnapshotHistory> entries, double reqAmt,int days, String metricName, DatabaseSession session) {
		
		boolean criteriaMet = false;
		BigDecimal requiredAmt = new BigDecimal(reqAmt);
		metricsHelper = new MetricsHelper(session);
		Metric metric = metricsHelper.findMetricByName(metricName);
		
			if (metric != null){ 
		
				/*
				 * Finds the direction of the metric.  
				 * If the direction == '1' (DIRECTION.BETTER): means that a larger measure value for that 
				 * particular metric is an improvement
				 * If the direction == '-1' (DIRECTION.WORST): means that a larger measure value for that
				 * particular metric is a degradation
				 * If the direction == '0' (DIRECTION.NONE):  means that the direction of the measure value
				 * has no meaning.
				 * Determining if the criteria for awarding a particular trophy has been met depends on the direction
				 * of the metric (whether or not a larger value is an improvement or degradation).
				 */
				int direction = metric.getDirection();
						
				if (direction == BIGGER_MEASURE_VALUE || direction == NO_MEANING_MEASURE_VALUE){
					if (criteriaMetForLargerMeasureValue(entries, requiredAmt, days)){
						criteriaMet = true;
					}
				}else if (direction == SMALLER_MEASURE_VALUE){
					if (criteriaMetForSmallerMeasureValue(entries, requiredAmt, days)){				
						criteriaMet = true;
					}
				}
			}
			
		return criteriaMet;  
		
	}

/**
 * Determines whether a list of snapshot build dates and measure
 * values for a given metric meet the trophy criteria.  This method is called 
 * if a smaller measure value for a metric is considered an improvement. 
 * @param entries: a list of snapshot build dates and measure values
 * @param reqAmt: required amount for metric from criteria list
 * @param days: number of days to hold a given metric from the criteria list
 * @returns true if the list of criteria is met for a given metric and project
 */
	private boolean criteriaMetForSmallerMeasureValue(List<SnapshotHistory> entries, BigDecimal requiredAmt, int days) {
		DateUtility dateUtility = new DateUtility();
		boolean criteriaForDegradation = false;
		int nextMeasureIndex = 0;
		Date goodMeasureDate;
		int goodMeasureIndex = 0;
		BigDecimal nextMeasureValue = null;
		Date nextMeasureValueDate;
		int daysBetweenDates = 0;
		
		/*
		 * loop through all the values until a good value is found: a value that
		 * is less than the required amount
		 */
		for (int i = 0; i < entries.size(); i++) {
			boolean goodNextValue = false;
			
			/*
			 * if the measure value is less than the required amount...a good measure has been found
			 */
			if ((entries.get(i).getMeasureValue()).compareTo(requiredAmt) == LESS_THAN) {
				
				goodMeasureIndex = i;
				// once a good value is found, get the next measure value
				nextMeasureValue = entries.get(i++).getMeasureValue();
				/*
				 * if the next measure value is less than the required amount,
				 * loop through the other measure values until a value that
				 * doesn't meet the requirement is reached.
				 */
				if ((nextMeasureValue.compareTo(requiredAmt) == LESS_THAN)) {
					int lastIndex = entries.size() - 1;
					goodNextValue = true;
					while (goodNextValue) {
						/*
						 * if the value is not the last entry in the list,
						 * retrieve the next measure value.
						 */
						
						if (lastIndex != i) {
							nextMeasureValue = entries.get(i++).getMeasureValue();
							nextMeasureIndex = i;
							/* if the next measure value is greater than 
							 * or equal to the required amount, exit the loop
							*/ 
							if ((nextMeasureValue.compareTo(requiredAmt) == GREATER_THAN) || (nextMeasureValue.compareTo(requiredAmt) == EQUAL_TO)) {
								goodNextValue = false;
							}
							/*
							 * if the value is the last entry, set the next
							 * measure value to the current measure value and
							 * exit the loop
							 */
							
						} else if (lastIndex == i) {
							nextMeasureValue = entries.get(i).getMeasureValue();
							nextMeasureIndex = i;
							goodNextValue = false;

						}

					}

					/*
					 * retrieve and compare the dates of the first good value
					 * found and the last good value found to see if the number
					 * of days the measure was held is greater than or equal to
					 * the required days to determine whether or not a trophy
					 * was earned.
					 */
					goodMeasureDate = entries.get(goodMeasureIndex)
							.getBuildDate();
					nextMeasureValueDate = entries.get(nextMeasureIndex)
							.getBuildDate();

					daysBetweenDates = dateUtility.getDaysBetweenDates(nextMeasureValueDate,goodMeasureDate);
					
					if (daysBetweenDates >= days) {
						//the criteria has been met
						criteriaForDegradation = true;
					}

				}
			}

		}

	
		return criteriaForDegradation;
	}

	/**
	 * Determines whether a list of snapshot build dates and measure
	 * values for a given metric meet the trophy criteria.  This method is called 
	 * if a larger measure value for a metric is considered an improvement. 
	 * @param entries: a list of snapshot build dates and measure values
	 * @param reqAmt: required amount for metric from criteria list
	 * @param days: number of days to hold a given metric from the criteria list
	 * @returns true if the list of criteria is met for a given metric and project
	 */
	public boolean criteriaMetForLargerMeasureValue(List<SnapshotHistory> entries, BigDecimal requiredAmt, int days) {
		DateUtility dateUtility = new DateUtility();
		boolean criteriaForImprovementMet = false;
		int nextMeasureIndex = 0;
		Date goodMeasureDate;
		int goodMeasureIndex = 0;
		BigDecimal nextMeasureValue = null;
		Date nextMeasureValueDate;
		int daysBetweenDates = 0;
		/*
		 * loop through all the values until a good value is found: a value that
		 * is greater than or equal to the required amount
		 */
		for (int i = 0; i < entries.size(); i++) {
			boolean goodNextValue = false;
			
			if ((entries.get(i).getMeasureValue()).compareTo(requiredAmt) == GREATER_THAN
					|| (entries.get(i).getMeasureValue())
							.compareTo(requiredAmt) == EQUAL_TO) {
				
				goodMeasureIndex = i;
				// once a good value is found, get the next measure value
				nextMeasureValue = entries.get(i++).getMeasureValue();
				/*
				 * if the next measure value is greater than the require amount,
				 * loop through the other measure values until a value that
				 * doesn't meet the requirement is reached.
				 */
				
				if ((nextMeasureValue.compareTo(requiredAmt) == GREATER_THAN || nextMeasureValue
						.compareTo(requiredAmt) ==  EQUAL_TO)) {
					int lastIndex = entries.size() - 1;
					goodNextValue = true;
					while (goodNextValue) { 
						/*
						 * if the value is not the last entry in the list,
						 * retrieve the next measure value.
						 */
						
						if (lastIndex != i) {
							nextMeasureValue = entries.get(i++)
									.getMeasureValue();
							nextMeasureIndex = i;
							/* if the next measure value is less than the
							 * required amount, exit the loop
							*/ 
							if (nextMeasureValue.compareTo(requiredAmt) == LESS_THAN) {
								goodNextValue = false;
							}
							/*
							 * if the value is the last entry, set the next
							 * measure value to the current measure value and
							 * exit the loop
							 */
							
						} else if (lastIndex == i) { 
							nextMeasureValue = entries.get(i).getMeasureValue();
							nextMeasureIndex = i;
							goodNextValue = false;

						}

					}

					/*
					 * retrieve and compare the dates of the first good value
					 * found and the last good value found to see if the number
					 * of days the measure was held is greater than or equal to
					 * the required days to determine whether or not a trophy
					 * was earned.
					 */
					goodMeasureDate = entries.get(goodMeasureIndex)
							.getBuildDate();
					nextMeasureValueDate = entries.get(nextMeasureIndex)
							.getBuildDate();
					
					daysBetweenDates = dateUtility.getDaysBetweenDates(nextMeasureValueDate,goodMeasureDate);

					if (daysBetweenDates >= days) {
						//the criteria has been met
						criteriaForImprovementMet = true;
					}

				}
			}

		}

		return criteriaForImprovementMet;
	}


	/**
	 * Check to make sure the trophy property doesn't exist for a given project before
	 * persisting the trophy property.
	 * 
	 * @param propertyKey
	 * @returns true if the property exists for the given project
	 */
	public boolean trophyPropertyExists(String propertyKey) {
		boolean trophyExists = false;
		Map<String, String> allProperties = new HashMap<String, String>();
		allProperties = settings.getProperties();
		for (Map.Entry<String, String> entry : allProperties.entrySet()) {
			String key = entry.getKey();
			String value = entry.getKey();
			if (key.equals(propertyKey) && (!value.isEmpty())) {
				trophyExists = true;
			}
		}

		return trophyExists;      
	}

	
}
