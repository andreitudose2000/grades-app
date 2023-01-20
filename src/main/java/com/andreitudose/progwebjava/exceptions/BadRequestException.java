package com.andreitudose.progwebjava.exceptions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;
import java.util.Set;

import com.andreitudose.progwebjava.utils.SerializationUtils;

public class BadRequestException extends Exception {
	public BadRequestException(Map<String, String> errors) {
		super(SerializationUtils.serialize(errors));
	}
}


