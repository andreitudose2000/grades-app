package com.andreitudose.progwebjava.exceptions;

public class DuplicateItemException extends Exception {
	public DuplicateItemException(String entityName, String propertyName, String propertyValue) {
		super(String.format("%s with %s = %s already exists",
							entityName, 
							propertyName, 
							propertyValue));
	}

}

