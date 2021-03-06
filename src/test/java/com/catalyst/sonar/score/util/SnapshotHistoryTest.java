package com.catalyst.sonar.score.util;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

public class SnapshotHistoryTest {
	
	
	private Date buildDate = new Date(1371193200000l);	
	private BigDecimal measureValue = new BigDecimal(500.00);
	private SnapshotValue sh = new SnapshotValue (measureValue, buildDate);
	private SnapshotValue snapshot1;
	@Before
	public void setUp(){
	sh.setBuildDate(buildDate)	;
	sh.setMeasureValue(measureValue);
	snapshot1 = new SnapshotValue (new BigDecimal(9090), new Date(756267889000l));
	}
	
	
/**
 * Testing that the build date returned when calling the getBuildDate method
 */
	@Test
	public void testGetBuildDate(){
		assertSame(buildDate, sh.getBuildDate());
	}

/**
 * Testing that the measure value is returned when calling the getMeasureValue method
 */
	@Test
	public void testGetMeasureValue(){
		assertSame(measureValue, sh.getMeasureValue());
	}

	/**
	 * Testing that when an null object is passed to the overridden
	 * equals method, the method returns false
	 */
	@Test
	public void testEqualWhenObjectIsNull(){
		Object obj = null;
		assertEquals(sh.equals(obj), false);
	}
	/**
	 * Testing that when an object is passed in to the overridden equals
	 * SnapshotHistory method and the object is not a SnapshotHistory object, but
	 * another type of object, the method returns false
	 */
	@Test
	public void testEqualWhenNotInstanceOfSnapshotHistory(){
		String myString = "test";
		assertEquals(sh.equals(myString), false);
	}
			
	/**
	 * If the build date is null when testing for SnapshotHistory equality, the overridden
	 * equals method returns false
	 */
	@Test
	public void testSnapshotHistoryBuildDateNullEquality(){
		SnapshotValue snapshotTest = new SnapshotValue(null, null);
		assertEquals(snapshotTest.equals(snapshot1), false);
	}
	
	/**
	 * Testing that if the build date is null and the other build date is null
	 * when testing for SnapshotHistory equality, the overridden equals
	 * method returns false
	 */
	@Test
	public void testSnapshotEqualityWhenBuildDatesNull(){
		SnapshotValue snapshotTest = new SnapshotValue(new BigDecimal (500),null );
		SnapshotValue snapshotTest1 = new SnapshotValue(null, null);
		assertEquals(snapshotTest.equals(snapshotTest1), false);
	}
	
	/**
	 * Testing that if the build dates are different, the SnapshotHistory
	 * objects are not eql.  Equals method returns false.
	 */
	@Test
	public void testDifferentBuildDatesSnapshotInEquality(){
		SnapshotValue snapshot1 = new SnapshotValue(new BigDecimal (500), new Date (774584689000l));
		SnapshotValue snapshot2 = new SnapshotValue(new BigDecimal (500), new Date (756267889000l));
		assertEquals(snapshot1.equals(snapshot2), false);
	}
	
	/**
	 * Testing that if two snapshots have the same build date, but the measure value is
	 * null, then the two snapshot history objects are not equal.
	 */
	@Test
	public void testSameBuildDateNullMeasureValue(){
		SnapshotValue snapshot1 = new SnapshotValue(null, new Date (774584689000l));
		SnapshotValue snapshot2 = new SnapshotValue(new BigDecimal(678), new Date (774584689000l));
		assertEquals(snapshot1.equals(snapshot2), false);	
	}
	
	/**
	 * Testing that when the measure values are null and the build dates are equal,
	 * the snapshot history objects are equal
	 */
	@Test
	public void testSnapshotHistoryEqualityWhenBuildDatesSameAndValuesAreNull(){
		SnapshotValue snapshot1 = new SnapshotValue(null, new Date (774584689000l));
		SnapshotValue snapshot2 = new SnapshotValue(null, new Date (774584689000l));
		assertEquals(snapshot1.equals(snapshot2), true);	

	}
	
	/**
	 * Testing that when testing for SnapshotHistory object equality,
	 * if the two snapshot objects are equal, the overridden equals method in the
	 * SnapshotHistory class returns true
	 */
	@Test
	public void testSnapshotObjectEquality(){
		assertEquals(snapshot1.equals(snapshot1), true);
	}

	/**
	 * Testing that when the build date and measure value are null,
	 * the hash code is 961.
	 * 
	 */
	@Test
	public void testhashWhenBuildDateAndValuesNull(){
		SnapshotValue sh1 = new SnapshotValue(null, null);
		assertEquals(sh1.hashCode(),961);	
		
	}
	/**
	 * Testing that when the build date is null, but the measure value is 1,
	 * the hash code is 992
	 */
	@Test
	public void testHashWhenBuildDateNullAndMeasureValueIsOne(){
		SnapshotValue sh1 = new SnapshotValue(new BigDecimal(1), null);
		assertEquals(sh1.hashCode(),992);		
	}
	
	/**
	 * Testing that when the build date is 10/19/1993 and the measure value is null,
	 * the hash code is 1930062035
	 */
	@Test
	public void testHashWhenBuildDateNotNullButValueIsNull(){
		SnapshotValue sh1 = new SnapshotValue(null, new Date (750988800000l));
		assertEquals(sh1.hashCode(),1930062035);	
	}

}
