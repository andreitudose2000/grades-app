package com.andreitudose.progwebjava.exceptions;

public class CannotDeleteException extends Exception {
	public CannotDeleteException(String entityName, String propertyName, String propertyValue, String associatedEntitiesName) {
		super(String.format("%s with %s = %s can not be deleted because it has associated %s",
							entityName, 
							propertyName, 
							propertyValue,
							associatedEntitiesName));
	}

}

