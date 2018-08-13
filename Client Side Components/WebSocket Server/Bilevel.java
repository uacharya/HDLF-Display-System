import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

/**
 * @author Ujjwal Acharya : ServerEndPoint that implements web socket protocol
 *         to communicate with the D3 webpages by providing them dimensions
 *         about the part of web page they need to display and also sends same
 *         event that occurs in either one of the 9 browsers
 */
@ServerEndpoint(value = "/bilevelserver")
public class Bilevel {
	private static final double height = 6480;
	private static final double width = 11520;
	private static double yaw = 0, dragged = 0;
	private boolean allowClick;
	private boolean resetCanvas;

	// list of all the sessions into one container and all instances in one
	private static final Set<Session> allSessions = Collections.synchronizedSet(new HashSet<Session>());
	private static final Set<Bilevel> allInstances = Collections.synchronizedSet(new HashSet<Bilevel>());

	/**
	 * This function creates an websocket instance with additional properties
	 * associated with each object
	 */
	public Bilevel() {
		this.allowClick = false;
		this.resetCanvas = false;
	}

	/**
	 * This method handles the incoming connection from d3 web page to the d3
	 * synchronize server
	 * 
	 * @param session
	 *            started by a d3 webpage client
	 */
	@OnOpen
	public void handleOpen(Session session) {
		// adding all of the sessions connected
		allSessions.add(session); // add all the new sessions into one set
		allInstances.add(this); // add all the new instances into one set
		System.out.println("The number of client connected are " + allSessions.size());
	}

	/**
	 * This method responds to each or all of the open browsers based on the
	 * type of message sent from either one of them
	 * 
	 * @param session
	 *            specific to a connection from a browser running a d3 page
	 * @param message
	 *            sent by either one of the browser or d3 page
	 */
	@OnMessage
	public void handleMessage(Session session, String message) {
		// getting ip address of the browser that has sent a message from web
		// filter used for the server
		String IPAddress = session.getRequestParameterMap().get("IP").get(0);
		System.out.println("the ip address for this connection is " + IPAddress);
		// message received from either one of the client browser
		System.out.println("recieved event from the client is " + message);
		// send dimension if dimension coordinates are requested for browser
		// belonging to screen in particular node
		if (message.contains("X=")) {
			sendDisplayDimension(session, IPAddress, message);
		} else if (message.contains("click") && message.contains("reset")) {
			this.allowClick = message.substring(message.indexOf(":") + 1, message.indexOf(",")).equals("false")
					? false : true;
			System.out.println(this.allowClick);
			this.resetCanvas = message.substring(message.lastIndexOf(":") + 1, message.length() - 1).equals("false")
					? false : true;
		} else if (message.contains("click")) {
			this.allowClick = message.substring(message.indexOf(":") + 1, message.length() - 1).equals("false") ? false
					: true;
			System.out.println(this.allowClick);
		} else if (message.contains("drag")) {
			synchronized (allInstances) {
				for (Bilevel e : allInstances) {
					if (e.getAllowInteraction() == false) {
						return;
					}
				}
			}
				
			// caling functin that handles the panning event
			handlePanning(Double.parseDouble(message.substring(message.indexOf(":") + 1, message.length() - 1)));
		} else if (message.contains("fetch")) {
			System.out.println("got inside fetch"+message);
			// sending fetch instruction after node 2 makes sure that the data
			// for that date is available
			sendMessageToAll(message);// sending events to all
		} else if (message.contains("reset")) {
			this.resetCanvas = message.substring(message.indexOf(":") + 1, message.length() - 1).equals("false") ? false
					: true;
			synchronized (allInstances) {
				for (Bilevel e : allInstances) {
					if (e.resetCanvasOrNot() == false) {
						return;
					}
				}
				sendMessageToAll("reset");// sending events to all
			}
		} else {
			synchronized (allInstances) {
				for (Bilevel e : allInstances) {
					if (e.getAllowInteraction() == false) {
						return;
					}
				}
				sendMessageToAll(message);// sending events to all
			}
		}

	}

	/**
	 * This method sends event data to all the browsers that has open connection
	 * with the server
	 * 
	 * @param message
	 *            event data to send
	 */
	private void sendMessageToAll(String message) {
		System.out.println("the data to send is" + message);
		// iterate over all active sessions and send the same event
		synchronized (allSessions) {
			for (Session ssn : allSessions) {
				if (ssn.isOpen()) {
					ssn.getAsyncRemote().sendText(message); // before
															// synchronously
															// message was sent
															// blocking until
															// message was sent
				}
			}
		}
	}

