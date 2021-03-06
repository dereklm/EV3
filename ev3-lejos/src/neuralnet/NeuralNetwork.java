package neuralnet;

import java.util.Random;

public class NeuralNetwork
{
	// -1 1
	// 1 0
	double[][] trainInputs = new double[][]{
		new double[]{0, 1},
		new double[]{1, 1}
	};
	double[][] trainOutputs = new double[][]{
		new double[]{1, 0},
		new double[]{0, 1}
	};
	/*
	// current status: degrees (motorA, motorB)
	// desired degrees: (motorA, motorB)
	double[][] trainInputs = new double[][]{
		new double[]{0, 0, 0, 300},		// down
		new double[]{0, 300, 50, 300},	// grab
		new double[]{50, 300, 50, 0},	// up
		new double[]{50, 0, 50, 300},		// down
		new double[]{50, 300, 0, 300},		// drop
		new double[]{0, 300, 0, 0}		// up
	};
	// target output degrees: (motorA, motorB)
	double[][] trainOutputs = new double[][]{
		new double[]{0, 300},	// down
		new double[]{50, 0},	// grab
		new double[]{0, -300},	// up
		new double[]{0, 300},	// down
		new double[]{-50, 0},	// drop
		new double[]{0, -300}	// up
	};
	/*
	 *  expected (0): 148.126608316005
		expected (300): 16.7290016149696
		Result: 0% (0/2)
		expected (50): 113.281596139988
		expected (0): 278.381238298129
		Result: 0% (0/2)
	 */
	/*
	  0.435779133743901 0.671258004623946 
	  0.295312760291069 0.91864357120767 
	  0.49375536105335 0.0557633387165652 
	  0.743352171704018 0.861532711544111
	 */
	double[][] weights; // need matrix?
	int epochs = 1000;
	double learningRate = 0.35;
	long seed = System.currentTimeMillis();
//	int seed = (int) DateTime.Now.Ticks & 0x0000FFFF;

	public static void main(String[] args) {
		NeuralNetwork net = new NeuralNetwork ();
		System.out.println ();
		net.learn ();
		System.out.println ();

		// sensors (touch, light|color, ultrasonic) and bias 1
		// light sensor (0|1)
		//			double[] inputs = {0, 0, 300, 0};
		double[] inputs = {0, 1};
		// motors B, C on/off (0|1)
		//			double[] outputs = {0, 300};
		double[] outputs = {1, 0};

		net.test (inputs, outputs);
		// inputs: light 0-2, 3-8, 9-100
		// inputs: current position/status => light?
		// desired: r1 (down), r2(pick), r3(up), r4(down), r5(drop)
		// outputs: direction, speed, degree
		//			net.test (inputs, desired);

		//			inputs = new double[]{0, 300, 50, 0};
		//			outputs = new double[] {50, 0};
		inputs = new double[]{1, 1};
		outputs = new double[] {0, 1};

		net.test (inputs, outputs);
	}
	
	public NeuralNetwork ()
	{
		init();
	}

	public void learn() {
		System.out.println("Training network for " + epochs + " epochs ...");

		int tInputs = trainInputs.length;
		int tOutputs = trainOutputs.length;
		System.out.println("(tInputs, tOutputs) = (" + tInputs + ", " + tOutputs + ")");
		if (tInputs != tOutputs) {
			System.out.println ("train data have inconsistent dimensions");
			return;
		}
		for (int epoch = 0; epoch < epochs; epoch ++) {
//			System.out.println("======== epoch " + epoch + " =========");
			for (int i = 0; i < tInputs; i++) {
//				System.out.println ("[" + i + "] ");
				learn (trainInputs[i], trainOutputs[i]);
//				print (weights);
			}
//			System.out.println ();
		}
		print (weights);
	}

	public void learn(double[] inputs, double[] targets) {
		double error = 0;
		double[] outputs = new double[targets.length];
		double[] gradients = new double[targets.length];
		double sum;

		// forward propagate
		for (int j = 0; j < targets.length; j++) {
			sum = 0;
			for (int i = 0; i < inputs.length; i++) {
				sum += inputs [i] * weights [i] [j];
			}
//			outputs [j] = sum;
			outputs[j] = 1 / (1 + Math.exp(sum));
			error = targets [j] - outputs [j];
//			gradients[j] = error;
			gradients[j] = outputs[j] * (1 - outputs[j]) * error;
//			System.out.println (error + " " + targets [j] + " " + outputs [j]);
		}
		// compute errors

		// compute gradients

		// backward propagate
		for (int i = 0; i < inputs.length; i++) {
			for (int j = 0; j < targets.length; j++) {
				weights [i][j] += learningRate * gradients[j]*inputs[i];
//				System.out.println (weights [i][j] + " " + learningRate * gradients[j]*inputs[i] +
//					" " + gradients [j] + " " + inputs [i]);
			}
		}
	}

	public void test(double[] inputs, double[] targets) {
		int corrects = 0;
		double[] outputs = new double[targets.length];
		double sum;
		for (int j = 0; j < outputs.length; j ++) {
			sum = 0;
			for (int i = 0; i < inputs.length; i++) {
				sum += inputs [i] * weights [i] [j];
			}
			outputs [j] = 1 / (1 + Math.exp (sum));
//			outputs [j] = sum;
			System.out.println ("expected (" + targets [j] + "): " + outputs [j]);
			if (outputs [j] == targets [j])
				corrects++;
		}
		System.out.println ("Result: " + 100*corrects/targets.length + "% (" + corrects + "/" + targets.length + ")");
	}

	// initialize random weights
	public void init() {
		System.out.println("trainInputs: ");
		print(trainInputs);
		System.out.println("trainOutputs: ");
		print (trainOutputs);

		System.out.println ();

		int nInputs = trainInputs[0].length;
		int nOutputs = trainOutputs[0].length;

		System.out.println("Initializing weight matrix " + nInputs + " x " + nOutputs + " ...");
		weights = new double[nInputs][];
		Random rand = new Random(seed);

		for (int i = 0; i < weights.length; i ++) {
			weights[i] = new double[nOutputs];
			for (int j = 0; j < weights [i].length; j++) {
				weights [i] [j] = rand.nextDouble ();
			}
		}
		print (weights);
	}

	public void print(double[][] matrix) {
		for (int i = 0; i < matrix.length; i ++) {
			for (int j = 0; j < matrix [i].length; j++) {
				System.out.print (matrix [i][j] + " ");
			}
			System.out.println ();
		}
	}
}
