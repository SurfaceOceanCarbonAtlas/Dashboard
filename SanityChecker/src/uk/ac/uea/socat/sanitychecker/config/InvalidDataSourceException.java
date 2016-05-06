package uk.ac.uea.socat.sanitychecker.config;

public class InvalidDataSourceException extends Exception {

	private static final long serialVersionUID = -260434723418360354L;

	public InvalidDataSourceException(String dataSource) {
		super("The data source '" + dataSource + "' is invalid");
	}
	
}
