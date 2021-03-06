package com.kelvin.kelvinTestProjectMaven.mapreduceTest;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;
import org.apache.hadoop.mrunit.MapOutputShuffler;
import org.apache.hadoop.mrunit.mapreduce.MapDriver;
import org.apache.hadoop.mrunit.mapreduce.ReduceDriver;
import org.apache.hadoop.mrunit.types.Pair;
import org.junit.Before;
import org.junit.Test;

import com.kelvin.kelvinTestProjectMaven.mapreduce.WordCount;
import com.kelvin.kelvinTestProjectMaven.mapreduce.WordCount.Reduce;
import com.kelvin.kelvinTestProjectMaven.mapreduce.WordCount.WordCountMapper;

public class MapreduceTest {
	  private MapOutputShuffler<Text, IntWritable> shuffler;
	/*
	private static class WordCountMapper extends Mapper<LongWritable, Text, Text, IntWritable>{
		private final static IntWritable one = new IntWritable(1);
		private Text word = new Text();
		
		@Override
		protected void map(LongWritable key, Text value,Mapper<LongWritable, Text, Text, IntWritable>.Context context)throws IOException, InterruptedException {
			String line = value.toString();
			StringTokenizer tokenizer = new StringTokenizer(line);
			while(tokenizer.hasMoreTokens()){
				word.set(tokenizer.nextToken().toLowerCase());
				context.write(word, one);
			}
		}
	}
	*/
	
	
	MapDriver<LongWritable, Text, Text, IntWritable> mapDriver;
	ReduceDriver<Text, IntWritable, Text, IntWritable> reduceDriver;
	
	@Before
	public void setUp(){
		WordCount.WordCountMapper mapper = new WordCount.WordCountMapper();		
		mapDriver = MapDriver.newMapDriver(mapper);
		
		WordCount.Reduce mrUnitReducer = new WordCount.Reduce();
		reduceDriver = ReduceDriver.newReduceDriver(mrUnitReducer);		

	    shuffler = new MapOutputShuffler<Text, IntWritable>(null, null, null);
	}
	
	@Test
	public void testMapperOutput() throws IOException{
		mapDriver.withInput(new LongWritable(), new Text("Hi welcome to Acadgild"));
		mapDriver.withInput(new LongWritable(), new Text("Acadgild is into e-learning"));
		
		//test mapper output:
		System.out.println("After mapper:");
		List<Pair<Text, IntWritable>> resultList = mapDriver.run();
		for(Pair<Text, IntWritable> p : resultList){
			System.out.println(String.format("%s %d", p.getFirst().toString(), p.getSecond().get()));
		}
	}
	
	@Test
	public void testReducerOutput() throws IOException {
		
		//test reducer output:
		Pair<Text,List<IntWritable>> pair;
		List<IntWritable> valueList = new LinkedList<IntWritable>();
		valueList.add(new IntWritable(1));
		valueList.add(new IntWritable(2));
		valueList.add(new IntWritable(1));
		pair = new Pair<Text, List<IntWritable>>(new Text("word"), valueList);
		
		List<Pair<Text, List<IntWritable>>> pairList = new LinkedList<Pair<Text, List<IntWritable>>>();
		pairList.add(pair);

		valueList = new LinkedList<IntWritable>();
		valueList.add(new IntWritable(3));
		valueList.add(new IntWritable(2));
		pair = new Pair<Text, List<IntWritable>>(new Text("word2"), valueList);
		pairList.add(pair);
		
		System.out.println("reducer result:");
		reduceDriver.addAll(pairList);
		List<Pair<Text, IntWritable>> reduceResult = reduceDriver.run();
		for(Pair<Text, IntWritable> p: reduceResult){
			System.out.println(p);
		}
	}
	
