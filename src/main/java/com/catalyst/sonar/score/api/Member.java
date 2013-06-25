/**
 * 
 */
package com.catalyst.sonar.score.api;

/**
 * @author JDunn
 * A member can be added to a {@link com.catalyst.sonar.score.api.Group}.
 * All classes that implement Member<E> must also extend E.
 *
 */
public interface Member<E> extends ScoreEntity {
	
	/**
	 * @return the name
	 */
	String getName();
	
	/**
	 * @param name the name to set
	 * @return this (note: <code>this</code> will be of type super and may need to be cast)
	 */
	E setName(String name);
	
	/**
	 * @return the uniqueId
	 */
	String getUniqueId();
	
	/**
	 * @return the description
	 */
	String getDescription();
	
	/**
	 * @param description the description to set
	 * @return this (note: <code>this</code> will be of type super and may need to be cast)
	 */
	E setDescription(String description);

}