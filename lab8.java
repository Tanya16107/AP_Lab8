import java.util.concurrent.*;
import java.util.*;
import java.io.*;


/**Part 1**/
class BinomialCoeff extends RecursiveTask<Long>{
	long n, k;

	public BinomialCoeff(long n, long k){
		this.n = n;
		this.k = k;
	}

	public Long compute(){
		if (n == 0 || k == 0 || n == k) {
			return (long)1;
		}

		else{
			BinomialCoeff left = new BinomialCoeff(n-1, k);
			BinomialCoeff right = new BinomialCoeff(n-1, k-1);
			left.fork();
			return right.compute()+left.join();
		}
	}

	public static void main(String[] args) throws Exception{
		Scanner sc = new Scanner(System.in);
		long n = sc.nextLong();
		long k = sc.nextLong();

		ForkJoinPool pool = new ForkJoinPool(3);

		BinomialCoeff task = new BinomialCoeff(n, k);
		
		Long out = pool.invoke(task);

		System.out.println(out);


	}

}

/** Part 2: Flyweighted**/
class FlyweightedBinomialCoeff extends RecursiveAction{

   private static final long serialVersionUID = 1L;

	private long n, k;
	private long result;

	public FlyweightedBinomialCoeff(long n, long k){
		this.n = n;
		this.k = k;
	}

	private static volatile Map<String, FlyweightedBinomialCoeff> instances = new HashMap<String, FlyweightedBinomialCoeff>();

	public static synchronized FlyweightedBinomialCoeff getInstance(long n, long k){
		String key = n+", "+k;

		if(!instances.containsKey(key)){
			instances.put(key, new FlyweightedBinomialCoeff(n, k));
		}

		return instances.get(key);

	}

	@Override
	public void compute() {
		if (n == 0 || k == 0 || n == k) {
			result = 1;
			return;
		}

		FlyweightedBinomialCoeff left = getInstance(n-1, k);
		FlyweightedBinomialCoeff right = getInstance(n-1, k-1);
		left.fork();
		right.compute();
		left.join();
		result = right.getResult()+left.getResult();
	
	}

	public long getResult(){
		return result;
	}

	public static void main(String[] args) throws Exception{
		Scanner sc = new Scanner(System.in);
		//long n = sc.nextLong();
		//long k = sc.nextLong();


		ForkJoinPool pool = new ForkJoinPool(1);

		FlyweightedBinomialCoeff action = getInstance(100, 10);
		

		long startTime = System.currentTimeMillis();
		pool.invoke(action);

		//System.out.println(action.getResult());

		long endTime   = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println(totalTime);

	}

}