	@Test
	public void testWordcountFlow() throws IOException{
		mapDriver.withInput(new LongWritable(), new Text("Hi welcome to Acadgild"));
		mapDriver.withInput(new LongWritable(), new Text("Acadgild is into e-learning"));
		
		//test mapper output:
		System.out.println("After mapper:");
		List<Pair<Text, IntWritable>> resultList = mapDriver.run();
		for(Pair<Text, IntWritable> p : resultList){
			System.out.println(String.format("%s %d", p.getFirst().toString(), p.getSecond().get()));
		}
		
		//test shuffler
		shuffler.shuffle(resultList);
		System.out.println("After shuffle:");
		for(Pair<Text, IntWritable> p : resultList){
			System.out.println(String.format("%s %d", p.getFirst().toString(), p.getSecond().get()));
		}
		
		//test reducer
		//send mapper output to reducer
		Pair<Text,List<IntWritable>> pair = new Pair<Text, List<IntWritable>>(new Text(""), new LinkedList<IntWritable>());
		String currentKey = "";
		String previousKey = "";
		List<IntWritable> valueList = new LinkedList<IntWritable>();
		boolean firstValue = false;
		List<Pair<Text,List<IntWritable>>> pairList = new LinkedList<Pair<Text, List<IntWritable>>>();
		for(Pair<Text, IntWritable> p : resultList){
			if(currentKey == "") {
				firstValue = true;
				currentKey = p.getFirst().toString();
				valueList = new LinkedList<IntWritable>();
				valueList.add(p.getSecond());
				pair = new Pair<Text, List<IntWritable>>(new Text(currentKey), valueList);
			}
			else {
				firstValue = false;
				currentKey = p.getFirst().toString();
			}
			
			//if currentKey == oldKey, add IntWritable to list
			if(!firstValue) {
				if(currentKey.equalsIgnoreCase(previousKey)) {
					pair.getSecond().add(p.getSecond());
				}
				//eles means find new key
				else {
					pairList.add(pair);
					//create new pair
					valueList = new LinkedList<IntWritable>();
					valueList.add(p.getSecond());
					pair = new Pair<Text, List<IntWritable>>(new Text(currentKey), valueList);
				}
			}
			previousKey = currentKey;
		}
		pairList.add(pair);
		System.out.println("send to reducer:");
		for(Pair<Text,List<IntWritable>> obj: pairList) {
			String key = obj.getFirst().toString();
			String value = " ";
			for(IntWritable intValue: obj.getSecond()) {
				value += intValue.toString() + " ";
			}
			System.out.println("key: " + key + "; value: " + value);
		}
		
		reduceDriver.addAll(pairList);
		List<Pair<Text, IntWritable>> reduceResult = reduceDriver.run();
		for(Pair<Text, IntWritable> p: reduceResult){
			System.out.println(p);
		}
	}
	
	@Test
	public void generatePoint() throws IOException{
		Random rd = new Random();
		double x = 0;
		double y = 0;
		int times = 1000;
		FileOutputStream fs = new FileOutputStream("points.txt");
		
		OutputStreamWriter or = new OutputStreamWriter(fs);
		BufferedWriter bw = new BufferedWriter(or);
		
		for(int i = 0; i < times; i++){
			x = rd.nextDouble()/2;
			y = rd.nextDouble()/2;
			
			bw.write(x + ";" + y);
			bw.newLine();
			bw.flush();
		}
		
		bw.close();		
	}
	
	@Test
	public void testpoint() throws IOException{
		//FileInputStream fs = new FileInputStream("points.txt");
		//InputStreamReader is = new InputStreamReader(fs);
		//BufferedReader bf = new BufferedReader(is);
		//String line = "";
		double totalPointsCount = 0;
		double pointsInCircleCount = 0;
		
		//while((line = bf.readLine()) != null){
		Random rd = new Random();
		for(int i = 0; i < 1000; i++){
			double xPosition = rd.nextDouble()/2;
			double yPosition = rd.nextDouble()/2;
			
			double distance = Math.sqrt(Math.pow(xPosition, 2) + Math.pow(yPosition, 2));
			totalPointsCount++;
			if(distance <= 0.5){
				pointsInCircleCount++;
			}
		}
		//}
		
		System.out.print("PI:" + 4 * pointsInCircleCount/totalPointsCount);
	}
	
//	@Test
//	public void testCalculatePIMapper() throws IOException{
//		LongWritable one = new LongWritable(1);
//		PICalculate.mapper mapper = new PICalculate.mapper();
//		MapDriver<LongWritable, Text, IntWritable, IntWritable> mapDriver = MapDriver.newMapDriver(mapper);
//		mapDriver.addInput(new Pair<LongWritable, Text>(one, new Text("1,1")));
//		mapDriver.addInput(new Pair<LongWritable, Text>(one, new Text("0.1,0.1")));
//		mapDriver.addInput(new Pair<LongWritable, Text>(one, new Text("0.2,0.1")));
//		mapDriver.addInput(new Pair<LongWritable, Text>(one, new Text("0.2,0.1")));
//		
//		List<Pair<IntWritable, IntWritable>> result = mapDriver.run();
//		for(Pair<IntWritable, IntWritable> p : result){
//			System.out.println(String.format("%d %d", p.getFirst().get(), p.getSecond().get()));
//		}
//	}

}