	/**
	 * This function is responsible for providing panning parameters to all of
	 * the monitors in connection
	 * 
	 * @param transformX
	 *            how much of total x has bee transformed
	 */
	private void handlePanning(double transformX) {
		// checking if new transformation is done only in x axis
		if (transformX != dragged) {
			double xMoved = transformX - dragged;
			System.out.println("xMoved is : " + xMoved);
			double degreesMoved = 360 * (xMoved / width);
			double newYaw = yaw + degreesMoved;
			// checking if the panning is inside bounds
			if (newYaw >= -40 && newYaw <= 40) {
				yaw = newYaw;
				dragged = transformX;
				System.out.println(" the yaw and dragged are " + yaw + " " + dragged);
				sendMessageToAll("{\"yaw\":" + newYaw + ",\"drag\":" + transformX + "}");
			} else {
				// if not inside bound resetting the front end event handler
				// object to old values
				sendMessageToAll("{\"drag\":" + dragged + "}");
			}

		}
	}

	/**
	 * This method creates and sends dimension data for each browser based on
	 * where the connection comes from
	 * 
	 * @param session
	 *            specific to a connection from a browser running a d3 page
	 * @param IPAddress
	 *            of the browser which is requesting for dimension data
	 * @param monitorLocation
	 *            screenX coordinate of the browser which is requesting
	 *            dimension data
	 */
	private void sendDisplayDimension(Session session, String IPAddress, String monitorLocation) {
		// getting parameter for the particular browser instance and sending it
		if (session.isOpen()) {
			try {
				session.getBasicRemote().sendText(determineParametersToReturn(IPAddress, monitorLocation));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * This method creates and returns dimension data based on the connection Ip
	 * address and the monitor screenX location
	 * 
	 * @param IPAddress
	 *            of the browser which is requesting for dimension data
	 * @param monitorLocation
	 *            screenX coordinate of the browser which is requesting
	 *            dimension data
	 * @return the required dimension data
	 */
	private String determineParametersToReturn(String IPAddress, String monitorLocation) {
		String dimension = "";
		if (IPAddress.equalsIgnoreCase("10.29.3.2")) {
			int browserLocation = Integer.parseInt(monitorLocation.split("=")[1]);
			if (browserLocation >= 0 && browserLocation < 3840) {
				dimension = "?0," + (int) Math.ceil((height / 3) * 2);
			} else if (browserLocation >= 3840 && browserLocation < 7680) {
				dimension = "?" + (int) Math.ceil(width / 3) + "," + (int) Math.ceil((height / 3) * 2);
			} else if (browserLocation >= 7680) {
				dimension = "?" + (int) Math.ceil((width / 3) * 2) + "," + (int) Math.ceil((height / 3) * 2);
			}
		} else if (IPAddress.equalsIgnoreCase("10.29.2.184")) {
			int browserLocation = Integer.parseInt(monitorLocation.split("=")[1]);
			if (browserLocation >= 0 && browserLocation < 3840) {
				dimension = "?0," + (int) Math.ceil(height / 3);
			} else if (browserLocation >= 3840 && browserLocation < 7680) {
				dimension = "?" + (int) Math.ceil(width / 3) + "," + (int) Math.ceil(height / 3);
			} else if (browserLocation >= 7680) {
				dimension = "?" + (int) Math.ceil((width / 3) * 2) + "," + (int) Math.ceil(height / 3);
			}

		} else if (IPAddress.equalsIgnoreCase("10.29.2.109")) {
			int browserLocation = Integer.parseInt(monitorLocation.split("=")[1]);
			if (browserLocation >= 0 && browserLocation < 3840) {
				dimension = "?0,0";
			} else if (browserLocation >= 3840 && browserLocation < 7680) {
				dimension = "?" + (int) Math.ceil(width / 3) + ",0";
			} else if (browserLocation >= 7680) {
				dimension = "?" + (int) Math.ceil((width / 3) * 2) + ",0";
			}
		}
		return dimension;
	}

	/**
	 * This function just returns a property associated with each websocket
	 * object created
	 * 
	 * @return boolean stating should click interaction be allowed in all nodes
	 *         or not
	 */
	public boolean getAllowInteraction() {
		return this.allowClick;
	}

	/**
	 * This function just returns a property associated with each websocket
	 * object created
	 * 
	 * @return boolean stating whether reset canvas or not in all nodes
	 */
	public boolean resetCanvasOrNot() {
		return this.resetCanvas;
	}

	/**
	 * This method closes the session if the web page is closed in the browser
	 * 
	 * @param session
	 *            specific to a connection from a browser running a d3 page
	 */
	@OnClose
	public void handleClose(Session session) {
		System.out.println("client is now closed with ID " + session.getId());
		allSessions.remove(session);
		allInstances.remove(this);

		if (allSessions.size() == 0) {
			yaw = 0;
			dragged = 0;
		}
	}

	/**
	 * This method prints stack trace if some kind of unwanted error occurred
	 * while running server
	 * 
	 * @param t
	 *            the error object
	 */
	@OnError
	public void handleError(Throwable t) {
		t.printStackTrace();

	}

}