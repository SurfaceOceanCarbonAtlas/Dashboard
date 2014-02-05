/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.server;

import gov.noaa.pmel.socat.dashboard.shared.SCMessage;
import gov.noaa.pmel.socat.dashboard.shared.SCMessage.SCMsgSeverity;
import gov.noaa.pmel.socat.dashboard.shared.SCMessage.SCMsgType;
import gov.noaa.pmel.socat.dashboard.shared.SCMessageList;
import gov.noaa.pmel.socat.dashboard.shared.SCMessagesService;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import uk.ac.uea.socat.sanitychecker.Message;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * Server-side implementation of SCMessagesService 
 * 
 * @author Karl Smith 
 */
public class SCMessagesServiceImpl extends RemoteServiceServlet 
									implements SCMessagesService {

	private static final long serialVersionUID = -8385436880674408476L;

	@Override
	public SCMessageList getDataMessages(String username, String passhash, 
						String cruiseExpocode) throws IllegalArgumentException {
		// Authenticate the user
		DashboardDataStore dataStore;
		try {
			dataStore = DashboardDataStore.get();
		} catch (IOException ex) {
			throw new IllegalArgumentException(
					"Unexpected configuration error: " + ex.getMessage());
		}
		if ( ! dataStore.validateUser(username, passhash) )
			throw new IllegalArgumentException(
					"Invalid authentication credentials");

		// Get the list of saved sanity checker Message objects for this cruise
		ArrayList<Message> cruiseMsgs;
		try {
			cruiseMsgs = dataStore.getCruiseFileHandler()
								  .getCruiseMessages(cruiseExpocode);
		} catch (FileNotFoundException ex) {
			throw new IllegalArgumentException("The sanity checker " +
					"has never been run on cruise " + cruiseExpocode);
		}

		// Create the SCMessageList set of data messages for passing to the client 
		SCMessageList scMsgList = new SCMessageList();
		scMsgList.setUsername(username);
		scMsgList.setExpocode(cruiseExpocode);
		for ( Message msg : cruiseMsgs ) {
			// Ignore messages that are not about data
			if ( msg.getMessageType() != Message.DATA_MESSAGE )
				continue;
			// Get the severity
			SCMsgSeverity severity;
			switch( msg.getSeverity() ) {
			case Message.WARNING:
				severity = SCMsgSeverity.WARNING;
				break;
			case Message.ERROR:
				severity = SCMsgSeverity.ERROR;
				break;
			default:
				severity = SCMsgSeverity.UNKNOWN;
			}
			// Ignore messages with an unknown severity (should not be any)
			if ( severity == SCMsgSeverity.UNKNOWN )
				continue;
			// Create the SCMessage from the sanity checker Message
			SCMessage scMsg = new SCMessage();
			scMsg.setType(SCMsgType.DATA);
			scMsg.setSeverity(severity);
			scMsg.setRowNumber(msg.getLineIndex());
			scMsg.setColNumber(msg.getInputItemIndex());
			scMsg.setColName(msg.getInputItemName());
			scMsg.setExplanation(msg.getMessage());
			// Add this SCMessage to the list
			scMsgList.add(scMsg);
		}
		return scMsgList;
	}

}
