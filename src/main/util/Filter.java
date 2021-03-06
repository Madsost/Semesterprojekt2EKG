package main.util;

/**
 * <h1>Hjælpeklasse til at filtrere data.</h1> </br>
 * 
 * Implementerer et: båndpasfilter (doFilter), udjævningsfilter (doSmooth) og et
 * båndstopfilter (doNotch).</br>
 * 
 * @author Mads Østergaard, Emma Lundgaard og Morten Vorborg.
 *
 */
public class Filter {
	/**
	 * Koefficienterne til et båndpasfilter.
	 */
	private static double[] coeffs = { -0.000456, -0.001121, -0.001181, -0.000565, 0.000167, 0.000327, -0.000265,
			-0.001087, -0.001379, -0.000842, 0.000049, 0.000459, -0.000044, -0.001047, -0.001634, -0.001226, -0.000157,
			0.000581, 0.000239, -0.000950, -0.001920, -0.001735, -0.000500, 0.000661, 0.000601, -0.000738, -0.002185,
			-0.002369, -0.001028, 0.000645, 0.001039, -0.000353, -0.002351, -0.003094, -0.001783, 0.000458, 0.001516,
			0.000250, -0.002326, -0.003849, -0.002786, 0.000018, 0.001968, 0.001091, -0.002012, -0.004542, -0.004034,
			-0.000765, 0.002300, 0.002165, -0.001312, -0.005055, -0.005496, -0.001976, 0.002392, 0.003437, -0.000129,
			-0.005245, -0.007117, -0.003704, 0.002095, 0.004849, 0.001636, -0.004940, -0.008818, -0.006050, 0.001220,
			0.006320, 0.004112, -0.003912, -0.010507, -0.009173, -0.000501, 0.007757, 0.007524, -0.001810, -0.012082,
			-0.013395, -0.003538, 0.009062, 0.012377, 0.002057, -0.013440, -0.019540, -0.008979, 0.010142, 0.020125,
			0.009569, -0.014488, -0.030384, -0.020444, 0.010912, 0.036818, 0.028987, -0.015150, -0.062475, -0.063333,
			0.011314, 0.142314, 0.267459, 0.318885, 0.267459, 0.142314, 0.011314, -0.063333, -0.062475, -0.015150,
			0.028987, 0.036818, 0.010912, -0.020444, -0.030384, -0.014488, 0.009569, 0.020125, 0.010142, -0.008979,
			-0.019540, -0.013440, 0.002057, 0.012377, 0.009062, -0.003538, -0.013395, -0.012082, -0.001810, 0.007524,
			0.007757, -0.000501, -0.009173, -0.010507, -0.003912, 0.004112, 0.006320, 0.001220, -0.006050, -0.008818,
			-0.004940, 0.001636, 0.004849, 0.002095, -0.003704, -0.007117, -0.005245, -0.000129, 0.003437, 0.002392,
			-0.001976, -0.005496, -0.005055, -0.001312, 0.002165, 0.002300, -0.000765, -0.004034, -0.004542, -0.002012,
			0.001091, 0.001968, 0.000018, -0.002786, -0.003849, -0.002326, 0.000250, 0.001516, 0.000458, -0.001783,
			-0.003094, -0.002351, -0.000353, 0.001039, 0.000645, -0.001028, -0.002369, -0.002185, -0.000738, 0.000601,
			0.000661, -0.000500, -0.001735, -0.001920, -0.000950, 0.000239, 0.000581, -0.000157, -0.001226, -0.001634,
			-0.001047, -0.000044, 0.000459, 0.000049, -0.000842, -0.001379, -0.001087, -0.000265, 0.000327, 0.000167,
			-0.000565, -0.001181, -0.001121, -0.000456 };
	private static int length = coeffs.length;
	private static double[] delayLine = new double[length];
	private static int count;

	/**
	 * Savitzky–Golay udjævner-filter
	 */
	private static double[] smoothCoeffs = { -2, 3, 6, 7, 6, 3, -2 };
	private static int smoothLength = smoothCoeffs.length;
	private static double[] smoothDelayLine = new double[smoothLength];
	private static int smoothCount;
	private static double normalize = 1.0 / 21.0;

