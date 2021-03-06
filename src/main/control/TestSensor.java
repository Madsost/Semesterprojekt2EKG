package main.control;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Klasse til afprøvning af systemet
 * @author Mads Østergaard, Emma Lundgaard og Morten Vorborg.
 *
 */
public class TestSensor implements Sensor {
	private ArrayList<Double> dataset = new ArrayList<>();
	private final String variant = "100";
	private int count = 0;
	private boolean running = false;
	private int toOutputCount = 0;
	private double[] outputBuffer = new double[250];
	/**
	 *  sætter instansen op af Queue så det er den samme som databaseConn tilgår
	 */
	private Queue q = Queue.getInstance();

	/**
	 * Opsætter testsensoren
	 */
	@Override
	public void init() {
		System.out.println("Opsætter testsensor: " + this.getClass().getName());
		try {
			String fileName = "resources/EKGdata_" + variant + ".txt";
			File file = new File(fileName);
			Scanner sc = new Scanner(new FileReader(file));
			ArrayList<Double> temp = new ArrayList<>();
			while (sc.hasNext()) {
				temp.add(Double.parseDouble(sc.nextLine()));
			}
			for (double floating : temp) {
				dataset.add(floating);
			}
			sc.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Starter sensor-tråden - kaldes af <code>Thread.start()</code> i MainApp
	 */
	public void run() {
		running = true;
		System.out.println("Starter sensor-tråd: " + this.getClass().getName());

		while (true) {
			while (!running) {
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			if (count >= dataset.size())
				count = 0;
			outputBuffer[toOutputCount++] = dataset.get(count);
			if (toOutputCount == 250) {
				q.addToBuffer(outputBuffer);
				outputBuffer = new double[250];
				toOutputCount = 0;
			}
			count++;
			try {
				Thread.sleep(4);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Pauser tråden ved at sætte running til falsk
	 */
	@Override
	public void pauseThread() throws InterruptedException {
		running = false;

	}

	/**
	 * Fortsætter tråden ved at sætte running til sand.
	 */
	@Override
	public void resumeThread() {
		running = true;
	}

	/**
	 * Stopper forbindelsen.
	 */
	@Override
	public void stopConn() {
		running = false;
	}
}
