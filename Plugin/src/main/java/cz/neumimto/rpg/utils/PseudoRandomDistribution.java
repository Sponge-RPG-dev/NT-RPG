package cz.neumimto.rpg.utils;

import static java.lang.Math.abs;
import static java.lang.Math.ceil;
import static java.lang.Math.min;

/**
 * Created by NeumimTo on 14.03.2016.
 */
public class PseudoRandomDistribution {

	public static double[] C = null;

	private int pmt = 0;

	public double getProbability(double c) {
		pmt++;
		return c * pmt;
	}

	public void reset() {
		pmt = 0;
	}


	public double c(double percentage) {
		double u = percentage;
		double l = 0;
		double m;
		double p1;
		double p2 = 1;
		while (true) {
			m = (u + l) / 2;
			p1 = p(m);
			if (abs(p1 - p2) <= 0) {
				break;
			}
			if (p1 > percentage) {
				u = m;
			} else {
				l = m;
			}

			p2 = p1;
		}

		return m;
	}

	private double p(double C) {
		double i;
		double j = 0;
		double sum = 0;
		int f = (int) ceil(1 / C);
		for (int N = 1; N <= f; ++N) {
			i = min(1, N * C) * (1 - j);
			j += i;
			sum += N * i;
		}
		return 1 / sum;
	}
}

