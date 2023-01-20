package com.andreitudose.progwebjava.exceptions;

public class NotFoundException extends Exception {
	public NotFoundException(String entityName, String propertyName, String propertyValue) {
		super(String.format("%s with %s = %s not found",
							entityName, 
							propertyName, 
							propertyValue));
	}

}

