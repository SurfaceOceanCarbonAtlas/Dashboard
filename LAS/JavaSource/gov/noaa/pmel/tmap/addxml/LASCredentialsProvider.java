package gov.noaa.pmel.tmap.addxml;

import org.apache.http.client.CredentialsProvider;


public abstract class LASCredentialsProvider implements CredentialsProvider{

	// Derived class defines these methods.
	public abstract void setUsername(String username);
	public abstract void setPassword(String password);
}
