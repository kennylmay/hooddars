package dars;

public class Message {
   public String message;
   public String destinationId;
   public String originId;
/**
 * This Message constructor takes 3 parameters, if the DestinationId is set 
 * to "ALL" a broadcast message will be assumed.
 * @param DestinationId
 * @param OriginId
 * @param Message
 */
   public Message(String DestinationId, String OriginId, String Message){
       message = Message;
       destinationId = DestinationId;
       originId = OriginId;
   }
   
}
