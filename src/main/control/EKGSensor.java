package main.control;

import java.util.ArrayList;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import jssc.SerialPortList;

public class EKGSensor implements Sensor {

	private Queue queue = Queue.getInstance();
	private int baudRate = 38400;
	private SerialPort port;
	private String input = "";

	public EKGSensor() {
		init();
	}

	public void run() {

		try {
			// we listen on the Serial port
			port.addEventListener(new SerialPortEventListener() {

				@Override
				public void serialEvent(SerialPortEvent event) {
					try {
						if (event.getEventValue() > 0) {

							// put what is on the buffer in a String
							input += port.readString(event.getEventValue());

							// while there is someting in the string we read, we
							// extract
							// values from it
							while (input.contains("-")) {
								// if there are null og nothing instead of a
								// value, remove
								// it
								if (input.substring(0, input.indexOf("-")).contains("null")
										|| input.substring(0, input.indexOf("-")).equals("")) {
									input = input.substring(input.indexOf("-") + 1);
								}

								// read the next value, parse it to an int and
								// put it in the Queue
								if (!(input.substring(0) == "-")) {
									try {
										queue.addToBuffer(Integer.parseInt(input.substring(0, input.indexOf("-"))));
										// removes the value we just saved to
										// the queue so
										// we dont read it again
										input = input.substring(input.indexOf("-") + 1);
									} catch (NumberFormatException ex) {
										System.out.println("NFE ved parsing" + ex);
									}
								}
							}
						}
					} catch (SerialPortException e) {
						// TODO Auto-generated catch block
						System.out.println("Fik ikke læst fra porten ");
						e.printStackTrace();
					}

					// we were done with the buffer on the arduino so we return
					// and stop running

				}

			});
		} catch (SerialPortException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void init() {
		try {
			// Laver et array af porte og sætter de tilgængelige porte ind
			String[] portArray = SerialPortList.getPortNames();
			// Gemmer navnet på den første port i portlisten i portName
			String portName = portArray[0];
			// laver en instans af SerialPort (jssc), port, med argumentet
			// portName, som var dne første port i listen af porte
			port = new SerialPort(portName);
			// porten åbnes
			port.openPort();

			// standard parametre der skal sætes ved åbning af port
			// - 9600 er standard baud rate, som sættes til at være det samme
			// som arduinoen kører
			// - 8 er antallet af dataBits
			// - 1 er stop bits
			// - 0 er paritetstypen
			port.setParams(baudRate, 8, 1, 0);
			port.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
			port.setDTR(true);

		} catch (SerialPortException ex) {
			System.out.println("Serial Port Exception: " + ex);
		}
	}

}