	/**
	 * Notch-filter-koefficienter
	 */
	private static double[] notchCoeffs = { 0.000106, 0.000072, -0.000014, 0.000000, 0.000014, -0.000075, -0.000115,
			0.000059, 0.000244, 0.000092, -0.000285, -0.000331, 0.000144, 0.000528, 0.000183, -0.000530, -0.000584,
			0.000244, 0.000861, 0.000288, -0.000815, -0.000876, 0.000358, 0.001238, 0.000407, -0.001132, -0.001198,
			0.000483, 0.001646, 0.000534, -0.001464, -0.001530, 0.000609, 0.002049, 0.000657, -0.001779, -0.001836,
			0.000722, 0.002399, 0.000759, -0.002029, -0.002067, 0.000802, 0.002627, 0.000819, -0.002158, -0.002163,
			0.000825, 0.002655, 0.000812, -0.002096, -0.002056, 0.000765, 0.002398, 0.000712, -0.001778, -0.001677,
			0.000597, 0.001775, 0.000494, -0.001137, -0.000966, 0.000297, 0.000712, 0.000137, -0.000123, 0.000127,
			-0.000151, -0.000838, -0.000374, 0.001295, 0.001629, -0.000756, -0.002898, -0.001042, 0.003125, 0.003538,
			-0.001515, -0.005451, -0.001859, 0.005339, 0.005824, -0.002415, -0.008443, -0.002808, 0.007882, 0.008423,
			-0.003427, -0.011779, -0.003855, 0.010664, 0.011239, -0.004514, -0.015328, -0.004960, 0.013569, 0.014153,
			-0.005628, -0.018930, -0.006069, 0.016460, 0.017024, -0.006715, -0.022406, -0.007129, 0.019189, 0.019703,
			-0.007717, -0.025571, -0.008081, 0.021609, 0.022044, -0.008579, -0.028250, -0.008873, 0.023582, 0.023912,
			-0.009251, -0.030286, -0.009457, 0.024992, 0.025198, -0.009694, -0.031559, -0.009800, 0.025755, 0.025825,
			-0.009881, 0.967746, -0.009881, 0.025825, 0.025755, -0.009800, -0.031559, -0.009694, 0.025198, 0.024992,
			-0.009457, -0.030286, -0.009251, 0.023912, 0.023582, -0.008873, -0.028250, -0.008579, 0.022044, 0.021609,
			-0.008081, -0.025571, -0.007717, 0.019703, 0.019189, -0.007129, -0.022406, -0.006715, 0.017024, 0.016460,
			-0.006069, -0.018930, -0.005628, 0.014153, 0.013569, -0.004960, -0.015328, -0.004514, 0.011239, 0.010664,
			-0.003855, -0.011779, -0.003427, 0.008423, 0.007882, -0.002808, -0.008443, -0.002415, 0.005824, 0.005339,
			-0.001859, -0.005451, -0.001515, 0.003538, 0.003125, -0.001042, -0.002898, -0.000756, 0.001629, 0.001295,
			-0.000374, -0.000838, -0.000151, 0.000127, -0.000123, 0.000137, 0.000712, 0.000297, -0.000966, -0.001137,
			0.000494, 0.001775, 0.000597, -0.001677, -0.001778, 0.000712, 0.002398, 0.000765, -0.002056, -0.002096,
			0.000812, 0.002655, 0.000825, -0.002163, -0.002158, 0.000819, 0.002627, 0.000802, -0.002067, -0.002029,
			0.000759, 0.002399, 0.000722, -0.001836, -0.001779, 0.000657, 0.002049, 0.000609, -0.001530, -0.001464,
			0.000534, 0.001646, 0.000483, -0.001198, -0.001132, 0.000407, 0.001238, 0.000358, -0.000876, -0.000815,
			0.000288, 0.000861, 0.000244, -0.000584, -0.000530, 0.000183, 0.000528, 0.000144, -0.000331, -0.000285,
			0.000092, 0.000244, 0.000059, -0.000115, -0.000075, 0.000014, 0.000000, -0.000014, 0.000072, 0.000106 };
	private static int notchLength = notchCoeffs.length;
	private static double[] notchDelay = new double[notchLength];
	private static int notchCount = 0;

	/**
	 * Folder input med koefficienterne for båndpasfilteret
	 * 
	 * @param data
	 *            værdien der skal filtreres
	 * @return den filtrerede værdi.
	 */
	public static double doFilter(double data) {
		// indsæt input på næste plads
		delayLine[count] = data;
		double result = 0.0;

		// gennemløb listerne og fold delayLine med coeffs: Sum(x(n-k)*h(n))
		int index = count;
		for (int i = 0; i < length; i++) { /* 1A */
			result += coeffs[i] * delayLine[index];
			// tæl index ned for at få den omvendte sekvens
			index--;

			// hvis index er < 0 skal vi fortsætte i max
			if (index < 0) /* 1B */
				index = length - 1;
		}
		// tæl count op, så næste måling kommer ind på næste plads
		count++;

		// hvis count er større end længden, sættes ind på plads 0.
		if (count >= length) /* 1C */
			count = 0;
		return result;
	}

	/**
	 * Folder input med koefficienterne for udjævningsfilteret
	 * 
	 * @param input
	 *            værdien der skal filtreres
	 * @return den filtrerede værdi.
	 */
	public static double doSmooth(double input) {
		// indsæt input på næste plads
		smoothDelayLine[smoothCount] = input;
		double result = 0.0;

		// gennemløb listerne og fold delayLine med coeffs: Sum(x(n-k)*h(n))
		int index = smoothCount;
		for (int i = 0; i < smoothLength; i++) { /* 2A */
			result += smoothCoeffs[i] * smoothDelayLine[index];
			// tæl index ned for at få den omvendte sekvens
			index--;

			// hvis index er < 0 skal vi fortsætte i max
			if (index < 0) /* 2B */
				index = smoothLength - 1;
		}
		// tæl count op, så næste måling kommer ind på næste plads
		smoothCount++;

		// hvis count er større end længden, sættes ind på plads 0.
		if (smoothCount >= smoothLength) /* 2C */
			smoothCount = 0;
		// normalisering ( 1/21 ):
		result *= normalize;
		return result;
	}

	/**
	 * Folder input med koefficienterne for båndstopfilteret
	 * 
	 * @param data
	 *            værdien der skal filtreres
	 * @return den filtrerede værdi.
	 */
	public static double doNotch(double data) {
		// indsæt input på næste plads
		notchDelay[notchCount] = data;
		double result = 0.0;

		// gennemløb listerne og fold delayLine med coeffs: Sum(x(n-k)*h(n))
		int index = notchCount;
		for (int i = 0; i < notchLength; i++) { /* 3A */
			result += notchCoeffs[i] * notchDelay[index];
			// tæl index ned for at få den omvendte sekvens
			index--;

			// hvis index er < 0 skal vi fortsætte i max
			if (index < 0) /* 3B */
				index = notchLength - 1;
		}
		// tæl count op, så næste måling kommer ind på næste plads
		notchCount++;

		// hvis count er større end længden, sættes ind på plads 0.
		if (notchCount >= notchLength) /* 3C */
			notchCount = 0;
		return result;
	}

}
