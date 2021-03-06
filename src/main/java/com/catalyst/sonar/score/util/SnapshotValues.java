/*
 * Copyright 2013 Catalyst IT Services
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 		http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package com.catalyst.sonar.score.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Extends {@link ArrayList}{@code <}{@link SnapshotValue}{@code >}, providing
 * extra methods.
 * 
 * @author JDunn
 */
public class SnapshotValues extends ArrayList<SnapshotValue> {

	/**
	 * Populates the {@code SnapshotValues} with the {@code Snapshot} value
	 * arguments.
	 * 
	 * @param snapshotValues
	 */
	public SnapshotValues(SnapshotValue... snapshotValues) {
		this.allValues = Arrays.asList(snapshotValues);
		this.addAll(allValues);
	}
	
	
	/**
	 * @see {@link ArrayList#ArrayList(Collection)}
	 * @param c
	 */
	public SnapshotValues(Collection<? extends SnapshotValue> c) {
		this.allValues = new ArrayList<SnapshotValue>(c);
		this.addAll(allValues);
	}



	/**
	 * Removes all {@link SnapshotValue}s with a {@code Date} older than the
	 * specified number of days ago.
	 * 
	 * @param days
	 * @return
	 */
	public int retainAllWithinDaysAgo(int days) {
		int valuesHidden = 0;
		for (int index = 0; index < this.size(); index++) {
			if (!this.get(index).isWithinDaysAgo(days)) {
				this.remove(index);
				index--;
				valuesHidden++;
			}
		}
		return valuesHidden;
	}

	private List<SnapshotValue> allValues;

	/**
	 * Restores the original contents of this {@link SnapshotValues}.
	 * @param index
	 */
	public final void restore() {
		this.clear();
		this.addAll(allValues);
	}

	@Override
	public final boolean add(SnapshotValue e) {
		allValues.add(e);
		return super.add(e);
	}

	@Override
	public final void add(int index, SnapshotValue element) {
		allValues.add(index, element);
		super.add(index, element);
	}

	@Override
	public final boolean addAll(Collection<? extends SnapshotValue> c) {
		allValues.addAll(c);
		return super.addAll(c);
	}

	@Override
	public final boolean addAll(int index, Collection<? extends SnapshotValue> c) {
		allValues.addAll(index, c);
		return super.addAll(index, c);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 3530568107262993794L;
}
