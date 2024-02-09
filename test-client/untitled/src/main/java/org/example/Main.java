package org.example;
// We use Paho because it is the most common MQTT client implementation for Java
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class Main
{
	public static class Callback implements MqttCallback
	{

		@Override
		public void connectionLost( java.lang.Throwable throwable )
		{
			System.out.println( "Connection lost" );
		}

		@Override
		public void messageArrived( java.lang.String s, MqttMessage mqttMessage ) throws Exception
		{
			System.out.println( "Message arrived" );
		}

		@Override
		public void deliveryComplete( IMqttDeliveryToken iMqttDeliveryToken )
		{
		}
	}

	public static void main( String[] args ) throws Exception
	{
		MqttAsyncClient client = new MqttAsyncClient(  "tcp://localhost:1883", "test", new MemoryPersistence() );
		MqttConnectOptions connectOptions = new MqttConnectOptions();
		connectOptions.setCleanSession( true );
		connectOptions.setKeepAliveInterval(5);
		System.out.println( "connecting..." );
		client.connect( connectOptions ).waitForCompletion();
		System.out.println( "connected" );

		// Another bug, you need to make a connection before the MoP proxy will even respond to pings, because you don't
		// have any adapter channels open to forward PINGREQ messages to.
		client.subscribe( "testsub1", 1 ).waitForCompletion();

		// sleep the keep alive period to show that PING will happen in abscence of other messages.
		Thread.sleep( 6000 );

		// make more subscriptions to connect to multiple brokers.
		client.subscribe( "testsub2", 1 ).waitForCompletion();
		client.subscribe( "testsub3", 1 ).waitForCompletion();


		// publish QoS 1 message to prevent the need for PINGREQ. Keep alive only sends ping in abscence of other
		// messages. Refer to section 3.1.2.10 of the MQTT 3.1.1 specification.
		while (true) {
			System.out.println( "Publishing message..." );
			client.publish( "test1", "test".getBytes(), 1, false ).waitForCompletion();
			Thread.sleep( 1000 );
		}
		// MoP keep alive is 1.5 * keep alive, so 7.5 seconds in this case. After 7.5 seconds from the last PINGREQ
		// the connection will die.
	}
}