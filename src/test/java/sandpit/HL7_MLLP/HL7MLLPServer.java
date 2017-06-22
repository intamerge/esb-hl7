package sandpit.HL7_MLLP;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.Executors;

import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.app.HL7Service;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.protocol.ReceivingApplication;
import ca.uhn.hl7v2.protocol.ReceivingApplicationException;
import ca.uhn.hl7v2.validation.builder.support.NoValidationBuilder;

public class HL7MLLPServer {

	public static void main(String[] args) throws InterruptedException {
		HapiContext ctx = new DefaultHapiContext();
		ctx.setValidationRuleBuilder(new NoValidationBuilder());
		HL7Service server1 = ctx.newServer(7777, false);
		ctx.setExecutorService(Executors.newCachedThreadPool());
		ReceivingApplication handler = new ExampleReceiverApplication();
		server1.registerApplication("*", "*", handler);
		// Start the server listening for messages
		server1.startAndWait();
		try{
			ctx.close();
			}
		catch(Exception e){}
	}

	public static class ExampleReceiverApplication implements ReceivingApplication
	{
		
		/**
		 * {@inheritDoc}
		 */
		public boolean canProcess(Message theIn) {
			return true;
		}
		
		
		/**
		 * {@inheritDoc}
		 */
		public Message processMessage(Message theMessage, Map<String, Object> theMetadata) throws ReceivingApplicationException, HL7Exception {
			HapiContext context = new DefaultHapiContext();
			String encodedMessage = context.getPipeParser().encode(theMessage);
			System.out.println("Received message:\n" + encodedMessage + "\n\n");
			// Now generate a simple acknowledgment message and return it
			try {
				return theMessage.generateACK();
			} catch (IOException e) {
				throw new HL7Exception(e);
			}finally{
				try{context.close();}catch(Exception e){}
			}
			
		}
		
	}
}

